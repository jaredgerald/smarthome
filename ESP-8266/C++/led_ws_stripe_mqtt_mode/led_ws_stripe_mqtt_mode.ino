#include <ESP8266WiFi.h>
#include <PubSubClient.h>
#include <ArduinoJson.h>

#include <NTPClient.h>
#include <WiFiUdp.h>
#include <iostream>
#include <cstring>
#include <string>
#include <sstream>
#include "arduino_local_config.h"

const String deviceID = h_device_id;

// Network config:
const char* ssid     = h_wifi_ssid;
const char* password = h_wifi_password;

// MQTT config:
const char* MQTT_HOST = h_mqttbroker_host;
const char* MQTT_CLIENT_ID = ("ESP8266Client" + deviceID).c_str();
const char* MQTT_USER = h_mqtt_user;
const char* MQTT_PASSWORD = h_mqtt_password;

// Output pins:
const int redOutputPin = h_red_pin;
const int greenOutputPin = h_green_pin;
const int blueOutputPin = h_blue_pin;

// Topics:
const String redLedTopic = "light/" +  deviceID + "/red";
const String greenLedTopic = "light/" +  deviceID + "/green";
const String blueLedTopic = "light/" +  deviceID + "/blue";
const String publishTopic = "main/" + deviceID + "/state";

unsigned long previousMillisPublish = 0; 
const long intervalPublish = h_publish_interval_s * 1000;

#define NTP_OFFSET   60 * 60      // In seconds
#define NTP_INTERVAL 60 * 1000    // In miliseconds
#define NTP_ADDRESS  "europe.pool.ntp.org"

WiFiUDP ntpUDP;
NTPClient timeClient(ntpUDP, NTP_ADDRESS, NTP_OFFSET, NTP_INTERVAL);

WiFiClient espClient;
PubSubClient pubSubClient(espClient);

void setup() {
  Serial.begin(115200);

  timeClient.begin();

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
  Serial.println(MQTT_HOST);
  pubSubClient.setServer(MQTT_HOST, 1883);
  pubSubClient.setCallback(callback);
  Serial.println("Setup done MQTT");
}

void connect_mqtt() {
  Serial.println("Connecting to MQTT: ");
  Serial.println(MQTT_HOST);

  if (!pubSubClient.connected()) {
    Serial.println("MQTT connecting");
    while (!pubSubClient.connected()) {
      pubSubClient.connect(MQTT_CLIENT_ID, MQTT_USER, MQTT_PASSWORD);
      delay(1000);
      Serial.print("-");
    }

    //Subscribe to topics
    pubSubClient.subscribe(redLedTopic.c_str());
    pubSubClient.subscribe(greenLedTopic.c_str());
    pubSubClient.subscribe(blueLedTopic.c_str());

    Serial.println("");
  }
  Serial.println("MQTT Connected");
}

void mqtt_publish_time() {  
  unsigned long time = timeClient.getEpochTime();

  std::ostringstream oss;
  oss << time;

  std::string timeString = oss.str();
  const char* timeCharConst = timeString.c_str();

  pubSubClient.publish(publishTopic.c_str(), timeCharConst);

  Serial.println();
  Serial.println("Published following time:");
  Serial.println(timeCharConst);
  Serial.println("To topic:");
  Serial.println(publishTopic);
  Serial.println();
}

// On arriving message:
void callback(char* topic, byte* message, unsigned int length) {
  Serial.println("---------------------------------------");
  Serial.print("Message arrived on topic: ");
  Serial.print(topic);

  std::string msg;
  for (int i = 0; i < length; i++) {
    msg += (char)message[i];
  }

  int value = std::stoi(msg);

  Serial.println();
  Serial.println("Value: ");
  Serial.println(value);
  Serial.println();

  if(String(topic) == redLedTopic)
  {
    analogWrite(redOutputPin, value);
  }
  else if (String(topic) == greenLedTopic)
  {
    analogWrite(greenOutputPin, value);
  }
  else if (String(topic) == blueLedTopic)
  {
    analogWrite(blueOutputPin, value);
  }

  Serial.println("---------------------------------------");
}

void loop() {
  if(!pubSubClient.connected()) {
    connect_mqtt();
  }
  pubSubClient.loop();

  unsigned long currentMillis = millis(); 

  if (currentMillis - previousMillisPublish >= intervalPublish)
  {
    previousMillisPublish = currentMillis;

    timeClient.update();
    mqtt_publish_time();
  }
}