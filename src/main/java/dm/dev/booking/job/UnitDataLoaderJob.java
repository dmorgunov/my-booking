package dm.dev.booking.job;

import dm.dev.booking.model.Unit;
import dm.dev.booking.model.User;
import dm.dev.booking.model.repos.UnitRepository;
import dm.dev.booking.model.repos.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Random;

@Slf4j
@Component
@RequiredArgsConstructor
public class UnitDataLoaderJob {

    private final UnitRepository unitRepository;
    private final UserRepository userRepository;

    @PostConstruct
    public void populateUnits() {
        var count = unitRepository.count();
        if (count >= 100) {
            log.info("Unit data loader skipped — {} units already exist.", count);
            return;
        }

        log.info("Populating unit data — current unit count: {}", count);

        var owner = userRepository.findAll().stream().findFirst().orElseGet(() -> {
            User newUser = new User();
            newUser.name("RuntimeOwner");
            return userRepository.save(newUser);
        });

        for (int i = 0; i < 90; i++) {
            var unit = new Unit().user(owner)
                    .numberOfRooms((int) (Math.random() * 5 + 1))
                    .accommodationType(Unit.AccommodationType.values()[new Random().nextInt(3)])
                    .floor((int) (Math.random() * 10))
                    .baseCost(BigDecimal.valueOf(50 + Math.random() * 300))
                    .description("Auto-generated unit " + i);
            unitRepository.save(unit);
        }

        log.info("Finished populating 90 auto-generated units.");
    }
}
