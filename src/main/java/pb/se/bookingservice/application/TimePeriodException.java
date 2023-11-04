package pb.se.bookingservice.application;

import pb.se.bookingservice.domain.Booking;

public class TimePeriodException extends RuntimeException {
    private final Booking existingBooking;
    public TimePeriodException(Booking booking) {
        super(booking.toString());
        this.existingBooking = booking;
    }

    public Booking getExistingBooking() {
        return existingBooking;
    }
}
