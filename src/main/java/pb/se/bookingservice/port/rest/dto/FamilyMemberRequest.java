package pb.se.bookingservice.port.rest.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Data Transfer Object for creating a new family member.
 */
public class FamilyMemberRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String phrase;

    // Default constructor for JSON deserialization
    public FamilyMemberRequest() {
    }

    public FamilyMemberRequest(String name, String phrase) {
        this.name = name;
        this.phrase = phrase;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhrase() {
        return phrase;
    }

    public void setPhrase(String phrase) {
        this.phrase = phrase;
    }
}
