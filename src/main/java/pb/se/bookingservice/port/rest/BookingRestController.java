package pb.se.bookingservice.port.rest;


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
import pb.se.bookingservice.application.BookingApplication;
import pb.se.bookingservice.domain.Booking;
import pb.se.bookingservice.port.rest.dto.BookingRequest;
import pb.se.bookingservice.port.rest.dto.BookingResponse;
import pb.se.bookingservice.port.security.CustomUserDetails;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/booking")
public class BookingRestController {

    @Autowired
    BookingApplication bookingApplication;

    @GetMapping()
    public List<Booking> get() {

        return bookingApplication.findAllBookings();

    }

    @PostMapping()
    public ResponseEntity<BookingResponse> createBooking (@AuthenticationPrincipal UserDetails userDetails, @RequestBody BookingRequest bookingRequest) {
        UUID memberId = UUID.fromString(((CustomUserDetails) userDetails).getMemberId());
        UUID id = bookingApplication.create(memberId, bookingRequest);
        return new ResponseEntity<>(new BookingResponse(id), HttpStatus.CREATED);
    }

    @DeleteMapping("{id}")
    public ResponseEntity delete(@AuthenticationPrincipal UserDetails userDetails, @PathVariable String id) {
        UUID memberId = UUID.fromString(((CustomUserDetails) userDetails).getMemberId());
        bookingApplication.delete(UUID.fromString(id), memberId);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/family-member/{memberId}")
    @PreAuthorize("hasRole('FAMILY_UBERHEAD')")
    public ResponseEntity<BookingResponse> createBookingForFamilyMember(
            @PathVariable String memberId,
            @RequestBody BookingRequest bookingRequest) {
        UUID familyMemberId = UUID.fromString(memberId);
        UUID id = bookingApplication.create(familyMemberId, bookingRequest);
        return new ResponseEntity<>(new BookingResponse(id), HttpStatus.CREATED);
    }
}
