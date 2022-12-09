import javax.persistence.EntityManagerFactory;
import model.DAO.ClienteDAO;
import model.DAO.ProductoBancarioDAO;
import model.DAO.UsuarioDAO;
import utilities.Constants.FormAction;
import utilities.Constants.PanelMode;
import utilities.DBConnection;
import view.frmCuentaCorriente;

/**
 *
 * @author Ignacio Pineda Martín
 */
public class frmCuentaCorrienteTest {
    
    public static void main(String[] args) {
        EntityManagerFactory emf = new DBConnection().getEntityManagerFactory();
        UsuarioDAO udao = new UsuarioDAO(emf);
        udao.buscarUsuario("test");     // Empleado
        
        // Modo "READ_ONLY" + "QUERY" (Visualización de cuenta y movimientos.) 
        // (requiere pasar información al formulario).
        // Pasamos información de una cuenta corriente específica.
        frmCuentaCorriente fs1 = new frmCuentaCorriente();
        fs1.setAppUser(udao.getUsuario());
        ProductoBancarioDAO pdao = new ProductoBancarioDAO(emf);
        if (pdao.buscarProductoBancario(1)) {
            fs1.setFormModeAndAction(PanelMode.READ_ONLY, FormAction.QUERY);
            fs1.setJPAObject(pdao.getProductoBancario());
            fs1.setVisible(true);
        }

        // Modo "READ_ONLY" + "QUERY" (Visualización de cuenta y movimientos.) 
        // (requiere pasar información al formulario).
        // Pasamos información de un cliente (se mostrarán todas sus cuentas).
        frmCuentaCorriente fs2 = new frmCuentaCorriente();
        ClienteDAO cdao = new ClienteDAO(emf);
        if (cdao.buscarCliente(8)) {
            fs2.setAppUser(cdao.getCliente().getUsuario());
            fs2.setFormModeAndAction(PanelMode.READ_ONLY, FormAction.QUERY);
            fs2.setJPAObject(cdao.getCliente());
            fs2.setVisible(true);
        }
        
        // Modo "CRUD" + "NEW" (Alta de cuenta. Modo por defecto.)
        frmCuentaCorriente fs3 = new frmCuentaCorriente();
        fs3.setAppUser(udao.getUsuario());
        fs3.setFormMode(PanelMode.CRUD);
        fs3.setFormAction(FormAction.NEW);        
        // Hacemos visible el formulario.
        fs3.setVisible(true);
        
        // Modo "SELECTION" + "MODIFY" (Modificación de cuenta)
        frmCuentaCorriente fs4 = new frmCuentaCorriente();
        fs4.setAppUser(udao.getUsuario());        
        fs4.setFormMode(PanelMode.SELECTION);
        fs4.setFormAction(FormAction.MODIFY);        
        // Hacemos visible el formulario.
        fs4.setVisible(true);
        
        // Modo "SELECTION" + "CANCEL" (Cancelación de cuenta)
        frmCuentaCorriente fs5 = new frmCuentaCorriente();
        fs5.setAppUser(udao.getUsuario());        
        fs5.setFormModeAndAction(PanelMode.SELECTION, FormAction.CANCEL);
        // Hacemos visible el formulario.
        fs5.setVisible(true);

        // Modo "SELECTION" + "MOVEMENT" (Alta, baja y modificación de movimientos)
        frmCuentaCorriente fs6 = new frmCuentaCorriente();
        fs6.setAppUser(udao.getUsuario());        
        fs6.setFormModeAndAction(PanelMode.SELECTION, FormAction.MOVEMENT);
        // Hacemos visible el formulario.
        fs6.setVisible(true);
    }
}
