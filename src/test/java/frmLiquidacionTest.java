import javax.persistence.EntityManagerFactory;
import model.DAO.CatalogoDAO;
import utilities.DBConnection;
import view.frmLiquidacion;

/**
 *
 * @author Ignacio Pineda Martín
 */
public class frmLiquidacionTest {
    
    public static void main(String[] args) {
        EntityManagerFactory emf = new DBConnection().getEntityManagerFactory();
        CatalogoDAO cdao = new CatalogoDAO(emf);
       
        // Liquidación de tarjetas de crédito.
        frmLiquidacion fl1 = new frmLiquidacion(cdao.getTarjetaCredito());
        fl1.setVisible(true);
        
        // Liquidación de préstamos.
        frmLiquidacion fl2 = new frmLiquidacion(cdao.getPrestamo());
        fl2.setVisible(true);
    }

}
