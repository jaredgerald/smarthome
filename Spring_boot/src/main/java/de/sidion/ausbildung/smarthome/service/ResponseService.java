package de.sidion.ausbildung.smarthome.service;

import de.sidion.ausbildung.smarthome.database.entity.Device;
import de.sidion.ausbildung.smarthome.database.entity.DeviceType;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ResponseService {
    private final MQTTService mqttService;
    private final DatabaseService databaseService;

    public <T> ResponseEntity<T> createSendResponse(HttpStatus status, T obj) {
        sendResponse(status);
        return ResponseEntity.status(status).body(obj);
    }

    public void sendResponse(HttpStatus status) {
        final Optional<DeviceType> deviceType = databaseService.findDeviceType("STATUS_LED");

        if (deviceType.isPresent()) {
            final List<Device> devices = databaseService.findDevicesByType(deviceType.get());

            devices.forEach(device -> {
                if(status.is2xxSuccessful()) {
                    mqttService.sendCommand(device.getId(), "Green");
                }
                else if (status.is4xxClientError()) {
                    mqttService.sendCommand(device.getId(), "Yellow");
                }
                else if (status.is5xxServerError()) {
                    mqttService.sendCommand(device.getId(), "Red-Blink");
                }
            });
        }
    }
}
