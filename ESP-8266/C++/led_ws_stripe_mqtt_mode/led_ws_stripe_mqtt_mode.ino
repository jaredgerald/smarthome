#include <ArduinoJson.h>
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
#include "local_config.h"

#define NTP_OFFSET   60 * 60      // In seconds
#define NTP_INTERVAL 60 * 1000    // In miliseconds
#define NTP_ADDRESS  "europe.pool.ntp.org"

WiFiUDP ntpUDP;
NTPClient timeClient(ntpUDP, NTP_ADDRESS, NTP_OFFSET, NTP_INTERVAL);

const String deviceID = h_device_id;

// Network config:
const char* ssid     = h_wifi_ssid;
const char* password = h_wifi_password;

// MQTT config:
const char* MQTT_HOST = h_mqttbroker_host;
const char* MQTT_CLIENT_ID = ("ESP8266Client" + deviceID).c_str();
const char* MQTT_USER = h_mqtt_user;
const char* MQTT_PASSWORD = h_mqtt_password;

// Topic:
// Mode topic like: "{\"mode\":\"static\",\"color\":{\"r\": 25, \"g\":25, \"b\":25}}"
const String modeTopic = "light/" +  deviceID + "/mode";

const String pubTopic = "main/" + deviceID + "/state";

unsigned long previousMillisPublish = 0;
const long intervalPublish = h_publish_interval_s * 1000;

unsigned long previousMillisMode = 0;
unsigned long intervalMode = 1;

CRGB leds[h_num_led];

int direction = 1;
int currentLed = 0;

DynamicJsonDocument currentMode(1024);

WiFiClient espClient;
PubSubClient pubSubClient(espClient);

void setup() {
  Serial.begin(115200);

  timeClient.begin();

  pinMode(h_outputPin, OUTPUT);

  // setup environments
  setup_wifi();
  setup_mqtt();
  setup_current_mode_json();
  setup_fastLED();
}

void setup_fastLED()
{
  FastLED.addLeds<WS2813, h_outputPin, GRB>(leds, h_num_led);
  Serial.println("FastLED activated!");
}

void setup_current_mode_json() {
  currentMode["mode"] = "static";
  currentMode["color"]["r"] = 200;
  currentMode["color"]["g"] = 5;
  currentMode["color"]["b"] = 100;
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

    //Subscribe to topic
    pubSubClient.subscribe(modeTopic.c_str());

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

  pubSubClient.publish(pubTopic.c_str(), timeCharConst);

  Serial.println();
  Serial.println("Published following time:");
  Serial.println(timeCharConst);
  Serial.println("To topic:");
  Serial.println(pubTopic.c_str());
  Serial.println();
}

// On arriving message:
void callback(char* topic, byte* message, unsigned int length) {
  Serial.println("-----------------");
  Serial.print("Arrived topic: ");
  Serial.print(topic);

  // If topic is modeTopic
  if (String(topic) == modeTopic)
  {
    String json;
    for (int i = 0; i < length; i++) {
      json += (char)message[i];
    }

    Serial.println();
    Serial.print("Arrived Json: ");
    Serial.println(json);

    deserializeJson(currentMode, json);

    intervalMode = currentMode["interval"];
    currentLed = currentMode["start_led"];
    direction = currentMode["direction"];

    execute_led_mode();
  }
  else {
    Serial.println("Error: Topic is not mode");
  }
}

void mode_static() { //Static other direction with direction -1
  if(currentLed > h_num_led) {
    int r = currentMode["color"]["r"];
    int g = currentMode["color"]["g"];
    int b = currentMode["color"]["b"];

    leds[currentLed] = CRGB(r, g, b);

    currentLed = currentLed + direction;
  }
}

void mode_shuffle() {
}

void mode_blink() {
}

void mode_fade() {
}

void mode_multicolor() {
}

void execute_led_mode() {
  const String mode = String(currentMode["mode"]); 

  if(mode == "static") {
    mode_static();
  }
  else if (mode == "shuffle") {
    mode_shuffle();
  }
  else if (mode == "blink") {
    mode_blink();
  }
  else if (mode == "fade" ) {
    mode_fade();
  }
  else if (mode == "sstripe" ) {
    mode_fade();
  }
  else if (mode == "mstripe" ) {
    mode_fade();
  }
  else if (mode == "stars" ) {
    mode_fade();
  }
  else if (mode == "meet" ) {
    mode_fade();
  }
  else if (mode == "multicolor") {
    mode_multicolor();
  }
  else {
    Serial.println("Error: Mode not existing:");
    Serial.println(mode);
  }

  FastLED.show();

  // Serial.println("-----------------");
  // Serial.print("m=");
  // Serial.print(mode);
  // Serial.print(", r=");
  // Serial.print(colorRed);
  // Serial.print(", g=");
  // Serial.print(colorGreen);
  // Serial.print(", b=");
  // Serial.println(colorBlue);
  
}

void loop() {
  if(!pubSubClient.connected()) {
    connect_mqtt();
  }
  pubSubClient.loop();

  unsigned long currentMillis = millis(); 

  //publisher timer
  if (currentMillis - previousMillisPublish >= intervalPublish)
  {
    previousMillisPublish = currentMillis;

    timeClient.update();
    mqtt_publish_time();
  }

  //Led mode timer
  if(currentMillis - previousMillisMode >= intervalMode) {
    previousMillisMode = currentMillis;
    execute_led_mode();
  }
}