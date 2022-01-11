package sci.java.logistic_system.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;
@Entity
public class OrderStatusEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer orderId;
    private OrderStatus orderStatus;
    private LocalDateTime orderStatusDate;

    public Integer getId() {
        return orderId;
    }
    public void setId(Integer id) {
        this.orderId = id;
    }

    public Integer getOrderId() {
        return orderId;
    }
    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }
    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public LocalDateTime getOrderStatusDate() {
        return orderStatusDate;
    }
    public void setOrderStatusDate(LocalDateTime orderStatusDate) {
        this.orderStatusDate = orderStatusDate;
    }
}
