package sci.java.logistic_system.services;

import org.springframework.stereotype.Service;
import sci.java.logistic_system.domain.DeliveryOrder;

import java.util.*;

@Service
public class OrderRepository {

    private Map<Integer, DeliveryOrder> allOrdersMap;

    public OrderRepository() {
        this.allOrdersMap = new HashMap<>();
    }

    public List<DeliveryOrder> listAll() {
        return new ArrayList<>(allOrdersMap.values());
    }

    public DeliveryOrder getById(Integer id) {
        return allOrdersMap.get(id);
    }

    public DeliveryOrder saveOrUpdate(DeliveryOrder order) {
        if (order != null) {
            if (order.getId() == null) {
                order.setId(getNextKey());
            }
            allOrdersMap.put(order.getId(), order);
            return order;
        } else {
            throw new RuntimeException("\u001B[38;5;213m" + "\u001B[41m"
                    + "Cannot be null, please fill all fields!!!"
                    + "\u001B[0m");
        }
    }

    private Integer getNextKey() {
        if (allOrdersMap.isEmpty()) {
            return 1;
        }
        return Collections.max(allOrdersMap.keySet()) + 1;
    }

}
