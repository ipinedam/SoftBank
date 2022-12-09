import java.util.List;
import model.DAO.ProductoBancarioDAO;
import model.entity.ProductoBancario;


/**
 * Unidad de pruebas relacionadas con la clase {@link ProductoBancario}.
 * 
 * @author Ignacio Pineda Martín
 */
public class ProductoBancarioTest {
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ProductoBancarioDAO pdao = new ProductoBancarioDAO();
        List<ProductoBancario> lstProductoBancario = null;

        lstProductoBancario = pdao.buscarProductoBancario();
        if (pdao.isOK()) {
            for (ProductoBancario pb : lstProductoBancario) {
               System.out.println(pb);
           }           
        }
        System.out.println(pdao.getMensaje());
    }    
        
}
