package pb.se.bookingservice.port.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import pb.se.bookingservice.domain.Booking;
import pb.se.bookingservice.domain.FamilyMember;

import java.io.Serializable;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class BookingRequest implements Serializable {
    @JsonProperty
    private final Instant from;
    @JsonProperty
    private final Instant to;

    public BookingRequest(@JsonProperty("from") Instant from, @JsonProperty("to") Instant to) {
        this.from = from;
        this.to = to;
    }

    public Booking toBooking(FamilyMember member) {
        return new Booking(from, to, member);
    }

    public Instant getFrom() {
        return from.truncatedTo(ChronoUnit.DAYS);
    }

    public Instant getTo() {
        return to.truncatedTo(ChronoUnit.DAYS);
    }
}
