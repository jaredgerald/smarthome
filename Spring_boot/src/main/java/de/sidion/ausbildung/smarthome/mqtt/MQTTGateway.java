package de.sidion.ausbildung.smarthome.mqtt;

import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.handler.annotation.Header;

@MessagingGateway(defaultRequestChannel = MQTTBeans.MQTT_OUTPUT_CHANNEL)
public interface MQTTGateway {
    void sendToMqtt(String data, @Header(MqttHeaders.TOPIC) String topic);
}
