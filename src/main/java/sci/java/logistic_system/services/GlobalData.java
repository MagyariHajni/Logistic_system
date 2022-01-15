package sci.java.logistic_system.services;

import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import sci.java.logistic_system.domain.DeliveryOrderEntity;
import sci.java.logistic_system.domain.OrderStatus;
import sci.java.logistic_system.domain.ThymeleafBindingObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Component
public class GlobalData {

    LocalDateTime currentDate = LocalDateTime.of(2021, 12, 15, 8, 0);
    List<DeliveryOrderEntity> currentViewOrderList;
    Map<LocalDateTime, Integer> profitByDayMap;


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

    public Map<LocalDateTime, Integer> getProfitByDayMap() {
        return profitByDayMap;
    }
    public void setProfitByDayMap(Map<LocalDateTime, Integer> profitByDayMap) {
        this.profitByDayMap = profitByDayMap;
    }

    public void setCommonModelAttributes(Model model){
        model.addAttribute("selectedlist", new ThymeleafBindingObject());
        model.addAttribute("orders", getCurrentViewOrderList());
        model.addAttribute("currentdate", getCurrentDate().toLocalDate());
        model.addAttribute("statuses", OrderStatus.values());
    }


}
