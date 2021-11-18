import paho.mqtt.client as mqtt
import argparse
import Adafruit_DHT
import time
from datetime import datetime

# The callback for when the client receives a CONNACK response from the server.
def on_connect(client, userdata, flags, rc):
    print("Connected with result code " + str(rc))

# Process arguments
parser = argparse.ArgumentParser()
parser.add_argument('-u', '--username', action='store', dest='username', help='The mqtt username.')
parser.add_argument('-p', '--password', action='store', dest='password', help='The mqtt password.')
parser.add_argument('-t', '--topic', action='store', dest='topic', help='The mqtt topic to publish to.')
parser.add_argument('-s', '--seconds', type=float, action='store', dest='seconds', help='The intervall to publish')
parser.add_argument('-n', '--pin', type=int, action='store', dest='pin', help='The sensor pin')
args = parser.parse_args()

DHT_SENSOR = Adafruit_DHT.AM2302

client = mqtt.Client()
client.on_connect = on_connect

client.username_pw_set(username = args.username, password = args.password)

client.connect("127.0.0.1", 1883, 60)

client.loop_start() #start the loop

while True:
    humidity, temperature = Adafruit_DHT.read_retry(DHT_SENSOR, args.pin)

    if humidity is not None and temperature is not None:
        value = "{2} - T={0:0.1f} H={1:0.1f}".format(temperature, humidity, datetime.now())

        print("Publishing message to topic", args.topic)

        client.publish(args.topic, value)
    else:
        print("Failed to retrieve data from humidity sensor")

    time.sleep(args.seconds) # wait

#client.loop_stop() #stop the loop