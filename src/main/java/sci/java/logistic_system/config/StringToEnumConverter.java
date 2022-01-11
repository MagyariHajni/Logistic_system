package sci.java.logistic_system.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.stereotype.Component;
import sci.java.logistic_system.domain.OrderStatus;

import java.lang.annotation.Annotation;
import java.util.Arrays;

@Component
public class StringToEnumConverter implements Converter<String, OrderStatus> {

    @Override
    public OrderStatus convert(String source) {
        return OrderStatus.valueOf(source);
    }
}
