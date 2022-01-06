package sci.java.logistic_system.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import sci.java.logistic_system.domain.DeliveryOrderEntity;

import java.util.Comparator;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/order/sort")
public class OrderSorterController extends OrderController {
//
//    @RequestMapping({"/byid"})
//    public String listProductsById(Model model) {
//
//        model.addAttribute("orders", orderRepository.listAll()
//                .stream()
//                .sorted(Comparator.comparing(DeliveryOrderEntity::getId))
//                .collect(Collectors.toList()));
//        model.addAttribute("currentdate", currentDate.toLocalDate());
//        return "orders";
//    }
//
//    @RequestMapping({"/bydestination"})
//    public String listProductsByDestination(Model model) {
//
//        model.addAttribute("orders", orderRepository.listAll()
//                .stream()
//                .sorted(Comparator.comparing(o1 -> o1.getOrderDestination().getDestinationName()))
//                .collect(Collectors.toList()));
//        model.addAttribute("currentdate", currentDate.toLocalDate());
//        return "orders";
//    }
//
//
//    @RequestMapping({"/byorderstatus"})
//    public String listProductsByOrderStatus(Model model) {
//        model.addAttribute("orders", orderRepository.listAll()
//                .stream()
//                .sorted(Comparator.comparing(DeliveryOrderEntity::getOrderStatus))
//                .collect(Collectors.toList()));
//        model.addAttribute("currentdate", currentDate.toLocalDate());
//        return "orders";
//    }
//
//
//    @RequestMapping({"/bydeliverydate"})
//    public String listProductsByDeliveryDate(Model model) {
//        model.addAttribute("orders", orderRepository.listAll()
//                .stream()
//                .sorted(Comparator.comparing(DeliveryOrderEntity::getDeliveryDate))
//                .collect(Collectors.toList()));
//        model.addAttribute("currentdate", currentDate.toLocalDate());
//        return "orders";
//    }
//
//
//    @RequestMapping({"/bylastupdated"})
//    public String listProductsByLastUpdated(Model model) {
//        model.addAttribute("orders", orderRepository.listAll()
//                .stream()
//                .sorted(Comparator.comparing(DeliveryOrderEntity::getLastUpDated))
//                .collect(Collectors.toList()));
//        model.addAttribute("currentdate", currentDate.toLocalDate());
//        return "orders";
//    }
}
