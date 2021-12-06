package de.sidion.ausbildung.smarthome.service;

import de.sidion.ausbildung.smarthome.dto.LEDModeDTO;
import de.sidion.ausbildung.smarthome.mqtt.MQTTGateway;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MQTTService {
    private static final String LIGHT_TOPIC = "light/"; //device/id/function
    private static final String SPEAKER_TOPIC = "speaker/";
    private static final String TV_TOPIC = "tv/";
    private static final String STATUS_TOPIC = "status/";
    private static final String TRAFFIC_LIGHT_TOPIC = "traffic_light/";
    private static final String MODE_TOPIC = "mode/";
    private static final String SPEED_TOPIC = "speed/";
    private static final String BRIGHTNESS_TOPIC = "brightness/";
    private static final String COLOR_TOPIC = "color/";

    private final MQTTGateway gateway;

    public void setMQTTLightActiveState(int id, boolean status) {
        gateway.sendToMqtt((status)? "on" : "off", LIGHT_TOPIC + id + "/" + STATUS_TOPIC);
    }

    public void setMQTTLightMode(int id, LEDModeDTO mode) {
        gateway.sendToMqtt(mode.getMode(), LIGHT_TOPIC + id + "/" + MODE_TOPIC);
        gateway.sendToMqtt(String.valueOf(mode.getSpeed()), LIGHT_TOPIC + id + "/" + SPEED_TOPIC);
        gateway.sendToMqtt(String.valueOf(mode.getBrightness()), LIGHT_TOPIC + id + "/" + BRIGHTNESS_TOPIC);
        gateway.sendToMqtt(String.valueOf(mode.getColor().getRed()), LIGHT_TOPIC + id + "/" + COLOR_TOPIC + "red");
        gateway.sendToMqtt(String.valueOf(mode.getColor().getBlue()), LIGHT_TOPIC + id + "/" + COLOR_TOPIC + "blue");
        gateway.sendToMqtt(String.valueOf(mode.getColor().getGreen()), LIGHT_TOPIC + id + "/" + COLOR_TOPIC + "green");
    }

    public void setMQTTSpeakerCommand(int id, String command) {
        gateway.sendToMqtt(command, SPEAKER_TOPIC + id);
    }

    public void setMQTTTVCommand(int id, String command) {
        gateway.sendToMqtt(command, TV_TOPIC + id);
    }

    public void setMQTTTrafficLightCommand(String command) {
        gateway.sendToMqtt(command, TRAFFIC_LIGHT_TOPIC);
    }
}
