package pb.se.bookingservice.port.task;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import pb.se.bookingservice.domain.Booking;
import pb.se.bookingservice.domain.FamilyMember;
import pb.se.bookingservice.port.persistence.BookingRepository;
import pb.se.bookingservice.port.persistence.FamilyMemberRepository;
import pb.se.bookingservice.port.persistence.MongoDBTestContainerConfig;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@ContextConfiguration(classes = MongoDBTestContainerConfig.class)
@Import(UpdateDatabaseTask.class)
class UpdateDatabaseTastTest {
    @Autowired
    UpdateDatabaseTask task;
    @Autowired
    BookingRepository bookings;
    @Autowired
    FamilyMemberRepository members;

    @BeforeEach
    @AfterEach
    void cleanDatabase() {
        bookings.deleteAll();
        members.deleteAll();
    }

    @Test
    void createsDeletedMemberEvenWhenThereAreNoBookings() {
        task.scheduledMethod();

        assertThat(members.findAll()).containsExactly(FamilyMember.deleted());
        assertThat(bookings.count()).isZero();
    }

    @Test
    void repairsMissingAndDanglingReferencesWithoutChangingValidBookings() {
        FamilyMember removed = members.save(new FamilyMember("Removed"));
        FamilyMember valid = members.save(new FamilyMember("Valid"));
        Booking dangling = saveBooking(removed);
        Booking missing = saveBooking(null);
        Booking healthy = saveBooking(valid);
        members.deleteById(removed.getUuid());
        assertThat(bookings.findById(dangling.getId()).orElseThrow().getFamilyMember()).isNull();

        task.scheduledMethod();

        assertThat(bookings.count()).isEqualTo(3);
        for (Booking orphan : new Booking[]{dangling, missing}) {
            Booking repaired = bookings.findById(orphan.getId()).orElseThrow();
            assertThat(repaired.getFamilyMember()).isEqualTo(FamilyMember.deleted());
            assertThat(repaired.getFrom()).isEqualTo(orphan.getFrom());
            assertThat(repaired.getTo()).isEqualTo(orphan.getTo());
        }
        assertThat(bookings.findById(healthy.getId()).orElseThrow()).isEqualTo(healthy);
    }

    @Test
    void reusesExistingDeletedMemberAndCanRunRepeatedly() {
        FamilyMember existing = members.save(new FamilyMember(
                FamilyMember.DELETED_MEMBER_ID, "borttagen", "Existing placeholder"));
        Booking orphan = saveBooking(null);
        Booking alreadyRepaired = saveBooking(existing);

        task.scheduledMethod();
        task.scheduledMethod();

        assertThat(members.findAll()).containsExactly(existing);
        assertThat(bookings.count()).isEqualTo(2);
        assertThat(bookings.findById(orphan.getId()).orElseThrow().getFamilyMember()).isEqualTo(existing);
        assertThat(bookings.findById(alreadyRepaired.getId()).orElseThrow()).isEqualTo(alreadyRepaired);
    }

    @Test
    void scheduledTaskRepairsBookingsAndStillDeletesExpiredBookings() {
        Booking orphan = saveBooking(null);
        Booking expired = bookings.save(new Booking(Instant.now().minus(90, ChronoUnit.DAYS),
                Instant.now().minus(70, ChronoUnit.DAYS), null));

        task.scheduledMethod();

        assertThat(bookings.findById(orphan.getId()).orElseThrow().getFamilyMember())
                .isEqualTo(FamilyMember.deleted());
        assertThat(bookings.existsById(expired.getId())).isFalse();
    }

    @Test
    void cleanupDeletesOldBookingsButKeepsRecentOngoingAndFutureBookings() {
        FamilyMember member = members.save(new FamilyMember("Valid"));
        Instant now = Instant.now();
        Booking old = bookings.save(new Booking(now.minus(90, ChronoUnit.DAYS),
                now.minus(61, ChronoUnit.DAYS), member));
        Booking recent = bookings.save(new Booking(now.minus(70, ChronoUnit.DAYS),
                now.minus(59, ChronoUnit.DAYS), member));
        Booking ongoing = bookings.save(new Booking(now.minus(2, ChronoUnit.DAYS),
                now.plus(2, ChronoUnit.DAYS), member));
        Booking future = saveBooking(member);

        task.scheduledMethod();

        assertThat(bookings.existsById(old.getId())).isFalse();
        assertThat(bookings.findAll()).containsExactlyInAnyOrder(recent, ongoing, future);
        assertThat(members.findById(member.getUuid())).contains(member);
    }

    private Booking saveBooking(FamilyMember member) {
        return bookings.save(new Booking(Instant.now().plus(1, ChronoUnit.DAYS),
                Instant.now().plus(7, ChronoUnit.DAYS), member));
    }
}
