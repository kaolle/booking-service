package pb.se.bookingservice.port.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;
import pb.se.bookingservice.domain.Booking;
import pb.se.bookingservice.domain.FamilyMember;

import java.util.List;
import java.util.UUID;

public interface BookingRepository extends MongoRepository<Booking, UUID> {

    List<Booking> findByFamilyMember(FamilyMember familyMember);

}
