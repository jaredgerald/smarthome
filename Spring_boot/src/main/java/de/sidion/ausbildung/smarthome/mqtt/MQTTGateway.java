package de.sidion.ausbildung.smarthome.mqtt;

import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.handler.annotation.Header;

@MessagingGateway(defaultRequestChannel = "mqttOutboundChannel")
public interface MQTTGateway {
    void sendToMqtt(String data, @Header(MqttHeaders.TOPIC) String topic);
}
