package de.sidion.ausbildung.smarthome.rest;

import de.sidion.ausbildung.smarthome.dto.LEDModeDTO;
import de.sidion.ausbildung.smarthome.service.MQTTService;
import de.sidion.ausbildung.smarthome.service.ResponseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/light")
public class LightController {
    private final MQTTService mqttService;
    private final ResponseService responseService;

    @PostMapping("/{id}/status/{status}")
    @PreAuthorize("@deviceService.isDeviceLight(#id)")
    public ResponseEntity<Object> setLightActiveState(@PathVariable("id") int id,
                                                      @PathVariable("status") boolean status) {
        mqttService.setMQTTLightActiveState(id, status);
        return responseService.createSendResponse(HttpStatus.OK, null);
    }

    @PostMapping("/{id}/mode")
    @PreAuthorize("@deviceService.isDeviceLight(#id)")
    public ResponseEntity<Object> setLightMode(@PathVariable("id") int id,
                                               @RequestBody @Valid LEDModeDTO mode) {
        mqttService.setMQTTLightMode(id, mode);
        return responseService.createSendResponse(HttpStatus.OK, null);
    }
}
