package pb.se.bookingservice.port.rest;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pb.se.bookingservice.application.BookingNotFoundException;
import pb.se.bookingservice.application.MemberNotFoundException;
import pb.se.bookingservice.domain.FamilyMember;
import pb.se.bookingservice.domain.GuestbookEntry;
import pb.se.bookingservice.port.persistence.FamilyMemberRepository;
import pb.se.bookingservice.port.persistence.GuestbookRepository;
import pb.se.bookingservice.port.rest.dto.GuestbookEntryRequest;
import pb.se.bookingservice.port.rest.dto.GuestbookEntryResponse;
import pb.se.bookingservice.port.security.CustomUserDetails;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/guestbook")
public class GuestbookController {

    @Autowired
    private GuestbookRepository guestbookRepository;

    @Autowired
    private FamilyMemberRepository familyMemberRepository;

    @GetMapping
    public ResponseEntity<List<GuestbookEntryResponse>> getAllEntries() {
        List<GuestbookEntry> entries = guestbookRepository.findAll();
        return new ResponseEntity<>(entries.stream().map(GuestbookEntryResponse::fromDomain).toList(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<GuestbookEntryResponse> addEntry(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody GuestbookEntryRequest request) {
        UUID memberId = UUID.fromString(((CustomUserDetails) userDetails).getMemberId());
        FamilyMember familyMember = familyMemberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("Family member not found with id: " + memberId));

        GuestbookEntry entry = new GuestbookEntry(
                familyMember,
                request.getStayFrom(),
                request.getStayTo(),
                request.getMessage()
        );
        GuestbookEntry saved = guestbookRepository.save(entry);
        return new ResponseEntity<>(GuestbookEntryResponse.fromDomain(saved), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('FAMILY_UBERHEAD')")
    public ResponseEntity<Void> deleteEntry(@PathVariable UUID id) {
        guestbookRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException("Guestbook entry not found with id: " + id));
        guestbookRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
