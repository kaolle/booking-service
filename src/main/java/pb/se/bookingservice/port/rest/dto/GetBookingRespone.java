package pb.se.bookingservice.port.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import pb.se.bookingservice.domain.Booking;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.SECONDS;

public class GetBookingRespone implements Serializable {
    @JsonProperty
    private UUID id;
    @JsonProperty
    private Instant from;
    @JsonProperty
    private Instant to;
    @JsonProperty
    private FamilyMemberResponse familyMember;

    public GetBookingRespone() {
    }

    public GetBookingRespone(UUID id, Instant from, Instant to, FamilyMemberResponse familyMember) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.familyMember = familyMember;
    }
    public static GetBookingRespone fromDomain(Booking booking){
        return new GetBookingRespone(booking.getId(), booking.getFrom(), booking.getTo(), FamilyMemberResponse.fromDomain(booking.getFamilyMember()));
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

        GetBookingRespone booking = (GetBookingRespone) o;

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
