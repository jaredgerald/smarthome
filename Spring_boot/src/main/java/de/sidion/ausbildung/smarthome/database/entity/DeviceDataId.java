package de.sidion.ausbildung.smarthome.database.entity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class DeviceDataId implements Serializable {
    private static final long serialVersionUID = 1275705581795791600L;
    @Column(name = "device_id", nullable = false)
    private Integer deviceId;
    @Column(name = "date", nullable = false)
    private LocalDateTime date;

    @Override
    public int hashCode() {
        return Objects.hash(date, deviceId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        DeviceDataId entity = (DeviceDataId) o;
        return Objects.equals(this.date, entity.date) &&
                Objects.equals(this.deviceId, entity.deviceId);
    }
}