package dm.dev.booking.model.repos;

import dm.dev.booking.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID> {
    List<Booking> findByUnitId(UUID unitId);
    List<Booking> findByUserId(UUID userId);
}