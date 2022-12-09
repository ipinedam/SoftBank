import javax.persistence.EntityManagerFactory;
import model.DAO.SucursalDAO;
import utilities.Constants.PanelMode;
import utilities.DBConnection;
import view.frmSucursal;

/**
 *
 * @author Ignacio Pineda Martín
 */
public class frmSucursalTest {
    
    public static void main(String[] args) {
        EntityManagerFactory emf = new DBConnection().getEntityManagerFactory();
        frmSucursal.main(null);
        
        // Modo "Visualización" (requiere pasar información al 
        // formulario).
        frmSucursal fs1 = new frmSucursal();
        SucursalDAO sdao = new SucursalDAO(emf);
        if (sdao.buscarSucursal(3)) {
            fs1.setJPAObject(sdao.getSucursal());
            fs1.setFormMode(PanelMode.READ_ONLY);
            fs1.setVisible(true);
        }

        // Modo "Consulta"
        frmSucursal fs2 = new frmSucursal();
        fs2.setFormMode(PanelMode.SELECTION);
        // Hacemos visible el formulario.
        fs2.setVisible(true);
        
//        // Modo "CRUD"
//        frmSucursal fs3 = new frmSucursal();
//        // Hacemos visible el formulario.
//        fs3.setVisible(true);
    }
}
