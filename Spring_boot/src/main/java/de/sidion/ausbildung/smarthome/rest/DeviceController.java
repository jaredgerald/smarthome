package de.sidion.ausbildung.smarthome.rest;

import de.sidion.ausbildung.smarthome.database.entity.Device;
import de.sidion.ausbildung.smarthome.service.DatabaseService;
import de.sidion.ausbildung.smarthome.dto.DeviceDTO;
import de.sidion.ausbildung.smarthome.dto.OutputDeviceDTO;
import de.sidion.ausbildung.smarthome.service.MQTTService;
import de.sidion.ausbildung.smarthome.service.OutputDeviceService;
import de.sidion.ausbildung.smarthome.service.ResponseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/device")
public class DeviceController {
    private final DatabaseService databaseService;
    private final OutputDeviceService outputDeviceService;
    private final ResponseService responseService;
    private final MQTTService mqttService;

    @GetMapping("")
    public ResponseEntity<List<OutputDeviceDTO>> getAllDevices() {
        final List<Device> allDevices = databaseService.findAllDeviceDTOs();
        final List<OutputDeviceDTO> outputDeviceDTOList = allDevices.stream()
                .map(outputDeviceService::createOutPutDeviceDTO)
                .collect(Collectors.toList());
        return responseService.createSendResponse(HttpStatus.OK, outputDeviceDTOList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OutputDeviceDTO> getDevice(@PathVariable("id") int id) {
        final Device device = databaseService.findDevice(id);
        final OutputDeviceDTO outputDeviceDTO= outputDeviceService.createOutPutDeviceDTO(device);
        return responseService.createSendResponse(HttpStatus.OK, outputDeviceDTO);
    }

    @PostMapping("/ping/{id}")
    public ResponseEntity<Void> requestStatusOfDevice(@PathVariable("id") int id) { //wait for response!
        mqttService.sendDeviceRequest(id);
        return responseService.createSendResponse(HttpStatus.OK, null);
    }

    @PostMapping("")
    public ResponseEntity<OutputDeviceDTO> createNewDevice(@RequestBody @Valid DeviceDTO deviceDTO) {
        final Device device = databaseService.saveDevice(deviceDTO);
        final OutputDeviceDTO outputDeviceDTO= outputDeviceService.createOutPutDeviceDTO(device);
        return responseService.createSendResponse(HttpStatus.CREATED, outputDeviceDTO);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<OutputDeviceDTO> updateDevice(@PathVariable("id") int id,
                                               @RequestBody @Valid DeviceDTO deviceDTO) {
        final Device device = databaseService.updateDevice(id, deviceDTO);
        final OutputDeviceDTO outputDeviceDTO= outputDeviceService.createOutPutDeviceDTO(device);
        return responseService.createSendResponse(HttpStatus.OK, outputDeviceDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDevice(@PathVariable("id") int id) {
        databaseService.deleteDevice(id);
        return responseService.createSendResponse(HttpStatus.OK, null);
    }
}
