package sci.java.logistic_system.bootstrap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import sci.java.logistic_system.domain.DeliveryOrderEntity;
import sci.java.logistic_system.domain.DestinationEntity;
import sci.java.logistic_system.domain.OrderStatus;
import sci.java.logistic_system.services.DeliveryOrderService;
import sci.java.logistic_system.services.DestinationService;


import java.io.BufferedReader;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

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

//    private void loadInitialOrders() {
//        Path fileIn = new File("src/main/resources/orders.csv").toPath();
//
//        try (BufferedReader reader = Files.newBufferedReader(fileIn)) {
//            String line;
//
//            while ((line = reader.readLine()) != null) {
//                String[] inputData = line.split(",");
//                String[] deliveryDate = inputData[1].split("-");
//
//                DeliveryOrderEntity order = new DeliveryOrderEntity();
//
//                order.setOrderDestination(destinationRepository.getByName(inputData[0]));
//                order.setDeliveryDate(LocalDateTime.of(
//                        Integer.parseInt(deliveryDate[2]),
//                        Integer.parseInt(deliveryDate[1]),
//                        Integer.parseInt(deliveryDate[0]),
//                        8, 0));
//                order.setOrderStatus(OrderStatus.NEW);
//                order.setLastUpDated(LocalDateTime.of(2021, 12, 15, 8, 0));
//
//                orderRepository.saveOrUpdate(order);
//                orderStatusRepository.addOrderStatusUpdate(order.getId(),
//                        LocalDateTime.of(2021, 12, 15, 8, 0),
//                        OrderStatus.NEW);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

//        for (DeliveryOrder order : orderRepository.listAll()) {
//            System.out.println(order.getId() + " "
//                    + order.getOrderDestination().getDestinationName() + " "
//                    + order.getDeliveryDate());
//        }

//        for(Integer ids: orderStatusRepository.listMap().keySet()){
//            System.out.println("****");
//            System.out.println("Order with id: " + ids);
//           orderStatusRepository.getById(ids).forEach(pair-> System.out.println(pair.getFirst() + " " + pair.getSecond()));
//        }

//    }
}

