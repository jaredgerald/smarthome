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

    @Lob
    @Column(name = "data")
    private String data;
}