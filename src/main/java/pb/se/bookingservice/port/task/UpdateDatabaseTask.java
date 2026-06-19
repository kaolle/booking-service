package pb.se.bookingservice.port.task;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pb.se.bookingservice.port.persistence.BookingRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
@Component
public class UpdateDatabaseTask {

    private static final Logger logger = LogManager.getLogger(UpdateDatabaseTask.class);
    @Autowired
    BookingRepository bookingRepository;



    @Scheduled(fixedDelay = 86400000, initialDelay = 3000) // Run once every 24 hour and 3 second after startup
    public void scheduledMethod() {
        logger.info("Cleanup old bookings");


        // delete old bookings that last until one week ago
        bookingRepository.findAll().stream()
                .filter(b -> b.getTo().isBefore(Instant.now().minus(60, ChronoUnit.DAYS)))
                .forEach( b -> {
                    logger.info("delete passed booking {} ",b);
                    bookingRepository.delete(b);
        });

        logger.info("Completed update and cleanup");
    }

}
