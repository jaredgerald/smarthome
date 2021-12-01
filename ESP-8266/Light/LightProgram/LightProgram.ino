#include <ESP8266WiFi.h>
#include <PubSubClient.h>
#include <ArduinoJson.h>

#include <NTPClient.h>
#include <WiFiUdp.h>
#include <sstream>
#include "arduino_local_config.h"

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

// Output pins:
const int redOutputPin = h_red_pin;
const int greenOutputPin = h_green_pin;
const int blueOutputPin = h_blue_pin;

// Topic:
// Mode topic like: "{\"mode\":\"static\",\"color\":{\"r\": 25, \"g\":25, \"b\":25}}"
const String modeTopic = "light/" +  deviceID + "/mode";

unsigned long previousMillisPublish = 0; 
const long intervalPublish = h_publish_interval_s * 1000;

unsigned long previousMillisMode = 0; 
unsigned long intervalMode = 1;
bool modeDone = false;

DynamicJsonDocument currentMode(1024);

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

  String pubTopic = "main/" + deviceID + "/state";

  pubSubClient.publish(pubTopic.c_str(), timeCharConst);

  Serial.println();
  Serial.println("Published following time:");
  Serial.println(timeCharConst);
  Serial.println("To topic:");
  Serial.println(pubTopic);
  Serial.println();
}

// On arriving message:
void callback(char* topic, byte* message, unsigned int length) {
  Serial.println("---------------------------------------");
  Serial.print("Message arrived on topic: ");
  Serial.print(topic);

  // If topic is modeTopic
  if (String(topic) == modeTopic)
  {
    String mode;
    for (int i = 0; i < length; i++) {
      mode += (char)message[i];
    }

    Serial.println("Arrived mode: ");
    Serial.print(mode);
    Serial.println();

    deserializeJson(currentMode, mode);
  }
  else {
    Serial.println("Error: Topic is not mode");
  }  
  Serial.println("---------------------------------------");
}

void mode_static(DynamicJsonDocument doc) {
  int red = doc["color"]["r"];
  int green = doc["color"]["g"];
  int blue = doc["color"]["b"];

  Serial.println(redOut);
  Serial.println(greenOut);
  Serial.println(blueOut);

  analogWrite(redOutputPin, redOut);
  analogWrite(greenOutputPin, greenOut);
  analogWrite(blueOutputPin, blueOut);

  modeDone = true;
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

void execute_led_mode() {
  const String mode = String(currentMode["mode"]); 

  unsigned long currentMillis = millis();

  bool isMillisGreaterInterval = currentMillis - previousMillisMode >= intervalMode;

  if(mode == "static" && intervalMode != 0) {
    mode_static(currentMode);
    intervalMode = 0;
  }
  else if (mode == "shuffle" && isMillisGreaterInterval) {
    mode_shuffle(currentMode);
    intervalMode = doc["interval"];
  }
  else if (mode == "blink" && isMillisGreaterInterval) {
    mode_blink(currentMode);
    intervalMode = doc["interval"];
  }
  else if (mode == "fade" && isMillisGreaterInterval) {
    mode_fade(currentMode);
    intervalMode = doc["interval"];
  }
  else if (mode == "multicolor" && isMillisGreaterInterval) {
    mode_multicolor(currentMode);
    intervalMode = doc["interval"];
  }
  else {
    Serial.println("Error: Mode not existing:");
    Serial.println(mode);
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

  execute_led_mode();
}