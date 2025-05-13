package dm.dev.booking.controller;

import dm.dev.booking.controller.dto.CreateUserRequest;
import dm.dev.booking.controller.dto.UserResponse;
import dm.dev.booking.model.User;
import dm.dev.booking.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody CreateUserRequest req) {
        User user = userService.save(new User().name(req.name()));
        return ResponseEntity.ok(new UserResponse(user.id(), user.name()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID id) {
        return userService.findById(id)
                .map(user -> ResponseEntity.ok(new UserResponse(user.id(), user.name())))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-name")
    public ResponseEntity<UserResponse> getUserByName(@RequestParam String name) {
        return userService.findByName(name)
                .map(user -> ResponseEntity.ok(new UserResponse(user.id(), user.name())))
                .orElse(ResponseEntity.notFound().build());
    }
}
