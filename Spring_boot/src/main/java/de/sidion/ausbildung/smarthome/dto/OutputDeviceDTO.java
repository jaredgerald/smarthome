package de.sidion.ausbildung.smarthome.dto;

import de.sidion.ausbildung.smarthome.database.entity.Device;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OutputDeviceDTO {
    public OutputDeviceDTO(Device device) {
        this.id = device.getId();
        this.name = device.getName();
        this.location = device.getLocation();
        this.deviceType = device.getDeviceType().getName();
        this.timestamp = 1;
        this.data = "";
    }

    private int id;
    private String name;
    private String location;
    private String deviceType;
    private long timestamp;
    private String data;
}