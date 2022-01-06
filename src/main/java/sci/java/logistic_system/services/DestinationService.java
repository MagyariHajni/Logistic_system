package sci.java.logistic_system.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sci.java.logistic_system.domain.DestinationEntity;
import sci.java.logistic_system.domain.repository.DestinationRepository;

import java.io.BufferedReader;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class DestinationService {

    private DestinationRepository destinationRepository;

    @Autowired
    public void setDestinationRepository(DestinationRepository destinationRepository) {
        this.destinationRepository = destinationRepository;
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
