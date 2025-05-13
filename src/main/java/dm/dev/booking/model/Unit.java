package dm.dev.booking.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Data
@Accessors(fluent = true, chain = true)
@Table(name = "units")
public class Unit {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    private User user;

    private int numberOfRooms;

    @Enumerated(EnumType.STRING)
    private AccommodationType accommodationType;

    private int floor;

    private BigDecimal baseCost;

    private String description;

    public enum AccommodationType {
        HOME,
        FLAT,
        APARTMENTS
    }
}