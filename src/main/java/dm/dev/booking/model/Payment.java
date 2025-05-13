package dm.dev.booking.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.UUID;

@Entity
@Data
@Accessors(fluent = true, chain = true)
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue
    private UUID id;

    @OneToOne
    private Booking booking;

    private boolean paid;
}