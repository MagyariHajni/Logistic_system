package sci.java.logistic_system.domain.repository;

import org.springframework.data.repository.CrudRepository;
import sci.java.logistic_system.domain.DeliveryOrderEntity;

public interface DeliveryOrderRepository extends CrudRepository<DeliveryOrderEntity, Integer> {

}
