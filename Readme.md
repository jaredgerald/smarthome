# Smarthome project:

## Get Started with ESP-82666 C++ Programs:
1. Open Arduino IDE.
2. Go to "Sketch", "Add file...".
3. Select the localconfig.h file.
4. Now a new File will be created in the Project this folder.
5. Edit the new file.
6. Upload sketch to Esp-8266

### rgb_stripe_mqtt_mode:
###### Topic:
**light/id/mode**
###### Value:
A Json String like:
``"{\"mode\": \"static\", \"color\": {\"r\": 25, \"g\": 25, \"b\": 25}}"``
``"{\"mode\": \"blink\", \"color\": {\"r\": 25, \"g\": 10, \"b\": 50}, \"interval\": 400}"``
``"{\"mode\": \"shuffle\", \"interval\": 80}"``
``"{\"mode\": \"multicolor\", \"color\": [{\"r\": 25, \"g\": 10, \"b\": 50},{\"r\": 255, \"g\": 0, \"b\": 1}], \"interval\": 200}"``

mode: static/blink/shuffle/multicolor/fade
color: Json Object for static, blink, fade. Array of Objects for multicolor
interval: int, needed for blink, shuffle, multicolor, fade 

**Mode FADE currently not working!**


ToDo:

#### Sequence:

###### Button click
When Button click -> 
mqtt -> 
Springboot -> 
Looks in Database how many devices are Active ->
sends on / off over mqtt ->
lights on / off

---

###### Motion detected:
Same as Button + 5 min till another request to turn off.
--> Count down. If motion gets detected -> start again at 5 min

---

###### Turn on / off all lights:
Http request ->
Springboot ->
looks in database for all lights + programmable_lights ->
mqtt ->
turns off

---

###### Turn on / off one light:
Http request ->
Springboot ->
looks in database for the light or programmable_light ->
mqtt ->
turns off

---

###### Change Color of all:
Http request ->
Springboot ->
looks in database for all lights + programmable_lights ->
mqtt ->
changes color

---

###### Change Light Animation for all
Http request ->
Springboot ->
Look in database for all programmable_lights ->
mqtt ->
changes animation

---

###### Change Light Animation for one Light
Http request ->
Springboot ->
Look in Database for this light ->
mqtt ->
changes animation

---

###### Temperature Sensor:
5 min ->
Temp sensor program sends data ->
springboot -> 
database

---

###### Remote and Traffic light:
mqtt ->
central program listening for mqtt gets called ->
calls function 

---
