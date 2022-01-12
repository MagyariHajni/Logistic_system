package sci.java.logistic_system.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sci.java.logistic_system.domain.DeliveryOrderEntity;
import sci.java.logistic_system.domain.OrderStatus;
import sci.java.logistic_system.domain.OrderStatusEntity;
import sci.java.logistic_system.domain.repository.DeliveryOrderRepository;
import sci.java.logistic_system.domain.repository.DestinationRepository;
import sci.java.logistic_system.domain.repository.OrderStatusRepository;

import java.io.BufferedReader;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void modifyOrderDetails(DeliveryOrderEntity order, LocalDateTime date){
        modifyOrderDetails(order,date,"");
    }

    public void modifyOrderDetails(DeliveryOrderEntity order, LocalDateTime date, String deletedDestinationName) {
        order.setLastUpDated(LocalDateTime.of(date.toLocalDate(), LocalTime.now()));
        DeliveryOrderEntity orderPreviousData = deliveryOrderRepository.findById(order.getId()).get();

        if (order.getOrderDestination() == null) {
            if (Objects.equals(orderPreviousData.getDestinationComment(), "")) {
                order.setDestinationComment("Initial destination: " + deletedDestinationName + ", "
                        + LocalDateTime.of(date.toLocalDate(), LocalTime.now()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                        + " Destination not available anymore");
            } else {
                order.setDestinationComment(orderPreviousData.getDestinationComment() + ", "
                        + LocalDateTime.of(date.toLocalDate(), LocalTime.now()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                        + " Destination not available anymore");
            }
            order.setOrderStatus(OrderStatus.CANCELED);
            orderStatusRepository.addOrderStatus(order.getId(), order.getOrderStatus(), order.getLastUpDated());
        } else {
            String initialComment = "Initial destination: " + orderPreviousData.getOrderDestination().getDestinationName();
            if (!Objects.equals(order.getOrderDestination(), orderPreviousData.getOrderDestination())) {
                if (Objects.equals(orderPreviousData.getDestinationComment(), "")) {
                    order.setDestinationComment(initialComment + ", "
                            + LocalDateTime.of(date.toLocalDate(), LocalTime.now()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                            + " Changed destination: "
                            + order.getOrderDestination().getDestinationName());
                } else {
                    order.setDestinationComment(orderPreviousData.getDestinationComment() + ", "
                            + LocalDateTime.of(date.toLocalDate(), LocalTime.now()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                            + " Changed destination: " + order.getOrderDestination().getDestinationName());
                }
            }

            if ((order.getOrderStatus() == orderPreviousData.getOrderStatus())) {
                OrderStatusEntity foundStatus = orderStatusRepository.findById(order.getId()).get();
                foundStatus.setOrderStatusDate(order.getLastUpDated());
                orderStatusRepository.save(foundStatus);
            } else {
                orderStatusRepository.addOrderStatus(order.getId(), order.getOrderStatus(), order.getLastUpDated());
            }

            if (Objects.equals(order.getDestinationComment(), "")) {
                order.setDestinationComment(orderPreviousData.getDestinationComment());
            }
        }

        deliveryOrderRepository.save(order);
    }

    public void cancelSelectedOrders(List<DeliveryOrderEntity> ordersToCancel, LocalDateTime date) {
        for (DeliveryOrderEntity order : ordersToCancel) {
            if (!order.getOrderStatus().equals(OrderStatus.DELIVERED)
                    && !order.getOrderStatus().equals(OrderStatus.CANCELED)) {
                order.setOrderStatus(OrderStatus.CANCELED);
                order.setLastUpDated(LocalDateTime.of(date.toLocalDate(), LocalTime.now()));
                deliveryOrderRepository.save(order);
                OrderStatusEntity orderStatusEntity = new OrderStatusEntity();
                orderStatusEntity.setOrderId(order.getId());
                orderStatusEntity.setOrderStatus(OrderStatus.CANCELED);
                orderStatusEntity.setOrderStatusDate(LocalDateTime.of(date.toLocalDate(), LocalTime.now()));
                orderStatusRepository.save(orderStatusEntity);
            }
        }
    }


}


