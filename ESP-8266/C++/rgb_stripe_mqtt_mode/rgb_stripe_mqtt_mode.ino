#include <ESP8266WiFi.h>
#include <PubSubClient.h>
#include <ArduinoJson.h>

#include <NTPClient.h>
#include <WiFiUdp.h>
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

int colorRed = 0;
int colorGreen = 0;
int colorBlue = 0;

int direction = 1;

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
  setup_current_mode_json();
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

    colorRed = 0;
    colorGreen = 0;
    colorBlue = 0;

    execute_led_mode();
  }
  else {
    Serial.println("Error: Topic is not mode");
  }
}

void mode_static() {
  colorRed = currentMode["color"]["r"];
  colorGreen = currentMode["color"]["g"];
  colorBlue = currentMode["color"]["b"];
}

void mode_shuffle() { //not good
  if(colorRed <= 0) {
    colorRed = 255;
  }
  else if (colorRed >= 255)
  {
    colorRed = 0;
  }

  if(colorGreen <= 0) {
    colorGreen = 255;
  }
  else if (colorGreen >= 255)
  {
    colorGreen = 0;
  }

  if(colorBlue <= 0) {
    colorBlue = 255;
  }
  else if (colorBlue >= 255)
  {
    colorBlue = 0;
  }

  double randRed = random(100);
  double randGreen = random(100);
  double randBlue = random(100);

  if(randRed >= 50) {
    colorRed = colorRed+1;
  }
  else {
    colorRed = colorRed-1;
  }

  if(randGreen >= 50) {
    colorGreen = colorGreen+1;
  }
  else {
    colorGreen = colorGreen-1;
  }

  if(randBlue >= 50) {
    colorBlue = colorBlue+1;
  }
  else {
    colorBlue = colorBlue-1;
  }
}

void mode_blink() {
  if(colorRed == 0) {
    colorRed = currentMode["color"]["r"];
    colorGreen = currentMode["color"]["g"];
    colorBlue = currentMode["color"]["b"];
  }
  else {
    colorRed = 0;
    colorGreen = 0;
    colorBlue = 0;
  }
}

void mode_fade() { //not working
  if(colorRed == 0) {
    colorRed = currentMode["color"]["r"];
    colorGreen = currentMode["color"]["g"];
    colorBlue = currentMode["color"]["b"];
  }

  if(colorRed <= currentMode["color"]["r"]) {
    colorRed + direction;
  }
}

void mode_multicolor() {
  bool isRed0 = colorRed == 0;
  bool isGreen0 = colorGreen == 0;
  bool isBlue0 = colorBlue == 0;
  //Check if its start. 
  if(isRed0 && isGreen0 && isBlue0) 
  {
    colorRed = currentMode["color"][0]["r"];
    colorGreen = currentMode["color"][0]["g"];
    colorBlue = currentMode["color"][0]["b"];
  }
  else
  {
    //Check if color is in color array. When in array, pick array +1.
    for (int i = 0; i < sizeof(currentMode["color"]); i++)
    {
      bool isSameRed = colorRed == currentMode["color"][i]["r"];
      bool isSameGreen = colorGreen == currentMode["color"][i]["g"];
      bool isSameBlue = colorBlue == currentMode["color"][i]["b"];

      Serial.println(sizeof(currentMode["color"]) / sizeof(currentMode["color"][0]));

      if(isSameRed && isSameGreen && isSameBlue) {
        if(i+1 == sizeof(currentMode["color"]) / sizeof(currentMode["color"][0])) { //line not working!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
          colorRed = currentMode["color"][0]["r"];
          colorGreen = currentMode["color"][0]["g"];
          colorBlue = currentMode["color"][0]["b"];
        }
        else {
          colorRed = currentMode["color"][i+1]["r"];
          colorGreen = currentMode["color"][i+1]["g"];
          colorBlue = currentMode["color"][i+1]["b"];
        }
        break;
      }
    }
  }
}

void execute_led_mode() {
  const String mode = String(currentMode["mode"]); 

  if(mode == "static") {
    mode_static();
    intervalMode = 6000000;
  }
  else if (mode == "shuffle") {
    mode_shuffle();
    intervalMode = currentMode["interval"];
  }
  else if (mode == "blink") {
    mode_blink();
    intervalMode = currentMode["interval"];
  }
  else if (mode == "fade" ) {
    mode_fade();
    intervalMode = currentMode["interval"];
  }
  else if (mode == "multicolor") {
    mode_multicolor();
    intervalMode = currentMode["interval"];
  }
  else {
    Serial.println("Error: Mode not existing:");
    Serial.println(mode);
  }

  //Output
  analogWrite(redOutputPin, colorRed);
  analogWrite(greenOutputPin, colorGreen);
  analogWrite(blueOutputPin, colorBlue);

  Serial.println("-----------------");
  Serial.print("m=");
  Serial.print(mode);
  Serial.print(", r=");
  Serial.print(colorRed);
  Serial.print(", g=");
  Serial.print(colorGreen);
  Serial.print(", b=");
  Serial.println(colorBlue);
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