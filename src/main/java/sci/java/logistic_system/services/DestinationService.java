package sci.java.logistic_system.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import sci.java.logistic_system.domain.DeliveryOrderEntity;
import sci.java.logistic_system.domain.DestinationEntity;
import sci.java.logistic_system.domain.OrderStatus;
import sci.java.logistic_system.domain.repository.DestinationRepository;

import javax.persistence.EntityManager;
import java.io.BufferedReader;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
public class DestinationService extends AbstractJpaDaoService{

    private DestinationRepository destinationRepository;

    @Autowired
    public void setDestinationRepository(DestinationRepository destinationRepository) {
        this.destinationRepository = destinationRepository;
    }
    public static ResponseEntity<DestinationEntity> updateDestination(DestinationService dest) {
        return null;
    }

    public static ResponseEntity<DestinationEntity> addDestination(DestinationService dest) {
        return null;
    }
    public static List<DestinationEntity> getAllDestinations(Long destinationId) {
        return null;
    }
//    public static void deleteDestination(int destinationId) {
//        DestinationRepository.findById(destinationId).ifPresent(DestinationRepository::delete);
//    }
    public DestinationRepository getDestinationRepository() {
        return destinationRepository;
    }

    public void loadDestinations() {
        Path fileIn = new File("src/main/resources/destinations.csv").toPath();
//        System.out.println(fileIn.toAbsolutePath());
        try (BufferedReader reader = Files.newBufferedReader(fileIn)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] inputData = line.split(",");
                DestinationEntity destinationEntity = new DestinationEntity();
                destinationEntity.setDestinationName(inputData[0]);
                destinationEntity.setDistance(Integer.parseInt(inputData[1]));
                destinationRepository.save(destinationEntity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

//        for (Destination destination : destinationRepository.listAll()) {
//            System.out.println(destination.getId() + " "
//                    + destination.getDestinationName() + " "
//                    + destination.getDistance());
//        }

    }
}
