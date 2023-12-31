package pb.se.bookingservice.port.task;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pb.se.bookingservice.domain.Booking;
import pb.se.bookingservice.domain.FamilyMember;
import pb.se.bookingservice.domain.User;
import pb.se.bookingservice.port.persistence.BookingRepository;
import pb.se.bookingservice.port.persistence.FamilyMemberRepository;
import pb.se.bookingservice.port.persistence.UserRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
@Component
public class UpdateTestBookingsTask {

    private static final String TEST_MEMBER_1 = "34ea9416-74c7-11ee-b962-0242ac121112";
    private static final String TEST_MEMBER_2 = "34ea9416-74c7-11ee-b962-0242ac122222";
    private static final String TEST_MEMBER_3 = "34ea9416-74c7-11ee-b962-0242ac123332";
    private static final String TEST_MEMBER_4 = "34ea9416-74c7-11ee-b962-0242ac124442";
    private static final String TEST_MEMBER_5 = "34ea9416-74c7-11ee-b962-0242ac125552";
    private static final String STEFAN_UUID = "34ea9416-74c7-11ee-b962-0242ac120002";
    private static final Logger logger = LogManager.getLogger(UpdateTestBookingsTask.class);
    @Autowired
    BookingRepository bookingRepository;
    @Autowired
    FamilyMemberRepository familyMemberRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder encoder;

    @Scheduled(fixedDelay = 3605000, initialDelay = 5000) // Run every hour and 1 second after startup
    public void scheduledMethod() {
        logger.info("Start create new test bookings");
        bookingRepository.deleteAll();
        //familyMemberRepository.deleteAll();
        //userRepository.deleteAll();

        FamilyMember familyMember1 = updateMember(TEST_MEMBER_1, "Ronja", "Sockerbubblan");
        FamilyMember familyMember2 = updateMember( TEST_MEMBER_2, "Robin", "Terro Bäbis");
        FamilyMember familyMember3 = updateMember( TEST_MEMBER_3, "Ingemar", "Brakfis mästarn");
        FamilyMember familyMember4 = updateMember( TEST_MEMBER_4, "Patrik", "Dykarn");
        FamilyMember familyMember5 = updateMember( TEST_MEMBER_5, "Ann", "Dåligt minne från 61");

        FamilyMember masterfamilyMember = updateMember(STEFAN_UUID, "Stefan", "The Star");
        updateUser(masterfamilyMember, "kaolle", "12345678");
        bookingRepository.save(new Booking(Instant.now().plus(7, ChronoUnit.DAYS), Instant.now().plus(14, ChronoUnit.DAYS), familyMember1));
        bookingRepository.save(new Booking(Instant.now().plus(28, ChronoUnit.DAYS), Instant.now().plus(35, ChronoUnit.DAYS), familyMember2));
        bookingRepository.save(new Booking(Instant.now().plus(1, ChronoUnit.DAYS), Instant.now().plus(3, ChronoUnit.DAYS), familyMember3));
        logger.info("Completed create new test bookings");
    }

    private FamilyMember updateMember(String uuid, String name, String aBitMore) {
        FamilyMember member = new FamilyMember( UUID.fromString(uuid), name, aBitMore);
        if (!familyMemberRepository.findById(UUID.fromString(uuid)).isPresent()) {
            familyMemberRepository.save(member);
        }
        return member;
    }

    private void updateUser(FamilyMember member, String username, String password) {
        User user = new User(member, username, encoder.encode(password));
        if (!userRepository.findById(user.getUsername()).isPresent()) {
            userRepository.save(user);
        }
    }
}
