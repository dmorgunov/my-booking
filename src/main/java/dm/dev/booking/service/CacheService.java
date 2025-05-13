package dm.dev.booking.service;

import dm.dev.booking.model.repos.UnitRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CacheService {

    private final RedissonClient redissonClient;
    private final UnitRepository unitRepository;
    private static final String AVAILABLE_UNIT_KEY = "available_units";

    public void updateAvailableUnitCount() {
        int count = unitRepository.countAvailableUnits();
        redissonClient.getBucket(AVAILABLE_UNIT_KEY).set(String.valueOf(count));
    }

    public int getAvailableUnitCount() {
        String value = (String) redissonClient.getBucket(AVAILABLE_UNIT_KEY).get();
        return value != null ? Integer.parseInt(value) : 0;
    }

    @PostConstruct
    public void init() {
        updateAvailableUnitCount();
    }
}