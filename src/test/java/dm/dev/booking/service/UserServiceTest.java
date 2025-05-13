package dm.dev.booking.service;

import dm.dev.booking.model.User;
import dm.dev.booking.model.repos.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserService userService;

    @Test
    void findById_shouldReturnUser() {
        UUID id = UUID.randomUUID();
        User user = new User().id(id).name("John");

        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        Optional<User> result = userService.findById(id);

        assertEquals("John", result.get().name());
        assertEquals(id, result.get().id());
    }

    @Test
    void save_shouldReturnSavedUser() {
        User user = new User().name("Jane");

        when(userRepository.save(user)).thenReturn(user);

        User saved = userService.save(user);

        assertEquals("Jane", saved.name());
    }

    @Test
    void findByName_shouldReturnUser() {
        User user = new User().id(UUID.randomUUID()).name("Alice");

        when(userRepository.findByName("Alice")).thenReturn(Optional.of(user));

        Optional<User> result = userService.findByName("Alice");

        assertEquals("Alice", result.get().name());
    }
}