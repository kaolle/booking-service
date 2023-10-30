package pb.se.bookingservice.port.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;
import pb.se.bookingservice.domain.Booking;

import java.util.UUID;

public interface BookingRepository extends MongoRepository<Booking, UUID> {

}
