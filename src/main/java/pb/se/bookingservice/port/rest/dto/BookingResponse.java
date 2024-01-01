package pb.se.bookingservice.port.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;
@SuppressWarnings("unused")
public class BookingResponse {
    @JsonProperty
    private final UUID id;

    public BookingResponse(UUID id) {
        this.id = id;
    }
}
