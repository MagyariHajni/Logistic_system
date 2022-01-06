package sci.java.logistic_system.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import sci.java.logistic_system.services.OrderRepository;

import java.time.LocalDateTime;

@Controller
public class OrderController {
    OrderRepository orderRepository;
    LocalDateTime currentDate;

    @Autowired
    public void setOrderRepository(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Autowired
    public void setCurrentDate() {
        this.currentDate = LocalDateTime.of(2021, 12, 15, 8, 0);
        ;
    }

    @RequestMapping({"order/list", "order/"})
    public String listProducts(Model model) {
        model.addAttribute("orders", orderRepository.listAll());
        model.addAttribute("currentdate", currentDate.toLocalDate());
        return "orders";
    }

    @RequestMapping({"/shipping/new-day"})
    public String newDay(Model model) {
        currentDate = currentDate.plusDays(1);
        model.addAttribute("orders", orderRepository.listAll());
        model.addAttribute("currentdate", currentDate.toLocalDate());
        return "orders";
    }

}
