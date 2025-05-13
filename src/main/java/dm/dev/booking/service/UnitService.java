package dm.dev.booking.service;

import dm.dev.booking.controller.dto.UnitRequest;
import dm.dev.booking.model.Unit;
import dm.dev.booking.model.User;
import dm.dev.booking.model.repos.UnitRepository;
import dm.dev.booking.model.repos.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UnitService {

    private final UnitRepository unitRepository;
    private final UserRepository userRepository;

    public Page<Unit> findAvailableUnits(Integer rooms, Unit.AccommodationType type, Integer floor,
                                         LocalDate startDate, LocalDate endDate,
                                         BigDecimal minCost, BigDecimal maxCost,
                                         Pageable pageable) {
        return unitRepository.findAvailableUnits(rooms, type, floor, startDate, endDate, minCost, maxCost, pageable);
    }


    public Unit createUnit(UnitRequest request) {
        User user = userRepository.findById(request.userId()).orElseThrow();
        Unit unit = new Unit()
                .user(user)
                .numberOfRooms(request.numberOfRooms())
                .accommodationType(Unit.AccommodationType.valueOf(request.accommodationType()))
                .floor(request.floor())
                .baseCost(request.baseCost())
                .description(request.description());

        return unitRepository.save(unit);
    }

    public Optional<Unit> findById(UUID id) {
        return unitRepository.findById(id);
    }
}