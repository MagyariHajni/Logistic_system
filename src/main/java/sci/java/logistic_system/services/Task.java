package sci.java.logistic_system.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sci.java.logistic_system.domain.DeliveryOrderEntity;
import sci.java.logistic_system.domain.DestinationEntity;

import java.time.LocalDateTime;
import java.util.List;


public class Task implements Runnable {
    private List<DeliveryOrderEntity> ordersToDeliver;
    private DestinationEntity destination;
    private String deliveryDriverNumber;
    private DeliveryOrderService deliveryOrderService;
    private LocalDateTime date;

    public static Logger logger = LoggerFactory.getLogger(GlobalData.class);

    public Task(List<DeliveryOrderEntity> ordersToDeliver, DestinationEntity destination, DeliveryOrderService deliveryOrderService, LocalDateTime date) {
        this.ordersToDeliver = ordersToDeliver;
        this.destination = destination;
        this.deliveryOrderService = deliveryOrderService;
        this.date = date;
    }

    @Override
    public void run() {
        deliveryDriverNumber = Thread.currentThread().getName().substring(Thread.currentThread().getName().length() - 1);

        logger.info("Delivery driver nr " + deliveryDriverNumber
                + " started deliveries for " + destination.getDestinationName()
                + ", distance " + destination.getDistance());

        try {
            Thread.sleep(1000L * destination.getDistance());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logger.info("Delivery driver nr " + deliveryDriverNumber
                + " completed " + + ordersToDeliver.size()
                + " deliveries for " + destination.getDestinationName());

        deliveryOrderService.updateGlobalData(ordersToDeliver,date);
    }

}
