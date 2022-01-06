package sci.java.logistic_system.domain.repository;

import org.springframework.data.repository.CrudRepository;
import sci.java.logistic_system.domain.DestinationEntity;

public interface DestinationRepository extends CrudRepository<DestinationEntity, Integer> {

    DestinationEntity findDestinationEntityByDestinationName(String destinationName);

}
