package dm.dev.booking.service;

import dm.dev.booking.model.User;
import dm.dev.booking.model.repos.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Optional<User> findById(UUID id) {
        log.debug("Looking up user by ID: {}", id);
        return userRepository.findById(id);
    }

    @Transactional
    public User save(User user) {
        log.info("Saving user: {}", user);
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public Optional<User> findByName(String name) {
        log.debug("Searching user by name: {}", name);
        return userRepository.findByName(name);
    }
}
