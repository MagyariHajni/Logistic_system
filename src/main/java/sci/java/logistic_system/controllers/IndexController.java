package sci.java.logistic_system.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import sci.java.logistic_system.domain.DeliveryOrderEntity;
import sci.java.logistic_system.domain.repository.DeliveryOrderRepository;
import sci.java.logistic_system.domain.repository.DestinationRepository;
import sci.java.logistic_system.services.GlobalData;

import java.util.List;

@Controller
public class IndexController {
//    private DeliveryOrderRepository deliveryOrderRepository;
//    private DestinationRepository destinationRepository;
//
//
//    @Autowired
//    public void setDestinationRepository(DestinationRepository destinationRepository) {
//        this.destinationRepository = destinationRepository;
//    }
//
//    @Autowired
//    public void setDeliveryOrderRepository(DeliveryOrderRepository deliveryOrderRepository) {
//        this.deliveryOrderRepository = deliveryOrderRepository;
//    }

    @RequestMapping("/")
    public String index() {
        return "index";
    }


}
