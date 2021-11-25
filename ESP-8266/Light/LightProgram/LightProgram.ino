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
const char* MQTT_CLIENT_ID = "ESP8266Client3";
const char* MQTT_USER = h_mqtt_user;
const char* MQTT_PASSWORD = h_mqtt_password;

const int redOutputPin = D6;
const int greenOutputPin = D7;
const int blueOutputPin = D8;

const char* deviceTopic = "light/3";
const char* stateTopic = "light/3/state"; //State topic: "on", "off"
const char* modeTopic = "light/3/mode";  // Mode topic "{\"mode\":\"static\",\"color\":{\"r\": 25, \"g\":25, \"b\":25}}"

String currentMode = "none";

unsigned long previousMillis = 0; 
const long interval = 20000; 

WiFiClient espClient;
PubSubClient pubSubClient(espClient);

void setup() {
  Serial.begin(115200);

  pinMode(redOutputPin, OUTPUT);
  pinMode(greenOutputPin, OUTPUT);
  pinMode(blueOutputPin, OUTPUT);

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
    pubSubClient.subscribe(stateTopic);
    pubSubClient.subscribe(modeTopic);

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

  if (String(topic) == stateTopic)
  {
    Serial.print("Changing status to ");
    if(messageTemp == "on"){
      Serial.println("on");
    }
    else if(messageTemp == "off"){
      Serial.println("off");
    }
  }
  else if (String(topic) == modeTopic)
  {
    on_mode_callback(messageTemp);
  }
  else {
    Serial.println("Error for Topic: ");
    Serial.println(messageTemp);
  }
}

void on_mode_callback(String msg) {
  currentMode = msg;
  Serial.println("Arrived mode: ");
  Serial.println(msg);

  DynamicJsonDocument doc(1024);
  deserializeJson(doc, msg);

  const char* mode = doc["mode"]; 

  if(strcmp(mode, "static") == 0) {
    mode_static(doc);
  }
  else if (strcmp(mode, "shuffle") == 0) {
    mode_shuffle(doc);
  }
  else if (strcmp(mode, "blink") == 0) {
    mode_blink(doc);
  }
  else if (strcmp(mode, "fade") == 0) {
    mode_fade(doc);
  }
  else if (strcmp(mode, "multicolor") == 0) {
    mode_multicolor(doc);
  }
  else {
    Serial.println("Error: Mode not existing:");
    Serial.println(mode);
  }

  if(!pubSubClient.connected()) {
    connect_mqtt();
  }

  mqtt_publish();
}

void mode_static(DynamicJsonDocument doc) {
  int red = doc["color"]["r"];
  int green = doc["color"]["g"];
  int blue = doc["color"]["b"];

  Serial.println(red);
  Serial.println(green);
  Serial.println(blue);
}

void mode_shuffle(DynamicJsonDocument doc) {
  int speed = doc["speed"];
  Serial.println(speed);
}

void mode_blink(DynamicJsonDocument doc) {
  int speed = doc["speed"];
  int red = doc["color"]["r"];
  int green = doc["color"]["g"];
  int blue = doc["color"]["b"];
  Serial.println(red);
  Serial.println(green);
  Serial.println(blue);
  Serial.println(speed);
}

void mode_fade(DynamicJsonDocument doc) {
  int speed = doc["speed"];
  Serial.println(speed);
}

void mode_multicolor(DynamicJsonDocument doc) {
  
}

void mqtt_publish() {
  // publish some data
  char* msg = "Test";

  pubSubClient.publish("main/3", msg);

  Serial.println("Published the following message:");
  Serial.println(msg);
}

void loop() {
  if(!pubSubClient.connected()) {
    connect_mqtt();
  }
  pubSubClient.loop();

  unsigned long currentMillis = millis(); 

  if (currentMillis - previousMillis >= interval)
  {
    previousMillis = currentMillis;

    Serial.println("Print");
  }  

  //mqtt_publish(eventBuf);
}
