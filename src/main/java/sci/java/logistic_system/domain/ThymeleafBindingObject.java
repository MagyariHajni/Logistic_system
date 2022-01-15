package sci.java.logistic_system.domain;

import java.util.ArrayList;
import java.util.List;


public class ThymeleafBindingObject {

    private int numberOfOrders;
    private List<DeliveryOrderEntity> listOfOrders;
    private List<DeliveryOrderData> listOfOrderData = new ArrayList<>();

    public List<DeliveryOrderData> getListOfOrderData() {
        return listOfOrderData;
    }

    public void setListOfOrderData(List<DeliveryOrderData> listOfOrderData) {
        this.listOfOrderData = listOfOrderData;
    }

    public void addDeliveryOrderData(DeliveryOrderData newData) {
        this.listOfOrderData.add(newData);
    }

    public List<DeliveryOrderEntity> getListOfOrders() {
        return listOfOrders;
    }

    public void setListOfOrders(List<DeliveryOrderEntity> listOfOrders) {
        this.listOfOrders = listOfOrders;
    }

    public int getNumberOfOrders() {
        return numberOfOrders;
    }

    public void setNumberOfOrders(int numberOfOrders) {
        this.numberOfOrders = numberOfOrders;
    }
}
