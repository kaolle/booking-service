package pb.se.bookingservice.port.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class GuestbookEntryRequest {

    @NotNull
    private LocalDate stayFrom;

    @NotNull
    private LocalDate stayTo;

    @NotBlank
    private String message;

    public GuestbookEntryRequest() {
    }

    public GuestbookEntryRequest(LocalDate stayFrom, LocalDate stayTo, String message) {
        this.stayFrom = stayFrom;
        this.stayTo = stayTo;
        this.message = message;
    }

    public LocalDate getStayFrom() {
        return stayFrom;
    }

    public void setStayFrom(LocalDate stayFrom) {
        this.stayFrom = stayFrom;
    }

    public LocalDate getStayTo() {
        return stayTo;
    }

    public void setStayTo(LocalDate stayTo) {
        this.stayTo = stayTo;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
