package de.sedriz.smarthome.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LightController {
    @GetMapping("/light")
    public String test() throws IOException {
        String[] cmd = {"/bin/bash","-c","sudo python3 /home/pi/Project/led_stripe_programms/led_static_color.py -c 0"};
        Process pb = Runtime.getRuntime().exec(cmd);

        return "test";
    }
}
