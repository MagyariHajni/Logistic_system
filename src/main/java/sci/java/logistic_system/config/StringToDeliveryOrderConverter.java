package sci.java.logistic_system.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import sci.java.logistic_system.domain.DeliveryOrderEntity;
import sci.java.logistic_system.domain.repository.DeliveryOrderRepository;
import sci.java.logistic_system.services.DeliveryOrderService;

import java.util.List;

@Component
public class StringToDeliveryOrderConverter implements Converter<String, DeliveryOrderEntity> {
    DeliveryOrderRepository deliveryOrderRepository;

    @Autowired
    public void setDeliveryOrderRepository(DeliveryOrderRepository deliveryOrderRepository) {
        this.deliveryOrderRepository = deliveryOrderRepository;
    }

    @Override
    public DeliveryOrderEntity convert(String source) {
        Integer id = Integer.valueOf(source);
        return deliveryOrderRepository.findById(id).get();
    }
}
