package pb.se.bookingservice.port.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;
import pb.se.bookingservice.domain.User;

public interface UserRepository extends MongoRepository<User, String> {
}
