package sci.java.logistic_system.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import sci.java.logistic_system.domain.DeliveryOrderEntity;
import sci.java.logistic_system.domain.OrderStatus;
import sci.java.logistic_system.domain.SelectedDeliveryOrders;
import sci.java.logistic_system.domain.repository.DeliveryOrderRepository;
import sci.java.logistic_system.domain.repository.DestinationRepository;
import sci.java.logistic_system.domain.repository.OrderStatusRepository;
import sci.java.logistic_system.services.DeliveryOrderService;
import sci.java.logistic_system.services.GlobalData;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
public class OrderController {
    private DeliveryOrderRepository deliveryOrderRepository;
    private DestinationRepository destinationRepository;
    private OrderStatusRepository orderStatusRepository;
    private GlobalData globalData;
    private DeliveryOrderService deliveryOrderService;


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

    @GetMapping(value = "order/")
    public String allOrders(Model model){

        globalData.setCurrentViewOrderList((List<DeliveryOrderEntity>) deliveryOrderRepository.findAll());
        globalData.setCommonModelAttributes(model);
        return "orders";
    }

    @GetMapping(value = "order/list")
    public String listOrders(@RequestParam(required = false) LocalDateTime date,
                             Model model) {

        if (!Objects.isNull(date)) {
            LocalDate dateDate = date.toLocalDate();
            List<DeliveryOrderEntity> listOfOrdersForToday = (List<DeliveryOrderEntity>) deliveryOrderRepository.findAll();
            globalData.setCurrentViewOrderList(
                    listOfOrdersForToday.stream()
                            .filter(order -> order.getDeliveryDate().toLocalDate().equals(dateDate))
                            .collect(Collectors.toList()));

        }
        globalData.setCommonModelAttributes(model);
        return "orders";
    }

    @GetMapping(value = {"order/status"})
    public String listByDateAndOrder(
            @RequestParam(required = false) LocalDateTime date,
            @RequestParam(required = false) String destination,
            Model model) {

        List<DeliveryOrderEntity> filteredOrdersList
                = deliveryOrderService.filterOrdersByDateAndDestination(date, destination, globalData.getCurrentDate());
        globalData.setCurrentViewOrderList(filteredOrdersList);
        globalData.setCommonModelAttributes(model);
        if (!Objects.isNull(destination)) {
            model.addAttribute("destinationtofind", destination);
        }
        return "orders";
    }

    @GetMapping("order/{id}")
    public String getOrder(@PathVariable Integer id, Model model) {
        model.addAttribute("order",
                deliveryOrderRepository.findById(id).isPresent() ?
                        deliveryOrderRepository.findById(id).get() : null);

        globalData.setCommonModelAttributes(model);
        model.addAttribute("orderstatuses", orderStatusRepository.findOrderStatusesById(id));
        return "orderdetails";
    }

    @GetMapping("order/edit/{id}")
    public String editOrder(@PathVariable Integer id, Model model) {
        model.addAttribute("order", deliveryOrderRepository.findById(id).isPresent() ?
                deliveryOrderRepository.findById(id).get() : null);
        globalData.setCommonModelAttributes(model);
        model.addAttribute("destinations", destinationRepository.getAvailableDestinations());
        return "orderform";
    }

    @PostMapping(value = "/order")
    public String updateOrder(@ModelAttribute("order") DeliveryOrderEntity order) {

        if (!order.getDeliveryDate().isBefore(globalData.getCurrentDate()) || order.getOrderStatus()==OrderStatus.CANCELED) {
            deliveryOrderService.modifyOrderDetails(order, globalData.getCurrentDate());
        } else {
//            TODO throw,console log, file log order couldn't be modified, delivery date before current date
        }

        List<DeliveryOrderEntity> updatedCurrentView
                = deliveryOrderService.updateView((List<DeliveryOrderEntity>) deliveryOrderRepository.findAll(), globalData.getCurrentViewOrderList());
        globalData.setCurrentViewOrderList(updatedCurrentView);

        return "redirect:order/" + order.getId();
    }

    @PostMapping(value = "/order/cancel")
    public String cancelOrders(@ModelAttribute("selectedlist") SelectedDeliveryOrders selectedOrders, Model model) {
        if (selectedOrders != null) {
            List<DeliveryOrderEntity> ordersToCancel = selectedOrders.getSelectedOrders().stream()
                    .filter(order -> (order.getOrderStatus() != OrderStatus.CANCELED) && (order.getOrderStatus() != OrderStatus.DELIVERED))
                    .collect(Collectors.toList());
            deliveryOrderService.cancelSelectedOrders(ordersToCancel, globalData.getCurrentDate());
            model.addAttribute("canceledorders", ordersToCancel);
        }
        List<DeliveryOrderEntity> updatedCurrentView
                = deliveryOrderService.updateView((List<DeliveryOrderEntity>) deliveryOrderRepository.findAll(), globalData.getCurrentViewOrderList());
        globalData.setCurrentViewOrderList(updatedCurrentView);
        globalData.setCommonModelAttributes(model);

        return "canceledorders";
    }

    @RequestMapping({"/order/new-day"})
    public String newDay(Model model) {
        globalData.setCurrentDate(globalData.getCurrentDate().plusDays(1));
        globalData.setCurrentViewOrderList((List<DeliveryOrderEntity>) deliveryOrderRepository.findAll());
        globalData.setCommonModelAttributes(model);
        return "orders";
    }

    @RequestMapping({"/order/previous-day"}) //previous day need to be >=with current day
    public String previousDay(Model model) {

        if(!globalData.getCurrentDate().minusDays(1).isBefore(LocalDateTime.of(2021,12,15,8,0))){
            globalData.setCurrentDate(globalData.getCurrentDate().minusDays(1));
        }
        globalData.setCurrentViewOrderList((List<DeliveryOrderEntity>) deliveryOrderRepository.findAll());
        globalData.setCommonModelAttributes(model);
        return "orders";
    }


}
