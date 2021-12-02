#include <ESP8266WiFi.h>
#include <PubSubClient.h>
#include <FastLED.h>
#include <NTPClient.h>
#include <WiFiUdp.h>
#include <iostream>
#include <cstring>
#include <vector>
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

// Topics:
const String inputTopic = "light/" +  deviceID + "/#";
const String publishTopic = "main/" +  deviceID + "/status";

unsigned long previousMillisPublish = 0;
const long intervalPublish = h_publish_interval_s * 1000;

#define NTP_OFFSET   60 * 60      // In seconds
#define NTP_INTERVAL 60 * 1000    // In miliseconds
#define NTP_ADDRESS  "europe.pool.ntp.org"

WiFiUDP ntpUDP;
NTPClient timeClient(ntpUDP, NTP_ADDRESS, NTP_OFFSET, NTP_INTERVAL);

WiFiClient espClient;
PubSubClient pubSubClient(espClient);

CRGB leds[h_num_led];

void setup() {
  Serial.begin(115200);

  timeClient.begin();

  pinMode(h_outputPin, OUTPUT);

  // setup environments
  setup_wifi();
  setup_mqtt();
  setup_fastLED();
}

void setup_fastLED()
{
  FastLED.addLeds<WS2813, h_outputPin, GRB>(leds, h_num_led);
  Serial.println("FastLED activated!");
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
    pubSubClient.subscribe(inputTopic.c_str());

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
  Serial.println(inputTopic);
  Serial.println();
}

// On arriving message:
void callback(char* topic, byte* message, unsigned int length) {
  Serial.println("---");
  Serial.print("Message arrived on topic: ");
  Serial.print(topic);

  std::stringstream topicString(topic);
  std::string segment;
  unsigned int i = 0;

  while(std::getline(topicString, segment, '/'))
  {
    if (i >= 2)
    {
      break;
    }
    i++;
  }

  std::string msg;
  for (int i = 0; i < length; i++) {
    msg += (char)message[i];
  }

  int index = std::stoi(segment);
  std::stringstream valueStream(msg);
  std::string segmentValue;
  int r = 0;
  int g = 0;
  int b = 0;

  unsigned int l = 0;

  while(std::getline(valueStream, segmentValue, '/'))
  {
    if(l == 0) {
      r = std::stoi(segmentValue);
    }
    else if (l == 1)
    {
      g = std::stoi(segmentValue);
    }
    else if (l == 2)
    {
      b = std::stoi(segmentValue);
    }
    l++;
  }

  Serial.println();
  Serial.print("Value: ");
  Serial.print(r);
  Serial.print("-");
  Serial.print(g);
  Serial.print("-");
  Serial.println(b);

  leds[index] = CRGB(r, g, b);

  if(index >= h_num_led) {
    FastLED.show();
    Serial.println("Show Fastled");
  }
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