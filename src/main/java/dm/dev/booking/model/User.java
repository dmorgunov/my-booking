package dm.dev.booking.model;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.UUID;

@Entity
@Data
@Accessors(fluent = true, chain = true)
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue
    private UUID id;

    private String name;

}