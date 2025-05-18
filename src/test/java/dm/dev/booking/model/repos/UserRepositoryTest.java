package dm.dev.booking.model.repos;

import dm.dev.booking.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest extends AbstractRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldSaveAndFindById() {
        User user = new User().name("Alice");
        User saved = userRepository.save(user);

        Optional<User> found = userRepository.findById(saved.id());

        assertThat(found).isPresent();
        assertThat(found.get().name()).isEqualTo("Alice");
    }

    @Test
    void shouldFindByName() {
        userRepository.save(new User().name("Bob"));

        Optional<User> result = userRepository.findByName("Bob");

        assertThat(result).isPresent();
        assertThat(result.get().name()).isEqualTo("Bob");
    }

    @Test
    void shouldReturnEmptyForUnknownId() {
        Optional<User> result = userRepository.findById(UUID.randomUUID());
        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnEmptyForUnknownName() {
        Optional<User> result = userRepository.findByName("NonExistent");
        assertThat(result).isEmpty();
    }
}
