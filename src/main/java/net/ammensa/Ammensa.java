package net.ammensa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"net.ammensa"})
@EnableScheduling
public class Ammensa {

    public static void main(String[] args) {
        SpringApplication.run(Ammensa.class, args);
    }
}