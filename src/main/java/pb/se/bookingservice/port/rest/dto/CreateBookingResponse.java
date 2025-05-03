package pb.se.bookingservice.port.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;
@SuppressWarnings("unused")
public class CreateBookingResponse {
    @JsonProperty
    private final UUID id;

    public CreateBookingResponse(UUID id) {
        this.id = id;
    }
}
