package dm.dev.booking.service;

import dm.dev.booking.model.repos.UnitRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CacheService {

    private final RedissonClient redissonClient;
    private final UnitRepository unitRepository;
    private static final String AVAILABLE_UNIT_KEY = "available_units";

    public void updateAvailableUnitCount() {
        int count = unitRepository.countAvailableUnits();
        redissonClient.getBucket(AVAILABLE_UNIT_KEY).set(String.valueOf(count));
        log.info("Updated available unit count in Redis: {}", count);
    }

    public int getAvailableUnitCount() {
        String value = (String) redissonClient.getBucket(AVAILABLE_UNIT_KEY).get();
        int count = value != null ? Integer.parseInt(value) : 0;
        log.debug("Retrieved available unit count from Redis: {}", count);
        return count;
    }

    @PostConstruct
    public void init() {
        log.info("Initializing CacheService: setting available unit count");
        updateAvailableUnitCount();
    }
}
