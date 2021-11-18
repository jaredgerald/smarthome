package de.sidion.ausbildung.smarthome.service;

import de.sidion.ausbildung.smarthome.database.entity.Device;
import de.sidion.ausbildung.smarthome.database.service.DatabaseService;
import de.sidion.ausbildung.smarthome.enums.DeviceType;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DeviceService {
    private final DatabaseService databaseService;

    public boolean isDeviceLight(int id) {
        final Device device = databaseService.findDeviceById(id);
        return device.getType().equals(DeviceType.LIGHT);
    }

    public boolean isDeviceSensor(int id) {
        final Device device = databaseService.findDeviceById(id);
        return device.getType().equals(DeviceType.SENSOR);
    }

    public boolean isDeviceSpeaker(int id) {
        final Device device = databaseService.findDeviceById(id);
        return device.getType().equals(DeviceType.SPEAKER_REMOTE);
    }

    public boolean isDeviceTV(int id) {
        final Device device = databaseService.findDeviceById(id);
        return device.getType().equals(DeviceType.TV_REMOTE);
    }
}
