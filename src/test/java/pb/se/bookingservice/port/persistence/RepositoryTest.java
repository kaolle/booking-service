package pb.se.bookingservice.port.persistence;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.junit.jupiter.Testcontainers;
import pb.se.bookingservice.domain.Booking;
import pb.se.bookingservice.domain.FamilyMember;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;

@DataMongoTest
@Testcontainers
@ContextConfiguration(classes = MongoDBTestContainerConfig.class)
class RepositoryTest {

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    BookingRepository bookingRepository;
    @Autowired
    FamilyMemberRepository familyMemberRepository;

    @BeforeEach
    void init() {
        bookingRepository.deleteAll();
        familyMemberRepository.deleteAll();
    }

    @AfterEach
    void shutDown() {
        bookingRepository.deleteAll();
        familyMemberRepository.deleteAll();
    }

    @Test
    void canSaveAndGetAllBooking() {

        //when
        FamilyMember member = new FamilyMember(UUID.randomUUID().toString(), "Mycho Brahe");
        familyMemberRepository.save(member);
        Booking booking = new Booking(Instant.now().minus(1, ChronoUnit.DAYS), Instant.now(), member);
        bookingRepository.save(booking);

        //then
        assertThat(bookingRepository.findAll().size(), Matchers.is(1));
        assertThat(bookingRepository.findAll().get(0), Matchers.is(booking));
    }

    @Test
    void canFindFamillyMemberById() {

        //when
        UUID uuid = UUID.randomUUID();
        FamilyMember member = new FamilyMember(uuid, "Mycho Brahe", "baba");
        familyMemberRepository.save(member);

        Optional<FamilyMember> foundMember = familyMemberRepository.findById(uuid);
        //then
        assertThat(foundMember.get(), Matchers.is(member));
    }


}
