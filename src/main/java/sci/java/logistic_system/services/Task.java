package sci.java.logistic_system.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import sci.java.logistic_system.domain.DeliveryOrderEntity;
import sci.java.logistic_system.domain.DestinationEntity;
import sci.java.logistic_system.domain.OrderStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public class Task extends AbstractJpaDaoService implements Runnable {
    private List<DeliveryOrderEntity> ordersToDeliver;
    private DestinationEntity destination;
    private String deliveryDriverNumber;
    private GlobalData globalData;
    private DeliveryOrderService deliveryOrderService;
    private LocalDateTime date;

    public Task(List<DeliveryOrderEntity> ordersToDeliver, DestinationEntity destination, GlobalData globalData, DeliveryOrderService deliveryOrderService, LocalDateTime date) {
        this.ordersToDeliver = ordersToDeliver;
        this.destination = destination;
        this.globalData = globalData;
        this.deliveryOrderService = deliveryOrderService;
        this.date = date;
    }

    @Override
    public void run() {
        deliveryDriverNumber = Thread.currentThread().getName().substring(Thread.currentThread().getName().length() - 1);

        System.out.println("Delivery driver nr " + deliveryDriverNumber + " started deliveries for " + destination.getDestinationName());
        try {
            Thread.sleep(1000L * destination.getDistance());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Delivery driver nr " + deliveryDriverNumber + " completed " + ordersToDeliver.size() + " deliveries for " + destination.getDestinationName());
        deliveryOrderService.updateGlobalData(ordersToDeliver,date);
    }

}
