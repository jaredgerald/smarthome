package de.sidion.ausbildung.smarthome.service;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ResponseService {
    private final MQTTService mqttService;

    public <T> ResponseEntity<T> createSendResponse(HttpStatus status, T obj) {
        sendResponse(status);
        return ResponseEntity.status(status).body(obj);
    }

    public void sendResponse(HttpStatus status) {
        if(status.is2xxSuccessful()) {
            mqttService.setMQTTTrafficLightCommand("Green");
        }
        else if (status.is4xxClientError()) {
            mqttService.setMQTTTrafficLightCommand("Yellow");
        }
        else if (status.is5xxServerError()) {
            mqttService.setMQTTTrafficLightCommand("Red-Blink");
        }
    }
}
