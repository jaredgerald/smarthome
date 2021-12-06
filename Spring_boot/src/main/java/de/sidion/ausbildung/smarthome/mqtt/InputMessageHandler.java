package de.sidion.ausbildung.smarthome.mqtt;

import de.sidion.ausbildung.smarthome.database.service.DatabaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;

@RequiredArgsConstructor
@Component
public class InputMessageHandler implements MessageHandler {
    private final DatabaseService databaseService;

    @Override
    public void handleMessage(Message<?> message) throws MessagingException {
        try {
            String topic = Objects.requireNonNull(message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC)).toString();

            //Array content: main/{ id }/{ function }
            String[] topicArray = topic.split("/");

            int deviceId = Integer.parseInt(topicArray[1]);

            if (databaseService.existsDeviceById(deviceId)) {
                String messagePayload = message.getPayload().toString();

                switch (topicArray[2]) {
                    case "timestamp":
                        long timestampInt = Long.getLong(messagePayload);
                        LocalDateTime timestamp = LocalDateTime.ofInstant(
                                Instant.ofEpochMilli(timestampInt), ZoneId.systemDefault());
                        databaseService.updateDeviceTimestamp(deviceId, timestamp);
                        break;
                    case "data":
                        databaseService.saveDeviceData(deviceId, messagePayload);
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
