package de.sidion.ausbildung.smarthome.service;

import de.sidion.ausbildung.smarthome.dto.LEDModeDTO;
import de.sidion.ausbildung.smarthome.mqtt.MQTTGateway;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MQTTService {
    private static final String SELECTOR_TOPIC = "device/";
    private static final String COMMAND_TOPIC = "/command";
    private static final String REQUEST_TOPIC = "/request";

    private final MQTTGateway gateway;

    public void sendLightMode(int id, LEDModeDTO mode) {
        sendCommand(id, mode.toString());
    } //Async response

    public void sendDeviceRequest(int id) {
        gateway.sendToMqtt("SEND_TIMESTAMP", SELECTOR_TOPIC + id + REQUEST_TOPIC); //Async response
    }

    public void sendCommand(int id, String command) {
        gateway.sendToMqtt(command, SELECTOR_TOPIC + id + COMMAND_TOPIC); //Async response
    }


}
