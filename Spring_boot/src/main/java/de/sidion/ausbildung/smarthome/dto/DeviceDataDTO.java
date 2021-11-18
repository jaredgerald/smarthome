package de.sidion.ausbildung.smarthome.dto;

import de.sidion.ausbildung.smarthome.database.entity.DeviceData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceDataDTO {
    private int deviceID;
    private LocalDateTime date;
    private String data;

    public DeviceDataDTO(DeviceData deviceData) {
        this.deviceID = deviceData.getId().getDeviceId();
        this.date = deviceData.getId().getDate();
        this.data = deviceData.getData();
    }
}
