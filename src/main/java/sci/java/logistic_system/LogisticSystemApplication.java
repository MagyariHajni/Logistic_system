package sci.java.logistic_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class LogisticSystemApplication {
    public static void main(String[] args) {

        SpringApplication.run(LogisticSystemApplication.class, args);
    }

}
