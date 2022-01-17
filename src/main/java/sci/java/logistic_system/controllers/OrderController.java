package sci.java.logistic_system.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import sci.java.logistic_system.domain.*;
import sci.java.logistic_system.domain.repository.DeliveryOrderRepository;
import sci.java.logistic_system.domain.repository.DestinationRepository;
import sci.java.logistic_system.domain.repository.OrderStatusRepository;
import sci.java.logistic_system.services.DeliveryOrderService;
import sci.java.logistic_system.services.Executor;
import sci.java.logistic_system.services.GlobalData;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Controller
public class OrderController {
    private DeliveryOrderRepository deliveryOrderRepository;
    private DestinationRepository destinationRepository;
    private OrderStatusRepository orderStatusRepository;
    private GlobalData globalData;
    private DeliveryOrderService deliveryOrderService;
    private Executor executor;


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
    @Autowired
    public void setDeliveryOrderService(DeliveryOrderService deliveryOrderService) {
        this.deliveryOrderService = deliveryOrderService;
    }

    @Autowired
    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    @GetMapping(value = "order/")
    public String allOrders(Model model) {
        globalData.setCurrentViewOrderList((List<DeliveryOrderEntity>) deliveryOrderRepository.findAll());
        globalData.setCommonModelAttributes(model);
        return "orders/orders";
    }

    @GetMapping(value = "order/list")
    public String listOrders(@RequestParam(required = false) LocalDateTime date,
                             Model model) {
        if (!Objects.isNull(date)) {
            List<DeliveryOrderEntity> listOfOrdersForToday = (List<DeliveryOrderEntity>) deliveryOrderRepository.findAll();
            globalData.setCurrentViewOrderList(deliveryOrderService.filterOrdersByDate(listOfOrdersForToday, date));
        }

        globalData.updateAndSetCurrentView(model, globalData.getCurrentViewOrderList(),(List<DeliveryOrderEntity>) deliveryOrderRepository.findAll(),deliveryOrderService);
        return "orders/orders";
    }

    @GetMapping(value = {"order/status"})
    public String listByDateAndOrder(
            @RequestParam(required = false) LocalDateTime date,
            @RequestParam(required = false) String destination,
            Model model) {

        List<DeliveryOrderEntity> filteredOrdersList
                = deliveryOrderService.filterOrdersByDateAndDestination(date, destination, globalData.getCurrentDate());

        globalData.updateAndSetCurrentView(model,filteredOrdersList,(List<DeliveryOrderEntity>) deliveryOrderRepository.findAll(),deliveryOrderService);
        if (!Objects.isNull(destination)) {
            model.addAttribute("destinationtofind", destination);
        }
        return "orders/orders";
    }

    @GetMapping(value = {"order/filter"})
    public String sortByDateAndOrderAndDestination(
            @RequestParam(required = false) LocalDateTime date,
            @RequestParam(required = false) String destination,
            @RequestParam(required = false) String status,
            Model model) {
        List<DeliveryOrderEntity> filteredOrdersList = (List<DeliveryOrderEntity>) deliveryOrderRepository.findAll();

        if ((!Objects.isNull(date))) {
            filteredOrdersList = deliveryOrderService.filterOrdersByDate(filteredOrdersList, date);
        }
        if ((!Objects.isNull(destination))) {
            destination = destination.substring(0, 1).toUpperCase() + destination.substring(1).toLowerCase();
            filteredOrdersList = deliveryOrderService.filterOrdersByDestination(filteredOrdersList, destination);
        }
        if ((!Objects.isNull(status))) {
            filteredOrdersList = deliveryOrderService.filterOrdersByStatus(filteredOrdersList, status);
        }

        globalData.updateAndSetCurrentView(model,filteredOrdersList,(List<DeliveryOrderEntity>) deliveryOrderRepository.findAll(),deliveryOrderService);
        if (!Objects.isNull(destination)) {
            model.addAttribute("destinationtofind", destination);
        }
        return "orders/orders";
    }

    @GetMapping("order/{id}")
    public String getOrder(@PathVariable Integer id, Model model) {
        model.addAttribute("order",
                deliveryOrderRepository.findById(id).isPresent() ? deliveryOrderRepository.findById(id).get() : null);

        globalData.setCommonModelAttributes(model);
        model.addAttribute("orderstatuses", orderStatusRepository.findOrderStatusesById(id));
        return "orders/orderdetails";
    }

    @GetMapping("order/edit/{id}")
    public String editOrder(@PathVariable Integer id, Model model) {
        model.addAttribute("order", deliveryOrderRepository.findById(id).isPresent() ? deliveryOrderRepository.findById(id).get() : null);
        model.addAttribute("destinations", destinationRepository.getAvailableDestinations());
        globalData.setCommonModelAttributes(model);
        return "orders/orderform";
    }

    @PostMapping(value = "/order")
    public String updateOrder(@ModelAttribute("order") DeliveryOrderEntity order,Model model) {

        if (order.getDeliveryDate().isBefore(globalData.getCurrentDate())) {
//            TODO throw,console log, file log order couldn 't be modified, delivery date before current date
        } else if ((deliveryOrderRepository.findById(order.getId()).get().equals(order))) {
//            TODO throw,console log, file log nothing has changed
        } else {
            deliveryOrderService.modifyOrderDetails(order, globalData.getCurrentDate());
        }

        globalData.updateAndSetCurrentView(model,globalData.getCurrentViewOrderList(),(List<DeliveryOrderEntity>) deliveryOrderRepository.findAll(),deliveryOrderService);

        return "redirect:order/" + order.getId();
    }


    @RequestMapping(value = "/order/cancel", method = {RequestMethod.GET, RequestMethod.POST})
    public String cancelOrders(@RequestParam(required = false) List<String> orders,
                               @ModelAttribute("selectedlist") ThymeleafBindingObject selectedOrders, Model model) {

        List<DeliveryOrderEntity> ordersToCancel = new ArrayList<>();
        Optional<DeliveryOrderEntity> foundOrder;

        if (!Objects.isNull(orders) && !orders.isEmpty()) {
            for (String id : orders) {
                foundOrder = deliveryOrderRepository.findById(Integer.valueOf(id));
                if (foundOrder.isPresent()) {
                    if ((foundOrder.get().getOrderStatus() != OrderStatus.CANCELED) && (foundOrder.get().getOrderStatus() != OrderStatus.DELIVERED))
                        ordersToCancel.add(deliveryOrderRepository.findById(Integer.valueOf(id)).get());
                }
            }
        } else {
            if (!Objects.isNull(selectedOrders) && !Objects.isNull(selectedOrders.getListOfOrders())) {
                ordersToCancel = selectedOrders.getListOfOrders().stream()
                        .filter(order -> (order.getOrderStatus() != OrderStatus.CANCELED) && (order.getOrderStatus() != OrderStatus.DELIVERED))
                        .collect(Collectors.toList());
            }
        }
        deliveryOrderService.cancelSelectedOrders(ordersToCancel, globalData.getCurrentDate());
        model.addAttribute("canceledorders", ordersToCancel);

        globalData.updateAndSetCurrentView(model,globalData.getCurrentViewOrderList(),(List<DeliveryOrderEntity>) deliveryOrderRepository.findAll(),deliveryOrderService);
        return "orders/canceledorders";
    }

    @GetMapping("order/new")
    public String setNumberOfOrdersToAdd(Model model) {
        globalData.setCommonModelAttributes(model);
        model.addAttribute("numberofneworders", new ThymeleafBindingObject());
        return "orders/setnumberofneworders";
    }

    @PostMapping(value = "/order/addorder")
    public String setNumberOfOrdersToAdd(@ModelAttribute("numberofneworders") ThymeleafBindingObject number) {
        return "redirect:/order/add?number=" + number.getNumberOfOrders();
    }

    @GetMapping("order/add")
    public String addOrders(@RequestParam(required = false) Map<String, String> orders,
                            @RequestParam(required = false) Integer number, Model model) {

        globalData.setCommonModelAttributes(model);
        ThymeleafBindingObject thymeleafBindingObject = new ThymeleafBindingObject();

        if ((Objects.isNull(number)) || number == 0) {
            number = 1;
        }
        if (!Objects.isNull(orders)) {
            orders.remove("number");
        }

        if (!Objects.isNull(orders) && !orders.isEmpty()) {
            List<DeliveryOrderEntity> savedOrdersList = new ArrayList<>();
            DeliveryOrderEntity savedOrder;
            for (String destinationName : orders.keySet()) {
                String originalName = destinationName;
                destinationName = destinationName.substring(0, 1).toUpperCase() + destinationName.substring(1).toLowerCase();
                DestinationEntity destination = destinationRepository.findDestinationEntityByDestinationName(destinationName);

                if (!Objects.isNull(destination)) {
                    savedOrder = addAndSaveOrder(destination, orders.get(originalName));
                    if (!Objects.isNull(savedOrder)) {
                        savedOrdersList.add(savedOrder);
                    }
                } else {
//                    TODO log +file given destination is not available
                }
            }
            model.addAttribute("addedorders", savedOrdersList);
            return "orders/addedorders";
        } else {
            for (int i = 0; i < number; i++) {
                thymeleafBindingObject.addDeliveryOrderData(new DeliveryOrderData());
            }
            model.addAttribute("listofneworders", thymeleafBindingObject);
            model.addAttribute("destinations", destinationRepository.getAvailableDestinations());
            return "orders/addordersform";
        }
    }

    @PostMapping("/order/add")
    public String addOrders(@ModelAttribute("listofneworders") ThymeleafBindingObject listOfData, Model model) {

        List<DeliveryOrderEntity> savedOrdersList = new ArrayList<>();
        DeliveryOrderEntity savedOrder;
        for (DeliveryOrderData dataToConvert : listOfData.getListOfOrderData()) {
            if (!Objects.isNull(dataToConvert)) {
                savedOrder = addAndSaveOrder(dataToConvert.getDestination(), dataToConvert.getDeliveryDate());
                if (!Objects.isNull(savedOrder)) {
                    savedOrdersList.add(savedOrder);
                }
            }
        }
        model.addAttribute("addedorders", savedOrdersList);
        globalData.setCommonModelAttributes(model);
        return "orders/addedorders";
    }

    @GetMapping("/shipping/new-day")
    public String shippingToday(Model model) {
        List<DeliveryOrderEntity> shippingList = (List<DeliveryOrderEntity>) deliveryOrderRepository.findAll();

        shippingList = shippingList.stream()
                .filter(order -> order.getDeliveryDate().toLocalDate().equals(globalData.getCurrentDate().toLocalDate()))
                .filter(order -> order.getOrderStatus().equals(OrderStatus.NEW)).collect(Collectors.toList());

        shippingList.forEach(order -> order.setOrderStatus(OrderStatus.DELIVERING));
        shippingList.forEach(order -> deliveryOrderService.modifyOrderDetails(order, globalData.getCurrentDate()));

        Map<DestinationEntity, List<DeliveryOrderEntity>> mapByDestination = deliveryOrderService.mapByDestination(shippingList);

        model.addAttribute("orderstodeliver", shippingList);
        model.addAttribute("mappedorders", mapByDestination);

        startShipping(mapByDestination, globalData.getCurrentDate());

        globalData.updateAndSetCurrentView(model,globalData.getCurrentViewOrderList(),(List<DeliveryOrderEntity>) deliveryOrderRepository.findAll(),deliveryOrderService);
        return "orders/deliveringorders";
    }

    @GetMapping("actuator/info")
    public String showProfit(@RequestParam(required = false) String until,
                             Model model) {
        LocalDate untilDate;
        int profit = 0;
        try {
            String[] dateToken = until.split("-");
            untilDate = LocalDate.of(Integer.parseInt(dateToken[2]),
                    Integer.parseInt(dateToken[1]),
                    Integer.parseInt(dateToken[0]));
        } catch (Exception e) {
//            TODO incorrect date input
            untilDate = globalData.getCurrentDate().toLocalDate();
        }

        for (LocalDate date : globalData.getProfitByDayMap().keySet()) {
            if (date.isBefore(untilDate.plusDays(1))) {
                profit += globalData.getProfitByDayMap().get(date).get();
            }
        }

        model.addAttribute("delivered",globalData.getDeliveriesByDayMap().get(untilDate));
        model.addAttribute("currentdate", untilDate);
        model.addAttribute("profit", profit);
        return "profit";
    }

    @GetMapping({"/order/next-day"})
    public String newDay(Model model) {
        globalData.setCurrentDate(globalData.getCurrentDate().plusDays(1));
        globalData.setCurrentViewOrderList((List<DeliveryOrderEntity>) deliveryOrderRepository.findAll());
        globalData.setCommonModelAttributes(model);
        return "orders/orders";
    }

    @GetMapping({"/order/previous-day"})
    public String previousDay(Model model) {
        if (!globalData.getCurrentDate().toLocalDate().minusDays(1).isBefore(LocalDateTime.of(2021, 12, 15, 8, 0).toLocalDate())) {
            globalData.setCurrentDate(globalData.getCurrentDate().minusDays(1));
        }
        globalData.setCurrentViewOrderList((List<DeliveryOrderEntity>) deliveryOrderRepository.findAll());
        globalData.setCommonModelAttributes(model);
        return "orders/orders";
    }


    @Async
    public void startShipping(Map<DestinationEntity, List<DeliveryOrderEntity>> mapByDestination, LocalDateTime date){
        executor.startShipping(mapByDestination, date,deliveryOrderService);
    }

    private DeliveryOrderEntity addAndSaveOrder(DestinationEntity destination, String dateToConvert) {
        DeliveryOrderEntity savedOrder = null;
        try {
            String[] deliveryDateData = dateToConvert.split("-");
            LocalDateTime deliveryDate = LocalDateTime.of(
                    Integer.parseInt(deliveryDateData[2]),
                    Integer.parseInt(deliveryDateData[1]),
                    Integer.parseInt(deliveryDateData[0]),
                    8, 0);
            if (!deliveryDate.toLocalDate().isBefore(globalData.getCurrentDate().toLocalDate().plusDays(1))) {
                savedOrder = deliveryOrderService.convertAndSaveOrder(destination.getDestinationName() + "," + dateToConvert
                        , LocalDateTime.of(globalData.getCurrentDate().toLocalDate(), LocalTime.now()));
            } else {
//                    TODO log+file date before current date, order not saved
            }
        } catch (Exception e) {
//                TODO log+file invalid input for date, order not saved
        }
        return savedOrder;
    }


}
