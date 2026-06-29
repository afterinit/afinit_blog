package top.afinit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"top.afinit"})
public class AfinitStartApplication {

    public static void main(String[] args) {
        SpringApplication.run(AfinitStartApplication.class, args);
    }

}
