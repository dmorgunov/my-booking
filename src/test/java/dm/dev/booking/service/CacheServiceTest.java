package dm.dev.booking.service;

import dm.dev.booking.model.repos.UnitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CacheServiceTest {

    @Mock
    private RedissonClient redissonClient;
    @Mock
    private UnitRepository unitRepository;
    @Mock
    private RBucket<Object> rBucket;

    @InjectMocks
    private CacheService cacheService;

    @BeforeEach
    void setup() {
        when(redissonClient.getBucket("available_units")).thenReturn(rBucket);
    }

    @Test
    void updateAvailableUnitCount_shouldSetValueInRedis() {
        when(unitRepository.countAvailableUnits()).thenReturn(5);

        cacheService.updateAvailableUnitCount();

        verify(rBucket).set("5");
    }

    @Test
    void getAvailableUnitCount_shouldReturnParsedInt() {
        when(rBucket.get()).thenReturn("7");

        int count = cacheService.getAvailableUnitCount();

        assertEquals(7, count);
    }

    @Test
    void getAvailableUnitCount_shouldReturnZeroIfNull() {
        when(rBucket.get()).thenReturn(null);

        int count = cacheService.getAvailableUnitCount();

        assertEquals(0, count);
    }
}