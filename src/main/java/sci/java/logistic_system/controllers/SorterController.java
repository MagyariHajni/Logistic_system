package sci.java.logistic_system.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import sci.java.logistic_system.domain.DeliveryOrderEntity;
import sci.java.logistic_system.domain.DestinationEntity;
import sci.java.logistic_system.domain.repository.DeliveryOrderRepository;
import sci.java.logistic_system.domain.repository.DestinationRepository;
import sci.java.logistic_system.services.GlobalData;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
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
    public String sortOrdersById(@RequestParam(required = false) String sortDir,
                                 Model model) {
        globalData.setCommonModelAttributes(model);

        List<DeliveryOrderEntity> sortedList = globalData.getCurrentViewOrderList().stream()
                .sorted(Comparator.comparing(DeliveryOrderEntity::getId))
                .collect(Collectors.toList());

        model.addAttribute("orders", sortList(sortDir, model, sortedList));
        return "orders/orders";
    }

    @RequestMapping({"/order/sort/bydestination"})
    public String sortOrdersByDestination(@RequestParam(required = false) String sortDir,
                                          Model model) {

        globalData.setCommonModelAttributes(model);
        List<DeliveryOrderEntity> deletedDestinationOrders = globalData.getCurrentViewOrderList().stream()
                .filter(order -> order.getOrderDestination() == null)
                .collect(Collectors.toList());
        List<DeliveryOrderEntity> availableDestinationOrders = globalData.getCurrentViewOrderList().stream()
                .filter(order -> order.getOrderDestination() != null)
                .sorted(Comparator.comparing(o1 -> o1.getOrderDestination().getDestinationName()))
                .collect(Collectors.toList());
        availableDestinationOrders.addAll(deletedDestinationOrders);

        model.addAttribute("orders", sortList(sortDir, model, availableDestinationOrders));
        return "orders/orders";
    }

    @RequestMapping({"/order/sort/byorderstatus"})
    public String sortOrdersByOrderStatus(@RequestParam(required = false) String sortDir,
                                          Model model) {

        globalData.setCommonModelAttributes(model);
        List<DeliveryOrderEntity> sortedList = globalData.getCurrentViewOrderList().stream()
                .sorted(Comparator.comparing(o1 -> o1.getOrderStatus().name()))
                .collect(Collectors.toList());

        model.addAttribute("orders", sortList(sortDir, model, sortedList));
        return "orders/orders";
    }

    @RequestMapping({"/order/sort/bydeliverydate"})
    public String sortOrdersByDeliveryDate(@RequestParam(required = false) String sortDir,
                                           Model model) {

        globalData.setCommonModelAttributes(model);
        List<DeliveryOrderEntity> sortedList = globalData.getCurrentViewOrderList().stream()
                .sorted(Comparator.comparing(DeliveryOrderEntity::getDeliveryDate))
                .collect(Collectors.toList());

        model.addAttribute("orders", sortList(sortDir, model, sortedList));

        return "orders/orders";
    }

    @RequestMapping({"/order/sort/bylastupdated"})
    public String sortOrdersByLastUpdated(@RequestParam(required = false) String sortDir,
                                          Model model) {

        globalData.setCommonModelAttributes(model);
        List<DeliveryOrderEntity> sortedList = globalData.getCurrentViewOrderList().stream()
                .sorted(Comparator.comparing(DeliveryOrderEntity::getLastUpDated))
                .collect(Collectors.toList());
        model.addAttribute("orders", sortList(sortDir, model, sortedList));
        return "orders/orders";
    }

    @RequestMapping({"/destinations/sort/byid"})
    public String sortDestinationsById(@RequestParam(required = false) String sortDir,
                                       Model model) {
        globalData.setCommonModelAttributes(model);
        List<DestinationEntity> destinations = (List<DestinationEntity>) destinationRepository.findAll();
        destinations = destinations.stream()
                .sorted(Comparator.comparing(DestinationEntity::getId))
                .collect(Collectors.toList());

        model.addAttribute("destinations", sortList(sortDir, model, destinations));
        return "destination/destinations";
    }

    @RequestMapping({"/destinations/sort/byname"})
    public String sortDestinationsByName(@RequestParam(required = false) String sortDir,
                                         Model model) {
        globalData.setCommonModelAttributes(model);
        List<DestinationEntity> destinations = (List<DestinationEntity>) destinationRepository.findAll();
        destinations = destinations.stream()
                .sorted(Comparator.comparing(DestinationEntity::getDestinationName))
                .collect(Collectors.toList());

        model.addAttribute("destinations", sortList(sortDir, model, destinations));
        return "destination/destinations";
    }

    @RequestMapping({"/destinations/sort/bydistance"})
    public String sortDestinationsByDistance(@RequestParam(required = false) String sortDir,
                                             Model model) {
        globalData.setCommonModelAttributes(model);
        List<DestinationEntity> destinations = (List<DestinationEntity>) destinationRepository.findAll();
        destinations = destinations.stream()
                .sorted(Comparator.comparing(DestinationEntity::getDistance))
                .collect(Collectors.toList());
        model.addAttribute("destinations", sortList(sortDir, model, destinations));
        return "destination/destinations";
    }


    private List<?> sortList(String sortDir, Model model, List<?> sortedList) {
        if (Objects.isNull(sortDir) || sortDir.isEmpty() || sortDir.equalsIgnoreCase("asc")) {
            model.addAttribute("sortDir", "desc");
        } else {
            if (sortDir.equalsIgnoreCase("desc")) {
                Collections.reverse(sortedList);
                model.addAttribute("sortDir", "asc");
            } else {
//                TODO by default sorting order for invalid input is asc
            }
        }
        return sortedList;
    }


}
