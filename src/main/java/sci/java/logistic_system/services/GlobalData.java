package sci.java.logistic_system.services;

import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import sci.java.logistic_system.domain.DeliveryOrderEntity;
import sci.java.logistic_system.domain.OrderStatus;
import sci.java.logistic_system.domain.ThymeleafBindingObject;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class GlobalData {

    LocalDateTime currentDate = LocalDateTime.of(2021, 12, 15, 8, 0);
    List<DeliveryOrderEntity> currentViewOrderList;
    Map<LocalDate, AtomicInteger> profitByDayMap = Collections.synchronizedMap(new HashMap<>());


    public LocalDateTime getCurrentDate() {
        return currentDate;
    }
    public void setCurrentDate(LocalDateTime currentDate) {
        this.currentDate = currentDate;
    }

    public List<DeliveryOrderEntity> getCurrentViewOrderList() {
        return currentViewOrderList;
    }
    public void setCurrentViewOrderList(List<DeliveryOrderEntity> currentViewOrderList) {
        this.currentViewOrderList = currentViewOrderList;
    }

    public Map<LocalDate, AtomicInteger> getProfitByDayMap() {
        return profitByDayMap;
    }

    public void setProfitByDayMap(Map<LocalDate, AtomicInteger> profitByDayMap) {
        this.profitByDayMap = profitByDayMap;
    }

    public void setCommonModelAttributes(Model model){
        model.addAttribute("selectedlist", new ThymeleafBindingObject());
        model.addAttribute("orders", getCurrentViewOrderList());
        model.addAttribute("currentdate", getCurrentDate().toLocalDate());
        model.addAttribute("statuses", OrderStatus.values());
    }


}
