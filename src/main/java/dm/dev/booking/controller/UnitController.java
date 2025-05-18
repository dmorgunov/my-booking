package dm.dev.booking.controller;

import dm.dev.booking.controller.dto.UnitRequest;
import dm.dev.booking.controller.dto.UnitResponse;
import dm.dev.booking.model.Unit;
import dm.dev.booking.service.CacheService;
import dm.dev.booking.service.UnitService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/units")
@RequiredArgsConstructor
public class UnitController {

    private final UnitService unitService;
    private final CacheService cacheService;

    @PostMapping
    public ResponseEntity<UnitResponse> createUnit(@RequestBody UnitRequest request) {
        Unit unit = unitService.createUnit(request);
        return ResponseEntity.ok(mapToResponse(unit));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<Unit>> searchUnits(
            @RequestParam(required = false) Integer rooms,
            @RequestParam(required = false) Unit.AccommodationType type,
            @RequestParam(required = false) Integer floor,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) BigDecimal minCost,
            @RequestParam(required = false) BigDecimal maxCost,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "baseCost") String sortBy
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<Unit> result = unitService.findAvailableUnits(rooms, type, floor, startDate, endDate, minCost, maxCost, pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UnitResponse> getUnitById(@PathVariable UUID id) {
        return unitService.findById(id)
                .map(unit -> ResponseEntity.ok(mapToResponse(unit)))
                .orElse(ResponseEntity.notFound().build());
    }

    private UnitResponse mapToResponse(Unit unit) {
        return new UnitResponse(
                unit.id(),
                unit.user().id(),
                unit.numberOfRooms(),
                unit.accommodationType().name(),
                unit.floor(),
                unit.baseCost(),
                unit.description()
        );
    }


    @GetMapping("/available")
    public ResponseEntity<Integer> getAvailableUnitCount() {
        return ResponseEntity.ok(cacheService.getAvailableUnitCount());
    }
}
