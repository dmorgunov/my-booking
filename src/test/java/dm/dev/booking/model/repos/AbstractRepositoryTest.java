package dm.dev.booking.model.repos;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest

public class AbstractRepositoryTest {
    @Autowired
    protected BookingRepository bookingRepository;
    @Autowired
    protected UnitRepository unitRepository;
    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected PaymentRepository paymentRepository;

    @BeforeEach
    void cleanUp() {
        paymentRepository.deleteAll();
        bookingRepository.deleteAll();
        unitRepository.deleteAll();
        userRepository.deleteAll();
    }
}
