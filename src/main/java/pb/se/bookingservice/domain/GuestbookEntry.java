package pb.se.bookingservice.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
@Document(collection = "guestbookEntries")
public class GuestbookEntry implements Serializable {

    @Id
    @JsonProperty
    private UUID uuid;

    @JsonProperty
    @DBRef
    private FamilyMember familyMember;

    @JsonProperty
    private LocalDate stayFrom;

    @JsonProperty
    private LocalDate stayTo;

    @JsonProperty
    private String message;

    @JsonProperty
    private Instant createdAt;

    public GuestbookEntry() {
    }

    public GuestbookEntry(FamilyMember familyMember, LocalDate stayFrom, LocalDate stayTo, String message) {
        this.uuid = UUID.randomUUID();
        this.familyMember = familyMember;
        this.stayFrom = stayFrom;
        this.stayTo = stayTo;
        this.message = message;
        this.createdAt = Instant.now();
    }

    public UUID getUuid() {
        return uuid;
    }

    public FamilyMember getFamilyMember() {
        return familyMember;
    }

    public LocalDate getStayFrom() {
        return stayFrom;
    }

    public LocalDate getStayTo() {
        return stayTo;
    }

    public String getMessage() {
        return message;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GuestbookEntry that = (GuestbookEntry) o;
        return new EqualsBuilder().append(uuid, that.uuid).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(uuid).toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("uuid", uuid)
                .append("familyMember", familyMember)
                .append("stayFrom", stayFrom)
                .append("stayTo", stayTo)
                .append("createdAt", createdAt)
                .toString();
    }
}
