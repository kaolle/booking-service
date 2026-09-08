package pb.se.bookingservice.port.rest.dto;

import pb.se.bookingservice.domain.FamilyMember;

import java.io.Serializable;
import java.util.UUID;

/** Public family member details included in booking responses. */
public record FamillyMemberAPI(UUID id, String name) implements Serializable {
    public static FamillyMemberAPI fromDomain(FamilyMember member) {
        if (member == null) {
            return new FamillyMemberAPI(null, "borttagen");
        }
        return new FamillyMemberAPI(member.getUuid(), member.getName());
    }
}
