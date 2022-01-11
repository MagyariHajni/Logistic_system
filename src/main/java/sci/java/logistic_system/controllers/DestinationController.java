package sci.java.logistic_system.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import sci.java.logistic_system.domain.*;
import sci.java.logistic_system.domain.repository.DestinationRepository;
import sci.java.logistic_system.services.DestinationService;
import sci.java.logistic_system.services.GlobalData;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Controller
public class DestinationController {
    DestinationRepository destinationRepository;
    GlobalData globalData;

    @Autowired
    public void setGlobalData(GlobalData globalData) {
        this.globalData = globalData;
    }

    @Autowired
    public void setDestinationRepository(DestinationRepository destinationRepository) {
        this.destinationRepository = destinationRepository;
    }

    @RequestMapping({"destination/list", "destination/"})
    public String listDestinations(Model model) {
        model.addAttribute("destinations", destinationRepository.findAll());
        model.addAttribute("currentdate", globalData.getCurrentDate().toLocalDate());
//        model.addAttribute("selectedtocancel", new SelectedDeliveryOrders());
        return "destinations";
    }

    @RequestMapping("destination/{id}")
    public String getDestination(@PathVariable Integer id, Model model) {
        model.addAttribute("destination",
                destinationRepository.findById(id).isPresent() ?
                        destinationRepository.findById(id).get() : null);
        return "destinationdetails";
    }


    @RequestMapping("destination/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        model.addAttribute("destination", destinationRepository.findById(id).isPresent() ?
                destinationRepository.findById(id).get() : null);
        return "destinationform";
    }


    @RequestMapping(value = "/destination", method = RequestMethod.POST)
    public String updateOrder(@ModelAttribute("destination") DestinationEntity destination) {
        DestinationEntity savedDestination;
        savedDestination = destinationRepository.save(destination);
        return "redirect:destination/" + savedDestination.getId();
    }




    @PostMapping("/destinations/add")
    public ResponseEntity<DestinationEntity> addDestination(@Valid @RequestBody DestinationService dest) {
        return DestinationService.addDestination(dest);
    }

    @PutMapping("/destinations/update")
    public ResponseEntity<DestinationEntity> updateDestination(@Valid @RequestBody DestinationService dest) {
        return DestinationService.updateDestination(dest);
    }

    @GetMapping("/destinations")
    public List<DestinationEntity> getDestination(@RequestParam(required = false) Long destinationId) {
        if (destinationId == null) {
            return DestinationService.getAllDestinations(destinationId);
        } else {
            return DestinationService.getAllDestinations(destinationId);
        }
    }

    @GetMapping("/destinations/{destinationId}")
    public String getDestinationById(@RequestParam int destinationId) {
        return null;
    }

//    @DeleteMapping("/destinations/{destinationId}")
//    public void deleteDestination(@RequestParam long destinationId) {
//        DestinationService.deleteDestination((int) destinationId);
//    }
}


