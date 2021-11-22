package de.sidion.ausbildung.smarthome.mqtt;

import lombok.RequiredArgsConstructor;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import java.util.Map;

@Configuration()
@RequiredArgsConstructor
public class MQTTBeans {

    private final InputMessageHandler handler;
    Map<String, String> env = System.getenv();
    private static final String TOPIC = "main/#";
    private static final String CLIENT_ID1 = "spring_boot_input";
    private static final String CLIENT_ID2 = "spring_boot_output";

    public static final String MQTT_INPUT_CHANNEL = "mqttInputChannel";
    public static final String MQTT_OUTPUT_CHANNEL = "mqttOutboundChannel";

    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();

        MqttConnectOptions options = new MqttConnectOptions();

        options.setServerURIs(new String[] {env.get("MQTT_URL")});
        options.setUserName(env.get("MQTT_USERNAME"));
        options.setPassword(env.get("MQTT_PASSWORD").toCharArray());
        options.setCleanSession(true);

        factory.setConnectionOptions(options);
        return factory;
    }

    @Bean
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel mqttOutboundChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageProducer inbound(MqttPahoClientFactory factory, @Qualifier(MQTT_INPUT_CHANNEL) MessageChannel channel) {
        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter( CLIENT_ID1, factory, TOPIC);
        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(1);
        adapter.setOutputChannel(channel);
        return adapter;
    }

    @Bean
    @ServiceActivator(inputChannel = MQTT_OUTPUT_CHANNEL)
    public MessageHandler mqttOutbound(MqttPahoClientFactory factory) {
        MqttPahoMessageHandler messageHandler =
                new MqttPahoMessageHandler(CLIENT_ID2, factory);
        messageHandler.setAsync(true);
        messageHandler.setDefaultTopic("error");
        return messageHandler;
    }

    @Bean
    @ServiceActivator(inputChannel = MQTT_INPUT_CHANNEL)
    public MessageHandler handler() {
        return handler;
    }
}
