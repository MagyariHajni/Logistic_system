package sci.java.logistic_system.domain;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class DeliveryOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "DESTINATION_ID")
    private Destination orderDestination;

    private LocalDateTime deliveryDate;
    private OrderStatus orderStatus;
    private LocalDateTime lastUpDated;

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public Destination getOrderDestination() {
        return orderDestination;
    }

    public void setOrderDestination(Destination orderDestination) {
        this.orderDestination = orderDestination;
    }

    public LocalDateTime getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(LocalDateTime deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public LocalDateTime getLastUpDated() {
        return lastUpDated;
    }

    public void setLastUpDated(LocalDateTime lastUpDated) {
        this.lastUpDated = lastUpDated;
    }
}
