package sci.java.logistic_system.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import sci.java.logistic_system.domain.DeliveryOrderEntity;
import sci.java.logistic_system.domain.DestinationEntity;
import sci.java.logistic_system.domain.repository.DeliveryOrderRepository;
import sci.java.logistic_system.domain.repository.DestinationRepository;
import sci.java.logistic_system.services.DeliveryOrderService;
import sci.java.logistic_system.services.GlobalData;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class DestinationController {
    DestinationRepository destinationRepository;
    DeliveryOrderRepository deliveryOrderRepository;
    private GlobalData globalData;
    private DeliveryOrderService deliveryOrderService;

    @Autowired
    public void setGlobalData(GlobalData globalData) {
        this.globalData = globalData;
    }

    @Autowired
    public void setDestinationRepository(DestinationRepository destinationRepository) {
        this.destinationRepository = destinationRepository;
    }

    @Autowired
    public void setDeliveryOrderRepository(DeliveryOrderRepository deliveryOrderRepository) {
        this.deliveryOrderRepository = deliveryOrderRepository;
    }

    @Autowired
    public void setDeliveryOrderService(DeliveryOrderService deliveryOrderService) {
        this.deliveryOrderService = deliveryOrderService;
    }

    @GetMapping({"destinations/list", "destinations/"})
    public String listDestinations(Model model) {
        model.addAttribute("destinations", destinationRepository.findAll());
        globalData.setCommonModelAttributes(model);
        return "destinations";
    }

    @GetMapping("destinations/{id}")
    public String getDestination(@PathVariable Integer id, Model model) {
        model.addAttribute("destination",
                destinationRepository.findById(id).isPresent() ?
                        destinationRepository.findById(id).get() : null);
        return "destinationdetails";
    }

    @GetMapping("destinations/edit/{id}")
    public String editDestination(@PathVariable Integer id, Model model) {
        model.addAttribute("destination", destinationRepository.findById(id).isPresent() ?
                destinationRepository.findById(id).get() : null);

        List<DeliveryOrderEntity> updatedCurrentView
                = deliveryOrderService.updateView((List<DeliveryOrderEntity>) deliveryOrderRepository.findAll(), globalData.getCurrentViewOrderList());
        globalData.setCurrentViewOrderList(updatedCurrentView);

        return "destinationform";
    }

    @GetMapping("destinations/add")
    public String addDestination(Model model) {
        model.addAttribute("destination", new DestinationEntity());
        return "destinationform";
    }

    @PostMapping(value = "/destinations")
    public String saveOrUpdateDestination(DestinationEntity destination) {
        List<String> destinationNameList = destinationRepository.getAvailableDestinations();
        String newDestinationName = destination.getDestinationName().substring(0, 1).toUpperCase()
                + destination.getDestinationName().substring(1).toLowerCase();
        if ((!Objects.equals(destination.getDestinationName(), "")) && (!Objects.equals(destination.getDistance(), ""))) {
            if (destinationNameList.contains(newDestinationName)) {
//    TODO log+file destination couldn't be saved because it already exists
                return "redirect:destinations/";
            } else {
                DestinationEntity savedDestination = destinationRepository.save(destination);
                return "redirect:destinations/" + savedDestination.getId();
            }
        } else {
            //TODO  log+file destination couldn't be saved because not all data was given
            return "redirect:destinations/";
        }

    }

    @GetMapping("destinations/delete/{id}")
    public String deleteDestination(@PathVariable Integer id) {

        Optional<DestinationEntity> destinationToDelete = destinationRepository.findById(id);
        if (destinationToDelete.isPresent()) {

            List<DeliveryOrderEntity> allOrders = (List<DeliveryOrderEntity>) deliveryOrderRepository.findAll();
            List<DeliveryOrderEntity> ordersWithDestinationToDelete = allOrders.stream()
                    .filter(o1 -> Objects.equals(o1.getOrderDestination(), destinationToDelete.get()))
                    .collect(Collectors.toList());

            for (DeliveryOrderEntity order : ordersWithDestinationToDelete) {
                deliveryOrderService.deleteOrderDestination(order, globalData.getCurrentDate());
            }

        } else {
//            TODO log+file destination couldn't be delete, not in the repository
        }

        List<DeliveryOrderEntity> updatedCurrentView
                = deliveryOrderService.updateView((List<DeliveryOrderEntity>) deliveryOrderRepository.findAll(), globalData.getCurrentViewOrderList());
        globalData.setCurrentViewOrderList(updatedCurrentView);

        destinationRepository.deleteById(id);
        return "redirect:/destinations/";
    }


//
//    @PostMapping("/destinations/add")
//    public ResponseEntity<DestinationEntity> addDestination(@Valid @RequestBody DestinationService dest) {
//        return DestinationService.addDestination(dest);
//    }
//
//    @PutMapping("/destinations/update")
//    public ResponseEntity<DestinationEntity> updateDestination(@Valid @RequestBody DestinationService dest) {
//        return DestinationService.updateDestination(dest);
//    }
//
//    @GetMapping("/destinations")
//    public List<DestinationEntity> getDestination(@RequestParam(required = false) Long destinationId) {
//        if (destinationId == null) {
//            return DestinationService.getAllDestinations(destinationId);
//        } else {
//            return DestinationService.getAllDestinations(destinationId);
//        }
//    }
//
//    @GetMapping("/destinations/{destinationId}")
//    public String getDestinationById(@RequestParam int destinationId) {
//        return null;
//    }

//    @DeleteMapping("/destinations/{destinationId}")
//    public void deleteDestination(@RequestParam long destinationId) {
//        DestinationService.deleteDestination((int) destinationId);
//    }
}


