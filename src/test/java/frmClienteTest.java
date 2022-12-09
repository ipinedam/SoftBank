import javax.persistence.EntityManagerFactory;
import model.DAO.ClienteDAO;
import model.DAO.UsuarioDAO;
import utilities.Constants.PanelMode;
import utilities.DBConnection;
import view.frmCliente;

/**
 *
 * @author Ignacio Pineda Martín
 */
public class frmClienteTest {
    
    public static void main(String[] args) {
        EntityManagerFactory emf = new DBConnection().getEntityManagerFactory();
        UsuarioDAO udao = new UsuarioDAO(emf);
        udao.buscarUsuario("test");     // Empleado
        
        // Modo "Visualización" (requiere pasar información al 
        // formulario).
        frmCliente fc1 = new frmCliente();
        ClienteDAO cdao = new ClienteDAO(emf);
        if (cdao.buscarCliente(13)) {
            fc1.setAppUser(cdao.getCliente().getUsuario());            
            fc1.setJPAObject(cdao.getCliente());
            fc1.setFormMode(PanelMode.READ_ONLY);
            fc1.setVisible(true);
        }

        // Modo "Consulta"
        frmCliente fc2 = new frmCliente();
        fc2.setAppUser(udao.getUsuario());        
        fc2.setFormMode(PanelMode.SELECTION);
        // Hacemos visible el formulario.
        fc2.setVisible(true);
        
        // Modo "CRUD"
        frmCliente fc3 = new frmCliente();
        fc3.setAppUser(udao.getUsuario());        
        // Hacemos visible el formulario.
        fc3.setVisible(true);
    }
}
