package model.DAO;

//<editor-fold defaultstate="collapsed" desc=" Librerías importadas... ">
import controller.UsuarioJpaController;
import controller.exceptions.NonexistentEntityException;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import model.entity.Usuario;
//</editor-fold>

/**
 *
 * @author Ignacio Pineda Martín
 */
public class UsuarioDAO {
    
    private EntityManagerFactory emf;    
    private UsuarioJpaController ujc;
    private Usuario usuario = new Usuario();
    
     /**
     * <p>Propiedad para almacenar el resultado de la última llamada a esta
     * clase.</p>
     * 
     * <p>{@code true} El resultado fue correcto.<br>
     * {@code false} El resultado fue erróneo.</p>
     */
    private boolean estado;
    
    /**
     * <p>Propiedad para almacenar información sobre el resultado de la 
     * última llamada a esta clase.</p>
     * 
     * <p>El formato de su contenido será {@literal <nombrefunción>} -> 
     * "OK:"/"ERROR:" {@literal <detalles>}</p>
     */    
    private String mensaje;
    
    /**
     * "Getter" para la propiedad {@link usuario}
     * 
     * @return El contenido del objeto {@link usuario}
     */
    public Usuario getUsuario() {
        return usuario;
    }
    
    public boolean getEstado() {
        return estado;
    }
    
    public boolean isOK() {
        return (estado == true);
    }

    public String getMensaje() {
        return mensaje;
    }

    public UsuarioDAO() {
    }

    public UsuarioDAO(EntityManagerFactory emf) {
        this.emf = emf;
        this.ujc = new UsuarioJpaController(emf);          
    }
    
    public boolean insertarUsuario(Usuario u) {
        return insertarUsuario(u.getCodUsuario(), u.getPassword());
    }
    
    public boolean insertarUsuario(String codUsuario, String password) {
        estado = false;
        mensaje = "insertarUsuario -> ";
        try {
            this.usuario.setCodUsuario(codUsuario);
            this.usuario.setPassword(password);
            ujc.create(this.usuario);
            estado = true;
            mensaje = mensaje + "OK: " + usuario;
        } catch (Exception E) {
            mensaje = mensaje + "ERROR: " + usuario + "\n" + E.getMessage();
            System.out.println(mensaje);
        }      
        return estado;
    }
    
    public boolean actualizarUsuario(Usuario u) {
        return actualizarUsuario(u.getIdUsuario(), u.getCodUsuario(), 
                                 u.getPassword());
    }
    
    public boolean actualizarUsuario(Integer idUsuario, String codUsuario, 
                                     String password) {
        estado = false;
        mensaje = "actualizarUsuario -> ";
        try {
            this.usuario.setIdUsuario(idUsuario);
            this.usuario.setCodUsuario(codUsuario);
            this.usuario.setPassword(password);
            // Antes de lanzar el "edit", comprobamos que el usuario con 
            // el ID seleccionado existe. En caso contrario, lanzaremos una
            // excepción.
            if (ujc.findUsuario(idUsuario) == null)
                throw new NonexistentEntityException("El Usuario con id " + idUsuario + " no existe.");            
            ujc.edit(this.usuario);
            estado = true;
            mensaje = mensaje + "OK: " + usuario;
        } catch (Exception E) {
            mensaje = mensaje + "ERROR: " + usuario + "\n" + E.getMessage();
            System.out.println(mensaje);
        }      
        return estado;
    }
    
    public boolean eliminarUsuario(Usuario u) {
        return eliminarUsuario(u.getIdUsuario());
    }
    
    public boolean eliminarUsuario(Integer idUsuario) {
        estado = false;
        mensaje = "eliminarUsuario -> ";
        try {
            ujc.destroy(idUsuario);
            estado = true;
            mensaje = mensaje + "OK: ID=" + idUsuario.toString();
        } catch (Exception E) {
            mensaje = mensaje + "ERROR: ID=" + idUsuario.toString()
                    + "\n" + E.getMessage();
            System.out.println(mensaje);
        }
        return estado;
    }
    
    /**
     * Búsqueda de {@link Usuario} por su ID. Si se encuentra se devolverá
     * en la propiedad {@link usuario}, accesible mediante su "getter"
     * {@link getUsuario()}.
     * 
     * @param idUsuario El ID del {@link Usuario} a buscar.
     * @return <b>true</b> El {@link Usuario} ha sido encontrado.
     * <br><b>false</b> El {@link Usuario} no ha sido encontrado.
     */ 
    public boolean buscarUsuario(Integer idUsuario) {
        estado = false;
        mensaje = "buscarUsuario -> ";
        try {
            usuario = ujc.findUsuario(idUsuario);
            if (usuario == null)
                throw new NonexistentEntityException("El Usuario con id " + idUsuario + " no existe.");
            estado = true;
            mensaje = mensaje + "OK: " + usuario;
        } catch (Exception E) {
            mensaje = mensaje + "ERROR: ID=" + idUsuario.toString() + "\n" + E.getMessage();
            System.out.println(mensaje);
        }      
        return estado;
    }

    /**
     * Búsqueda de {@link Usuario} por su código de usuario. Si se encuentra, 
     * se devolverá en la propiedad {@link usuario}, accesible mediante 
     * su "getter" @link getUsuario()}.
     * 
     * @param idUsuario El ID del {@link Usuario} a buscar.
     * @return <b>true</b> El {@link Usuario} ha sido encontrado.
     * <br><b>false</b> El {@link Usuario} no ha sido encontrado.
     */ 
    public boolean buscarUsuario(String codUsuario) {
        estado = false;
        mensaje = "buscarUsuario -> ";
        EntityManager em = ujc.getEntityManager();
        try {
            Query q = em.createNamedQuery("Usuario.findByCodUsuario");
            q.setParameter("codUsuario", codUsuario);
            List<Usuario> lstUsuario = q.getResultList();
            if (!lstUsuario.isEmpty()) {
                // El usuario se ha encontrado.
                usuario = lstUsuario.get(0);
                estado = true;
                mensaje = mensaje + "OK: " + codUsuario + " encontrado.";                     
            } else {
                mensaje =  mensaje + "ERROR: " + codUsuario + " NO encontrado.";
                System.out.println(mensaje);                   
            }            
        } catch (Exception E) {
            mensaje = mensaje + "ERROR: " + codUsuario + "\n" + E.getMessage();
            System.out.println(mensaje);            
        } finally {
            em.close();
        }        
        return estado;
    }
    
    /**
     * Función para la validación de usuario/contraseña.
     * 
     * @param codUsuario Código de usuario
     * @param password Contraseña del usuario
     * @param tipoUsuario <ul><li>{@code Cliente} El usuario debe corresponder a un cliente.</li>
     *                        <li>{@code Empleado} El usuario debe corresponder a un empleado.</li></ul>
     * @return {@code true} El usuario/contraseña son correctos.
     * <br>{@code false} El usuario/contraseña son incorrectos.
     */
    public boolean login(String codUsuario, String password, 
                         String tipoUsuario) {
        estado = false;
        mensaje = "login -> ";
        EntityManager em = ujc.getEntityManager();
        Query q;
        try {
            if (tipoUsuario.equals("Cliente")) {
                q = em.createQuery("SELECT u FROM Usuario u " + 
                                          "WHERE u.codUsuario = :codUsuario" +
                                          "  AND u.password = :password" +
                                          "  AND u.cliente IS NOT NULL");
            } else {
                q = em.createQuery("SELECT u FROM Usuario u " +
                                          "WHERE u.codUsuario = :codUsuario" +
                                          "  AND u.password = :password" +
                                          "  AND u.empleado IS NOT NULL");
            }
            q.setParameter("codUsuario", codUsuario);
            q.setParameter("password", password);
            List<Usuario> lstUsuario = q.getResultList();
            if (!lstUsuario.isEmpty()) {
                // El usuario/contraseña se han encontrado.
                usuario = lstUsuario.get(0);
                estado = true;
                mensaje = mensaje + "OK: " + codUsuario + " logueado correctamente.";                     
            } else {
                mensaje = mensaje + "ERROR: usuario/contraseña/tipo incorrectos.";
                System.out.println(mensaje);                
            }            
        } catch (Exception E) {
            mensaje = mensaje + "ERROR: " + codUsuario + "\n" + E.getMessage();
            System.out.println(mensaje);
        } finally {
            em.close();
        }        
        return estado;
    }
    
}
