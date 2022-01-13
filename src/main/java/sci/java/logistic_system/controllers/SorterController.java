package sci.java.logistic_system.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import sci.java.logistic_system.domain.DeliveryOrderEntity;
import sci.java.logistic_system.domain.DestinationEntity;
import sci.java.logistic_system.domain.repository.DeliveryOrderRepository;
import sci.java.logistic_system.domain.repository.DestinationRepository;
import sci.java.logistic_system.services.GlobalData;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class SorterController {
    DeliveryOrderRepository deliveryOrderRepository;
    DestinationRepository destinationRepository;
    GlobalData globalData;

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

    @RequestMapping({"/order/sort/byid"})
    public String sortOrdersById(Model model) {

        globalData.setCommonModelAttributes(model);
        model.addAttribute("orders", globalData.getCurrentViewOrderList().stream()
                .sorted(Comparator.comparing(DeliveryOrderEntity::getId))
                .collect(Collectors.toList()));
        return "orders";
    }

    @RequestMapping({"/order/sort/bydestination"})
    public String sortOrdersByDestination(Model model) {

        globalData.setCommonModelAttributes(model);

        List<DeliveryOrderEntity> deletedDestinationOrders = globalData.getCurrentViewOrderList().stream()
                .filter(order -> order.getOrderDestination() == null)
                .collect(Collectors.toList());
        List<DeliveryOrderEntity> availableDestinationOrders = globalData.getCurrentViewOrderList().stream()
                .filter(order -> order.getOrderDestination() != null)
                .sorted(Comparator.comparing(o1 -> o1.getOrderDestination().getDestinationName()))
                .collect(Collectors.toList());
        availableDestinationOrders.addAll(deletedDestinationOrders);

        model.addAttribute("orders", availableDestinationOrders);

        return "orders";
    }

    @RequestMapping({"/order/sort/byorderstatus"})
    public String sortOrdersByOrderStatus(Model model) {

        globalData.setCommonModelAttributes(model);
        model.addAttribute("orders", globalData.getCurrentViewOrderList().stream()
                .sorted(Comparator.comparing(o1 -> o1.getOrderStatus().name()))
                .collect(Collectors.toList()));

        return "orders";
    }

    @RequestMapping({"/order/sort/bydeliverydate"})
    public String sortOrdersByDeliveryDate(Model model) {

        globalData.setCommonModelAttributes(model);
        model.addAttribute("orders", globalData.getCurrentViewOrderList().stream()
                .sorted(Comparator.comparing(DeliveryOrderEntity::getDeliveryDate))
                .collect(Collectors.toList()));

        return "orders";
    }

    @RequestMapping({"/order/sort/bylastupdated"})
    public String sortOrdersByLastUpdated(Model model) {

        globalData.setCommonModelAttributes(model);
        model.addAttribute("orders", globalData.getCurrentViewOrderList().stream()
                .sorted(Comparator.comparing(DeliveryOrderEntity::getLastUpDated))
                .collect(Collectors.toList()));
        return "orders";
    }

    @RequestMapping({"/destinations/sort/byid"})
    public String sortDestinationsById(Model model) {
        List<DestinationEntity> orders = (List<DestinationEntity>) destinationRepository.findAll();
        globalData.setCommonModelAttributes(model);
        model.addAttribute("destinations", orders.stream()
                .sorted(Comparator.comparing(DestinationEntity::getId))
                .collect(Collectors.toList()));
        return "destinations";
    }

    @RequestMapping({"/destinations/sort/byname"})
    public String sortDestinationsByName(Model model) {
        List<DestinationEntity> orders = (List<DestinationEntity>) destinationRepository.findAll();
        globalData.setCommonModelAttributes(model);
        model.addAttribute("destinations", orders.stream()
                .sorted(Comparator.comparing(DestinationEntity::getDestinationName))
                .collect(Collectors.toList()));
        return "destinations";
    }

    @RequestMapping({"/destinations/sort/bydistance"})
    public String sortDestinationsByDistance(Model model) {
        List<DestinationEntity> orders = (List<DestinationEntity>) destinationRepository.findAll();
        globalData.setCommonModelAttributes(model);
        model.addAttribute("destinations", orders.stream()
                .sorted(Comparator.comparing(DestinationEntity::getDistance))
                .collect(Collectors.toList()));
        return "destinations";
    }

}
