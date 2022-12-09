import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import model.DAO.CatalogoDAO;
import utilities.Constants.PanelMode;
import view.frmCatalogo;

/**
 *
 * @author Ignacio Pineda Martín
 */
public class frmCatalogoTest {
    
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("SoftBank");
        frmCatalogo.main(null);
        
        // Modo "Visualización" (requiere pasar información al 
        // formulario).
        frmCatalogo fc1 = new frmCatalogo();
        CatalogoDAO cdao = new CatalogoDAO(emf);
        if (cdao.buscarCatalogo(1)) {
            fc1.setJPAObject(cdao.getCatalogo());
            fc1.setFormMode(PanelMode.READ_ONLY);
            fc1.setVisible(true);
        }
        
        // Modo "Consulta"
        frmCatalogo fc2 = new frmCatalogo();
        fc2.setFormMode(PanelMode.SELECTION);
        // Hacemos visible el formulario.
        fc2.setVisible(true);
        
//        // Modo "CRUD"
//        frmCatalogo fc3 = new frmCatalogo();
//        // Hacemos visible el formulario.
//        fc3.setVisible(true);
    }
}
