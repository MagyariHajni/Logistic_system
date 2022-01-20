package sci.java.logistic_system.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class StringToDateConverter implements Converter<String, LocalDateTime> {

    @Override
    public LocalDateTime convert(String source) {
        String[] inputDateFirst = source.split(" ");
        String[] inputDate = inputDateFirst[0].split("-");

        return LocalDateTime.of(Integer.parseInt(inputDate[2]),
                Integer.parseInt(inputDate[1]),
                Integer.parseInt(inputDate[0]),
                8, 0);
    }
}
