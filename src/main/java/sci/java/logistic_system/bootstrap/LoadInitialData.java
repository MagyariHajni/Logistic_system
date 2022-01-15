package sci.java.logistic_system.bootstrap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import sci.java.logistic_system.services.DeliveryOrderService;
import sci.java.logistic_system.services.DestinationService;


@Component
public class LoadInitialData implements ApplicationListener<ContextRefreshedEvent> {

    private DestinationService destinationService;
    private DeliveryOrderService deliveryOrderService;

    @Autowired
    public void setDestinationService(DestinationService destinationService) {
        this.destinationService = destinationService;
    }

    @Autowired
    public void setDeliveryOrderService(DeliveryOrderService deliveryOrderService) {
        this.deliveryOrderService = deliveryOrderService;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        destinationService.loadDestinations();
        deliveryOrderService.loadInitialOrders();
    }

}

