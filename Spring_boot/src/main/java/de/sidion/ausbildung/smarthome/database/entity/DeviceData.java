package de.sidion.ausbildung.smarthome.database.entity;

import lombok.*;

import javax.persistence.*;

@Table(name = "device_data")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeviceData {
    @EmbeddedId
    private DeviceDataId id;

    @Column(name = "data_type", nullable = false, length = 50)
    private String dataType;

    @Column(name = "data", nullable = false, length = 200)
    private String data;
}