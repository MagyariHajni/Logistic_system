package sci.java.logistic_system.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sci.java.logistic_system.domain.DeliveryOrderEntity;
import sci.java.logistic_system.domain.OrderStatus;
import sci.java.logistic_system.domain.repository.DeliveryOrderRepository;
import sci.java.logistic_system.domain.repository.DestinationRepository;

import java.io.BufferedReader;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

@Service
public class DeliveryOrderService {
    private DeliveryOrderRepository deliveryOrderRepository;
    private DestinationRepository destinationRepository;


    @Autowired
    public void setDeliveryOrderRepository(DeliveryOrderRepository deliveryOrderRepository) {
        this.deliveryOrderRepository = deliveryOrderRepository;
    }

    @Autowired
    public void setDestinationRepository(DestinationRepository destinationRepository) {
        this.destinationRepository = destinationRepository;
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
//                orderStatusRepository.addOrderStatusUpdate(order.getId(),
//                        LocalDateTime.of(2021, 12, 15, 8, 0),
//                        OrderStatus.NEW);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}


