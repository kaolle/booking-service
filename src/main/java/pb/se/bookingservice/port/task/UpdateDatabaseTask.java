package pb.se.bookingservice.port.task;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pb.se.bookingservice.application.BookingApplication;
import pb.se.bookingservice.domain.FamilyMember;
import pb.se.bookingservice.domain.User;
import pb.se.bookingservice.port.persistence.BookingRepository;
import pb.se.bookingservice.port.persistence.FamilyMemberRepository;
import pb.se.bookingservice.port.persistence.UserRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
@Component
public class UpdateDatabaseTask {

    private static final String MEMBER_1 = "34ea9416-74c7-11ee-b962-0242ac121112";
    private static final String MEMBER_2 = "34ea9416-74c7-11ee-b962-0242ac122222";
    private static final String MEMBER_3 = "34ea9416-74c7-11ee-b962-0242ac123332";
    private static final String MEMBER_4 = "34ea9416-74c7-11ee-b962-0242ac124442";
    private static final String MEMBER_5 = "34ea9416-74c7-11ee-b962-0242ac125552";
    private static final String MEMBER_6 = "34ea9416-74c7-11ee-b962-0242ac126662";
    private static final String MEMBER_7 = "34ea9416-74c7-11ee-b962-0242ac128882";
    private static final String MEMBER_8 = "34ea9416-74c7-11ee-b962-0242ac129992";
    private static final String TEST_MEMBER = "34ea9416-74c7-11ee-b962-0242ac129992";
    private static final String STEFAN_UUID = "34ea9416-74c7-11ee-b962-0242ac120002";
    private static final Logger logger = LogManager.getLogger(UpdateDatabaseTask.class);
    @Autowired
    BookingRepository bookingRepository;
    @Autowired
    FamilyMemberRepository familyMemberRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder encoder;
    @Autowired
    BookingApplication bookingApplication;

    @Scheduled(fixedDelay = 86400000, initialDelay = 3000) // Run once every 24 hour and 3 second after startup
    public void scheduledMethod() {
        logger.info("Start update members and cleanup old bookings");

        updateMember(TEST_MEMBER, "Test Demo användare", "min fras åäö");

        updateMember(MEMBER_1, "Ronja", "Sockerbubblan");
        updateMember(MEMBER_2, "Robin", "Terro Bäbis");
        updateMember(MEMBER_3, "Ingemar", "Brakfis mästarn");
        updateMember(MEMBER_4, "Patrik", "Dykarn");
        updateMember(MEMBER_5, "Ann", "Dåligt minne från 61");
        updateMember(MEMBER_6, "Ebba", "Ebbalunda");
        updateMember(MEMBER_7, "test member", "test member");
        updateMember(MEMBER_8, "test member2", "test member2");

        updateMember(STEFAN_UUID, "Stefan", "The Star");

        // delete old bookings that last until one week ago
        bookingRepository.findAll().stream()
                .filter(b -> b.getTo().isBefore(Instant.now().minus(7, ChronoUnit.DAYS)))
                .forEach( b -> {
                    logger.info("delete passed booking {} ",b);
                    bookingRepository.delete(b);
        });
        logger.info("Completed update and cleanup");
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
