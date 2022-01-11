package sci.java.logistic_system.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sci.java.logistic_system.domain.DestinationEntity;
import sci.java.logistic_system.services.DestinationService;

import javax.validation.Valid;
import java.util.List;

public class DestinationController {

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


