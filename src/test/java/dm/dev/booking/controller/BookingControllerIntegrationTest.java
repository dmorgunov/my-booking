package dm.dev.booking.controller;

import dm.dev.booking.model.Booking;
import dm.dev.booking.model.Payment;
import dm.dev.booking.model.Unit;
import dm.dev.booking.model.User;
import dm.dev.booking.model.repos.BookingRepository;
import dm.dev.booking.model.repos.PaymentRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@Transactional
@ActiveProfiles("test")
class BookingControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UnitRepository unitRepository;

    @Autowired
    private UserRepository userRepository;

    private User createUser() {
        return userRepository.save(new User().name("Test User"));
    }

    private Unit createUnit(User user) {
        return unitRepository.save(new Unit()
                .user(user)
                .numberOfRooms(2)
                .accommodationType(Unit.AccommodationType.FLAT)
                .baseCost(BigDecimal.valueOf(100))
                .floor(1));
    }

    private Booking createBooking(User user, Unit unit) {
        return bookingRepository.save(new Booking()
                .user(user)
                .unit(unit)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(1))
                .status(Booking.BookingStatus.PENDING));
    }

    @Test
    void shouldBookUnitSuccessfully() throws Exception {
        User user = createUser();
        Unit unit = createUnit(user);

        String json = """
                {
                  "unitId": "%s",
                  "userId": "%s",
                  "startDate": "%s",
                  "endDate": "%s"
                }
                """.formatted(unit.id(), user.id(), LocalDate.now(), LocalDate.now().plusDays(1));

        mockMvc.perform(post("/api/bookings/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void shouldConfirmBookingPayment() throws Exception {
        User user = createUser();
        Unit unit = createUnit(user);
        Booking booking = createBooking(user, unit);

        mockMvc.perform(post("/api/bookings/" + booking.id() + "/confirm"))
                .andExpect(status().isOk());

        Booking updated = bookingRepository.findById(booking.id()).orElseThrow();
        assertThat(updated.status()).isEqualTo(Booking.BookingStatus.CONFIRMED);
    }

    @Test
    void shouldCancelBooking() throws Exception {
        User user = createUser();
        Unit unit = createUnit(user);
        Booking booking = createBooking(user, unit);

        mockMvc.perform(post("/api/bookings/" + booking.id() + "/cancel"))
                .andExpect(status().isOk());

        Booking updated = bookingRepository.findById(booking.id()).orElseThrow();
        assertThat(updated.status()).isEqualTo(Booking.BookingStatus.CANCELLED);
    }

    @Test
    void shouldGetPaymentForBooking() throws Exception {
        User user = createUser();
        Unit unit = createUnit(user);
        Booking booking = createBooking(user, unit);

        // Save a real payment in DB before querying it
        Payment payment = new Payment()
                .booking(booking)
                .paid(true);
        paymentRepository.save(payment);

        mockMvc.perform(get("/api/bookings/" + booking.id() + "/payment"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paid").value(true));
    }

    @Test
    void shouldReturn404WhenPaymentNotFound() throws Exception {
        UUID randomId = UUID.randomUUID();

        mockMvc.perform(get("/api/bookings/" + randomId + "/payment"))
                .andExpect(status().isNotFound());
    }
}
