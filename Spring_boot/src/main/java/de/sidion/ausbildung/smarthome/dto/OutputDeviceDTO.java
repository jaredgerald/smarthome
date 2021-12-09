package de.sidion.ausbildung.smarthome.dto;

import de.sidion.ausbildung.smarthome.database.entity.Device;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OutputDeviceDTO {
    public OutputDeviceDTO(Device device, LocalDateTime lastSeen, String state) {
        this.id = device.getId();
        this.name = device.getName();
        this.location = device.getLocation();
        this.deviceType = device.getDeviceType().getName();
        this.lastSeen = lastSeen;
        this.state = state;
    }

    private int id;
    private String name;
    private String location;
    private String deviceType;
    private LocalDateTime lastSeen;
    private String state;
}