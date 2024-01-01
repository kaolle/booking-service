package pb.se.bookingservice.port.rest.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SignupRequest {
    private final String username;
    private final String password;

    private final String familyPhrase;

    @JsonCreator
    SignupRequest(@JsonProperty("username") String username, @JsonProperty("password") String password, @JsonProperty("familyPhrase") String familyPhrase) {
        this.username = username;
        this.password = password;
        this.familyPhrase = familyPhrase;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getFamilyPhrase() {
        return familyPhrase;
    }
}
