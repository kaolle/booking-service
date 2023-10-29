package pb.se.bookingservice.port.rest;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pb.se.bookingservice.application.BookingApplication;
import pb.se.bookingservice.domain.Booking;
import pb.se.bookingservice.port.rest.dto.BookingRequest;
import java.util.List;
import java.util.UUID;

@RestController
public class BookingRestController {

    @Autowired
    BookingApplication bookingApplication;

    @GetMapping("/booking")
    public List<Booking> get() {
        return bookingApplication.findAllBookings();
    }

    @PostMapping("/booking")
    public UUID createBooking (@RequestBody BookingRequest bookingRequest) {
        return bookingApplication.save(bookingRequest);
    }
}
