package sci.java.logistic_system.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import sci.java.logistic_system.domain.DeliveryOrderEntity;
import sci.java.logistic_system.domain.DestinationEntity;
import sci.java.logistic_system.domain.OrderStatus;
import sci.java.logistic_system.domain.repository.DeliveryOrderRepository;
import sci.java.logistic_system.domain.repository.DestinationRepository;
import sci.java.logistic_system.domain.repository.OrderStatusRepository;

import java.io.BufferedReader;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class DeliveryOrderService extends AbstractJpaDaoService {

    private DeliveryOrderRepository deliveryOrderRepository;
    private DestinationRepository destinationRepository;
    private OrderStatusRepository orderStatusRepository;
    private GlobalData globalData;

    public static Logger logger = LoggerFactory.getLogger(GlobalData.class);

    @Autowired
    public void setGlobalData(GlobalData globalData) {
        this.globalData = globalData;
    }

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

    public List<DeliveryOrderEntity> loadInitialOrders() {
        Path fileIn = new File("src/main/resources/orders.csv").toPath();
        int i = 0;
        try (BufferedReader reader = Files.newBufferedReader(fileIn)) {
            String line;
            while ((line = reader.readLine()) != null) {
                i++;
                convertAndSaveOrder(line, LocalDateTime.of(2021, 12, 15, 8, 0));
            }
        } catch (Exception e) {
            logger.warn("Error found in initial data at line: " + i);
            e.printStackTrace();
        }
        return (List<DeliveryOrderEntity>) getDeliveryOrderRepository().findAll();
    }

    public void deleteOrderDestination(DeliveryOrderEntity order, LocalDateTime date) {
        String deletedDestinationName = order.getOrderDestination().getDestinationName();

        Optional<DeliveryOrderEntity> orderPreviousData = deliveryOrderRepository.findById(order.getId());
        if (orderPreviousData.isPresent()) {
            order.setOrderDestination(null);
            modifyOrderDetails(order, orderPreviousData.get(), date, deletedDestinationName);
        } else {
            logger.warn("Order number: " + order.getId() + " was deleted from database");
        }
    }

    public void modifyOrderDetails(DeliveryOrderEntity order, LocalDateTime date) {

        Optional<DeliveryOrderEntity> orderPreviousData = deliveryOrderRepository.findById(order.getId());
        if (orderPreviousData.isPresent()) {
            modifyOrderDetails(order, orderPreviousData.get(), date, "");
        } else {
            logger.warn("Order number: " + order.getId() + " was deleted from database");
        }

    }

    public void modifyOrderDetails(DeliveryOrderEntity order, DeliveryOrderEntity orderPreviousData, LocalDateTime date, String deletedDestinationName) {

        if (order.getOrderDestination() == null) {
            modifyOrderWithDeletedDestination(order, orderPreviousData, date, deletedDestinationName);
        } else {
            modifyOrderWithChangedDestination(order, orderPreviousData, date);
            modifyOrderStatusAndComment(order, orderPreviousData, date);
        }
        deliveryOrderRepository.save(order);
    }

    public void modifyOrderWithDeletedDestination(DeliveryOrderEntity order, DeliveryOrderEntity orderPreviousData, LocalDateTime date, String deletedDestinationName) {
        order.setDestinationComment(orderPreviousData.getDestinationComment() + " // "
                + LocalDateTime.of(date.toLocalDate(), LocalTime.now()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) +
                " Initial destination: " + deletedDestinationName + " - not available anymore");

        order.setLastUpDated(LocalDateTime.of(date.toLocalDate(), LocalTime.now()));

        if (order.getOrderStatus() != OrderStatus.DELIVERED) {
            order.setOrderStatus(OrderStatus.CANCELED);
            orderStatusRepository.addOrderStatus(order.getId(), order.getOrderStatus(), order.getLastUpDated());
        }
    }

    public void modifyOrderWithChangedDestination(DeliveryOrderEntity order, DeliveryOrderEntity orderPreviousData, LocalDateTime date) {
        if (!Objects.equals(order.getOrderDestination(), orderPreviousData.getOrderDestination())) {
            order.setDestinationComment(orderPreviousData.getDestinationComment() + " // "
                    + LocalDateTime.of(date.toLocalDate(), LocalTime.now()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                    + " Changed destination: from " + orderPreviousData.getOrderDestination().getDestinationName() + " to " +
                    order.getOrderDestination().getDestinationName());
        }
    }

    public void modifyOrderStatusAndComment(DeliveryOrderEntity order, DeliveryOrderEntity orderPreviousData, LocalDateTime date) {
        if (Objects.isNull(order.getOrderStatus())) {
            order.setOrderStatus(OrderStatus.NEW);
        }

        if (!Objects.equals(order.getDeliveryDate(), orderPreviousData.getDeliveryDate())) {
            order.setDestinationComment(orderPreviousData.getDestinationComment() + " // "
                    + LocalDateTime.of(date.toLocalDate(), LocalTime.now()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                    + " Changed delivery date: from " + orderPreviousData.getDeliveryDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + " to "
                    + order.getDeliveryDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        }

        order.setLastUpDated(LocalDateTime.of(date.toLocalDate(), LocalTime.now()));
        orderStatusRepository.addOrderStatus(order.getId(), order.getOrderStatus(), order.getLastUpDated());

        if (Objects.equals(order.getDestinationComment(), "")) {
            order.setDestinationComment(orderPreviousData.getDestinationComment());
        }
    }

    public void cancelSelectedOrders(List<DeliveryOrderEntity> ordersToCancel, LocalDateTime date) {
        for (DeliveryOrderEntity order : ordersToCancel) {
            if (order.getOrderStatus() != OrderStatus.DELIVERED) {
                order.setOrderStatus(OrderStatus.CANCELED);
                order.setLastUpDated(LocalDateTime.of(date.toLocalDate(), LocalTime.now()));
                deliveryOrderRepository.save(order);
                orderStatusRepository.addOrderStatus(order.getId(), OrderStatus.CANCELED, LocalDateTime.of(date.toLocalDate(), LocalTime.now()));
            }
        }
    }

    public DeliveryOrderEntity convertAndSaveOrder(String input, LocalDateTime date) {
        String[] inputData = input.split(",");
        String[] deliveryDate = inputData[1].split("-");
        DeliveryOrderEntity order = new DeliveryOrderEntity();
        order.setOrderDestination(destinationRepository.findDestinationEntityByDestinationName(inputData[0]));
        order.setDeliveryDate(LocalDateTime.of(
                Integer.parseInt(deliveryDate[2]),
                Integer.parseInt(deliveryDate[1]),
                Integer.parseInt(deliveryDate[0]),
                8, 0));
        order.setOrderStatus(OrderStatus.NEW);
        order.setLastUpDated(date);
        deliveryOrderRepository.save(order);
        orderStatusRepository.addOrderStatus(order.getId(), OrderStatus.NEW, date);
        return order;
    }

    public List<DeliveryOrderEntity> filterOrdersByDateAndDestination
            (LocalDateTime date, String destinationName, LocalDateTime globalDate) {

        List<DeliveryOrderEntity> listOfOrdersForToday = (List<DeliveryOrderEntity>) deliveryOrderRepository.findAll();
        LocalDate dateDate;
        List<DestinationEntity> destinationsList = (List<DestinationEntity>) destinationRepository.findAll();
        boolean foundDestination = destinationsList.stream().anyMatch(dest -> dest.getDestinationName().equals(destinationName));

        if (Objects.isNull(date)) {
            dateDate = globalDate.toLocalDate();
        } else {
            dateDate = date.toLocalDate();
        }

        if (Objects.isNull(destinationName) || destinationName.isEmpty()) {
            return listOfOrdersForToday.stream()
                    .filter(order -> order.getDeliveryDate().toLocalDate().equals(dateDate))
                    .collect(Collectors.toList());
        } else {
            if (foundDestination) {
                return listOfOrdersForToday.stream()
                        .filter(order -> order.getDeliveryDate().toLocalDate().equals(dateDate))
                        .filter(order -> order.getOrderDestination().getDestinationName().equals(destinationName))
                        .collect(Collectors.toList());
            } else {
                return new ArrayList<>();
            }
        }
    }

    public List<DeliveryOrderEntity> filterOrdersByDate(List<DeliveryOrderEntity> orderListToFilter, LocalDateTime date) {
        if (!(Objects.isNull(date))) {
            LocalDate dateDate = date.toLocalDate();
            orderListToFilter = orderListToFilter.stream()
                    .filter(order -> order.getDeliveryDate().toLocalDate().equals(dateDate))
                    .collect(Collectors.toList());
        }
        return orderListToFilter;
    }

    public List<DeliveryOrderEntity> filterOrdersByDestination(List<DeliveryOrderEntity> orderListToFilter, String destinationName) {
        if (!(Objects.isNull(destinationName)) && !(destinationName.isEmpty())) {
            List<DestinationEntity> destinationsList = (List<DestinationEntity>) destinationRepository.findAll();
            boolean foundDestination = destinationsList.stream().anyMatch(dest -> dest.getDestinationName().equals(destinationName));
            if (foundDestination) {
                orderListToFilter = orderListToFilter.stream()
                        .filter(order -> order.getOrderDestination().getDestinationName().equals(destinationName))
                        .collect(Collectors.toList());
            } else {
                orderListToFilter = new ArrayList<>();
                logger.info("Destination: " + destinationName + " is not currently available");
            }
        }
        return orderListToFilter;
    }

    public List<DeliveryOrderEntity> filterOrdersByStatus(List<DeliveryOrderEntity> orderListToFilter, String statusName) {
        if (!(Objects.isNull(statusName)) && !(statusName.isEmpty())) {
            if (Arrays.stream(OrderStatus.values()).anyMatch(orderStatus -> orderStatus.name().equals(statusName.toUpperCase()))) {
                orderListToFilter = orderListToFilter.stream().filter(order -> order.getOrderStatus().name().equals(statusName.toUpperCase())).collect(Collectors.toList());
            } else {
                logger.info("Status: " + statusName + " is not a valid order status");
            }
        }
        return orderListToFilter;
    }

    public List<DeliveryOrderEntity> updateView(List<DeliveryOrderEntity> allOrders, List<DeliveryOrderEntity> listToCheck) {
        List<DeliveryOrderEntity> updatedList = new ArrayList<>();
        for (DeliveryOrderEntity order : listToCheck) {
            if (!allOrders.contains(order)) {
                Optional<DeliveryOrderEntity> foundOrder = deliveryOrderRepository.findById(order.getId());
                if (foundOrder.isPresent()) {
                    order = foundOrder.get();
                } else {
                    logger.warn("Order number: " + order.getId() + " was deleted from database");
                }
            }
            updatedList.add(order);
        }
        return updatedList;
    }

    public Map<DestinationEntity, List<DeliveryOrderEntity>> mapByDestination(List<DeliveryOrderEntity> listToMap) {
        Map<DestinationEntity, List<DeliveryOrderEntity>> mapByDestination = new HashMap<>();
        for (DeliveryOrderEntity order : listToMap) {
            if (!mapByDestination.containsKey(order.getOrderDestination())) {
                mapByDestination.put(order.getOrderDestination(), new ArrayList<>());
            }
            mapByDestination.get(order.getOrderDestination()).add(order);
        }
        return mapByDestination;
    }

    @Async
    public synchronized void updateGlobalData(String destinationName, List<DeliveryOrderEntity> ordersToDeliver, LocalDateTime date) {
        for (DeliveryOrderEntity order : ordersToDeliver) {
            Optional<DeliveryOrderEntity> foundOrder = deliveryOrderRepository.findById(order.getId());
            if (foundOrder.isPresent()) {
                order = foundOrder.get();
                if (order.getOrderStatus() != OrderStatus.CANCELED) {
                    order.setOrderStatus(OrderStatus.DELIVERED);
                    modifyOrderDetails(order, LocalDateTime.of(date.toLocalDate(), LocalTime.now()));
                    updateGlobalMaps(date, order);
                } else {
                    logger.info("Order number: " + order.getId() + " was canceled after deliveries started, no profit");
                }
            } else {
                logger.warn("Order number: " + order.getId() + " was deleted from database, no profit");
            }
        }

        logger.trace("Updated global data for deliveries to "
                + destinationName + " on " + date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
    }

    private void updateGlobalMaps(LocalDateTime date, DeliveryOrderEntity order) {
        if (!globalData.getProfitByDayMap().containsKey(date.toLocalDate())) {
            globalData.getProfitByDayMap().put(date.toLocalDate(), new AtomicInteger());
            globalData.getDeliveriesByDayMap().put(date.toLocalDate(), new AtomicInteger());
        }
        globalData.getProfitByDayMap().get(date.toLocalDate()).addAndGet(order.getOrderDestination().getDistance());
        globalData.getDeliveriesByDayMap().get(date.toLocalDate()).addAndGet(1);
    }

}



