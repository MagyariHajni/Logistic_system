package sci.java.logistic_system.domain.repository;

import org.springframework.data.repository.CrudRepository;
import sci.java.logistic_system.domain.OrderStatus;
import sci.java.logistic_system.domain.OrderStatusEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public interface OrderStatusRepository extends CrudRepository<OrderStatusEntity, Integer> {

    default List<OrderStatusEntity> findOrderStatusesById(Integer orderId) {
        List<OrderStatusEntity> orderStatusesById
                = (List<OrderStatusEntity>) findAll();
        return orderStatusesById.stream()
                .filter(orderStatus -> orderStatus.getOrderId() == orderId)
                .collect(Collectors.toList());
    }

    default OrderStatusEntity addOrderStatus(Integer orderId, OrderStatus statusToAdd, LocalDateTime time) {
        OrderStatusEntity orderStatusEntity = new OrderStatusEntity();
        orderStatusEntity.setOrderId(orderId);
        orderStatusEntity.setOrderStatus(statusToAdd);
        orderStatusEntity.setOrderStatusDate(time);
        save(orderStatusEntity);
        return orderStatusEntity;
    }

}
