package pb.se.bookingservice.port.rest.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import pb.se.bookingservice.application.MemberNotFoundException;
import pb.se.bookingservice.application.TimePeriodException;

import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionAdvisor extends ResponseEntityExceptionHandler {


    @ExceptionHandler(TimePeriodException.class)
    public ResponseEntity<Object> toResponse(TimePeriodException e, WebRequest request) {
        return new ResponseEntity<>(e.getExistingBooking(), HttpStatus.CONFLICT);
    }
    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<Object> toResponse(MemberNotFoundException e, WebRequest request) {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

}
