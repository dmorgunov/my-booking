package dm.dev.booking.model.repos;

import dm.dev.booking.model.Unit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Repository
public interface UnitRepository extends JpaRepository<Unit, UUID> {

    @Query("SELECT u FROM Unit u WHERE " +
            "(:rooms IS NULL OR u.numberOfRooms = :rooms) AND " +
            "(:type IS NULL OR u.accommodationType = :type) AND " +
            "(:floor IS NULL OR u.floor = :floor) AND " +
            "u.id NOT IN (" +
            "  SELECT b.unit.id FROM Booking b WHERE " +
            "    b.startDate < :endDate AND b.endDate > :startDate AND b.status = 'CONFIRMED'" +
            ") AND " +
            "(:minCost IS NULL OR u.baseCost >= :minCost) AND " +
            "(:maxCost IS NULL OR u.baseCost <= :maxCost)")
    Page<Unit> findAvailableUnits(
            @Param("rooms") Integer numberOfRooms,
            @Param("type") Unit.AccommodationType type,
            @Param("floor") Integer floor,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("minCost") BigDecimal minCost,
            @Param("maxCost") BigDecimal maxCost,
            Pageable pageable
    );

    @Query("SELECT COUNT(u) FROM Unit u WHERE u.id NOT IN (" +
            "SELECT b.unit.id FROM Booking b WHERE b.status = 'CONFIRMED')")
    int countAvailableUnits();
}
