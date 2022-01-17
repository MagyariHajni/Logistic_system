package sci.java.logistic_system.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sci.java.logistic_system.domain.DeliveryOrderEntity;
import sci.java.logistic_system.domain.OrderStatus;
import sci.java.logistic_system.domain.OrderStatusEntity;
import sci.java.logistic_system.domain.repository.OrderStatusRepository;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderStatusService extends AbstractJpaDaoService {
    private OrderStatusRepository orderStatusRepository;

    @Autowired
    public void setOrderStatusRepository(OrderStatusRepository orderStatusRepository) {
        this.orderStatusRepository = orderStatusRepository;
    }

    public List<OrderStatusEntity> listAll() {
        EntityManager em = emf.createEntityManager();
        return em.createQuery("from OrderStatusEntity", OrderStatusEntity.class).getResultList();
    }

    public List<OrderStatusEntity> getAllByOrderId(Integer orderId) {
        return listAll().stream().filter(orderStatusEntity -> orderStatusEntity.getOrderId()==orderId).collect(Collectors.toList());
    }

}
