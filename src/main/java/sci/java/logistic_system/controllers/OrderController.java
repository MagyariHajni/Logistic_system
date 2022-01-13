package sci.java.logistic_system.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import sci.java.logistic_system.config.OrderReq;
import sci.java.logistic_system.domain.DeliveryOrderEntity;
import sci.java.logistic_system.domain.OrderStatus;
import sci.java.logistic_system.domain.SelectedDeliveryOrders;
import sci.java.logistic_system.domain.repository.DeliveryOrderRepository;
import sci.java.logistic_system.domain.repository.DestinationRepository;
import sci.java.logistic_system.domain.repository.OrderStatusRepository;
import sci.java.logistic_system.services.DeliveryOrderService;
import sci.java.logistic_system.services.GlobalData;
import sci.java.logistic_system.services.OrderStatusService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class OrderController {
    private DeliveryOrderRepository deliveryOrderRepository;
    private DestinationRepository destinationRepository;
    private OrderStatusRepository orderStatusRepository;
    private GlobalData globalData;
    private DeliveryOrderService deliveryOrderService;
    private OrderStatusService orderStatusService;


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

    @GetMapping(value = {"order/list", "order/"})
    public String listOrders(Model model) {
        globalData.setCurrentViewOrderList((List<DeliveryOrderEntity>) deliveryOrderRepository.findAll());
        globalData.setCommonModelAttributes(model);
        return "orders";
    }

    @GetMapping("order/{id}")
    public String getOrder(@PathVariable Integer id, Model model) {
        model.addAttribute("order",
                deliveryOrderRepository.findById(id).isPresent() ?
                        deliveryOrderRepository.findById(id).get() : null);
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

        if (!order.getDeliveryDate().isBefore(globalData.getCurrentDate())) {
            deliveryOrderService.modifyOrderDetails(order, globalData.getCurrentDate());
        } else {
//            TODO throw,console log, file log order couldn't be modified, delivery date before current date
        }
        return "redirect:order/" + order.getId();
    }


    @PostMapping(value = "/order/cancel")
    public String cancelOrders(@ModelAttribute("selectedlist") SelectedDeliveryOrders selectedOrders, Model model) {
        if (selectedOrders != null) {
            List<DeliveryOrderEntity> ordersToCancel = selectedOrders.getSelectedOrders().stream()
                    .filter(order -> order.getOrderStatus() != OrderStatus.CANCELED)
                    .collect(Collectors.toList());
            deliveryOrderService.cancelSelectedOrders(ordersToCancel, globalData.getCurrentDate());
            model.addAttribute("canceledorders", ordersToCancel);
        }
        globalData.setCommonModelAttributes(model);
        return "canceledorders";
    }

    @RequestMapping({"/shipping/previous-day"}) //previous day need to be >=with current day
    public String previousDay(Model model) {
        globalData.setCurrentDate(globalData.getCurrentDate().minusDays(1));
        globalData.setCurrentViewOrderList((List<DeliveryOrderEntity>) deliveryOrderRepository.findAll());
        globalData.setCommonModelAttributes(model);
        return "orders";
    }

    //
    @RequestMapping({"/shipping/new-day"})
    public String newDay(Model model) {
        globalData.setCurrentDate(globalData.getCurrentDate().plusDays(1));
        globalData.setCurrentViewOrderList((List<DeliveryOrderEntity>) deliveryOrderRepository.findAll());
        globalData.setCommonModelAttributes(model);
        return "orders";
    }

    @PostMapping(value = "/add/orders")
    public OrderReq addOrder(@Valid @RequestBody OrderReq load){
        return orderStatusService.addOrder(load);

    }
}
