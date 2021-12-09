package de.sidion.ausbildung.smarthome.rest;

import de.sidion.ausbildung.smarthome.database.entity.Device;
import de.sidion.ausbildung.smarthome.database.entity.DeviceData;
import de.sidion.ausbildung.smarthome.dto.OutputDeviceDTO;
import de.sidion.ausbildung.smarthome.service.DatabaseService;
import de.sidion.ausbildung.smarthome.service.MQTTService;
import de.sidion.ausbildung.smarthome.service.OutputDeviceService;
import de.sidion.ausbildung.smarthome.service.ResponseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/speaker")
public class SpeakerRemoteController {
    private final MQTTService mqttService;
    private final DatabaseService databaseService;
    private final ResponseService responseService;
    private final OutputDeviceService outputDeviceService;

    @PostMapping("/{id}")
    @PreAuthorize("@deviceService.isDeviceString(#id, 'SPEAKER_REMOTE')")
    public ResponseEntity<Object> changeActiveState(@PathVariable("id") int id) {
        final String dataType = "STATE";
        final Optional<DeviceData> data = databaseService.findLastDataOfDeviceByIdAndDataType(id, dataType);

        String command = "off";
        if (data.isPresent()) {
            command = (data.get().getData().equals("off")) ? "on" : "off";
        }
        mqttService.sendCommand(id, command);

        databaseService.saveDeviceData(id, dataType, command);

        final Device device = databaseService.findDevice(id);
        final OutputDeviceDTO deviceDTO = outputDeviceService.createOutPutDeviceDTO(device);

        return responseService.createSendResponse(HttpStatus.OK, deviceDTO);
    }

    @PostMapping("/{id}/volume/{direction}")
    @PreAuthorize("@deviceService.isDeviceString(#id, 'SPEAKER_REMOTE')")
    public ResponseEntity<Object> changeSpeakerVolume(@PathVariable("id") int id,
                                                      @PathVariable("direction") int direction) {
        if(direction > 0) {
            mqttService.sendCommand(id, "up");
        }
        else {
            mqttService.sendCommand(id, "down");
        }

        final Device device = databaseService.findDevice(id);
        final OutputDeviceDTO deviceDTO = outputDeviceService.createOutPutDeviceDTO(device);

        return responseService.createSendResponse(HttpStatus.OK, deviceDTO);
    }

    @PostMapping("/{id}/mute")
    @PreAuthorize("@deviceService.isDeviceString(#id, 'SPEAKER_REMOTE')")
    public ResponseEntity<Object> muteSpeaker(@PathVariable("id") int id) {
        mqttService.sendCommand(id, "mute");

        final Device device = databaseService.findDevice(id);
        final OutputDeviceDTO deviceDTO = outputDeviceService.createOutPutDeviceDTO(device);

        return responseService.createSendResponse(HttpStatus.OK, deviceDTO);
    }

    @PostMapping("/{id}/source")
    @PreAuthorize("@deviceService.isDeviceString(#id, 'SPEAKER_REMOTE')")
    public ResponseEntity<Object> changeSpeakerSource(@PathVariable("id") int id) {
        mqttService.sendCommand(id, "src");

        final Device device = databaseService.findDevice(id);
        final OutputDeviceDTO deviceDTO = outputDeviceService.createOutPutDeviceDTO(device);

        return responseService.createSendResponse(HttpStatus.OK, deviceDTO);
    }
}
