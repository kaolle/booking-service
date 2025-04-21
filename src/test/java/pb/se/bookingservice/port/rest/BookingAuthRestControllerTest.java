package pb.se.bookingservice.port.rest;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import pb.se.bookingservice.domain.FamilyMember;
import pb.se.bookingservice.domain.Role;
import pb.se.bookingservice.domain.User;
import pb.se.bookingservice.port.persistence.BookingRepository;
import pb.se.bookingservice.port.persistence.FamilyMemberRepository;
import pb.se.bookingservice.port.persistence.UserRepository;
import pb.se.bookingservice.port.rest.dto.BookingResponse;
import pb.se.bookingservice.port.rest.dto.JwtResponse;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

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
    @Autowired
    PasswordEncoder encoder;

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
        //given family member cabitso is created by the family uberhead using end point /family/member
        //familly uberhead exists in that base as user grgrbr02

        String cabitso = "cabitso";
        familyMemberRepository.save(new FamilyMember(UUID.randomUUID(), "gulli gullan", cabitso));

        JsonObject json = new JsonObject();
        json.addProperty("username", "steken");
        json.addProperty("password", "12345678");
        json.addProperty("familyPhrase", cabitso);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(json.toString(), headers);

        ResponseEntity<String> response = restTemplate.postForEntity("/auth/signup", entity, String.class);

        assertThat(response.getStatusCode(), is(CREATED));
    }

    @Test
    void canSignIn() {
        // First create a user to sign in with
        String cabitso = "cabitso";
        FamilyMember familyMember = new FamilyMember(UUID.randomUUID(), "gulli gullan", cabitso);
        familyMemberRepository.save(familyMember);

        String username = "testuser";
        String password = "password123";

        // Create and save the user with encoded password
        User user = new User(familyMember, username,
                encoder.encode(password),
                Role.FAMILY_MEMBER);
        userRepository.save(user);

        // Create signin request
        JsonObject json = new JsonObject();
        json.addProperty("username", username);
        json.addProperty("password", password);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(json.toString(), headers);

        ResponseEntity<JwtResponse> response = restTemplate.postForEntity("/auth/signin", entity, JwtResponse.class);

        assertThat(response.getStatusCode(), is(OK));
        assertThat(response.getBody(), notNullValue());
        assertThat(response.getBody().getAccessToken(), notNullValue());
    }

    @Test
    void cannotSignUpWithExistingUsername() {
        // Create a family member
        String cabitso = "cabitso";
        FamilyMember familyMember = new FamilyMember(UUID.randomUUID(), "gulli gullan", cabitso);
        familyMemberRepository.save(familyMember);

        // Create a user
        String username = "existinguser";
        User user = new User(familyMember, username, encoder.encode("anypassword"), // "password123" encoded
                Role.FAMILY_MEMBER);
        userRepository.save(user);

        // Try to sign up with the same username
        JsonObject json = new JsonObject();
        json.addProperty("username", username);
        json.addProperty("password", "newpassword");
        json.addProperty("familyPhrase", cabitso);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(json.toString(), headers);

        ResponseEntity<String> response = restTemplate.postForEntity("/auth/signup", entity, String.class);

        assertThat(response.getStatusCode(), is(CONFLICT));
    }

    @Test
    void cannotSignUpWithExistingFamilyMember() {
        // Create a family member
        String cabitso = "cabitso";
        FamilyMember familyMember = new FamilyMember(UUID.randomUUID(), "gulli gullan", cabitso);
        familyMemberRepository.save(familyMember);

        // Create a user with this family member
        User user = new User(familyMember, "existinguser",
                "$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG", // "password123" encoded
                Role.FAMILY_MEMBER);
        userRepository.save(user);

        // Try to sign up with a different username but the same family member
        JsonObject json = new JsonObject();
        json.addProperty("username", "newuser");
        json.addProperty("password", "password123");
        json.addProperty("familyPhrase", cabitso);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(json.toString(), headers);

        ResponseEntity<String> response = restTemplate.postForEntity("/auth/signup", entity, String.class);

        assertThat(response.getStatusCode(), is(CONFLICT));
    }

    @Test
    void canSignDownUser() {
        // First create a user
        String cabitso = "cabitso";
        FamilyMember familyMember = new FamilyMember(UUID.randomUUID(), "gulli gullan", cabitso);
        familyMemberRepository.save(familyMember);

        String username = "userToDelete";
        String password = "password123";

        // Create and save the user with encoded password
        User user = new User(familyMember, username, encoder.encode(password),
                Role.FAMILY_MEMBER);
        userRepository.save(user);

        // Sign in to get a token
        JsonObject signinJson = new JsonObject();
        signinJson.addProperty("username", username);
        signinJson.addProperty("password", password);

        HttpHeaders signinHeaders = new HttpHeaders();
        signinHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> signinEntity = new HttpEntity<>(signinJson.toString(), signinHeaders);

        ResponseEntity<JwtResponse> signinResponse = restTemplate.postForEntity("/auth/signin", signinEntity, JwtResponse.class);
        String token = signinResponse.getBody().getAccessToken();

        // Now sign down the user
        HttpHeaders deleteHeaders = new HttpHeaders();
        deleteHeaders.set("Authorization", "Bearer " + token);
        HttpEntity<String> deleteEntity = new HttpEntity<>(null, deleteHeaders);

        ResponseEntity<String> response = restTemplate.exchange(
                "/auth/signdown",
                HttpMethod.DELETE,
                deleteEntity,
                String.class);

        assertThat(response.getStatusCode(), is(NO_CONTENT));
        assertThat(userRepository.findById(username).isPresent(), is(false));
    }

    @Test
    void canPromoteUserToUberhead() {
        // First create two users - one uberhead and one regular
        String cabitso1 = "cabitso1";
        String cabitso2 = "cabitso2";
        FamilyMember familyMember1 = new FamilyMember(UUID.randomUUID(), "uberhead member", cabitso1);
        FamilyMember familyMember2 = new FamilyMember(UUID.randomUUID(), "regular member", cabitso2);
        familyMemberRepository.save(familyMember1);
        familyMemberRepository.save(familyMember2);

        String uberheadUsername = "uberhead";
        String regularUsername = "regular";
        String password = "password123";

        // Create and save the users
        User uberheadUser = new User(familyMember1, uberheadUsername,
                encoder.encode(password),
                Role.FAMILY_UBERHEAD);
        User regularUser = new User(familyMember2, regularUsername,
                encoder.encode(password),
                Role.FAMILY_MEMBER);
        userRepository.save(uberheadUser);
        userRepository.save(regularUser);

        // Sign in as uberhead to get a token
        JsonObject signinJson = new JsonObject();
        signinJson.addProperty("username", uberheadUsername);
        signinJson.addProperty("password", password);

        HttpHeaders signinHeaders = new HttpHeaders();
        signinHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> signinEntity = new HttpEntity<>(signinJson.toString(), signinHeaders);

        ResponseEntity<JwtResponse> signinResponse = restTemplate.postForEntity("/auth/signin", signinEntity, JwtResponse.class);
        String token = signinResponse.getBody().getAccessToken();

        // Now promote the regular user to uberhead
        HttpHeaders promoteHeaders = new HttpHeaders();
        promoteHeaders.set("Authorization", "Bearer " + token);
        HttpEntity<String> promoteEntity = new HttpEntity<>(null, promoteHeaders);

        ResponseEntity<User> response = restTemplate.exchange(
                "/auth/promote/" + regularUsername,
                HttpMethod.PUT,
                promoteEntity,
                User.class);

        assertThat(response.getStatusCode(), is(OK));
        assertThat(response.getBody().getRole(), is(Role.FAMILY_UBERHEAD));

        // Verify the user was updated in the database
        User updatedUser = userRepository.findById(regularUsername).orElseThrow();
        assertThat(updatedUser.getRole(), is(Role.FAMILY_UBERHEAD));
    }

    @Test
    void userHeadUsersCanCreateBookingForOtherFamilyMember() {
        // Create two family members
        String phrase1 = "uberhead-phrase";
        String phrase2 = "member-phrase";
        FamilyMember uberheadFamilyMember = new FamilyMember(UUID.randomUUID(), "Uberhead Member", phrase1);
        FamilyMember regularFamilyMember = new FamilyMember(UUID.randomUUID(), "Regular Member", phrase2);
        familyMemberRepository.save(uberheadFamilyMember);
        familyMemberRepository.save(regularFamilyMember);

        // Create users with different roles
        String uberheadUsername = "uberhead-user";
        String regularUsername = "regular-user";
        String password = "password123";

        User uberheadUser = new User(uberheadFamilyMember, uberheadUsername,
                encoder.encode(password),
                Role.FAMILY_UBERHEAD);
        User regularUser = new User(regularFamilyMember, regularUsername,
                encoder.encode(password),
                Role.FAMILY_MEMBER);
        userRepository.save(uberheadUser);
        userRepository.save(regularUser);

        // Sign in as uberhead to get a token
        JsonObject uberheadSigninJson = new JsonObject();
        uberheadSigninJson.addProperty("username", uberheadUsername);
        uberheadSigninJson.addProperty("password", password);

        HttpHeaders uberheadSigninHeaders = new HttpHeaders();
        uberheadSigninHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> uberheadSigninEntity = new HttpEntity<>(uberheadSigninJson.toString(), uberheadSigninHeaders);

        ResponseEntity<JwtResponse> uberheadSigninResponse = restTemplate.postForEntity("/auth/signin", uberheadSigninEntity, JwtResponse.class);
        String uberheadToken = uberheadSigninResponse.getBody().getAccessToken();

        // Sign in as regular user to get a token
        JsonObject regularSigninJson = new JsonObject();
        regularSigninJson.addProperty("username", regularUsername);
        regularSigninJson.addProperty("password", password);

        HttpHeaders regularSigninHeaders = new HttpHeaders();
        regularSigninHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> regularSigninEntity = new HttpEntity<>(regularSigninJson.toString(), regularSigninHeaders);

        ResponseEntity<JwtResponse> regularSigninResponse = restTemplate.postForEntity("/auth/signin", regularSigninEntity, JwtResponse.class);
        String regularToken = regularSigninResponse.getBody().getAccessToken();

        // Create a booking request
        Instant from = Instant.now().plus(1, ChronoUnit.DAYS).truncatedTo(ChronoUnit.DAYS);
        Instant to = from.plus(7, ChronoUnit.DAYS);

        JsonObject bookingJson = new JsonObject();
        bookingJson.addProperty("from", from.toString());
        bookingJson.addProperty("to", to.toString());

        // Test 1: Uberhead user creates booking for regular family member (should succeed)
        HttpHeaders uberheadHeaders = new HttpHeaders();
        uberheadHeaders.setContentType(MediaType.APPLICATION_JSON);
        uberheadHeaders.set("Authorization", "Bearer " + uberheadToken);
        HttpEntity<String> uberheadBookingEntity = new HttpEntity<>(bookingJson.toString(), uberheadHeaders);

        ResponseEntity<BookingResponse> uberheadResponse = restTemplate.postForEntity(
                "/booking/family-member/" + regularFamilyMember.getUuid(),
                uberheadBookingEntity,
                BookingResponse.class);

        assertThat(uberheadResponse.getStatusCode(), is(CREATED));
        assertThat(uberheadResponse.getBody(), notNullValue());
    }

    @Test
    void regularUserCannotCreateBookingForOtherFamilyMembers() {
        // Create two family members
        String phrase1 = "uberhead-phrase";
        String phrase2 = "member-phrase";
        FamilyMember uberheadFamilyMember = new FamilyMember(UUID.randomUUID(), "Uberhead Member", phrase1);
        FamilyMember regularFamilyMember = new FamilyMember(UUID.randomUUID(), "Regular Member", phrase2);
        familyMemberRepository.save(uberheadFamilyMember);
        familyMemberRepository.save(regularFamilyMember);

        // Create users with different roles
        String uberheadUsername = "uberhead-user";
        String regularUsername = "regular-user";
        String password = "password123";

        User uberheadUser = new User(uberheadFamilyMember, uberheadUsername,
                encoder.encode(password),
                Role.FAMILY_UBERHEAD);
        User regularUser = new User(regularFamilyMember, regularUsername,
                encoder.encode(password),
                Role.FAMILY_MEMBER);
        userRepository.save(uberheadUser);
        userRepository.save(regularUser);

        // Sign in as regular user to get a token
        JsonObject regularSigninJson = new JsonObject();
        regularSigninJson.addProperty("username", regularUsername);
        regularSigninJson.addProperty("password", password);

        HttpHeaders regularSigninHeaders = new HttpHeaders();
        regularSigninHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> regularSigninEntity = new HttpEntity<>(regularSigninJson.toString(), regularSigninHeaders);

        ResponseEntity<JwtResponse> regularSigninResponse = restTemplate.postForEntity("/auth/signin", regularSigninEntity, JwtResponse.class);
        String regularToken = regularSigninResponse.getBody().getAccessToken();

        // Create a booking request
        Instant from = Instant.now().plus(1, ChronoUnit.DAYS).truncatedTo(ChronoUnit.DAYS);
        Instant to = from.plus(7, ChronoUnit.DAYS);

        JsonObject bookingJson = new JsonObject();
        bookingJson.addProperty("from", from.toString());
        bookingJson.addProperty("to", to.toString());

        // Test: Regular user tries to create booking for another family member (should fail)
        HttpHeaders regularHeaders = new HttpHeaders();
        regularHeaders.setContentType(MediaType.APPLICATION_JSON);
        regularHeaders.set("Authorization", "Bearer " + regularToken);
        HttpEntity<String> regularBookingEntity = new HttpEntity<>(bookingJson.toString(), regularHeaders);

        ResponseEntity<String> regularResponse = restTemplate.postForEntity(
                "/booking/family-member/" + uberheadFamilyMember.getUuid(),
                regularBookingEntity,
                String.class);
        assertThat(regularResponse.getStatusCode(), is(UNAUTHORIZED));
    }
}
