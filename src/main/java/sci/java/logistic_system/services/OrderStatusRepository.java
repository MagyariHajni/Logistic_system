package sci.java.logistic_system.services;

import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import sci.java.logistic_system.domain.DeliveryOrder;
import sci.java.logistic_system.domain.OrderStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderStatusRepository {

    private Map<Integer, List<Pair<LocalDateTime, OrderStatus>>> allOrderStatusChangeMap;

    public OrderStatusRepository() {
        this.allOrderStatusChangeMap = new HashMap<>();
    }


    public List<List<Pair<LocalDateTime, OrderStatus>>> listAll() {
        return new ArrayList<>(allOrderStatusChangeMap.values());
    }

    public Map<Integer, List<Pair<LocalDateTime, OrderStatus>>> listMap(){
        return allOrderStatusChangeMap;
    }

    public List<Pair<LocalDateTime, OrderStatus>> getById(Integer id) {
        return allOrderStatusChangeMap.get(id);
    }

    public void addOrderStatusUpdate(Integer id, LocalDateTime date, OrderStatus newStatus){
        List<Pair<LocalDateTime, OrderStatus>> foundList = allOrderStatusChangeMap.get(id);
        if (foundList == null){
            List<Pair<LocalDateTime, OrderStatus>> list= new ArrayList<>();
            list.add(Pair.of(date,newStatus));
            allOrderStatusChangeMap.put(id,list);
        } else {
            foundList.add(Pair.of(date,newStatus));
        }



//     for (Integer ids : allOrderStatusChangeMap.keySet()){
//        for(Pair<LocalDateTime,OrderStatus> pair: allOrderStatusChangeMap.get(ids)){
//            System.out.println(pair.getFirst());
//            System.out.println(pair.getSecond());
//        }
//     }
    }

}
