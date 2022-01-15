package sci.java.logistic_system.domain;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class DeliveryOrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "DESTINATION_ID")
    private DestinationEntity orderDestination;

    @DateTimeFormat(pattern="dd-MM-YYYY")
    private LocalDateTime deliveryDate;
    private OrderStatus orderStatus;
    private LocalDateTime lastUpDated;
    private String destinationComment ="";

    public String getDestinationComment() {
        return destinationComment;
    }
    public void setDestinationComment(String destinationComment) {
        this.destinationComment = destinationComment;
    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public DestinationEntity getOrderDestination() {
        return orderDestination;
    }
    public void setOrderDestination(DestinationEntity orderDestination) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DeliveryOrderEntity)) return false;
        DeliveryOrderEntity that = (DeliveryOrderEntity) o;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getOrderDestination(), that.getOrderDestination()) && Objects.equals(getDeliveryDate(), that.getDeliveryDate()) && getOrderStatus() == that.getOrderStatus();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getOrderDestination(), getDeliveryDate(), getOrderStatus());
    }
}
