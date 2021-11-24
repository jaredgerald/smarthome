// Library für WiFi-Verbindung
#include <ESP8266WiFi.h>
#include <PubSubClient.h>
#include <ArduinoJson.h>

#include <NTPClient.h>
#include <WiFiUdp.h>
#include "arduino_local_config.h"

// Config des WiFi-Netzwerks
const char* ssid     = h_wifi_ssid;
const char* password = h_wifi_password;
// Config des MQTT Brokers
const char* MQTT_HOST = h_mqttbroker_host;
const char* MQTT_CLIENT_ID = "ESP8266Client" + h_device_id;
const char* MQTT_USER = h_mqtt_user;
const char* MQTT_PASSWORD = h_mqtt_password;

const char* deviceTopic = "light/" + h_device_id;
const char* statusTopic = deviceTopic + "/status"; //Status topic: "on", "off"
const char* modeTopic = deviceTopic + "/mode";  // Mode topic '{"mode": "static", "color": {"r": 0, "g": 0, "b": 0} }', '{"mode": "shuffle", "speed": 0 }'

WiFiClient espClient;
PubSubClient pubSubClient(espClient);

void setup() {
  Serial.begin(115200);

  // setup environments
  setup_wifi();
  setup_mqtt();
}

void setup_wifi() {
  Serial.println("Connecting to WiFi: ");
  Serial.print(h_wifi_ssid);
  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED) {
    delay(1000);
    Serial.print(".");
  }
  Serial.println("");
  Serial.println("WiFi connected, IP address: ");
  Serial.println(WiFi.localIP());
}

void setup_mqtt() {
  Serial.println("Setting up MQTT: ");
  Serial.println(h_mqttbroker_host);
  pubSubClient.setServer(MQTT_HOST, 1883);
  pubSubClient.setCallback(callback);
  Serial.println("Setup done MQTT");
}

void connect_mqtt() {
  Serial.println("Connecting to MQTT: ");
  Serial.println(h_mqttbroker_host);

  if (!pubSubClient.connected()) {
    Serial.println("MQTT connecting");
    while (!pubSubClient.connected()) {
      pubSubClient.connect(MQTT_CLIENT_ID, MQTT_USER, MQTT_PASSWORD);
      delay(1000);
      Serial.print("-");
    }

    //Topic
    client.subscribe(statusTopic)
    client.subscribe(modeTopic)

    Serial.println("");
  }
  Serial.println("MQTT Connected");
}

//Topics to subscribe
void callback(char* topic, byte* message, unsigned int length) {
  Serial.print("Message arrived on topic: ");
  Serial.print(topic);
  Serial.print(". Message: ");
  String messageTemp;
  
  for (int i = 0; i < length; i++) {
    Serial.print((char)message[i]);
    messageTemp += (char)message[i];
  }
  Serial.println();

  switch (String(topic))
  {
  case statusTopic:
    Serial.print("Changing status to ");
    if(messageTemp == "on"){
      Serial.println("on");
    }
    else if(messageTemp == "off"){
      Serial.println("off");
    }
    break;

  case modeTopic:
    Serial.println(messageTemp);
    break;
  
  default:
    Serial.println("Error for Topic: ");
    Serial.println(messageTemp);
    break;
  }
  
}

// void mqtt_publish(char* msg) {
//   // publish some data
//   if(!pubSubClient.connected()) {
//     connect_mqtt();
//   }
//   pubSubClient.publish("/iot/esp8266", msg);
//   Serial.println("Published the following message:");
//   Serial.println(msg);
// }

void loop() {
  if(!pubSubClient.connected()) {
    connect_mqtt();
  }
  pubSubClient.loop();

  //mqtt_publish(eventBuf);
}
