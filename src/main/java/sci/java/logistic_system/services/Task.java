package sci.java.logistic_system.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import sci.java.logistic_system.domain.DeliveryOrderEntity;
import sci.java.logistic_system.domain.DestinationEntity;
import sci.java.logistic_system.domain.OrderStatus;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public class Task extends AbstractJpaDaoService implements Runnable {
    private List<DeliveryOrderEntity> ordersToDeliver;
    private DestinationEntity destination;
    private String deliveryDriverNumber;
    private GlobalData globalData;
    private DeliveryOrderService deliveryOrderService;

    public Task(List<DeliveryOrderEntity> ordersToDeliver, DestinationEntity destination, GlobalData globalData, DeliveryOrderService deliveryOrderService) {
        this.ordersToDeliver = ordersToDeliver;
        this.destination = destination;
        this.globalData = globalData;
        this.deliveryOrderService = deliveryOrderService;
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
        updateGlobalData();
    }

    @Async
    private void updateGlobalData() {
        for (DeliveryOrderEntity order : ordersToDeliver) {
            order = deliveryOrderService.getDeliveryOrderRepository().findById(order.getId()).get();
            if (order.getOrderStatus() != OrderStatus.CANCELED) {
                order.setOrderStatus(OrderStatus.DELIVERED);
                deliveryOrderService.modifyOrderDetails(order, globalData.getCurrentDate());
                if (!globalData.getProfitByDayMap().containsKey(globalData.getCurrentDate())) {
                    globalData.getProfitByDayMap().put(globalData.getCurrentDate(), new AtomicInteger());
                }
                globalData.getProfitByDayMap().get(globalData.getCurrentDate()).addAndGet(order.getOrderDestination().getDistance());
            }
        }
    }
}
