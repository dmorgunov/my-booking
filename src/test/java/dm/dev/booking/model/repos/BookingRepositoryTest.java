package dm.dev.booking.model.repos;

import dm.dev.booking.model.Booking;
import dm.dev.booking.model.Unit;
import dm.dev.booking.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BookingRepositoryTest extends AbstractRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UnitRepository unitRepository;

    private User createUser(String name) {
        return userRepository.save(new User().name(name));
    }

    private Unit createUnit(User user) {
        return unitRepository.save(new Unit()
                .user(user)
                .numberOfRooms(2)
                .floor(1)
                .baseCost(java.math.BigDecimal.valueOf(120))
                .accommodationType(Unit.AccommodationType.FLAT)
                .description("Test Unit"));
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
    void shouldSaveAndFindByUnitId() {
        User user = createUser("Alice");
        Unit unit = createUnit(user);
        Booking booking = createBooking(user, unit);

        List<Booking> results = bookingRepository.findByUnitId(unit.id());

        assertThat(results).hasSize(1);
        assertThat(results.get(0).id()).isEqualTo(booking.id());
    }

    @Test
    void shouldSaveAndFindByUserId() {
        User user = createUser("Bob");
        Unit unit = createUnit(user);
        Booking booking = createBooking(user, unit);

        List<Booking> results = bookingRepository.findByUserId(user.id());

        assertThat(results).hasSize(1);
        assertThat(results.get(0).id()).isEqualTo(booking.id());
    }

    @Test
    void shouldReturnEmptyForUnknownUnitId() {
        List<Booking> results = bookingRepository.findByUnitId(UUID.randomUUID());
        assertThat(results).isEmpty();
    }

    @Test
    void shouldReturnEmptyForUnknownUserId() {
        List<Booking> results = bookingRepository.findByUserId(UUID.randomUUID());
        assertThat(results).isEmpty();
    }
}
