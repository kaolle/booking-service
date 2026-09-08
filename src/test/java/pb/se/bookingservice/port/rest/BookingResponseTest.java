package pb.se.bookingservice.port.rest;

import org.junit.jupiter.api.Test;
import pb.se.bookingservice.application.BookingApplication;
import pb.se.bookingservice.domain.Booking;
import pb.se.bookingservice.domain.FamilyMember;

import java.time.Instant;
import java.util.List;

import static org.hamcrest.Matchers.aMapWithSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

class BookingResponseTest {
    @Test
    void getBookingsOnlyExposesMemberIdAndName() throws Exception {
        FamilyMember member = new FamilyMember("Member", "private phrase");
        Booking booking = new Booking(Instant.parse("2026-09-10T00:00:00Z"),
                Instant.parse("2026-09-17T00:00:00Z"), member);
        BookingApplication application = returning(booking);
        BookingRestController controller = new BookingRestController();
        controller.bookingApplication = application;

        standaloneSetup(controller).build().perform(get("/booking"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(booking.getId().toString()))
                .andExpect(jsonPath("$[0].familyMember", aMapWithSize(2)))
                .andExpect(jsonPath("$[0].familyMember.id").value(member.getUuid().toString()))
                .andExpect(jsonPath("$[0].familyMember.name").value("Member"))
                .andExpect(jsonPath("$[0].familyMember.phrase").doesNotHaveJsonPath())
                .andExpect(jsonPath("$[0].familyMember.stayFrom").doesNotHaveJsonPath())
                .andExpect(jsonPath("$[0].familyMember.stayTo").doesNotHaveJsonPath());
    }

    private BookingApplication returning(Booking booking) {
        return new BookingApplication() {
            @Override
            public List<Booking> findAllBookings() {
                return List.of(booking);
            }
        };
    }

    @Test
    void getBookingsPreservesDeletedMemberFallback() throws Exception {
        BookingApplication application = returning(new Booking(Instant.now(), Instant.now(), null));
        BookingRestController controller = new BookingRestController();
        controller.bookingApplication = application;

        standaloneSetup(controller).build().perform(get("/booking"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].familyMember", aMapWithSize(2)))
                .andExpect(jsonPath("$[0].familyMember.name").value("borttagen"));
    }
}
