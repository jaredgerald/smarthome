package de.sidion.ausbildung.smarthome.database.entity;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Table(name = "device", indexes = {
        @Index(name = "device_un", columnList = "name", unique = true)
})
@Entity
@Getter
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

    @ManyToOne(optional = false)
    @JoinColumn(name = "device_type_id", nullable = false)
    private DeviceType deviceType;

    @OneToMany
    @JoinColumn(name = "device_id") //Look if necessary
    private List<DeviceData> deviceData;
}