package sci.java.logistic_system.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import sci.java.logistic_system.domain.DeliveryOrderEntity;
import sci.java.logistic_system.domain.DestinationEntity;
import sci.java.logistic_system.domain.repository.DeliveryOrderRepository;
import sci.java.logistic_system.domain.repository.DestinationRepository;
import sci.java.logistic_system.services.DeliveryOrderService;
import sci.java.logistic_system.services.GlobalData;

import javax.servlet.http.HttpServletRequest;
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
    public static Logger logger = LoggerFactory.getLogger(GlobalData.class);

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
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        logger.trace("Viewing all destinations, accessed: " + request.getRequestURL());
        model.addAttribute("destinations", destinationRepository.findAll());
        globalData.setCommonModelAttributes(model);
        return "destination/destinations";
    }

    @GetMapping("destinations/{id}")
    public String getDestination(@PathVariable Integer id, Model model) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        logger.trace("Viewing destination with the id " + id + ", accessed: " + request.getRequestURL().append('?').append(request.getQueryString()));
        model.addAttribute("destination",
                destinationRepository.findById(id).isPresent() ?
                        destinationRepository.findById(id).get() : null);
        return "destination/destinationdetails";
    }

    @GetMapping("destinations/edit/{id}")
    public String editDestination(@PathVariable Integer id, Model model) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        logger.trace("Editing destination with the id " + id + ", accessed: " + request.getRequestURL().append('?').append(request.getQueryString()));
        model.addAttribute("destination", destinationRepository.findById(id).isPresent() ?
                destinationRepository.findById(id).get() : null);

        List<DeliveryOrderEntity> updatedCurrentView
                = deliveryOrderService.updateView((List<DeliveryOrderEntity>) deliveryOrderRepository.findAll(), globalData.getCurrentViewOrderList());
        globalData.setCurrentViewOrderList(updatedCurrentView);

        return "destination/destinationform";
    }

    @GetMapping("destinations/add")
    public String addDestination(Model model) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        logger.info("Submitted destination to add by form, accessed: " + request.getRequestURL().append('?').append(request.getQueryString()));
        model.addAttribute("destination", new DestinationEntity());
        return "destination/destinationform";
    }

    @PostMapping(value = "/destinations")
    public String saveOrUpdateDestination(DestinationEntity destination) {
        List<String> destinationNameList = destinationRepository.getAvailableDestinations();

        if ((!Objects.equals(destination.getDestinationName(), "")) && (!Objects.equals(destination.getDistance(), 0))) {
            String newDestinationName = destination.getDestinationName().substring(0, 1).toUpperCase()
                    + destination.getDestinationName().substring(1).toLowerCase();

            if (destinationNameList.contains(newDestinationName)) {
                DestinationEntity foundDestination = destinationRepository.findDestinationEntityByDestinationName(newDestinationName);
                if (Objects.isNull(foundDestination)) {
                    logger.info("Destination to " + destination.getDestinationName() + ", at " + destination.getDistance() + "km, successfully saved");
                    destinationRepository.save(destination);
                } else {
                    logger.info("Destination already exists, distance was modified from "
                            + foundDestination.getDistance() + "km to " + destination.getDistance() + "km");
                    foundDestination.setDistance(destination.getDistance());
                    destinationRepository.save(foundDestination);
                }
            } else {
                if (!Objects.isNull(destination.getId())) {
                    logger.info("Destination name modified from " + destination.getDestinationName() + " to " + newDestinationName);
                } else {
                    logger.info("Destination to " + destination.getDestinationName() + ", at " + destination.getDistance() + "km, successfully saved");
                }
                destinationRepository.save(destination);
            }
            return "redirect:/destinations/" + destination.getId();
        } else {
            logger.info("Destination couldn't be saved, because not all data was given");
            //TODO aici da eroare daca lasi km blank
            return "redirect:/destinations/";
        }

    }

    @GetMapping("destinations/delete/{id}")
    public String deleteDestination(@PathVariable Integer id) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        logger.info("Destination to be deleted with the id " + id + " accessed: " + request.getRequestURL().append('?').append(request.getQueryString()));
        Optional<DestinationEntity> destinationToDelete = destinationRepository.findById(id);
        if (destinationToDelete.isPresent()) {
            logger.info("Destination " + destinationToDelete.get().getDestinationName() +" was successfully deleted");
            List<DeliveryOrderEntity> allOrders = (List<DeliveryOrderEntity>) deliveryOrderRepository.findAll();
            List<DeliveryOrderEntity> ordersWithDestinationToDelete = allOrders.stream()
                    .filter(o1 -> Objects.equals(o1.getOrderDestination(), destinationToDelete.get()))
                    .collect(Collectors.toList());
            for (DeliveryOrderEntity order : ordersWithDestinationToDelete) {
                deliveryOrderService.deleteOrderDestination(order, globalData.getCurrentDate());
            }
        } else {
            logger.info("Destination couldn't be deleted, not in the repository");

        }

        List<DeliveryOrderEntity> updatedCurrentView
                = deliveryOrderService.updateView((List<DeliveryOrderEntity>) deliveryOrderRepository.findAll(), globalData.getCurrentViewOrderList());
        globalData.setCurrentViewOrderList(updatedCurrentView);
        destinationRepository.deleteById(id);
        return "redirect:/destinations/";
    }

}


