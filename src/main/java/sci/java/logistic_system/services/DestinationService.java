package sci.java.logistic_system.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import sci.java.logistic_system.domain.DestinationEntity;
import sci.java.logistic_system.domain.repository.DestinationRepository;

import java.io.BufferedReader;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
public class DestinationService extends AbstractJpaDaoService {

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

    public DestinationRepository getDestinationRepository() {
        return destinationRepository;
    }

    public void loadDestinations() {
        Path fileIn = new File("src/main/resources/destinations.csv").toPath();
//        System.out.println(fileIn.toAbsolutePath());
        try (BufferedReader reader = Files.newBufferedReader(fileIn)) {
            String line;
            while ((line = reader.readLine()) != null) {
                convertAndSaveDestination(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void convertAndSaveDestination(String input) {
        String[] inputData = input.split(",");
        DestinationEntity destinationEntity = new DestinationEntity();
        destinationEntity.setDestinationName(inputData[0]);
        destinationEntity.setDistance(Integer.parseInt(inputData[1]));
        destinationRepository.save(destinationEntity);

    }
}
