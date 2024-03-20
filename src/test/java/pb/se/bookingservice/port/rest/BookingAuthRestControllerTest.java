package pb.se.bookingservice.port.rest;

import com.google.gson.JsonObject;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import pb.se.bookingservice.domain.FamilyMember;
import pb.se.bookingservice.port.persistence.BookingRepository;
import pb.se.bookingservice.port.persistence.FamilyMemberRepository;
import pb.se.bookingservice.port.persistence.UserRepository;

import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.http.HttpStatus.CREATED;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
class BookingAuthRestControllerTest {
    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    BookingRepository bookingRepository;
    @Autowired
    FamilyMemberRepository familyMemberRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    TestRestTemplate restTemplate;

    @BeforeEach
    void init() {
        bookingRepository.deleteAll();
        userRepository.deleteAll();
        familyMemberRepository.deleteAll();

    }

    @AfterEach
    void shutDown() {
        bookingRepository.deleteAll();
        userRepository.deleteAll();
        familyMemberRepository.deleteAll();
    }

    @Test
    void canSignUp() {
        //given
        String cabitso = "cabitso";
        familyMemberRepository.save(new FamilyMember(UUID.randomUUID(), "gulli gullan", cabitso));

        JsonObject json = new JsonObject();
        json.addProperty("username", "steken");
        json.addProperty("password", "12345678");
        json.addProperty("familyPhrase", cabitso);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(json.toString(), headers);

        ResponseEntity<String> response = restTemplate.postForEntity( "/auth/signup", entity, String.class);

        assertThat(response.getStatusCode(), Matchers.is(CREATED));
    }
}
