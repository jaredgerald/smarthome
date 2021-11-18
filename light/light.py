import paho.mqtt.client as mqtt
from rpi_ws281x import *
import argparse
import configparser
import time

# The callback for when the client receives a CONNACK response from the server.
def on_connect(client, userdata, flags, rc):
    print("Connected with result code "+str(rc))

    # Subscribing in on_connect() means that if we lose the connection and
    # reconnect then subscriptions will be renewed.

    for t in args.topics:
        client.subscribe(t)


# The callback for when a PUBLISH message is received from the server.
def on_message(client, userdata, msg):
    print(msg.topic+" "+str(msg.payload))

    # code to call lightstripe here

#----------------------------------------------------------------------------------------------------------

def colorWipe(strip, color, wait_ms=50):
    """Wipe color across display a pixel at a time."""
    for i in range(strip.numPixels()):
        strip.setPixelColor(i, color)
        strip.show()
        time.sleep(wait_ms/1000.0)

def rainbowCycle(strip, wait_ms=20, iterations=5):
    """Draw rainbow that uniformly distributes itself across all pixels."""
    for j in range(256*iterations):
        for i in range(strip.numPixels()):
            strip.setPixelColor(i, wheel((int(i * 256 / strip.numPixels()) + j) & 255))
        strip.show()
        time.sleep(wait_ms/1000.0)
                
def wheel(pos):
    """Generate rainbow colors across 0-255 positions."""
    if pos < 85:
        return Color(pos * 3, 255 - pos * 3, 0)
    elif pos < 170:
        pos -= 85
        return Color(255 - pos * 3, 0, pos * 3)
    else:
        pos -= 170
        return Color(0, pos * 3, 255 - pos * 3)

#----------------------------------------------------------------------------------------------------------

# Process arguments
parser = argparse.ArgumentParser()
parser.add_argument('-u', '--username', action='store', dest='username', help='The mqtt username.')
parser.add_argument('-p', '--password', action='store', dest='password', help='The mqtt password.')
parser.add_argument('-t','--topics', nargs='+', help='<Required> Subscribe to topics', required=True)
args = parser.parse_args()

#----------------------------------------------------------------------------------------------------------

# Read config
config = configparser.ConfigParser()
config.read(r'/home/pi/smarthome/light/config.cfg')
    
led_strip_configuration_name = 'led_strip_configuration'
    
LED_COUNT = int(config.get(led_strip_configuration_name, 'led_count'))
LED_PIN = int(config.get(led_strip_configuration_name, 'led_pin'))
LED_FREQ_HZ = int(config.get(led_strip_configuration_name, 'led_freq_hz'))
LED_DMA = int(config.get(led_strip_configuration_name, 'led_dma'))
LED_INVERT = config.get(led_strip_configuration_name, 'led_invert') == 'True'
LED_BRIGHTNESS = int(config.get(led_strip_configuration_name, 'led_brightness'))
LED_CHANNEL = int(config.get(led_strip_configuration_name, 'led_channel'))

#----------------------------------------------------------------------------------------------------------

# Create NeoPixel object with appropriate configuration.
strip = Adafruit_NeoPixel(LED_COUNT, LED_PIN, LED_FREQ_HZ, LED_DMA, LED_INVERT, LED_BRIGHTNESS, LED_CHANNEL)
# Intialize the library (must be called once before other functions).
strip.begin()

#----------------------------------------------------------------------------------------------------------

client = mqtt.Client()
client.on_connect = on_connect
client.on_message = on_message

client.username_pw_set(username = args.username, password = args.password)

client.connect("127.0.0.1", 1883, 60)

# Blocking call that processes network traffic, dispatches callbacks and
# handles reconnecting.
# Other loop*() functions are available that give a threaded interface and a
# manual interface.
client.loop_forever()

#----------------------------------------------------------------------------------------------------------

def rainbow():
    try:
        while True:
            rainbowCycle(strip)

    except KeyboardInterrupt:
        if args.clear:
            colorWipe(strip, Color(0,0,0), 10)