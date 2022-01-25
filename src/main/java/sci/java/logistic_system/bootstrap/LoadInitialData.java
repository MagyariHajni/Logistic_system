package sci.java.logistic_system.bootstrap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import sci.java.logistic_system.services.DeliveryOrderService;
import sci.java.logistic_system.services.DestinationService;
import sci.java.logistic_system.services.GlobalData;


@Component
public class LoadInitialData implements ApplicationListener<ContextRefreshedEvent> {

    private DestinationService destinationService;
    private DeliveryOrderService deliveryOrderService;
    private GlobalData globalData;

    @Autowired
    public void setGlobalData(GlobalData globalData) {
        this.globalData = globalData;
    }

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
        globalData.setCurrentViewOrderList(deliveryOrderService.loadInitialOrders());
        destinationService.setDestinationRepository(destinationService.getDestinationRepository());
    }

}

