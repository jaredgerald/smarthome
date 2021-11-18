package de.sidion.ausbildung.smarthome.rest;

import de.sidion.ausbildung.smarthome.database.entity.Device;
import de.sidion.ausbildung.smarthome.database.service.DatabaseService;
import de.sidion.ausbildung.smarthome.dto.DeviceDTO;
import de.sidion.ausbildung.smarthome.service.ResponseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/device")
public class DeviceController {
    private final DatabaseService databaseService;
    private final ResponseService responseService;

    @GetMapping("")
    public ResponseEntity<List<Device>> getAllDevices() {
        final List<Device> allDevices = databaseService.findAllDevices();
        return responseService.createSendResponse(HttpStatus.OK, allDevices);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Device> getDevice(@PathVariable("id") int id) {
        final Device device = databaseService.findDeviceById(id);
        return responseService.createSendResponse(HttpStatus.OK, device);
    }

    @PostMapping("")
    public ResponseEntity<Device> createNewDevice(@RequestBody @Valid DeviceDTO deviceDTO) {
        final Device device = databaseService.saveDevice(deviceDTO);
        return responseService.createSendResponse(HttpStatus.CREATED, device);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Device> updateDevice(@PathVariable("id") int id,
                                               @RequestBody @Valid DeviceDTO deviceDTO) {
        final Device device = databaseService.updateDevice(id, deviceDTO);
        return responseService.createSendResponse(HttpStatus.OK, device);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Device> deleteDevice(@PathVariable("id") int id) {
        databaseService.deleteDevice(id);
        return responseService.createSendResponse(HttpStatus.OK, null);
    }
}
