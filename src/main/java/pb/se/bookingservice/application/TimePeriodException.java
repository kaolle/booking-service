package pb.se.bookingservice.application;

import pb.se.bookingservice.domain.Booking;

public class TimePeriodException extends RuntimeException {

    public TimePeriodException(Booking booking) {
        super(booking.toString());
    }
}
