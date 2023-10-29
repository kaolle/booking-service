package pb.se.bookingservice.port.persistence;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.junit.jupiter.Testcontainers;
import pb.se.bookingservice.domain.Booking;
import pb.se.bookingservice.domain.FamilyMember;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataMongoTest
@Testcontainers
@ContextConfiguration(classes = MongoDBTestContainerConfig.class)
class RepositoryTest {

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    BookingRepository bookingRepository;
    @Autowired
    FamilyMemberRepository familyMemberRepository;


    @Test
   void canSaveAndGetAllBooking(){

       //when
       FamilyMember member = new FamilyMember(UUID.randomUUID().toString(), "Mycho Brahe");
       familyMemberRepository.save(member);
        Booking booking = new Booking(Instant.now().minus(1, ChronoUnit.DAYS), Instant.now(), member);
        bookingRepository.save(booking);

       //then
       assertThat(bookingRepository.findAll().size(), Matchers.is(1));
       assertThat(bookingRepository.findAll().get(0), Matchers.is(booking));
   }


}
