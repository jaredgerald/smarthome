package de.sidion.ausbildung.smarthome.rest;

import de.sidion.ausbildung.smarthome.database.service.DatabaseService;
import de.sidion.ausbildung.smarthome.dto.DeviceDTO;
import de.sidion.ausbildung.smarthome.dto.OutputDeviceDTO;
import de.sidion.ausbildung.smarthome.service.MQTTService;
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
    private final MQTTService mqttService;

    @GetMapping("")
    public ResponseEntity<List<OutputDeviceDTO>> getAllDevices() {
        final List<OutputDeviceDTO> allDevices = databaseService.findAllDeviceDTOs();
        return responseService.createSendResponse(HttpStatus.OK, allDevices);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OutputDeviceDTO> getDevice(@PathVariable("id") int id) {
        final OutputDeviceDTO device = databaseService.findDeviceDTO(id);
        return responseService.createSendResponse(HttpStatus.OK, device);
    }

    @PostMapping("/ping/{id}")
    public ResponseEntity<Void> requestStatusOfDevice(@PathVariable("id") int id) { //wait for response!
        mqttService.sendDeviceRequest(id);
        return responseService.createSendResponse(HttpStatus.OK, null);
    }

    @PostMapping("")
    public ResponseEntity<OutputDeviceDTO> createNewDevice(@RequestBody @Valid DeviceDTO deviceDTO) {
        final OutputDeviceDTO device = databaseService.saveDevice(deviceDTO);
        return responseService.createSendResponse(HttpStatus.CREATED, device);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<OutputDeviceDTO> updateDevice(@PathVariable("id") int id,
                                               @RequestBody @Valid DeviceDTO deviceDTO) {
        final OutputDeviceDTO device = databaseService.updateDevice(id, deviceDTO);
        return responseService.createSendResponse(HttpStatus.OK, device);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDevice(@PathVariable("id") int id) {
        databaseService.deleteDevice(id);
        return responseService.createSendResponse(HttpStatus.OK, null);
    }
}
