import javax.persistence.EntityManagerFactory;
import model.DAO.ClienteDAO;
import model.DAO.ProductoBancarioDAO;
import model.DAO.UsuarioDAO;
import utilities.Constants.FormAction;
import utilities.Constants.PanelMode;
import utilities.DBConnection;
import view.frmTarjetaCredito;

/**
 *
 * @author Ignacio Pineda Martín
 */
public class frmTarjetaCreditoTest {
    
    public static void main(String[] args) {
        EntityManagerFactory emf = new DBConnection().getEntityManagerFactory();
        UsuarioDAO udao = new UsuarioDAO(emf);
        udao.buscarUsuario("test");     // Empleado
        
        // Modo "READ_ONLY" + "QUERY" (Visualización de tarjeta y movimientos.) 
        // (requiere pasar información al formulario).
        // Pasamos información de una tarjeta de crédito específica.
        frmTarjetaCredito ft1 = new frmTarjetaCredito();
        ft1.setAppUser(udao.getUsuario());
        ProductoBancarioDAO pdao = new ProductoBancarioDAO(emf);
        if (pdao.buscarProductoBancario(4)) {
            ft1.setFormModeAndAction(PanelMode.READ_ONLY, FormAction.QUERY);
            ft1.setJPAObject(pdao.getProductoBancario());
            ft1.setVisible(true);
        }

        // Modo "READ_ONLY" + "QUERY" (Visualización de tarjeta y movimientos.) 
        // (requiere pasar información al formulario).
        // Pasamos información de un cliente (se mostrarán todas sus tarjetas).
        frmTarjetaCredito ft2 = new frmTarjetaCredito();
        ClienteDAO cdao = new ClienteDAO(emf);
        if (cdao.buscarCliente(1)) {
            ft2.setAppUser(cdao.getCliente().getUsuario());
            ft2.setFormModeAndAction(PanelMode.READ_ONLY, FormAction.QUERY);
            ft2.setJPAObject(cdao.getCliente());
            ft2.setVisible(true);
        }
        
        // Modo "CRUD" + "NEW" (Alta de tarjeta. Modo por defecto.)
        frmTarjetaCredito ft3 = new frmTarjetaCredito();
        ft3.setAppUser(udao.getUsuario());
        ft3.setFormMode(PanelMode.CRUD);
        ft3.setFormAction(FormAction.NEW);        
        // Hacemos visible el formulario.
        ft3.setVisible(true);
        
        // Modo "SELECTION" + "MODIFY" (Modificación de tarjeta)
        frmTarjetaCredito ft4 = new frmTarjetaCredito();
        ft4.setAppUser(udao.getUsuario());
        ft4.setFormMode(PanelMode.SELECTION);
        ft4.setFormAction(FormAction.MODIFY);        
        // Hacemos visible el formulario.
        ft4.setVisible(true);
        
        // Modo "SELECTION" + "CANCEL" (Cancelación de tarjeta)
        frmTarjetaCredito ft5 = new frmTarjetaCredito();
        ft5.setAppUser(udao.getUsuario());
        ft5.setFormModeAndAction(PanelMode.SELECTION, FormAction.CANCEL);
        // Hacemos visible el formulario.
        ft5.setVisible(true);

        // Modo "SELECTION" + "MOVEMENT" (Alta, baja y modificación de movimientos)
        frmTarjetaCredito ft6 = new frmTarjetaCredito();
        ft6.setAppUser(udao.getUsuario());
        ft6.setFormModeAndAction(PanelMode.SELECTION, FormAction.MOVEMENT);
        // Hacemos visible el formulario.
        ft6.setVisible(true);
    }
}
