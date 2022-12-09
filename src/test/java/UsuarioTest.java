import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import model.DAO.UsuarioDAO;
import model.entity.Usuario;

/**
 * Unidad de pruebas relacionadas con la clase {@link Usuario}.
 * 
 * @author Ignacio Pineda Martín
 */
public class UsuarioTest {
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("SoftBank");        
        UsuarioDAO udao = new UsuarioDAO(emf);

        if (udao.buscarUsuario("jsanchezp")) {
            System.out.println(udao.getUsuario());
        }
        System.out.println(udao.getMensaje());
    }

}
