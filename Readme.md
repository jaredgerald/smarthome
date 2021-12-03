# Smarthome project:

## Get Started with ESP-82666 C++ Programs:
    1. Open Arduino IDE.
    2. Go to "Sketch", "Add file...".
    3. Select the localconfig.h file.
    4. Now a new File will be created in the Project this folder.
    5. Edit the new file.
    6. Upload sketch to Esp-8266



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
