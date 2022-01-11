package sci.java.logistic_system.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import sci.java.logistic_system.domain.DeliveryOrderEntity;
import sci.java.logistic_system.domain.OrderStatus;
import sci.java.logistic_system.domain.OrderStatusEntity;
import sci.java.logistic_system.domain.SelectedDeliveryOrders;
import sci.java.logistic_system.domain.repository.DeliveryOrderRepository;
import sci.java.logistic_system.domain.repository.DestinationRepository;
import sci.java.logistic_system.domain.repository.OrderStatusRepository;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Controller
public class OrderController {
    DeliveryOrderRepository deliveryOrderRepository;
    DestinationRepository destinationRepository;
    OrderStatusRepository orderStatusRepository;
    LocalDateTime currentDate;


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
    public void setCurrentDate() {
        this.currentDate = LocalDateTime.of(2021, 12, 15, 8, 0);
    }

    @RequestMapping({"order/list", "order/"})
    public String listOrders(Model model) {
        model.addAttribute("orders", deliveryOrderRepository.findAll());
        model.addAttribute("currentdate", currentDate.toLocalDate());
        model.addAttribute("selectedtocancel", new SelectedDeliveryOrders());
        return "orders";
    }

    @RequestMapping("order/{id}")
    public String getOrder(@PathVariable Integer id, Model model) {
        model.addAttribute("order",
                deliveryOrderRepository.findById(id).isPresent() ?
                        deliveryOrderRepository.findById(id).get() : null);

        model.addAttribute("orderstatuses", orderStatusRepository.findOrderStatusesById(id));

        return "orderdetails";
    }

    @RequestMapping("order/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        model.addAttribute("order", deliveryOrderRepository.findById(id).isPresent() ?
                deliveryOrderRepository.findById(id).get() : null);
        model.addAttribute("destinations", destinationRepository.getAvailableDestinations());
        model.addAttribute("statuses", OrderStatus.values());
        return "orderform";
    }

    @RequestMapping(value = "/order", method = RequestMethod.POST)
    public String updateOrder(@ModelAttribute("order") DeliveryOrderEntity order) {
        DeliveryOrderEntity savedOrder;
        order.setLastUpDated(LocalDateTime.of(currentDate.toLocalDate(), LocalTime.now()));
        savedOrder = deliveryOrderRepository.save(order);

        OrderStatusEntity foundStatus = orderStatusRepository.findById(order.getId()).get();
        foundStatus.setOrderStatusDate(order.getLastUpDated());
        orderStatusRepository.save(foundStatus);


//        DeliveryOrderEntity savedOrder;
//        if (!order.getDeliveryDate().isBefore(currentDate)) {
//            order.setLastUpDated(LocalDateTime.of(currentDate.toLocalDate(), LocalTime.now()));
//            savedOrder = deliveryOrderRepository.save(order);
//
//            if (order.getOrderStatus() == OrderStatus.NEW) {
//                OrderStatusEntity foundStatus = orderStatusRepository.findById(order.getId()).get();
//                foundStatus.setOrderStatusDate(order.getLastUpDated());
//                orderStatusRepository.save(foundStatus);
//            } else {
//                OrderStatusEntity savedOrderStatus = orderStatusRepository.addOrderStatus(order.getId(), order.getOrderStatus(), order.getLastUpDated());
//            }
//        } else {
//            //TODO throw,console log, file log order couldn't be modified, delivery date before current date
//            savedOrder = order;
//        }
        return "redirect:order/" + savedOrder.getId();
    }

    @RequestMapping(value = "/order/cancel")
//    @RequestMapping(value = "/order/cancel", method = RequestMethod.POST )
    public String cancelOrders(@ModelAttribute("selectedtocancel") SelectedDeliveryOrders selectedDeliveryOrders,
                               @RequestParam(value = "checkedorders", required = false) int[] cers,
                               BindingResult bindingResult, Model model) {
        System.out.println(selectedDeliveryOrders.getSelectedOrders().size());
        System.out.println(cers.length);
        if (selectedDeliveryOrders != null) {
            for (DeliveryOrderEntity order : selectedDeliveryOrders.getSelectedOrders()) {

                if (!order.getOrderStatus().equals(OrderStatus.DELIVERED)
                        && !order.getOrderStatus().equals(OrderStatus.CANCELED)) {
                    order.setOrderStatus(OrderStatus.CANCELED);
                    order.setLastUpDated(currentDate);
                    deliveryOrderRepository.save(order);
                    OrderStatusEntity orderStatusEntity = new OrderStatusEntity();
                    orderStatusEntity.setOrderId(order.getId());
                    orderStatusEntity.setOrderStatus(OrderStatus.CANCELED);
                    orderStatusEntity.setOrderStatusDate(currentDate);
                    orderStatusRepository.save(orderStatusEntity);
                }
            }
        }
        model.addAttribute("orders", deliveryOrderRepository.findAll());
        model.addAttribute("currentdate", currentDate.toLocalDate());
        return "orders";
    }

    @RequestMapping({"/shipping/new-day"})
    public String newDay(Model model) {
        currentDate = currentDate.plusDays(1);
        model.addAttribute("orders", deliveryOrderRepository.findAll());
        model.addAttribute("currentdate", currentDate.toLocalDate());
        model.addAttribute("selectedtocancel", new SelectedDeliveryOrders());
        return "orders";
    }

}
