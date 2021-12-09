package de.sidion.ausbildung.smarthome.mqtt;

import de.sidion.ausbildung.smarthome.service.DatabaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Component;

import java.util.Objects;

@RequiredArgsConstructor
@Component
public class InputMessageHandler implements MessageHandler {
    private final DatabaseService databaseService;

    private static final String TIMESTAMP = "timestamp";
    private static final String SENSOR_DATA = "sensor_data";

    @Override
    public void handleMessage(Message<?> message) throws MessagingException {
        try {
            String topic = Objects.requireNonNull(message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC)).toString();

            //--> Array content: main/{ id }/{ function }
            String[] topicArray = topic.split("/");

            int deviceId = Integer.parseInt(topicArray[1]);

            if (databaseService.existsDeviceById(deviceId)) {
                String messagePayload = message.getPayload().toString();

                switch (topicArray[2]) {
                    case TIMESTAMP:
                        databaseService.saveDeviceData(deviceId, TIMESTAMP, messagePayload);
                        break;
                    case SENSOR_DATA:
                        databaseService.saveDeviceData(deviceId, SENSOR_DATA, messagePayload);
                        break;
                    default:
                        //Set traffic light to blink red! for 15 min
                        //Log error!
                        break;
                }
            }
        }
        catch (Exception e) {
            //Log error!
        }
    }
}
