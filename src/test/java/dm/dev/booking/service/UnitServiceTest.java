package dm.dev.booking.service;

import dm.dev.booking.controller.dto.UnitRequest;
import dm.dev.booking.model.Unit;
import dm.dev.booking.model.User;
import dm.dev.booking.model.repos.UnitRepository;
import dm.dev.booking.model.repos.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UnitServiceTest {

    @Mock
    private UnitRepository unitRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UnitService unitService;

    @Test
    void createUnit_shouldSaveNewUnit() {
        UUID userId = UUID.randomUUID();
        User user = new User().id(userId).name("John");

        UnitRequest request = new UnitRequest(
                userId, 2, "FLAT", 3,
                BigDecimal.valueOf(120.0), "Nice flat"
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(unitRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Unit saved = unitService.createUnit(request);

        assertEquals(2, saved.numberOfRooms());
        assertEquals("Nice flat", saved.description());
        assertEquals(Unit.AccommodationType.FLAT, saved.accommodationType());
        verify(unitRepository).save(any());
    }

    @Test
    void findById_shouldReturnUnit() {
        UUID unitId = UUID.randomUUID();
        Unit unit = new Unit().id(unitId);
        when(unitRepository.findById(unitId)).thenReturn(Optional.of(unit));

        Optional<Unit> found = unitService.findById(unitId);

        assertEquals(unitId, found.get().id());
    }

    @Test
    void findAvailableUnits_shouldReturnPagedUnits() {
        PageRequest pageable = PageRequest.of(0, 10);
        Unit sample = new Unit().id(UUID.randomUUID());
        when(unitRepository.findAvailableUnits(
                any(), any(), any(), any(), any(), any(), any(), eq(pageable))
        ).thenReturn(new PageImpl<>(List.of(sample)));

        Page<Unit> page = unitService.findAvailableUnits(
                null, null, null, null, null, null, null, pageable
        );

        assertEquals(1, page.getContent().size());
    }
}