package dm.dev.booking.controller;

import dm.dev.booking.model.Booking;
import dm.dev.booking.model.Unit;
import dm.dev.booking.model.User;
import dm.dev.booking.model.repos.BookingRepository;
import dm.dev.booking.model.repos.UnitRepository;
import dm.dev.booking.model.repos.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@Transactional
@ActiveProfiles("test")
class PaymentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UnitRepository unitRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private User createUser() {
        return userRepository.save(new User().name("Payment User"));
    }

    private Unit createUnit(User user) {
        return unitRepository.save(new Unit()
                .user(user)
                .numberOfRooms(1)
                .floor(2)
                .baseCost(BigDecimal.valueOf(80))
                .accommodationType(Unit.AccommodationType.FLAT));
    }

    private Booking createBooking(User user, Unit unit) {
        return bookingRepository.save(new Booking()
                .user(user)
                .unit(unit)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(2))
                .status(Booking.BookingStatus.PENDING));
    }

    @Test
    void shouldSavePaymentSuccessfully() throws Exception {
        User user = createUser();
        Unit unit = createUnit(user);
        Booking booking = createBooking(user, unit);

        String json = """
                {
                  "bookingId": "%s",
                  "paid": true
                }
                """.formatted(booking.id());

        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingId").value(booking.id().toString()))
                .andExpect(jsonPath("$.paid").value(true));
    }

    @Test
    void shouldReturn400IfBookingDoesNotExist() throws Exception {
        String json = """
                {
                  "bookingId": "%s",
                  "paid": false
                }
                """.formatted(UUID.randomUUID());

        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is4xxClientError());
    }
}
