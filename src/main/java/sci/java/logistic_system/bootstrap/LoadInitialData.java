package sci.java.logistic_system.bootstrap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import sci.java.logistic_system.domain.DeliveryOrder;
import sci.java.logistic_system.domain.Destination;
import sci.java.logistic_system.domain.OrderStatus;
import sci.java.logistic_system.services.DestinationRepository;
import sci.java.logistic_system.services.OrderRepository;
import sci.java.logistic_system.services.OrderStatusRepository;

import java.io.BufferedReader;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class LoadInitialData implements ApplicationListener<ContextRefreshedEvent> {

    private OrderRepository orderRepository;
    private DestinationRepository destinationRepository;
    private OrderStatusRepository orderStatusRepository;


    @Autowired
    public void setOrderRepository(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Autowired
    public void setDestinationRepository(DestinationRepository destinationRepository) {
        this.destinationRepository = destinationRepository;
    }

    @Autowired
    public void setOrderStatusRepository(OrderStatusRepository orderStatusRepository) {
        this.orderStatusRepository = orderStatusRepository;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        loadDestinations();
        loadInitialOrders();

    }

    private void loadDestinations() {
        Path fileIn = new File("src/main/resources/destinations.csv").toPath();
//        System.out.println(fileIn.toAbsolutePath());
        try (BufferedReader reader = Files.newBufferedReader(fileIn)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] inputData = line.split(",");
                Destination destination = new Destination();
                destination.setDestinationName(inputData[0]);
                destination.setDistance(Integer.parseInt(inputData[1]));
                destinationRepository.saveOrUpdate(destination);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

//        for (Destination destination : destinationRepository.listAll()) {
//            System.out.println(destination.getId() + " "
//                    + destination.getDestinationName() + " "
//                    + destination.getDistance());
//        }

    }


    private void loadInitialOrders() {
        Path fileIn = new File("src/main/resources/orders.csv").toPath();

        try (BufferedReader reader = Files.newBufferedReader(fileIn)) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] inputData = line.split(",");
                String[] deliveryDate = inputData[1].split("-");

                DeliveryOrder order = new DeliveryOrder();

                order.setOrderDestination(destinationRepository.getByName(inputData[0]));
                order.setDeliveryDate(LocalDateTime.of(
                        Integer.parseInt(deliveryDate[2]),
                        Integer.parseInt(deliveryDate[1]),
                        Integer.parseInt(deliveryDate[0]),
                        8, 0));
                order.setOrderStatus(OrderStatus.NEW);
                order.setLastUpDated(LocalDateTime.of(2021, 12, 15, 8, 0));

                orderRepository.saveOrUpdate(order);
                orderStatusRepository.addOrderStatusUpdate(order.getId(),
                        LocalDateTime.of(2021, 12, 15, 8, 0),
                        OrderStatus.NEW);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

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

    }
}

