package sci.java.logistic_system.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sci.java.logistic_system.domain.DeliveryOrderEntity;
import sci.java.logistic_system.domain.OrderStatus;
import sci.java.logistic_system.domain.OrderStatusEntity;
import sci.java.logistic_system.domain.SelectedDeliveryOrders;
import sci.java.logistic_system.domain.repository.DeliveryOrderRepository;
import sci.java.logistic_system.domain.repository.DestinationRepository;
import sci.java.logistic_system.domain.repository.OrderStatusRepository;

import java.io.BufferedReader;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

@Service
public class DeliveryOrderService extends AbstractJpaDaoService {

    private DeliveryOrderRepository deliveryOrderRepository;
    private DestinationRepository destinationRepository;
    private OrderStatusRepository orderStatusRepository;


    @Autowired
    public void setDeliveryOrderRepository(DeliveryOrderRepository deliveryOrderRepository) {
        this.deliveryOrderRepository = deliveryOrderRepository;
    }

    @Autowired
    public void setDestinationRepository(DestinationRepository destinationRepository) {
        this.destinationRepository = destinationRepository;
    }

    @Autowired
    public void setOrderStatusRepository(OrderStatusRepository orderStatusRepository) {
        this.orderStatusRepository = orderStatusRepository;
    }


    public DeliveryOrderRepository getDeliveryOrderRepository() {
        return deliveryOrderRepository;
    }

    public void loadInitialOrders() {
        Path fileIn = new File("src/main/resources/orders.csv").toPath();

        try (BufferedReader reader = Files.newBufferedReader(fileIn)) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] inputData = line.split(",");
                String[] deliveryDate = inputData[1].split("-");

                DeliveryOrderEntity order = new DeliveryOrderEntity();

                order.setOrderDestination(destinationRepository.findDestinationEntityByDestinationName(inputData[0]));
                order.setDeliveryDate(LocalDateTime.of(
                        Integer.parseInt(deliveryDate[2]),
                        Integer.parseInt(deliveryDate[1]),
                        Integer.parseInt(deliveryDate[0]),
                        8, 0));
                order.setOrderStatus(OrderStatus.NEW);
                order.setLastUpDated(LocalDateTime.of(2021, 12, 15, 8, 0));

                deliveryOrderRepository.save(order);

                OrderStatusEntity orderStatusEntity = new OrderStatusEntity();
                orderStatusEntity.setOrderId(order.getId());
                orderStatusEntity.setOrderStatus(OrderStatus.NEW);
                orderStatusEntity.setOrderStatusDate(LocalDateTime.of(2021, 12, 15, 8, 0));
                orderStatusRepository.save(orderStatusEntity);
            }

//            OrderStatusEntity ose1 = new OrderStatusEntity();
//            ose1.setOrderId(1);
//            ose1.setOrderStatus(OrderStatus.DELIVERING);
//            ose1.setOrderStatusDate(LocalDateTime.of(2021, 12, 16, 8, 0));
//            orderStatusRepository.save(ose1);
//            OrderStatusEntity ose2 = new OrderStatusEntity();
//            ose2.setOrderId(1);
//            ose2.setOrderStatus(OrderStatus.DELIVERED);
//            ose2.setOrderStatusDate(LocalDateTime.of(2021, 12, 16, 12, 0));
//            orderStatusRepository.save(ose2);
//            OrderStatusEntity ose3 = new OrderStatusEntity();
//            ose3.setOrderId(5);
//            ose3.setOrderStatus(OrderStatus.DELIVERING);
//            ose3.setOrderStatusDate(LocalDateTime.of(2021, 12, 16, 17, 0));
//            orderStatusRepository.save(ose3);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}


