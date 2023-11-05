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
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.SECONDS;

@Document(collection = "bookings")
public class Booking implements Serializable {
    @Id
    @JsonProperty
    private UUID id;
    @JsonProperty
    private Instant from;
    @JsonProperty
    private Instant to;
    @JsonProperty
    @SuppressWarnings("unused")
    private Instant bookedAt;
    @JsonProperty
    @DBRef
    private FamilyMember familyMember;


    public Booking() {
    }

    public Booking(Instant from, Instant to, FamilyMember familyMember) {
        this.id = UUID.randomUUID();
        this.from = from;
        this.to = to;
        this.familyMember = familyMember;
        this.bookedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public Instant getFrom() {
        return from.truncatedTo(DAYS);
    }

    public Instant getTo() {
        return to.truncatedTo(DAYS);
    }

    public FamilyMember getFamilyMember() {
        return familyMember;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("from", from)
                .append("to", to)
                .append("familyMember", familyMember)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Booking booking = (Booking) o;

        return new EqualsBuilder().append(id, booking.id)
                .append(from.truncatedTo(SECONDS), booking.from.truncatedTo(SECONDS))
                .append(to.truncatedTo(SECONDS), booking.to.truncatedTo(SECONDS))
                .append(familyMember, booking.familyMember).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(id).append(from).append(to).append(familyMember).toHashCode();
    }
}
