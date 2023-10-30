package pb.se.bookingservice.port.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;
import pb.se.bookingservice.domain.FamilyMember;

import java.util.UUID;

public interface FamilyMemberRepository extends MongoRepository<FamilyMember, UUID> {

}
