package sci.java.logistic_system.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import sci.java.logistic_system.domain.DeliveryOrderEntity;
import sci.java.logistic_system.domain.DestinationEntity;
import sci.java.logistic_system.domain.repository.DeliveryOrderRepository;
import sci.java.logistic_system.domain.repository.DestinationRepository;

@Component
public class StringToDestinationConverter implements Converter<String, DestinationEntity> {
    DestinationRepository destinationRepository;

    @Autowired
    public void setDestinationRepository(DestinationRepository destinationRepository) {
        this.destinationRepository = destinationRepository;
    }

    @Override
    public DestinationEntity convert(String source) {
        return destinationRepository.findDestinationEntityByDestinationName(source);
    }
}
