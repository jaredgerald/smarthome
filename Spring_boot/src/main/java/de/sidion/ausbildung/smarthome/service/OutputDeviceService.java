package de.sidion.ausbildung.smarthome.service;

import de.sidion.ausbildung.smarthome.database.entity.Device;
import de.sidion.ausbildung.smarthome.database.entity.DeviceData;
import de.sidion.ausbildung.smarthome.dto.OutputDeviceDTO;
import de.sidion.ausbildung.smarthome.enums.DataType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OutputDeviceService {
    private final DatabaseService databaseService;

    public OutputDeviceDTO createOutPutDeviceDTO(Device device) {
        final Optional<DeviceData> deviceDataStateLastSeen =
                databaseService.findLastDataOfDeviceByIdAndDataType(device.getId(), DataType.TIMESTAMP.name());
        LocalDateTime lastSeenData = null;
        try {
            String lastSeenDataString = deviceDataStateLastSeen
                    .map(DeviceData::getData)
                    .orElseThrow(NullPointerException::new);

            long lastSeenDataLong = Long.parseLong(lastSeenDataString);
            lastSeenData = LocalDateTime.ofInstant(Instant.ofEpochMilli(lastSeenDataLong), ZoneId.systemDefault());
        }
        catch (Exception ignored) {}


        final Optional<DeviceData> deviceDataState =
                databaseService.findLastDataOfDeviceByIdAndDataType(device.getId(), DataType.STATE.name());
        String stateData = deviceDataState
                .map(DeviceData::getData)
                .orElse(null);

        return new OutputDeviceDTO(device, lastSeenData, stateData);
    }
}
