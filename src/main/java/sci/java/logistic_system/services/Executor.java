package sci.java.logistic_system.services;

import org.springframework.stereotype.Component;
import sci.java.logistic_system.domain.DeliveryOrderEntity;
import sci.java.logistic_system.domain.DestinationEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
public class Executor {

    private final ThreadPoolExecutor executor = new ThreadPoolExecutor(4, 4, 10, TimeUnit.SECONDS, new ArrayBlockingQueue<>(100));

    public void startShipping(Map<DestinationEntity, List<DeliveryOrderEntity>> shippingMap, LocalDateTime date, DeliveryOrderService deliveryOrderService) {
        for (DestinationEntity destination : shippingMap.keySet()) {
//            System.out.println(executor.getQueue().size());
            executor.execute(new Task(shippingMap.get(destination), destination, deliveryOrderService, date));
        }
    }

}
