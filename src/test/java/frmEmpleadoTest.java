import javax.persistence.EntityManagerFactory;
import model.DAO.EmpleadoDAO;
import utilities.Constants.PanelMode;
import utilities.DBConnection;
import view.frmEmpleado;

/**
 *
 * @author Ignacio Pineda Martín
 */
public class frmEmpleadoTest {
    
    public static void main(String[] args) {
        EntityManagerFactory emf = new DBConnection().getEntityManagerFactory();
        frmEmpleado.main(null);
        
        // Modo "Visualización" (requiere pasar información al 
        // formulario).
        frmEmpleado fe1 = new frmEmpleado();
        EmpleadoDAO sdao = new EmpleadoDAO(emf);
        if (sdao.buscarEmpleado(7)) {
            fe1.setJPAObject(sdao.getEmpleado());
            fe1.setFormMode(PanelMode.READ_ONLY);
            fe1.setVisible(true);
        }
        
        // Modo "Consulta"
        frmEmpleado fe2 = new frmEmpleado();
        fe2.setFormMode(PanelMode.SELECTION);
        // Hacemos visible el formulario.
        fe2.setVisible(true);
        
//        // Modo "CRUD"
//        frmEmpleado fe3 = new frmEmpleado();
//        // Hacemos visible el formulario.
//        fe3.setVisible(true);
    }
}
