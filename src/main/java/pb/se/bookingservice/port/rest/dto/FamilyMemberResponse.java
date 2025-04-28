package pb.se.bookingservice.port.rest.dto;

import pb.se.bookingservice.domain.FamilyMember;

import java.util.UUID;

public class FamilyMemberResponse {
    private UUID id;
    private String name;
    private String phrase;

    public FamilyMemberResponse(UUID id, String name, String phrase) {
        this.id = id;
        this.name = name;
        this.phrase = phrase;
    }
    public static FamilyMemberResponse fromDomain(FamilyMember familyMember) {
        return new FamilyMemberResponse(familyMember.getUuid(), familyMember.getName(), familyMember.getaBitMore());
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhrase() {
        return phrase;
    }

}
