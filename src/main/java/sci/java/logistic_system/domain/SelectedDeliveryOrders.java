package sci.java.logistic_system.domain;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


public class SelectedDeliveryOrders {

    private List<DeliveryOrderEntity> selectedOrders;

    public List<DeliveryOrderEntity> getSelectedOrders() {
        return selectedOrders;
    }
    public void setSelectedOrders(List<DeliveryOrderEntity> selectedOrders) {
        this.selectedOrders = selectedOrders;
    }
}
