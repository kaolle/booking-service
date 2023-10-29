package pb.se.bookingservice.port.task;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pb.se.bookingservice.domain.Booking;
import pb.se.bookingservice.domain.FamilyMember;
import pb.se.bookingservice.port.persistence.BookingRepository;
import pb.se.bookingservice.port.persistence.FamilyMemberRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
@Component
public class UpdateTestBookingsTask {

    private static final String MY_UUID = "34ea9416-74c7-11ee-b962-0242ac120002";
    private static final Logger logger = LogManager.getLogger(UpdateTestBookingsTask.class);
    @Autowired
    BookingRepository bookingRepository;
    @Autowired
    FamilyMemberRepository familyMemberRepository;

    @Scheduled(fixedDelay = 3605000, initialDelay = 5000) // Run every hour and 1 second after startup
    public void scheduledMethod() {
        logger.info("Start create new test bookings");
        bookingRepository.deleteAll();
        familyMemberRepository.deleteAll();
        FamilyMember familyMember1 = new FamilyMember( "Donald Thrump", "A piece full of shit");
        FamilyMember familyMember2 = new FamilyMember( "Putin", "Dictator");
        FamilyMember familyMember3 = new FamilyMember( "Åkesson", "Dictator wanna be");
        FamilyMember familyMember4 = new FamilyMember( UUID.fromString(MY_UUID), "Stefan", "The Start");
        familyMemberRepository.save(familyMember1);
        familyMemberRepository.save(familyMember2);
        familyMemberRepository.save(familyMember3);
        familyMemberRepository.save(familyMember4);
        bookingRepository.save(new Booking(Instant.now().plus(7, ChronoUnit.DAYS), Instant.now().plus(14, ChronoUnit.DAYS), familyMember1));
        bookingRepository.save(new Booking(Instant.now().plus(28, ChronoUnit.DAYS), Instant.now().plus(35, ChronoUnit.DAYS), familyMember2));
        bookingRepository.save(new Booking(Instant.now().plus(1, ChronoUnit.DAYS), Instant.now().plus(3, ChronoUnit.DAYS), familyMember3));
        logger.info("Completed create new test bookings");
    }
}
