package sci.java.logistic_system.domain.repository;

import org.springframework.data.repository.CrudRepository;
import sci.java.logistic_system.domain.DestinationEntity;

import java.util.List;
import java.util.stream.Collectors;

public interface DestinationRepository extends CrudRepository<DestinationEntity, Integer> {

    DestinationEntity findDestinationEntityByDestinationName(String destinationName);

    default List<String> getAvailableDestinations() {
        List<DestinationEntity> allAvailableDestinations = (List<DestinationEntity>) findAll();
        return allAvailableDestinations.stream()
                .map(DestinationEntity::getDestinationName)
                .collect(Collectors.toList());
    }


}
