package pb.se.bookingservice.port.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;
import pb.se.bookingservice.domain.GuestbookEntry;

import java.util.UUID;

public interface GuestbookRepository extends MongoRepository<GuestbookEntry, UUID> {
}
