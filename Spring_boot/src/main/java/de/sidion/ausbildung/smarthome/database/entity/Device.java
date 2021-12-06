package de.sidion.ausbildung.smarthome.database.entity;

import de.sidion.ausbildung.smarthome.enums.DeviceType;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Table(name = "device", indexes = {
        @Index(name = "device_un", columnList = "name", unique = true)
})
@Entity
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "name", nullable = false, length = 40)
    private String name;

    @Column(name = "location", nullable = false, length = 40)
    private String location;

    @Column(name = "type", nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    private DeviceType type;

    @Column(name = "state", length = 100)
    private String state;

    @Column(name = "last_timestamp", nullable = false)
    private LocalDateTime lastTimestamp;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Device device = (Device) o;
        return id != null && Objects.equals(id, device.id);
    }

    @Override
    public int hashCode() {
        return 0;
    }
}