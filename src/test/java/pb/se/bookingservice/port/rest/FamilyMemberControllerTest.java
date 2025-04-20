package pb.se.bookingservice.port.rest;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
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
import pb.se.bookingservice.port.rest.dto.JwtResponse;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
class FamilyMemberControllerTest {
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

    private String uberheadToken;
    private String regularUserToken;

    @BeforeEach
    void init() {
        // Clean up repositories
        bookingRepository.deleteAll();
        userRepository.deleteAll();
        familyMemberRepository.deleteAll();

        // Create family members
        FamilyMember uberheadMember = new FamilyMember(UUID.randomUUID(), "Uberhead Member", "uberhead-phrase");
        FamilyMember regularMember = new FamilyMember(UUID.randomUUID(), "Regular Member", "regular-phrase");
        familyMemberRepository.save(uberheadMember);
        familyMemberRepository.save(regularMember);

        // Create users
        String uberheadUsername = "uberhead-user";
        String regularUsername = "regular-user";
        String password = "password123";

        User uberheadUser = new User(uberheadMember, uberheadUsername,
                encoder.encode(password), Role.FAMILY_UBERHEAD);
        User regularUser = new User(regularMember, regularUsername,
                encoder.encode(password), Role.FAMILY_MEMBER);
        userRepository.save(uberheadUser);
        userRepository.save(regularUser);

        // Get tokens
        uberheadToken = getToken(uberheadUsername, password);
        regularUserToken = getToken(regularUsername, password);
    }

    @AfterEach
    void shutDown() {
        bookingRepository.deleteAll();
        userRepository.deleteAll();
        familyMemberRepository.deleteAll();
    }

    @Test
    void canCreateFamilyMemberAsUberhead() {
        // Create request
        JsonObject json = new JsonObject();
        json.addProperty("name", "New Family Member");
        json.addProperty("phrase", "new-member-phrase");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + uberheadToken);
        HttpEntity<String> entity = new HttpEntity<>(json.toString(), headers);

        // Send request
        ResponseEntity<FamilyMember> response = restTemplate.postForEntity(
                "/family/member", entity, FamilyMember.class);

        // Verify response
        assertThat(response.getStatusCode(), is(CREATED));
        assertThat(response.getBody(), notNullValue());
        assertThat(response.getBody().getUuid(), notNullValue());

        // Verify family member was created in database
        FamilyMember createdMember = familyMemberRepository.findById(response.getBody().getUuid()).orElse(null);
        assertThat(createdMember, notNullValue());
        assertThat(createdMember.getUuid(), is(response.getBody().getUuid()));
    }

    @Test
    void cannotCreateFamilyMemberAsRegularUser() {
        // Create request
        JsonObject json = new JsonObject();
        json.addProperty("name", "Another Family Member");
        json.addProperty("phrase", "another-phrase");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + regularUserToken);
        HttpEntity<String> entity = new HttpEntity<>(json.toString(), headers);

        // Send request
        ResponseEntity<String> response = restTemplate.postForEntity(
                "/family/member", entity, String.class);

        // Verify response - is UNAUTHORIZED (401)
        assertThat(response.getStatusCode(), is(UNAUTHORIZED));
    }

    @Test
    void canCreateFamilyMemberAfterPromotion() {
        // First promote the regular user to uberhead
        String regularUsername = "regular-user";

        HttpHeaders promoteHeaders = new HttpHeaders();
        promoteHeaders.set("Authorization", "Bearer " + uberheadToken);
        HttpEntity<String> promoteEntity = new HttpEntity<>(null, promoteHeaders);

        ResponseEntity<User> promoteResponse = restTemplate.exchange(
                "/auth/promote/" + regularUsername,
                HttpMethod.PUT,
                promoteEntity,
                User.class);

        assertThat(promoteResponse.getStatusCode(), is(OK));

        // Get a new token for the promoted user
        String promotedUserToken = getToken(regularUsername, "password123");

        // Now try to create a family member with the promoted user
        JsonObject json = new JsonObject();
        json.addProperty("name", "Another Family Member");
        json.addProperty("phrase", "another-phrase");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + promotedUserToken);
        HttpEntity<String> entity = new HttpEntity<>(json.toString(), headers);

        // Send request
        ResponseEntity<FamilyMember> response = restTemplate.postForEntity(
                "/family/member", entity, FamilyMember.class);

        // Verify response
        assertThat(response.getStatusCode(), is(CREATED));
        assertThat(response.getBody(), notNullValue());
        assertThat(response.getBody().getUuid(), notNullValue());
    }

    @Test
    void cannotCreateFamilyMemberWithInvalidRequest() {
        // Create request with missing name
        JsonObject json = new JsonObject();
        json.addProperty("phrase", "new-member-phrase");
        // name is missing

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + uberheadToken);
        HttpEntity<String> entity = new HttpEntity<>(json.toString(), headers);

        // Send request
        ResponseEntity<String> response = restTemplate.postForEntity(
                "/family/member", entity, String.class);

        // Verify response (should be 400 Bad Request)
        assertThat(response.getStatusCode().is4xxClientError(), is(true));
    }

    @Test
    void canGetAllFamilyMembers() {
        // Create headers with authentication
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + uberheadToken);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        // Get all family members
        ResponseEntity<List<FamilyMember>> response = restTemplate.exchange(
                "/family/members",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<FamilyMember>>() {});

        // Verify response
        assertThat(response.getStatusCode(), is(OK));
        assertThat(response.getBody(), notNullValue());
        assertThat(response.getBody(), hasSize(2)); // We have 2 members from setup

        // Verify the members are the ones we created in setup
        List<String> memberNames = response.getBody().stream()
                .map(FamilyMember::toString)
                .toList();

        assertThat(memberNames.toString().contains("Uberhead Member"), is(true));
        assertThat(memberNames.toString().contains("Regular Member"), is(true));
    }

    @Test
    void canGetFamilyMemberById() {
        // Create headers with authentication
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + uberheadToken);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        // First get all members to find an ID
        ResponseEntity<List<FamilyMember>> listResponse = restTemplate.exchange(
                "/family/members",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<FamilyMember>>() {});

        assertThat(listResponse.getBody(), notNullValue());
        UUID memberId = listResponse.getBody().get(0).getUuid();

        // Get the member by ID
        ResponseEntity<FamilyMember> response = restTemplate.exchange(
                "/family/member/" + memberId,
                HttpMethod.GET,
                entity,
                FamilyMember.class);

        // Verify response
        assertThat(response.getStatusCode(), is(OK));
        assertThat(response.getBody(), notNullValue());
        assertThat(response.getBody().getUuid(), is(memberId));
    }

    @Test
    void returnsNotFoundForNonExistentMember() {
        // Create headers with authentication
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + uberheadToken);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        UUID nonExistentId = UUID.randomUUID();

        // Try to get a non-existent member
        ResponseEntity<String> response = restTemplate.exchange(
                "/family/member/" + nonExistentId,
                HttpMethod.GET,
                entity,
                String.class);

        // Verify response
        assertThat(response.getStatusCode(), is(NOT_FOUND));
    }

    @Test
    void canUpdateFamilyMemberAsUberhead() {
        // Create headers for getting members
        HttpHeaders getHeaders = new HttpHeaders();
        getHeaders.set("Authorization", "Bearer " + uberheadToken);
        HttpEntity<String> getEntity = new HttpEntity<>(null, getHeaders);

        // First get all members to find an ID
        ResponseEntity<List<FamilyMember>> listResponse = restTemplate.exchange(
                "/family/members",
                HttpMethod.GET,
                getEntity,
                new ParameterizedTypeReference<List<FamilyMember>>() {});

        assertThat(listResponse.getBody(), notNullValue());
        UUID memberId = listResponse.getBody().get(0).getUuid();

        // Create update request
        JsonObject json = new JsonObject();
        json.addProperty("name", "Updated Name");
        json.addProperty("phrase", "updated-phrase");

        HttpHeaders updateHeaders = new HttpHeaders();
        updateHeaders.setContentType(MediaType.APPLICATION_JSON);
        updateHeaders.set("Authorization", "Bearer " + uberheadToken);
        HttpEntity<String> updateEntity = new HttpEntity<>(json.toString(), updateHeaders);

        // Send update request
        ResponseEntity<FamilyMember> response = restTemplate.exchange(
                "/family/member/" + memberId,
                HttpMethod.PUT,
                updateEntity,
                FamilyMember.class);

        // Verify response
        assertThat(response.getStatusCode(), is(OK));
        assertThat(response.getBody(), notNullValue());
        assertThat(response.getBody().getUuid(), is(memberId));

        // Verify the member was updated in the database
        FamilyMember updatedMember = familyMemberRepository.findById(memberId).orElse(null);
        assertThat(updatedMember, notNullValue());
        assertThat(updatedMember.toString().contains("Updated Name"), is(true));
        assertThat(updatedMember.toString().contains("updated-phrase"), is(true));
    }

    @Test
    void cannotUpdateFamilyMemberAsRegularUser() {
        // Create headers for getting members
        HttpHeaders getHeaders = new HttpHeaders();
        getHeaders.set("Authorization", "Bearer " + uberheadToken); // Use uberhead token to get the list
        HttpEntity<String> getEntity = new HttpEntity<>(null, getHeaders);

        // First get all members to find an ID
        ResponseEntity<List<FamilyMember>> listResponse = restTemplate.exchange(
                "/family/members",
                HttpMethod.GET,
                getEntity,
                new ParameterizedTypeReference<List<FamilyMember>>() {});

        assertThat(listResponse.getBody(), notNullValue());
        UUID memberId = listResponse.getBody().get(0).getUuid();

        // Create update request
        JsonObject json = new JsonObject();
        json.addProperty("name", "Should Not Update");
        json.addProperty("phrase", "should-not-update");

        HttpHeaders updateHeaders = new HttpHeaders();
        updateHeaders.setContentType(MediaType.APPLICATION_JSON);
        updateHeaders.set("Authorization", "Bearer " + regularUserToken); // Use regular user token for update
        HttpEntity<String> updateEntity = new HttpEntity<>(json.toString(), updateHeaders);

        // Send update request
        ResponseEntity<String> response = restTemplate.exchange(
                "/family/member/" + memberId,
                HttpMethod.PUT,
                updateEntity,
                String.class);

        // Verify response - is UNAUTHORIZED (401)
        assertThat(response.getStatusCode(), is(UNAUTHORIZED));

        // Verify the member was NOT updated in the database
        FamilyMember notUpdatedMember = familyMemberRepository.findById(memberId).orElse(null);
        assertThat(notUpdatedMember, notNullValue());
        assertThat(notUpdatedMember.toString().contains("Should Not Update"), is(false));
    }

    @Test
    void canDeleteFamilyMemberAsUberhead() {
        // Create a member to delete
        FamilyMember memberToDelete = new FamilyMember("Member To Delete", "delete-me");
        memberToDelete = familyMemberRepository.save(memberToDelete);
        UUID deleteId = memberToDelete.getUuid();

        // Verify the member exists
        assertThat(familyMemberRepository.existsById(deleteId), is(true));

        // Create delete request
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + uberheadToken);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        // Send delete request
        ResponseEntity<Void> response = restTemplate.exchange(
                "/family/member/" + deleteId,
                HttpMethod.DELETE,
                entity,
                Void.class);

        // Verify response
        assertThat(response.getStatusCode(), is(NO_CONTENT));

        // Verify the member was deleted from the database
        assertThat(familyMemberRepository.existsById(deleteId), is(false));
    }

    @Test
    void cannotDeleteFamilyMemberAsRegularUser() {
        // Create a member that should not be deleted
        FamilyMember memberNotToDelete = new FamilyMember("Do Not Delete Me", "do-not-delete");
        memberNotToDelete = familyMemberRepository.save(memberNotToDelete);
        UUID memberId = memberNotToDelete.getUuid();

        // Verify the member exists
        assertThat(familyMemberRepository.existsById(memberId), is(true));

        // Create delete request
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + regularUserToken);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        // Send delete request
        ResponseEntity<String> response = restTemplate.exchange(
                "/family/member/" + memberId,
                HttpMethod.DELETE,
                entity,
                String.class);

        // Verify response - is UNAUTHORIZED (401)
        assertThat(response.getStatusCode(), is(UNAUTHORIZED));

        // Verify the member was NOT deleted from the database
        assertThat(familyMemberRepository.existsById(memberId), is(true));
    }

    private String getToken(String username, String password) {
        JsonObject signinJson = new JsonObject();
        signinJson.addProperty("username", username);
        signinJson.addProperty("password", password);

        HttpHeaders signinHeaders = new HttpHeaders();
        signinHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> signinEntity = new HttpEntity<>(signinJson.toString(), signinHeaders);

        ResponseEntity<JwtResponse> signinResponse = restTemplate.postForEntity(
                "/auth/signin", signinEntity, JwtResponse.class);

        return signinResponse.getBody().getAccessToken();
    }
}
