package de.sidion.ausbildung.smarthome.mqtt;

import de.sidion.ausbildung.smarthome.database.service.DatabaseService;
import de.sidion.ausbildung.smarthome.service.DeviceStateService;
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
    private final DeviceStateService deviceStateService;

    @Override
    public void handleMessage(Message<?> message) throws MessagingException {
        try {
            String topic = Objects.requireNonNull(message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC)).toString();
            String[] topicArray = topic.split("/"); //Input like: main/sensor/3

            int deviceId = Integer.parseInt(topicArray[2]);

            boolean isDeviceExisting = databaseService.existsDeviceById(deviceId);

            boolean isStatus = topicArray[1].equals("status");
            boolean isData = topicArray[1].equals("data");

            deviceStateService.setActive(deviceId); //get active state from mqtt

            String messagePayload = message.getPayload().toString();

            if(isDeviceExisting && isStatus) {
                databaseService.updateDeviceStatus(deviceId, messagePayload);
            }
            else if(isDeviceExisting && isData) {
                databaseService.saveDeviceData(deviceId, messagePayload);
            }
            else {
                System.out.println("Error!");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
