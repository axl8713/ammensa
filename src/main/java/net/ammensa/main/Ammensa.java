package net.ammensa.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"net.ammensa"})
@EnableScheduling
public class Ammensa {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Ammensa.class, args);
    }
}