package dm.dev.booking.service;

import dm.dev.booking.controller.dto.BookingRequest;
import dm.dev.booking.model.Booking;
import dm.dev.booking.model.Unit;
import dm.dev.booking.model.User;
import dm.dev.booking.model.repos.BookingRepository;
import dm.dev.booking.model.repos.UnitRepository;
import dm.dev.booking.model.repos.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UnitRepository unitRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RedissonClient redissonClient;
    @Mock
    private CacheService cacheService;
    @Mock
    private RBucket<Object> rBucket;

    @InjectMocks
    private BookingService bookingService;

    private UUID unitId;
    private UUID userId;
    private Unit unit;
    private User user;

    @BeforeEach
    void setup() {
        unitId = UUID.randomUUID();
        userId = UUID.randomUUID();
        unit = new Unit().id(unitId);
        user = new User().id(userId);
    }

    @Test
    void bookUnit_shouldCreatePendingBookingAndCache() {
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(1);
        BookingRequest request = new BookingRequest(unitId, userId, start, end);

        when(unitRepository.findById(unitId)).thenReturn(Optional.of(unit));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(redissonClient.getBucket(anyString())).thenReturn(rBucket);

        Booking savedBooking = new Booking().id(UUID.randomUUID()).unit(unit).user(user).startDate(start).endDate(end).status(Booking.BookingStatus.PENDING);
        when(bookingRepository.save(any())).thenReturn(savedBooking);

        Booking result = bookingService.bookUnit(request);

        assertEquals(Booking.BookingStatus.PENDING, result.status());
        verify(bookingRepository).save(any());
        verify(redissonClient).getBucket(startsWith("booking:timeout:"));
        verify(rBucket).set(eq("PENDING"), any());
        verify(cacheService).updateAvailableUnitCount();
    }

    @Test
    void confirmPayment_shouldSetStatusConfirmed() {
        UUID bookingId = UUID.randomUUID();
        Booking booking = new Booking().id(bookingId).status(Booking.BookingStatus.PENDING);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        bookingService.confirmPayment(bookingId);

        assertEquals(Booking.BookingStatus.CONFIRMED, booking.status());
        verify(bookingRepository).save(booking);
    }

    @Test
    void cancelBooking_shouldSetStatusCancelledAndUpdateCache() {
        UUID bookingId = UUID.randomUUID();
        Booking booking = new Booking().id(bookingId).status(Booking.BookingStatus.CONFIRMED);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        bookingService.cancelBooking(bookingId);

        assertEquals(Booking.BookingStatus.CANCELLED, booking.status());
        verify(bookingRepository).save(booking);
        verify(cacheService).updateAvailableUnitCount();
    }

    @Test
    void cancelUnpaidBooking_shouldOnlyCancelIfPending() {
        UUID bookingId = UUID.randomUUID();
        Booking booking = new Booking().id(bookingId).status(Booking.BookingStatus.PENDING);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        bookingService.cancelUnpaidBooking(bookingId);

        assertEquals(Booking.BookingStatus.CANCELLED, booking.status());
        verify(bookingRepository).save(booking);
        verify(cacheService).updateAvailableUnitCount();
    }
}