package org.example.pionerpixeltest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PionerPixelTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(PionerPixelTestApplication.class, args);
    }

}
