package sci.java.logistic_system.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import sci.java.logistic_system.domain.DeliveryOrderEntity;
import sci.java.logistic_system.domain.repository.DeliveryOrderRepository;
import sci.java.logistic_system.services.GlobalData;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/order/sort")
public class OrderSorterController {
    DeliveryOrderRepository deliveryOrderRepository;
    GlobalData globalData;

    @Autowired
    public void setGlobalData(GlobalData globalData) {
        this.globalData = globalData;
    }

    @Autowired
    public void setDeliveryOrderRepository(DeliveryOrderRepository deliveryOrderRepository) {
        this.deliveryOrderRepository = deliveryOrderRepository;
    }


    @RequestMapping({"/byid"})
    public String listProductsById(Model model) {
        List<DeliveryOrderEntity> orders = (List<DeliveryOrderEntity>)deliveryOrderRepository.findAll();
        model.addAttribute("orders", orders.stream()
                .sorted(Comparator.comparing(DeliveryOrderEntity::getId))
                .collect(Collectors.toList()));
        model.addAttribute("currentdate", globalData.getCurrentDate().toLocalDate());
        return "orders";
    }
//
    @RequestMapping({"/bydestination"})
    public String listProductsByDestination(Model model) {
        List<DeliveryOrderEntity> orders = (List<DeliveryOrderEntity>)deliveryOrderRepository.findAll();
        model.addAttribute("orders", orders.stream()
                .sorted(Comparator.comparing(o1 -> o1.getOrderDestination().getDestinationName()))
                .collect(Collectors.toList()));
        model.addAttribute("currentdate", globalData.getCurrentDate().toLocalDate());
        return "orders";
    }
//
//    @RequestMapping({"/byorderstatus"})
//    public String listProductsByOrderStatus(Model model) {
//
//        model.addAttribute("orders", orders.stream()
//                .sorted(Comparator.comparing(DeliveryOrderEntity::getOrderStatus))
//                .collect(Collectors.toList()));
//        model.addAttribute("currentdate", globalData.getCurrentDate().toLocalDate());
//        return "orders";
//    }
//
//
//    @RequestMapping({"/bydeliverydate"})
//    public String listProductsByDeliveryDate(Model model) {
//        model.addAttribute("orders", orders.stream()
//                .sorted(Comparator.comparing(DeliveryOrderEntity::getDeliveryDate))
//                .collect(Collectors.toList()));
//        model.addAttribute("currentdate", globalData.getCurrentDate().toLocalDate());
//        return "orders";
//    }
//
//
//    @RequestMapping({"/bylastupdated"})
//    public String listProductsByLastUpdated(Model model) {
//        model.addAttribute("orders", orders.stream()
//                .sorted(Comparator.comparing(DeliveryOrderEntity::getLastUpDated))
//                .collect(Collectors.toList()));
//        model.addAttribute("currentdate", globalData.getCurrentDate().toLocalDate());
//        return "orders";
//    }
}
