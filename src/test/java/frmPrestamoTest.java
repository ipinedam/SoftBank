import javax.persistence.EntityManagerFactory;
import model.DAO.ClienteDAO;
import model.DAO.ProductoBancarioDAO;
import model.DAO.UsuarioDAO;
import utilities.Constants.FormAction;
import utilities.Constants.PanelMode;
import utilities.DBConnection;
import view.frmPrestamo;

/**
 *
 * @author Ignacio Pineda Martín
 */
public class frmPrestamoTest {
    
    public static void main(String[] args) {
        EntityManagerFactory emf = new DBConnection().getEntityManagerFactory();
        UsuarioDAO udao = new UsuarioDAO(emf);
        udao.buscarUsuario("test");     // Empleado
        
        // Modo "READ_ONLY" + "QUERY" (Visualización de préstamo y movimientos.) 
        // (requiere pasar información al formulario).
        // Pasamos información de un préstao específico.
        frmPrestamo fp1 = new frmPrestamo();
        fp1.setAppUser(udao.getUsuario());
        ProductoBancarioDAO pdao = new ProductoBancarioDAO(emf);
        if (pdao.buscarProductoBancario(20)) {
            fp1.setFormModeAndAction(PanelMode.READ_ONLY, FormAction.QUERY);
            fp1.setJPAObject(pdao.getProductoBancario());
            fp1.setVisible(true);
        }

        // Modo "READ_ONLY" + "QUERY" (Visualización de préstamo y movimientos.) 
        // (requiere pasar información al formulario).
        // Pasamos información de un cliente (se mostrarán todos sus préstamos).
        frmPrestamo fp2 = new frmPrestamo();
        ClienteDAO cdao = new ClienteDAO(emf);
        if (cdao.buscarCliente(9)) {
            fp2.setAppUser(cdao.getCliente().getUsuario());
            fp2.setFormModeAndAction(PanelMode.READ_ONLY, FormAction.QUERY);
            fp2.setJPAObject(cdao.getCliente());
            fp2.setVisible(true);
        }
        
        // Modo "CRUD" + "NEW" (Alta de préstamo. Modo por defecto.)
        frmPrestamo fp3 = new frmPrestamo();
        fp3.setAppUser(udao.getUsuario());
        fp3.setFormMode(PanelMode.CRUD);
        fp3.setFormAction(FormAction.NEW);        
        // Hacemos visible el formulario.
        fp3.setVisible(true);
            
        // Modo "SELECTION" + "MODIFY" (Modificación de préstamo)
        frmPrestamo fp4 = new frmPrestamo();
        fp4.setAppUser(udao.getUsuario());
        fp4.setFormMode(PanelMode.SELECTION);
        fp4.setFormAction(FormAction.MODIFY);        
        // Hacemos visible el formulario.
        fp4.setVisible(true);
        
        // Modo "SELECTION" + "CANCEL" (Cancelación de préstamo)
        frmPrestamo fp5 = new frmPrestamo();
        fp5.setAppUser(udao.getUsuario());
        fp5.setFormModeAndAction(PanelMode.SELECTION, FormAction.CANCEL);
        // Hacemos visible el formulario.
        fp5.setVisible(true);

        // Modo "SELECTION" + "MOVEMENT" (Alta, baja y modificación de movimientos)
        frmPrestamo fp6 = new frmPrestamo();
        fp6.setAppUser(udao.getUsuario());
        fp6.setFormModeAndAction(PanelMode.SELECTION, FormAction.MOVEMENT);
        // Hacemos visible el formulario.
        fp6.setVisible(true);
    }
}
