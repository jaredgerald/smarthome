package de.sidion.ausbildung.smarthome.rest;

import de.sidion.ausbildung.smarthome.database.entity.DeviceType;
import de.sidion.ausbildung.smarthome.service.DatabaseService;
import de.sidion.ausbildung.smarthome.dto.DeviceTypeDTO;
import de.sidion.ausbildung.smarthome.service.ResponseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/device_type")
public class DeviceTypeController {

    private final DatabaseService databaseService;
    private final ResponseService responseService;

    @GetMapping("")
    public ResponseEntity<List<DeviceType>> getAllDeviceTypes() {
        final List<DeviceType> allDevices = databaseService.findAllDeviceTypes();
        return responseService.createSendResponse(HttpStatus.OK, allDevices);
    }

    @GetMapping("/{name}")
    public ResponseEntity<DeviceType> getDeviceType(@PathVariable("name") String name) {
        final DeviceType device = databaseService.findDeviceTypeByName(name);
        return responseService.createSendResponse(HttpStatus.OK, device);
    }

    @PostMapping("")
    public ResponseEntity<DeviceType> createNewDeviceType(@RequestBody @Valid DeviceTypeDTO deviceTypeDTO) {
        final DeviceType deviceType = databaseService.saveDeviceType(deviceTypeDTO);
        return responseService.createSendResponse(HttpStatus.CREATED, deviceType);
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<Void> deleteDeviceType(@PathVariable("name") String name) {
        databaseService.deleteDeviceType(name);
        return responseService.createSendResponse(HttpStatus.OK, null);
    }
}
