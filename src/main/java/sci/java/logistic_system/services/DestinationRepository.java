package sci.java.logistic_system.services;

import org.springframework.stereotype.Service;
import sci.java.logistic_system.domain.Destination;

import java.util.*;

@Service
public class DestinationRepository {
    private Map<Integer, Destination> allDestinationsMap;

    public DestinationRepository() {
        this.allDestinationsMap = new HashMap<>();
    }

    public List<Destination> listAll() {
        return new ArrayList<>(allDestinationsMap.values());
    }

    public Destination getById(Integer id) {
        return allDestinationsMap.get(id);
    }


    public Destination getByName(String destinationName) {
        Optional<Destination> foundDestination = allDestinationsMap.values().stream().filter(
                destination -> destination.getDestinationName().equalsIgnoreCase(destinationName)).findFirst();
        return foundDestination.get();
    }


    public Destination saveOrUpdate(Destination destination) {
        if (destination != null) {
            if (destination.getId() == null) {
                destination.setId(getNextKey());
            }
            allDestinationsMap.put(destination.getId(), destination);
            return destination;
        } else {
            throw new RuntimeException("\u001B[38;5;213m" + "\u001B[41m"
                    + "Cannot be null, please fill all fields!!!"
                    + "\u001B[0m");
        }
    }

    private Integer getNextKey() {
        if (allDestinationsMap.isEmpty()) {
            return 1;
        }
        return Collections.max(allDestinationsMap.keySet()) + 1;
    }

}
