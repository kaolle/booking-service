package pb.se.bookingservice.port.rest.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import pb.se.bookingservice.application.BookingNotFoundException;
import pb.se.bookingservice.application.MemberNotFoundException;
import pb.se.bookingservice.application.TimePeriodException;
import pb.se.bookingservice.application.UserNotFoundException;

@ControllerAdvice
@SuppressWarnings("unused")
public class GlobalExceptionAdvisor extends ResponseEntityExceptionHandler {

    @ExceptionHandler(TimePeriodException.class)
    public ResponseEntity<Object> toResponse(TimePeriodException e, WebRequest request) {
        return new ResponseEntity<>(e.getExistingBooking(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<Object> toResponse(MemberNotFoundException e, WebRequest request) {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Object> toResponse(UserNotFoundException e, WebRequest request) {
        return new ResponseEntity<>(HttpStatus.CONFLICT);
    }
    @ExceptionHandler(BookingNotFoundException.class)
    public ResponseEntity<Object> toResponse(BookingNotFoundException e, WebRequest request) {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

}
