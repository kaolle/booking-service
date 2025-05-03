package pb.se.bookingservice.port.rest.dto;

import pb.se.bookingservice.domain.FamilyMember;

import java.util.UUID;

public class FamilyMemberResponse {
    private UUID id;
    private String name;

    public FamilyMemberResponse(UUID id, String name) {
        this.id = id;
        this.name = name;
    }
    public static FamilyMemberResponse fromDomain(FamilyMember familyMember) {
        return new FamilyMemberResponse(familyMember.getUuid(), familyMember.getName());
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }


}
