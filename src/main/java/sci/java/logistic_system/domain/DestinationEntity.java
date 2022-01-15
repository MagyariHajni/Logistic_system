package sci.java.logistic_system.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Objects;

@Entity
public class DestinationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String destinationName;
    private int distance;

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public String getDestinationName() {
        return destinationName;
    }
    public void setDestinationName(String destinationName) {
        this.destinationName = destinationName;
    }

    public int getDistance() {
        return distance;
    }
    public void setDistance(int distance) {
        this.distance = distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DestinationEntity)) return false;
        DestinationEntity that = (DestinationEntity) o;
        return getDistance() == that.getDistance() && Objects.equals(getId(), that.getId()) && Objects.equals(getDestinationName(), that.getDestinationName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getDestinationName(), getDistance());
    }
}
