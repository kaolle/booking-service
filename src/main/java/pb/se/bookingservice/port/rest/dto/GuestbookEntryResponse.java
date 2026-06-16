package pb.se.bookingservice.port.rest.dto;

import pb.se.bookingservice.domain.GuestbookEntry;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public class GuestbookEntryResponse {

    private final UUID id;
    private final String name;
    private final LocalDate stayFrom;
    private final LocalDate stayTo;
    private final String message;
    private final Instant createdAt;

    public GuestbookEntryResponse(UUID id, String name, LocalDate stayFrom, LocalDate stayTo, String message, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.stayFrom = stayFrom;
        this.stayTo = stayTo;
        this.message = message;
        this.createdAt = createdAt;
    }

    public static GuestbookEntryResponse fromDomain(GuestbookEntry entry) {
        return new GuestbookEntryResponse(
                entry.getUuid(),
                entry.getFamilyMember().getName(),
                entry.getStayFrom(),
                entry.getStayTo(),
                entry.getMessage(),
                entry.getCreatedAt()
        );
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
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
}
