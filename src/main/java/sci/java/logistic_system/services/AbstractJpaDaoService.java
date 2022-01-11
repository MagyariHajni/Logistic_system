package sci.java.logistic_system.services;

import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

public abstract class AbstractJpaDaoService {
    protected EntityManagerFactory emf;

    @PersistenceUnit
    public void setEmf(EntityManagerFactory emf) {
        this.emf = emf;
    }
}
