package dm.dev.booking.controller;

import dm.dev.booking.model.Unit;
import dm.dev.booking.model.User;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@Transactional
@ActiveProfiles("test")
class UnitControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UnitRepository unitRepository;

    private User createUser() {
        return userRepository.save(new User().name("Unit Owner"));
    }

    private Unit createUnit(User user) {
        return unitRepository.save(new Unit()
                .user(user)
                .numberOfRooms(3)
                .accommodationType(Unit.AccommodationType.FLAT)
                .floor(2)
                .baseCost(BigDecimal.valueOf(150))
                .description("Integration test unit"));
    }

    @Test
    void shouldCreateUnit() throws Exception {
        User user = createUser();

        String json = """
                {
                    "userId": "%s",
                    "numberOfRooms": 3,
                    "accommodationType": "FLAT",
                    "floor": 2,
                    "baseCost": 150,
                    "description": "Test unit"
                }
                """.formatted(user.id());

        mockMvc.perform(post("/api/units")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(user.id().toString()))
                .andExpect(jsonPath("$.accommodationType").value("FLAT"))
                .andExpect(jsonPath("$.baseCost").value(150));
    }

    @Test
    void shouldGetUnitById() throws Exception {
        User user = createUser();
        Unit unit = createUnit(user);

        mockMvc.perform(get("/api/units/" + unit.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(unit.id().toString()))
                .andExpect(jsonPath("$.userId").value(user.id().toString()));
    }

    @Test
    void shouldReturn404ForMissingUnit() throws Exception {
        mockMvc.perform(get("/api/units/" + java.util.UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnAvailableUnitCount() throws Exception {
        mockMvc.perform(get("/api/units/available"))
                .andExpect(status().isOk());
    }
}
