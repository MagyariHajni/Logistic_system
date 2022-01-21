package sci.java.logistic_system.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import sci.java.logistic_system.domain.*;
import sci.java.logistic_system.domain.repository.DeliveryOrderRepository;
import sci.java.logistic_system.domain.repository.DestinationRepository;
import sci.java.logistic_system.domain.repository.OrderStatusRepository;
import sci.java.logistic_system.services.DeliveryOrderService;
import sci.java.logistic_system.services.Executor;
import sci.java.logistic_system.services.GlobalData;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class OrderController {
    public static Logger logger = LoggerFactory.getLogger(GlobalData.class);

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
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        logger.trace("Viewing all orders, accessed: " + request.getRequestURL());

        globalData.setCurrentViewOrderList((List<DeliveryOrderEntity>) deliveryOrderRepository.findAll());
        globalData.setCommonModelAttributes(model);
        return "orders/orders";
    }

    @GetMapping(value = "order/list")
    public String listOrders(@RequestParam(required = false) LocalDateTime date,
                             Model model) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        StringBuffer url = Objects.isNull(date) ? request.getRequestURL() : request.getRequestURL().append('?').append(request.getQueryString());
        logger.trace("Filtering list by given date (if given), accessed: " + url);

        if (!Objects.isNull(date)) {
            List<DeliveryOrderEntity> listOfOrdersForToday = (List<DeliveryOrderEntity>) deliveryOrderRepository.findAll();
            globalData.setCurrentViewOrderList(deliveryOrderService.filterOrdersByDate(listOfOrdersForToday, date));
        }

        globalData.updateAndSetCurrentView(model, globalData.getCurrentViewOrderList(), (List<DeliveryOrderEntity>) deliveryOrderRepository.findAll(), deliveryOrderService);
        return "orders/orders";
    }

    @GetMapping(value = {"order/status"})
    public String listByDateAndOrder(
            @RequestParam(required = false) LocalDateTime date,
            @RequestParam(required = false) String destination,
            Model model) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        logger.trace("Filtering list by given date and destination (if given), accessed: " + request.getRequestURL().append('?').append(request.getQueryString()));

        List<DeliveryOrderEntity> filteredOrdersList
                = deliveryOrderService.filterOrdersByDateAndDestination(date, destination, globalData.getCurrentDate());

        globalData.updateAndSetCurrentView(model, filteredOrdersList, (List<DeliveryOrderEntity>) deliveryOrderRepository.findAll(), deliveryOrderService);
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
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        logger.trace("Filtering list by given date and/or destination and/or status (if given), accessed: " + request.getRequestURL().append('?').append(request.getQueryString()));


        List<DeliveryOrderEntity> filteredOrdersList = (List<DeliveryOrderEntity>) deliveryOrderRepository.findAll();
        if ((!Objects.isNull(date))) {
            logger.trace("Filtering all orders by date: " + date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
            filteredOrdersList = deliveryOrderService.filterOrdersByDate(filteredOrdersList, date);
        }
        if ((!Objects.isNull(destination))) {
            logger.trace("Filtering all orders by destination: " + destination);
            destination = destination.substring(0, 1).toUpperCase() + destination.substring(1).toLowerCase();
            filteredOrdersList = deliveryOrderService.filterOrdersByDestination(filteredOrdersList, destination);
        }
        if ((!Objects.isNull(status))) {
            logger.trace("Filtering all orders by destination: " + status);
            filteredOrdersList = deliveryOrderService.filterOrdersByStatus(filteredOrdersList, status);
        }

        globalData.updateAndSetCurrentView(model, filteredOrdersList, (List<DeliveryOrderEntity>) deliveryOrderRepository.findAll(), deliveryOrderService);
        if (!Objects.isNull(destination)) {
            model.addAttribute("destinationtofind", destination);
        }
        return "orders/orders";
    }

    @GetMapping("order/{id}")
    public String getOrder(@PathVariable Integer id, Model model) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        logger.trace("Viewing order nr " + id + ", accessed: " + request.getRequestURL().append('?').append(request.getQueryString()));

        model.addAttribute("order",
                deliveryOrderRepository.findById(id).isPresent() ? deliveryOrderRepository.findById(id).get() : null);

        globalData.setCommonModelAttributes(model);
        model.addAttribute("orderstatuses", orderStatusRepository.findOrderStatusesById(id));
        return "orders/orderdetails";
    }

    @GetMapping("order/edit/{id}")
    public String editOrder(@PathVariable Integer id, Model model) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        logger.trace("Submitted order nr " + id + " for editing, accessed: " + request.getRequestURL().append('?').append(request.getQueryString()));
//TODO daca dau edit pe URL nu pot da nici back nici submit, eroare
        model.addAttribute("order", deliveryOrderRepository.findById(id).isPresent() ? deliveryOrderRepository.findById(id).get() : null);
        model.addAttribute("destinations", destinationRepository.getAvailableDestinations());
        globalData.setCommonModelAttributes(model);
        return "orders/orderform";
    }

    @PostMapping(value = "/order")
    public String updateOrder(@ModelAttribute("order") DeliveryOrderEntity order, Model model) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        logger.info("Editing order nr " + order.getId() + ", accessed: " + request.getRequestURL().append('?').append(request.getQueryString()));

        if (order.getDeliveryDate().isBefore(globalData.getCurrentDate())) {
            logger.info("Order number: " + order.getId() + " was not modified, delivery date must be after current date");
        } else {
            Optional<DeliveryOrderEntity> foundOrder = deliveryOrderRepository.findById(order.getId());
            if (foundOrder.isPresent()) {
                if ((foundOrder.get().equals(order))) {
                    logger.info("Order number: " + order.getId() + " was not modified, no information was changed");
                } else {
                    deliveryOrderService.modifyOrderDetails(order, globalData.getCurrentDate());
                    logger.info("Order number: " + order.getId()
                            + " was successfully modified today: "
                            + globalData.getCurrentDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
                }
            } else {
                logger.warn("Order number: " + order.getId() + " was deleted from database");
            }
        }
        globalData.updateAndSetCurrentView(model, globalData.getCurrentViewOrderList(), (List<DeliveryOrderEntity>) deliveryOrderRepository.findAll(), deliveryOrderService);
        return "redirect:order/" + order.getId();
    }

    @RequestMapping(value = "/order/cancel", method = {RequestMethod.GET, RequestMethod.POST})
    public String cancelOrders(@RequestParam(required = false) List<String> orders,
                               @ModelAttribute("selectedlist") ThymeleafBindingObject selectedOrders, Model model) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        logger.info("Submitting orders to cancel, accessed: " + request.getRequestURL().append('?').append(request.getQueryString()));

        List<DeliveryOrderEntity> ordersToCancel = new ArrayList<>();
        Optional<DeliveryOrderEntity> foundOrder;

        if (!Objects.isNull(orders) && !orders.isEmpty()) {
            logger.info("Submitted orders to cancel by url: " + orders);
            for (String id : orders) {
                foundOrder = deliveryOrderRepository.findById(Integer.valueOf(id));
                if (foundOrder.isPresent()) {
                    if ((foundOrder.get().getOrderStatus() != OrderStatus.CANCELED) && (foundOrder.get().getOrderStatus() != OrderStatus.DELIVERED)) {
                        Optional<DeliveryOrderEntity> orderToCancel = deliveryOrderRepository.findById(Integer.valueOf(id));
                        orderToCancel.ifPresent(ordersToCancel::add);
                    }
                }
            }
        } else {
            if (!Objects.isNull(selectedOrders) && !Objects.isNull(selectedOrders.getListOfOrders())) {
                logger.info("Submitted orders to cancel by form: "
                        + selectedOrders.getListOfOrders().stream().reduce("", (all, order) -> all + order.getId() + "   ", String::concat));

                ordersToCancel = selectedOrders.getListOfOrders().stream()
                        .filter(order -> (order.getOrderStatus() != OrderStatus.CANCELED) && (order.getOrderStatus() != OrderStatus.DELIVERED))
                        .collect(Collectors.toList());
            }
        }

        if (ordersToCancel.isEmpty()) {
            logger.info("No available orders selected to cancel");
        }

        deliveryOrderService.cancelSelectedOrders(ordersToCancel, globalData.getCurrentDate());
        model.addAttribute("canceledorders", ordersToCancel);
        globalData.updateAndSetCurrentView(model, globalData.getCurrentViewOrderList(), (List<DeliveryOrderEntity>) deliveryOrderRepository.findAll(), deliveryOrderService);
        return "orders/canceledorders";
    }

    @GetMapping("order/new")
    public String setNumberOfOrdersToAdd(Model model) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        logger.info("Setting number of orders to add, accessed: " + request.getRequestURL().append('?').append(request.getQueryString()));

        globalData.setCommonModelAttributes(model);
        model.addAttribute("numberofneworders", new ThymeleafBindingObject());
        return "orders/setnumberofneworders";
    }

    @PostMapping(value = "/order/addorder")
    public String setNumberOfOrdersToAdd(@ModelAttribute("numberofneworders") ThymeleafBindingObject number) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        int numberOfOrdersToAdd = number.getNumberOfOrders() == 0 ? 1 : number.getNumberOfOrders();
        logger.info("Number of orders to add set to " + numberOfOrdersToAdd + ", accessed: " + request.getRequestURL().append('?').append(request.getQueryString()));

        return "redirect:/order/add?number=" + numberOfOrdersToAdd;
    }

    @GetMapping("order/add")
    public String addOrders(@RequestParam(required = false) Map<String, String> orders,
                            @RequestParam(required = false) String number, Model model) {

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        globalData.setCommonModelAttributes(model);
        ThymeleafBindingObject thymeleafBindingObject = new ThymeleafBindingObject();
        int intNumber = 1;
        try {
            intNumber = Integer.parseInt(number);
        } catch (Exception e) {
            logger.info("Invalid number, number of orders to add set to 1 by default");
        }

        if (!Objects.isNull(orders)) {
            orders.remove("number");
        }

        if (!Objects.isNull(orders) && !orders.isEmpty()) {
            logger.info("Submitting orders to add by url, accessed: " + request.getRequestURL().append('?').append(request.getQueryString()));
            List<DeliveryOrderEntity> savedOrdersList = new ArrayList<>();
            DeliveryOrderEntity savedOrder;

            for (String destinationName : orders.keySet()) {
                String originalName = destinationName;
                destinationName = destinationName.substring(0, 1).toUpperCase() + destinationName.substring(1).toLowerCase();
                DestinationEntity destination = destinationRepository.findDestinationEntityByDestinationName(destinationName);

                if (!Objects.isNull(destination)) {
                    savedOrder = addAndSaveOrder(destination, orders.get(originalName));
                    if (!Objects.isNull(savedOrder)) {
                        logger.info("Order to " + destinationName + " for delivery on "
                                + savedOrder.getDeliveryDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                                + " successfully saved");
                        savedOrdersList.add(savedOrder);
                    }
                } else {
                    logger.info("Order with destination: " + destinationName + " was not saved, destination isn't available");
                }
            }

            if (savedOrdersList.isEmpty()) {
                logger.info("No valid data given to save");
            }

            model.addAttribute("addedorders", savedOrdersList);
            return "orders/addedorders";
        } else {
            logger.info("Submitting orders to add by form");
            for (int i = 0; i < intNumber; i++) {
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
                    logger.info("Order to " + savedOrder.getOrderDestination().getDestinationName()
                            + " for delivery on " + savedOrder.getDeliveryDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                            + " successfully saved");
                    savedOrdersList.add(savedOrder);
                }
            }
        }

        if (savedOrdersList.isEmpty()) {
            logger.info("No valid data given to save");
        }

        model.addAttribute("addedorders", savedOrdersList);
        globalData.setCommonModelAttributes(model);
        return "orders/addedorders";
    }

    @GetMapping("/shipping/new-day")
    public String shippingToday(@RequestParam(required = false) LocalDateTime date,
                                Model model) {

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        LocalDateTime dateToShip;

        if (!Objects.isNull(date)) {
            dateToShip = date;
        } else {
            dateToShip = globalData.getCurrentDate().plusDays(1);
        }
        logger.info("Prepare shipping on " + dateToShip.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                + ", accessed: " + request.getRequestURL().append('?').append(request.getQueryString()));

        List<DeliveryOrderEntity> shippingList = (List<DeliveryOrderEntity>) deliveryOrderRepository.findAll();

        List<DeliveryOrderEntity> undelivered = shippingList.stream()
                .filter(order -> order.getDeliveryDate().toLocalDate().isBefore(dateToShip.toLocalDate()))
                .filter(order -> order.getOrderStatus().equals(OrderStatus.NEW)).collect(Collectors.toList());

        Map<DestinationEntity, List<DeliveryOrderEntity>> mapByDestination = new HashMap<>();

        if (undelivered.isEmpty()) {
            shippingList = shippingList.stream()
                    .filter(order -> order.getDeliveryDate().toLocalDate().equals(dateToShip.toLocalDate()))
                    .filter(order -> order.getOrderStatus().equals(OrderStatus.NEW)).collect(Collectors.toList());

            shippingList.forEach(order -> order.setOrderStatus(OrderStatus.DELIVERING));
            shippingList.forEach(order -> deliveryOrderService.modifyOrderDetails(order, dateToShip));

            mapByDestination = deliveryOrderService.mapByDestination(shippingList);

            logger.info("Started shipping on " + dateToShip.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                    + " deliveries to: " + mapByDestination.keySet().stream().map(destination -> destination.getDestinationName() + "   ").reduce("", String::concat));

            startShipping(mapByDestination, dateToShip);
        } else {
            logger.warn("Shipping orders on " + dateToShip.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                    + " not possible, undelivered orders from previous dates, ship or cancel them");
        }

        model.addAttribute("undelivered", undelivered);
        model.addAttribute("orderstodeliver", shippingList);
        model.addAttribute("mappedorders", mapByDestination);

        globalData.updateAndSetCurrentView(model, globalData.getCurrentViewOrderList(), (List<DeliveryOrderEntity>) deliveryOrderRepository.findAll(), deliveryOrderService);
        model.addAttribute("currentdate", dateToShip);
        return "orders/deliveringorders";
    }

    @GetMapping("actuator/info")
    public String showProfit(@RequestParam(required = false) String until,
                             Model model) {
        LocalDate untilDate = null;
        int profit = 0;

        if (!Objects.isNull(until)) {
            try {
                String[] dateToken = until.split("-");
                untilDate = LocalDate.of(Integer.parseInt(dateToken[2]),
                        Integer.parseInt(dateToken[1]),
                        Integer.parseInt(dateToken[0]));
            } catch (Exception e) {
                logger.warn("Incorrect data input, data must be of type dd-MM-yyyy");
            }
        }

        if (Objects.isNull(untilDate)) {
            untilDate = globalData.getCurrentDate().toLocalDate();
        }

        for (LocalDate date : globalData.getProfitByDayMap().keySet()) {
            if (date.isBefore(untilDate.plusDays(1))) {
                profit += globalData.getProfitByDayMap().get(date).get();
            }
        }

        model.addAttribute("delivered", globalData.getDeliveriesByDayMap().get(untilDate));
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
    public void startShipping(Map<DestinationEntity, List<DeliveryOrderEntity>> mapByDestination, LocalDateTime date) {
        executor.startShipping(mapByDestination, date, deliveryOrderService);
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
                logger.info("Order with destination: " + destination.getDestinationName() + " was not saved, delivery date is before current date");
            }
        } catch (Exception e) {
            logger.info("Order with destination: " + destination.getDestinationName() + " was not saved, invalid entry for date");
        }
        return savedOrder;
    }

}
