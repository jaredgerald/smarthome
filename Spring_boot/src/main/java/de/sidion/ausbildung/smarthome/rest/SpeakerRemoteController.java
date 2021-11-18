package de.sidion.ausbildung.smarthome.rest;

import de.sidion.ausbildung.smarthome.database.entity.Device;
import de.sidion.ausbildung.smarthome.database.service.DatabaseService;
import de.sidion.ausbildung.smarthome.service.DeviceStateService;
import de.sidion.ausbildung.smarthome.service.MQTTService;
import de.sidion.ausbildung.smarthome.service.ResponseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/speaker")
public class SpeakerRemoteController {
    private final MQTTService mqttService;
    private final DatabaseService databaseService;
    private final DeviceStateService deviceStateService;
    private final ResponseService responseService;

    @PostMapping("/{id}")
    @PreAuthorize("@deviceService.isDeviceSpeaker(#id)")
    public ResponseEntity<Object> turnOnOff(@PathVariable("id") int id) {
        final Device device = databaseService.findDeviceById(id);
        if(!"off".equals(device.getStatus())) {
            mqttService.setMQTTSpeakerCommand(id, "on");
        }
        else {
            mqttService.setMQTTSpeakerCommand(id, "off");
        }
        deviceStateService.setInactive(id);
        return responseService.createSendResponse(HttpStatus.OK, null);
    }

    @PostMapping("/{id}/volume/{direction}")
    @PreAuthorize("@deviceService.isDeviceSpeaker(#id)")
    public ResponseEntity<Object> changeSpeakerVolume(@PathVariable("id") int id,
                                                      @PathVariable("direction") int direction) {
        if(direction > 0) {
            mqttService.setMQTTSpeakerCommand(id, "up");
        }
        else {
            mqttService.setMQTTSpeakerCommand(id, "down");
        }
        deviceStateService.setInactive(id);
        return responseService.createSendResponse(HttpStatus.OK, null);
    }

    @PostMapping("/{id}/mute")
    @PreAuthorize("@deviceService.isDeviceSpeaker(#id)")
    public ResponseEntity<Object> muteSpeaker(@PathVariable("id") int id) {
        mqttService.setMQTTSpeakerCommand(id, "mute");
        deviceStateService.setInactive(id);
        return responseService.createSendResponse(HttpStatus.OK, null);
    }

    @PostMapping("/{id}/source")
    @PreAuthorize("@deviceService.isDeviceSpeaker(#id)")
    public ResponseEntity<Object> changeSpeakerSource(@PathVariable("id") int id) {
        mqttService.setMQTTSpeakerCommand(id, "src");
        return responseService.createSendResponse(HttpStatus.OK, null);
    }
}
