package pb.se.bookingservice.application;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pb.se.bookingservice.domain.Booking;
import pb.se.bookingservice.domain.FamilyMember;
import pb.se.bookingservice.port.persistence.BookingRepository;
import pb.se.bookingservice.port.persistence.FamilyMemberRepository;
import pb.se.bookingservice.port.rest.dto.BookingRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

@Service
@Transactional
public class BookingApplication {

    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    FamilyMemberRepository familyMemberRepository;

    private static boolean isBefore(BookingRequest request, Booking eb) {
        return eb.getTo().isAfter(request.getTo()) || eb.getTo().equals(request.getTo());
    }

    private static boolean isAfter(BookingRequest request, Booking eb) {
        return eb.getFrom().isBefore(request.getFrom()) || eb.getFrom().equals(request.getFrom());
    }

    public UUID create(UUID memberId, BookingRequest request) {

        validate(request);

        FamilyMember member = familyMemberRepository.findById(memberId).orElseThrow(() -> new MemberNotFoundException("Member with UUID " + memberId + " not found"));
        Booking booking = request.toBooking(member);
        bookingRepository.save(booking);
        return booking.getId();
    }

    public List<Booking> findAllBookings() {
        return bookingRepository.findAll();
    }

    public void delete(UUID bookingId, UUID memberId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new BookingNotFoundException(bookingId.toString()));
        if (booking.getFamilyMember().getUuid().equals(memberId)) {
            bookingRepository.delete(booking);
        } else {
            throw new ForbiddenException();
        }
    }

    public UUID createDemoBooking(UUID memberId, BookingRequest request) {
        validate(request);
        FamilyMember member = familyMemberRepository.findById(memberId).orElseThrow(() -> new MemberNotFoundException("Member with UUID " + memberId + " not found"));
        Booking booking = request.toDemoBooking(member);
        bookingRepository.save(booking);
        return booking.getId();
    }

    private void validate(BookingRequest request) {
        Predicate<Booking> isWithin = eb -> isAfter(request, eb) && isBefore(request, eb);
        Predicate<Booking> isBeginningOf = eb -> eb.getFrom().isBefore(request.getTo()) && isBefore(request, eb);
        Predicate<Booking> isEndOf = eb -> isAfter(request, eb) && eb.getTo().isAfter(request.getFrom());
        Predicate<Booking> isBeforeAndAfter = eb -> eb.getFrom().isAfter(request.getFrom()) && eb.getTo().isBefore(request.getTo());

        List<Booking> existingBookings = bookingRepository.findAll();
        Optional<Booking> conflictingBooking = existingBookings.stream()
                .filter(isWithin.or(isBeginningOf).or(isEndOf).or(isBeforeAndAfter))
                .findAny();

        conflictingBooking.ifPresent(b -> {
            throw new TimePeriodException(b);
        });
    }
}
