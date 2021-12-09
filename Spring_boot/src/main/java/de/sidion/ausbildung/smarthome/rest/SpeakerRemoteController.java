package de.sidion.ausbildung.smarthome.rest;

import de.sidion.ausbildung.smarthome.database.entity.DeviceData;
import de.sidion.ausbildung.smarthome.database.service.DatabaseService;
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
    private final ResponseService responseService;

    @PostMapping("/{id}")
    @PreAuthorize("@deviceService.isDeviceString(#id, 'SPEAKER_REMOTE')")
    public ResponseEntity<Object> changeActiveState(@PathVariable("id") int id) {

        final DeviceData data = databaseService.findLastDataOfDeviceByIdAndDataType(id, "state");

        mqttService.sendCommand(id, (data.getData().equals("off")) ? "on" : "off");

        return responseService.createSendResponse(HttpStatus.OK, null);
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
        return responseService.createSendResponse(HttpStatus.OK, null);
    }

    @PostMapping("/{id}/mute")
    @PreAuthorize("@deviceService.isDeviceString(#id, 'SPEAKER_REMOTE')")
    public ResponseEntity<Object> muteSpeaker(@PathVariable("id") int id) {
        mqttService.sendCommand(id, "mute");
        return responseService.createSendResponse(HttpStatus.OK, null);
    }

    @PostMapping("/{id}/source")
    @PreAuthorize("@deviceService.isDeviceString(#id, 'SPEAKER_REMOTE')")
    public ResponseEntity<Object> changeSpeakerSource(@PathVariable("id") int id) {
        mqttService.sendCommand(id, "src");
        return responseService.createSendResponse(HttpStatus.OK, null);
    }
}
