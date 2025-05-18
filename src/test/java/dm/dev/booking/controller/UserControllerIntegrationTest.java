package dm.dev.booking.controller;

import dm.dev.booking.model.User;
import dm.dev.booking.model.repos.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
@Transactional
@ActiveProfiles("test")
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldCreateUser() throws Exception {
        String json = """
        {
          "name": "Alice"
        }
        """;

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Alice"))
                .andExpect(jsonPath("$.id").isNotEmpty());
    }

    @Test
    void shouldGetUserById() throws Exception {
        User user = userRepository.save(new User().name("Bob"));

        mockMvc.perform(get("/api/users/" + user.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.id().toString()))
                .andExpect(jsonPath("$.name").value("Bob"));
    }

    @Test
    void shouldReturn404ForMissingUserById() throws Exception {
        mockMvc.perform(get("/api/users/" + UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetUserByName() throws Exception {
        userRepository.save(new User().name("Charlie"));

        mockMvc.perform(get("/api/users/by-name")
                        .param("name", "Charlie"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Charlie"));
    }

    @Test
    void shouldReturn404ForMissingUserByName() throws Exception {
        mockMvc.perform(get("/api/users/by-name")
                        .param("name", "NonExistent"))
                .andExpect(status().isNotFound());
    }
}
