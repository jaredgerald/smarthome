package de.sidion.ausbildung.smarthome.service;

import de.sidion.ausbildung.smarthome.database.entity.Device;
import de.sidion.ausbildung.smarthome.database.entity.DeviceType;
import de.sidion.ausbildung.smarthome.dto.LEDModeDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DeviceService {
    private final DatabaseService databaseService;

    public boolean isDeviceString(int id, String... typeStringArray) {
        final Device device = databaseService.findDevice(id);
        final List<DeviceType> deviceTypes = Arrays.stream(typeStringArray)
                .map(databaseService::findDeviceTypeByName)
                .collect(Collectors.toList());
        return deviceTypes.contains(device.getDeviceType());
    }

    public boolean isDeviceModeValid(int id, LEDModeDTO modeDTO) { //custom error message
        final Device device = databaseService.findDevice(id);
        final String mode = modeDTO.getMode();
        return databaseService.existsModeByNameAndType(mode, device.getDeviceType());
    }
}
