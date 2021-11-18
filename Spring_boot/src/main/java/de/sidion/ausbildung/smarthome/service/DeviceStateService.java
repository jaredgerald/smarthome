package de.sidion.ausbildung.smarthome.service;

import de.sidion.ausbildung.smarthome.database.service.DatabaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeviceStateService {
    private final DatabaseService databaseService;

    public void setInactive(int id) {
        databaseService.updateDeviceState(id, false);
    }

    public void setActive(int id) {
        databaseService.updateDeviceState(id, true);
    }
}
