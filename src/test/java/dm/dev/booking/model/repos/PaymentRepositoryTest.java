package dm.dev.booking.model.repos;

import dm.dev.booking.model.Booking;
import dm.dev.booking.model.Payment;
import dm.dev.booking.model.Unit;
import dm.dev.booking.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PaymentRepositoryTest extends AbstractRepositoryTest {

    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UnitRepository unitRepository;
    @Autowired
    private UserRepository userRepository;

    private User createUser() {
        return userRepository.save(new User().name("TestUser"));
    }

    private Unit createUnit(User user) {
        return unitRepository.save(new Unit()
                .user(user)
                .numberOfRooms(2)
                .floor(1)
                .baseCost(BigDecimal.valueOf(100))
                .accommodationType(Unit.AccommodationType.FLAT)
                .description("Sample Unit"));
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
    void shouldSaveAndFindPaymentByBookingId() {
        User user = createUser();
        Unit unit = createUnit(user);
        Booking booking = createBooking(user, unit);

        Payment payment = new Payment().booking(booking).paid(true);
        paymentRepository.save(payment);

        Optional<Payment> result = paymentRepository.findByBookingId(booking.id());

        assertThat(result).isPresent();
        assertThat(result.get().paid()).isTrue();
        assertThat(result.get().booking().id()).isEqualTo(booking.id());
    }

    @Test
    void shouldReturnEmptyIfNoPaymentForBooking() {
        UUID bookingId = UUID.randomUUID();

        Optional<Payment> result = paymentRepository.findByBookingId(bookingId);

        assertThat(result).isEmpty();
    }
}
