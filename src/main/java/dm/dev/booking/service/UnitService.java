package dm.dev.booking.service;

import dm.dev.booking.controller.dto.UnitRequest;
import dm.dev.booking.model.Unit;
import dm.dev.booking.model.repos.UnitRepository;
import dm.dev.booking.model.repos.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UnitService {

    private final UnitRepository unitRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<Unit> findAvailableUnits(Integer rooms, Unit.AccommodationType type, Integer floor,
                                         LocalDate startDate, LocalDate endDate,
                                         BigDecimal minCost, BigDecimal maxCost,
                                         Pageable pageable) {
        log.debug("Finding available units with filters: rooms={}, type={}, floor={}, start={}, end={}, costRange=[{}, {}]",
                rooms, type, floor, startDate, endDate, minCost, maxCost);
        return unitRepository.findAvailableUnits(rooms, type, floor, startDate, endDate, minCost, maxCost, pageable);
    }

    @Transactional
    public Unit createUnit(UnitRequest request) {
        log.info("Creating unit for user ID: {}", request.userId());

        var user = userRepository.findById(request.userId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + request.userId()));

        var unit = new Unit()
                .user(user)
                .numberOfRooms(request.numberOfRooms())
                .accommodationType(Unit.AccommodationType.valueOf(request.accommodationType()))
                .floor(request.floor())
                .baseCost(request.baseCost())
                .description(request.description());

        return unitRepository.save(unit);
    }

    @Transactional(readOnly = true)
    public Optional<Unit> findById(UUID id) {
        log.debug("Finding unit by ID: {}", id);
        return unitRepository.findById(id);
    }
}
