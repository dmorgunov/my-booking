package dm.dev.booking.model.repos;

import dm.dev.booking.model.Booking;
import dm.dev.booking.model.Unit;
import dm.dev.booking.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UnitRepositoryTest extends AbstractRepositoryTest {

    @Autowired
    private UnitRepository unitRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookingRepository bookingRepository;

    private User createUser(String name) {
        return userRepository.save(new User().name(name));
    }

    private Unit createUnit(User user, String description) {
        return unitRepository.save(new Unit()
                .user(user)
                .numberOfRooms(2)
                .floor(1)
                .baseCost(BigDecimal.valueOf(120))
                .accommodationType(Unit.AccommodationType.FLAT)
                .description(description));
    }

    private void createConfirmedBooking(Unit unit, User user, LocalDate start, LocalDate end) {
        bookingRepository.save(new Booking()
                .user(user)
                .unit(unit)
                .startDate(start)
                .endDate(end)
                .status(Booking.BookingStatus.CONFIRMED));
    }

    @Test
    void shouldReturnAvailableUnitsWithoutConfirmedBookings() {
        User user = createUser("Alice");
        Unit availableUnit = createUnit(user, "Available Unit");
        Unit bookedUnit = createUnit(user, "Booked Unit");

        createConfirmedBooking(bookedUnit, user, LocalDate.now(), LocalDate.now().plusDays(2));

        Page<Unit> result = unitRepository.findAvailableUnits(
                null, null, null,
                LocalDate.now(), LocalDate.now().plusDays(1),
                null, null,
                PageRequest.ofSize(10));

        List<Unit> units = result.getContent();

        assertThat(units).contains(availableUnit);
        assertThat(units).doesNotContain(bookedUnit);
    }

    @Test
    void shouldFilterUnitsByRoomsAndCost() {
        User user = createUser("Bob");
        createUnit(user, "Ignore").numberOfRooms(1);
        Unit match = unitRepository.save(new Unit()
                .user(user)
                .numberOfRooms(3)
                .floor(2)
                .baseCost(BigDecimal.valueOf(200))
                .accommodationType(Unit.AccommodationType.FLAT)
                .description("Filtered Unit"));

        Page<Unit> result = unitRepository.findAvailableUnits(
                3, Unit.AccommodationType.FLAT, 2,
                LocalDate.now(), LocalDate.now().plusDays(1),
                BigDecimal.valueOf(100), BigDecimal.valueOf(300),
                PageRequest.of(0, 10));

        assertThat(result.getContent()).contains(match);
    }

    @Test
    void shouldCountAvailableUnits() {
        User user = createUser("Charlie");
        Unit unit1 = createUnit(user, "U1");
        Unit unit2 = createUnit(user, "U2");

        // Book unit2
        createConfirmedBooking(unit2, user, LocalDate.now(), LocalDate.now().plusDays(2));

        int count = unitRepository.countAvailableUnits();

        assertThat(count).isEqualTo(1);
    }
}
