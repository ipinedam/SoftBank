package utilities;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * Clase para declarar la conexión a la BB.DD.
 *
 * @author Ignacio Pineda Martín
 */
public class DBConnection {

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("SoftBank");

    public EntityManagerFactory getEntityManagerFactory() {
        return this.emf;
    }

}
