import paho.mqtt.client as mqtt
from rpi_ws281x import *
import argparse
import configparser
import time


stripe_status = "off"
stripe_mode = "static"
stripe_speed = 100
stripe_brightness = 80
stripe_red = 0
stripe_blue = 0
stripe_green = 0


# The callback for when the client receives a CONNACK response from the server.
def on_connect(client, userdata, flags, rc):
    print("Connected with result code " + str(rc))

    client.subscribe(LIGHT_STATUS_TOPIC)
    client.subscribe(LIGHT_MODE_TOPIC)
    client.subscribe(LIGHT_SPEED_TOPIC)
    client.subscribe(LIGHT_BRIGHTNESS_TOPIC)
    client.subscribe(LIGHT_COLOR_RED_TOPIC)
    client.subscribe(LIGHT_COLOR_BLUE_TOPIC)
    client.subscribe(LIGHT_COLOR_GREEN_TOPIC)


# The callback for when a PUBLISH message is received from the server.
def on_message(client, userdata, msg):
    print(msg.topic + " " + str(msg.payload))

    payload = str(msg.payload)

    if msg.topic is LIGHT_STATUS_TOPIC:
        global stripe_status
        stripe_status = payload
    elif msg.topic is LIGHT_MODE_TOPIC:
        global stripe_mode
        stripe_mode = payload
    elif msg.topic is LIGHT_SPEED_TOPIC:
        global stripe_speed
        stripe_speed = payload
    elif msg.topic is LIGHT_BRIGHTNESS_TOPIC:
        global stripe_brightness
        stripe_brightness = payload
    elif msg.topic is LIGHT_COLOR_RED_TOPIC:
        global stripe_red
        stripe_red = payload
    elif msg.topic is LIGHT_COLOR_BLUE_TOPIC:
        global stripe_blue
        stripe_blue = payload
    elif msg.topic is LIGHT_COLOR_GREEN_TOPIC:
        global stripe_green
        stripe_green = payload


# ----------------------------------------------------------------------------------------------------------

# ----------------------------------------------------------------------------------------------------------

def color_wipe(strip, color, wait_ms=50):
    """Wipe color across display a pixel at a time."""
    for i in range(strip.numPixels()):
        strip.setPixelColor(i, color)
        strip.show()
        time.sleep(wait_ms / 1000.0)


def rainbow_cycle(strip, wait_ms=20, iterations=5):
    """Draw rainbow that uniformly distributes itself across all pixels."""
    for j in range(256 * iterations):
        for i in range(strip.numPixels()):
            strip.setPixelColor(i, wheel((int(i * 256 / strip.numPixels()) + j) & 255))
        strip.show()
        time.sleep(wait_ms / 1000.0)


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


# ----------------------------------------------------------------------------------------------------------

# Process arguments
parser = argparse.ArgumentParser()
parser.add_argument('-u', '--username', action='store', dest='username', help='The mqtt username.', required=True)
parser.add_argument('-P', '--password', action='store', dest='password', help='The mqtt password.', required=True)
parser.add_argument('-i', '--id', type=int, action='store', help='<Required> Subscribe to topics', required=True)
# parser.add_argument('-c', '--config', action='store', default='', dest='config', help='The light strip config')
args = parser.parse_args()

# ----------------------------------------------------------------------------------------------------------

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

# ----------------------------------------------------------------------------------------------------------

LIGHT_STATUS_TOPIC = f"light/{args.id}/status"
LIGHT_MODE_TOPIC = f"light/{args.id}/mode"
LIGHT_SPEED_TOPIC = f"light/{args.id}/speed"
LIGHT_BRIGHTNESS_TOPIC = f"light/{args.id}/brightness"
LIGHT_COLOR_RED_TOPIC = f"light/{args.id}/color/red"
LIGHT_COLOR_BLUE_TOPIC = f"light/{args.id}/color/blue"
LIGHT_COLOR_GREEN_TOPIC = f"light/{args.id}/color/green"

# ----------------------------------------------------------------------------------------------------------

# Create NeoPixel object with appropriate configuration.
strip = Adafruit_NeoPixel(LED_COUNT, LED_PIN, LED_FREQ_HZ, LED_DMA, LED_INVERT, LED_BRIGHTNESS, LED_CHANNEL)
# Initialize the library (must be called once before other functions).
strip.begin()

# ----------------------------------------------------------------------------------------------------------

client = mqtt.Client()
client.on_connect = on_connect
client.on_message = on_message

client.username_pw_set(username=args.username, password=args.password)

client.connect("127.0.0.1", 1883, 60)

# Blocking call that processes network traffic, dispatches callbacks and
# handles reconnecting.
# Other loop*() functions are available that give a threaded interface and a
# manual interface.
client.loop_forever()


# ----------------------------------------------------------------------------------------------------------

while True:
    if stripe_status is "on":
        if stripe_mode is "rainbow":
            rainbow_cycle(strip)
        elif stripe_mode is "static":
            color_wipe(strip, Color(stripe_red, stripe_blue, stripe_green))
    else:
        color_wipe(strip, Color(0, 0, 0), 10)


