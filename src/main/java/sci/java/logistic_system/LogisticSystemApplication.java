package sci.java.logistic_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;
import java.util.logging.Logger;

@SpringBootApplication
@EnableAsync
public class LogisticSystemApplication {
    public static void main(String[] args) {

        SpringApplication.run(LogisticSystemApplication.class, args);
    }

}
