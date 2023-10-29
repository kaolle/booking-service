package pb.se.bookingservice.port.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;
import pb.se.bookingservice.domain.FamilyMember;

public interface FamilyMemberRepository extends MongoRepository<FamilyMember, String> {

}
