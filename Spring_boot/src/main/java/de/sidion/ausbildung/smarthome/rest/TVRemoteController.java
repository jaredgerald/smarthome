package de.sidion.ausbildung.smarthome.rest;

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
@RequestMapping("/tv")
public class TVRemoteController {
    private final MQTTService mqttService;
    private final ResponseService responseService;

    @PostMapping("/{id}/{command}")
    @PreAuthorize("@deviceService.isDeviceString(#id, 'TV_REMOTE')")
    public ResponseEntity<Object> publishCommand(@PathVariable("id") int id,
                                                 @PathVariable("command") String command) {
        mqttService.sendCommand(id, command);
        return responseService.createSendResponse(HttpStatus.OK, null);
    }
}
