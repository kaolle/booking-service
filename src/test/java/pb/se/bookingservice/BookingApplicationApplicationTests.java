package pb.se.bookingservice;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pb.se.bookingservice.application.BookingApplication;
import pb.se.bookingservice.application.TimePeriodException;
import pb.se.bookingservice.domain.Booking;
import pb.se.bookingservice.domain.FamilyMember;
import pb.se.bookingservice.port.persistence.BookingRepository;
import pb.se.bookingservice.port.persistence.FamilyMemberRepository;
import pb.se.bookingservice.port.rest.dto.BookingRequest;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.fasterxml.jackson.databind.type.LogicalType.Collection;
import static java.time.Instant.now;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.WEEKS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingApplicationApplicationTests {


	@InjectMocks
	BookingApplication application;
	@Captor
	ArgumentCaptor<Booking> bookingCaptor;
	@Mock
	private FamilyMemberRepository memberRepository;
	@Mock
	private BookingRepository bookingRepository;

	@Test
	void bookingRequestIsSavedWhenNoOverlappingBookingsExists() {
		when(bookingRepository.findAll()).thenReturn(Collections.emptyList());
		UUID familyMemberUUID = UUID.randomUUID();
		when(memberRepository.findById(familyMemberUUID.toString())).thenReturn(Optional.of(new FamilyMember("baba")));

		Instant from = now().plus(8, DAYS).truncatedTo(DAYS);
		Instant to	 = now().plus(10, DAYS).truncatedTo(DAYS);
		application.save(new BookingRequest(from, to	, familyMemberUUID));

		verify(bookingRepository).save(bookingCaptor.capture());
		assertThat(bookingCaptor.getValue().getFrom(), Matchers.is(from));
		assertThat(bookingCaptor.getValue().getTo(), Matchers.is(to));
	}

	@Test
	void bookingRequestTimePeriodWithinAlreadyBookedCauseBookedTimePeriodException() {
		Booking existingBooking = new Booking(now().plus(4, DAYS), now().plus(9, DAYS), mock(FamilyMember.class) );

		when(bookingRepository.findAll()).thenReturn(List.of(existingBooking));

		assertThrows(TimePeriodException.class, () -> application.save(new BookingRequest(now().plus(5, DAYS), now().plus(8, DAYS), UUID.randomUUID())));
	}

	@Test
	void bookingRequestPartlyOverlappsBeginningOfAnotherBookingCauseBookedTimePeriodException() {
		Booking existingBooking = new Booking(now().plus(4, DAYS), now().plus(9, DAYS), mock(FamilyMember.class) );

		when(bookingRepository.findAll()).thenReturn(List.of(existingBooking));

		assertThrows(TimePeriodException.class, () -> application.save(new BookingRequest(now().plus(3, DAYS), now().plus(5, DAYS), UUID.randomUUID())));

	}
	@Test
	void bookingRequestPartlyOverlappsEndOfAnotherBookingCauseBookedTimePeriodException() {
		Booking existingBooking = new Booking(now().plus(4, DAYS), now().plus(9, DAYS), mock(FamilyMember.class) );

		when(bookingRepository.findAll()).thenReturn(List.of(existingBooking));

		assertThrows(TimePeriodException.class, () -> application.save(new BookingRequest(now().plus(5, DAYS), now().plus(11, DAYS), UUID.randomUUID())));

	}
	@Test
	void bookingRequestHasExactlySamePeriodAsAnotherBookingCauseBookedTimePeriodException() {
		Booking existingBooking = new Booking(now().plus(4, DAYS), now().plus(9, DAYS), mock(FamilyMember.class) );

		when(bookingRepository.findAll()).thenReturn(List.of(existingBooking));

		assertThrows(TimePeriodException.class, () -> application.save(new BookingRequest(now().plus(4, DAYS), now().plus(9, DAYS), UUID.randomUUID())));

	}
	@Test
	void bookingRequestHasSameStartDateAsAnotherBookingCauseBookedTimePeriodException() {
		Booking existingBooking = new Booking(now().plus(4, DAYS), now().plus(13, DAYS), mock(FamilyMember.class) );

		when(bookingRepository.findAll()).thenReturn(List.of(existingBooking));

		assertThrows(TimePeriodException.class, () -> application.save(new BookingRequest(now().plus(4, DAYS), now().plus(16, DAYS), UUID.randomUUID())));

	}
	@Test
	void bookingRequestHasSameEndDateAsAnotherBookingCauseBookedTimePeriodException() {
		Booking existingBooking = new Booking(now().plus(6, DAYS), now().plus(9, DAYS), mock(FamilyMember.class) );

		when(bookingRepository.findAll()).thenReturn(List.of(existingBooking));

		assertThrows(TimePeriodException.class, () -> application.save(new BookingRequest(now().plus(4, DAYS), now().plus(9, DAYS), UUID.randomUUID())));

	}

	@Test
	void bookingRequestHasADateRangeBeforeAndAfterAnotherBookingCauseBookedTimePeriodException() {
		Booking existingBooking = new Booking(now().plus(6, DAYS), now().plus(9, DAYS), mock(FamilyMember.class) );

		when(bookingRepository.findAll()).thenReturn(List.of(existingBooking));

		assertThrows(TimePeriodException.class, () -> application.save(new BookingRequest(now().plus(4, DAYS), now().plus(11, DAYS), UUID.randomUUID())));

	}

}
