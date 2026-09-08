package pb.se.bookingservice.port.rest.dto;

import pb.se.bookingservice.domain.FamilyMember;

import java.time.Instant;
import java.util.UUID;

public class FamilyMemberResponse {
    private final UUID id;
    private final String name;
    private final String phrase;
    private final Instant stayFrom;
    private final Instant stayTo;

    public FamilyMemberResponse(UUID id, String name, String phrase, Instant stayFrom, Instant stayTo) {
        this.id = id;
        this.name = name;
        this.phrase = phrase;
        this.stayFrom = stayFrom;
        this.stayTo = stayTo;
    }

    public static FamilyMemberResponse fromDomain(FamilyMember familyMember) {
        if (familyMember == null) {
            return new FamilyMemberResponse(null, "borttagen", "", null, null);
        }
        return new FamilyMemberResponse(familyMember.getUuid(), familyMember.getName(), familyMember.getaBitMore(), null, null);
    }

    public static FamilyMemberResponse fromDomainWithStay(FamilyMember familyMember, Instant stayFrom, Instant stayTo) {
        return new FamilyMemberResponse(familyMember.getUuid(), familyMember.getName(), familyMember.getaBitMore(), stayFrom, stayTo);
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

    public Instant getStayFrom() {
        return stayFrom;
    }

    public Instant getStayTo() {
        return stayTo;
    }
}
