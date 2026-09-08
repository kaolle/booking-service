package pb.se.bookingservice.port.task;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pb.se.bookingservice.domain.FamilyMember;
import pb.se.bookingservice.port.persistence.BookingRepository;
import pb.se.bookingservice.port.persistence.FamilyMemberRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
@Component
public class UpdateDatabaseTask {

    private static final Logger logger = LogManager.getLogger(UpdateDatabaseTask.class);
    @Autowired
    BookingRepository bookingRepository;
    @Autowired
    FamilyMemberRepository familyMemberRepository;

    private void repairBookings() {
        FamilyMember deletedMember = familyMemberRepository.findById(FamilyMember.DELETED_MEMBER_ID)
                .orElseGet(() -> familyMemberRepository.save(FamilyMember.deleted()));

        // MongoDB resolves a DBRef whose target was deleted to null.
        bookingRepository.findAll().stream()
                .filter(booking -> booking.getFamilyMember() == null)
                .forEach(booking -> {
                    booking.setFamilyMember(deletedMember);
                    bookingRepository.save(booking);
                    logger.info("Repaired orphaned booking {} from {} to {}",
                            booking.getId(), booking.getFrom(), booking.getTo());
                });
    }

    @Scheduled(fixedDelay = 86400000, initialDelay = 3000) // Run once every 24 hour and 3 second after startup
    public void scheduledMethod() {
        repairBookings();
        logger.info("Cleanup old bookings");


        // Delete bookings that ended more than 60 days ago.
        bookingRepository.findAll().stream()
                .filter(b -> b.getTo().isBefore(Instant.now().minus(60, ChronoUnit.DAYS)))
                .forEach( b -> {
                    logger.info("delete passed booking {} ",b);
                    bookingRepository.delete(b);
        });

        logger.info("Completed update and cleanup");
    }

}
