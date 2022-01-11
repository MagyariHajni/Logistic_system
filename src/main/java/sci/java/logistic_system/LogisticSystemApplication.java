package sci.java.logistic_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@SpringBootApplication
public class LogisticSystemApplication extends WebMvcConfigurerAdapter {

	public static void main(String[] args) {
		SpringApplication.run(LogisticSystemApplication.class, args);
	}

}
