package model.DAO;

//<editor-fold defaultstate="collapsed" desc=" Librerías importadas... ">
import controller.ClienteJpaController;
import controller.exceptions.NonexistentEntityException;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import model.entity.Cliente;
//</editor-fold>

/**
 *
 * @author Ignacio Pineda Martín
 */
public class ClienteDAO {
    
    private ClienteJpaController cjc;
    private Cliente cliente = new Cliente();
    
    /**
     * <p>Propiedad para almacenar el resultado de la última llamada a esta
     * clase.</p>
     *
     * <p>{@code true} El resultado fue correcto.
     * <br> {@code false} El resultado fue erróneo.</p>
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
    
    public Cliente getCliente() {
        return cliente;
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

    public ClienteDAO() {
    }

    public ClienteDAO(EntityManagerFactory emf) {
        this.cjc = new ClienteJpaController(emf);          
    }

    /**
     * Método para el alta de un nuevo {@link Cliente}.
     * 
     * @param c El objeto {@link Cliente} a crear.
     * @return <b>true</b> El {@link Cliente} ha sido dada de alta.
     * <br><b>false</b> El {@link Cliente} no ha sido dada de alta.
     */
    public boolean insertarCliente(Cliente c) {
        estado = false;
        mensaje = "insertarCliente -> ";
        try {
            c.setIdCliente(null);
            cjc.create(c);
            estado = true;
            mensaje = mensaje + "OK: " + c;
            cliente = c;
        } catch (Exception E) {
            mensaje = mensaje + "ERROR: " + c + "\n" + E.getMessage();
            System.out.println(mensaje);
        }      
        return estado;
    }

    /**
     * Método para la modificación de un {@link Cliente}.
     * 
     * @param c El objeto {@link Cliente} a modificar.
     * @return <b>true</b> El {@link Cliente} ha sido modificada.
     * <br><b>false</b> El {@link Cliente} no ha sido modificada.
     */
    public boolean actualizarCliente(Cliente c) {
        estado = false;
        mensaje = "actualizarCliente -> ";
        try {
            // Antes de lanzar el "edit", comprobamos que el cliente con 
            // el ID seleccionado existe. En caso contrario, lanzaremos una
            // excepción.
            if (cjc.findCliente(c.getIdCliente()) == null)
                throw new NonexistentEntityException("El Cliente con id " + c.getIdCliente() + " no existe.");            
            cjc.edit(c);
            estado = true;
            mensaje = mensaje + "OK: " + c;
            cliente = c;
        } catch (Exception E) {
            mensaje = mensaje + "ERROR: " + c + "\n" + E.getMessage();
            System.out.println(mensaje);
        }      
        return estado;
    }

    /**
     * Método para el borrado de un {@link Cliente}.
     * 
     * @param c El objeto {@link Cliente} a eliminar.
     * @return <b>true</b> El {@link Cliente} ha sido eliminada.
     * <br><b>false</b> El {@link Cliente} no ha sido eliminada.
     */
    public boolean eliminarCliente(Cliente c) {
        return eliminarCliente(c.getIdCliente());
    }

    /**
     * Método para el borrado de un {@link Cliente}.
     * 
     * @param idCliente El ID de la {@link Cliente} a eliminar.
     * @return <b>true</b> El {@link Cliente} ha sido eliminada.
     * <br><b>false</b> El {@link Cliente} no ha sido eliminada.
     */
    public boolean eliminarCliente(Integer idCliente) {
        estado = false;
        mensaje = "eliminarCliente -> ";
        try {
            cjc.destroy(idCliente);
            estado = true;
            mensaje = mensaje + "OK: ID=" + idCliente.toString();
        } catch (Exception E) {
            mensaje = mensaje + "ERROR: ID=" + idCliente.toString()
                    + "\n" + E.getMessage();
            System.out.println(mensaje);
        }
        return estado;
    }
    
    /**
     * Búsqueda de {@link Cliente} por su ID. Si se encuentra se devolverá
     * en la propiedad {@link cliente}, accesible mediante su "getter"
     * {@link getCliente()}.
     * 
     * @param idCliente El ID del {@link Cliente} a buscar.
     * @return <b>true</b> El {@link Cliente} ha sido encontrado.
     * <br><b>false</b> El {@link Cliente} no ha sido encontrado.
     */ 
    public boolean buscarCliente(Integer idCliente) {
        estado = false;
        mensaje = "buscarCliente -> ";
        try {
            cliente = cjc.findCliente(idCliente);
            if (cliente == null)
                throw new NonexistentEntityException("El Cliente con ID " + idCliente + " no existe.");
            estado = true;
            mensaje = mensaje + "OK: " + cliente;
        } catch (Exception E) {
            mensaje = mensaje + "ERROR: ID=" + idCliente.toString() + "\n" + E.getMessage();
            System.out.println(mensaje);
        }      
        return estado;
    }

    /**
     * Búsqueda de los clientes cuyo nombre coincida (parcialmente) con el 
     * nombre facilitado. En caso de que el nombre esté vacío, se devolverán 
     * todos los clientes.
     * 
     * @param nombreCliente El nombre (parcial) del cliente.
     * @return Lista de clientes que cumplan el criterio de búsqueda.
     */
    public List<Cliente> buscarCliente(String nombreCliente) {
        List<Cliente> lstCliente = null;
        EntityManager em = cjc.getEntityManager();
        try {
            Query q = em.createQuery("SELECT c FROM Cliente c " + 
                                     "WHERE c.nombreCliente LIKE :nombreCliente");
            q.setParameter("nombreCliente", "%" + nombreCliente + "%");
            lstCliente = q.getResultList();
        } finally {
            em.close();
        }
        return lstCliente;
    }

    /**
     * Búsqueda de los clientes cuyos apellidos coincidan (parcialmente) con los
     * apellidos facilitados. En caso de que los apellidos estén vacíos, se
     * devolverán todos los clientes que sean personas físicas: las personas
     * jurídicas no se devolverán como parte de la búsqueda.
     *
     * @param apellidosCliente El nombre (parcial) del cliente.
     * @return Lista de clientes que cumplan el criterio de búsqueda.
     */
    public List<Cliente> buscarClienteApellidos(String apellidosCliente) {
        List<Cliente> lstCliente = null;
        EntityManager em = cjc.getEntityManager();
        try {
            Query q = em.createQuery("SELECT c FROM Cliente c "
                                   + "WHERE c.apellidosCliente LIKE :apellidosCliente");
            q.setParameter("apellidosCliente", "%" + apellidosCliente + "%");
            lstCliente = q.getResultList();
        } finally {
            em.close();
        }
        return lstCliente;
    }
    
    /**
     * Búsqueda de los clientes cuya clave de identificación comience con la
     * clave facilitada.
     * 
     * @param claveIdentificacion La clave de identificación del cliente.
     * @return Lista de clientes que cumplan el criterio de búsqueda.
     */
    private List<Cliente> buscarClienteID(String claveIdentificacion) {
        List<Cliente> lstCliente = null;
        EntityManager em = cjc.getEntityManager();
        try {
            Query q = em.createQuery("SELECT c FROM Cliente c " + 
                                     "WHERE c.claveIdentificacion LIKE :claveIdentificacion");
            q.setParameter("claveIdentificacion", claveIdentificacion + "%");
            lstCliente = q.getResultList();
        } finally {
            em.close();
        }
        return lstCliente;
    }    
    
    /**
     * Búsqueda de los clientes cuyo identificación coincida con la
     * facilitada.
     * 
     * @param claveIdentificacion El documento de identificación del cliente.
     * @return Lista de clientes que cumplan el criterio de búsqueda.
     */
    public boolean buscarClaveCliente(String claveIdentificacion) {
        estado = false;
        mensaje = "buscarClaveCliente -> ";        
        EntityManager em = cjc.getEntityManager();
        try {
            Query q = em.createQuery("SELECT c FROM Cliente c " +
                                     "WHERE c.claveIdentificacion = :claveIdentificacion");
            q.setParameter("claveIdentificacion", claveIdentificacion);
            List<Cliente> lstCliente = q.getResultList();
            if (lstCliente.isEmpty())
                throw new NonexistentEntityException("El Cliente con clave " + claveIdentificacion + " no existe.");
            cliente = lstCliente.get(0);
            estado = true;
            mensaje = mensaje + "OK: " + cliente;
        } catch (Exception E) {
            mensaje = mensaje + "ERROR: claveIdentificacion=" + claveIdentificacion + "\n" + E.getMessage();
            System.out.println(mensaje);
        } finally {
            em.close();
        }      
        return estado;
    }
    
    /**
     * Lista el contenido de la tabla "Cliente" en un JTable.
     * 
     * @param tabla JTable a rellenar.
     * @param nombreCliente El nombre (parcial) del cliente a buscar.
     * Si este valor está en vacío, se devolverán todos los clientes.
     * @return <b>true</b> La ejecución se ha completado correctamente.
     * <br><b>false</b> La ejecución ha terminado con problemas. Los detalles
     * se pueden recuperar en la propiedad {@link mensaje}.
     */    
    public boolean listarClienteNombre(JTable tabla, String nombreCliente) {
        // Recuperamos el contenido de la tabla "Clientes" en un
        // objeto "List".
        List<Cliente> lstCliente = buscarCliente(nombreCliente);
        // Mostramos los clientes recuperados.
        return listarCliente(tabla, lstCliente);
    }

    /**
     * Lista el contenido de la tabla "Cliente" en un JTable.
     *
     * @param tabla JTable a rellenar.
     * @param apellidosCliente Los apellidos (parciales) del cliente a buscar.
     * Si este valor está en vacío, se devolverán todos los clientes que sean
     * personas físicas: las personas jurídicas no se devolverán como parte de
     * la búsqueda.
     * @return <b>true</b> La ejecución se ha completado correctamente.
     * <br><b>false</b> La ejecución ha terminado con problemas. Los detalles se
     * pueden recuperar en la propiedad {@link mensaje}.
     */
    public boolean listarClienteApellidos(JTable tabla, String apellidosCliente) {
        // Recuperamos el contenido de la tabla "Clientes" en un
        // objeto "List".
        List<Cliente> lstCliente = buscarClienteApellidos(apellidosCliente);
        // Mostramos los clientes recuperados.
        return listarCliente(tabla, lstCliente);
    }    
    
    /**
     * Lista el contenido de la tabla "Cliente" en un JTable. Se mostrarán
     * aquellos clientes cuya clave de identificación comience con la clave
     * aportada.
     * 
     * @param tabla JTable a rellenar.
     * @param claveIdentificacion La clave de identificación (parcial) del 
     * cliente a buscar.
     * @return <b>true</b> La ejecución se ha completado correctamente.
     * <br><b>false</b> La ejecución ha terminado con problemas. Los detalles
     * se pueden recuperar en la propiedad {@link mensaje}.
     */     
    public boolean listarClienteID(JTable tabla, String claveIdentificacion) {
        // Recuperamos el contenido de la tabla "Clientes" en un
        // objeto "List".
        List<Cliente> lstCliente = buscarClienteID(claveIdentificacion);
        // Mostramos los clientes recuperados.
        return listarCliente(tabla, lstCliente);
    }
    
    /**
     * Lista el contenido de la tabla "Cliente" en un JTable.
     * 
     * @param tabla JTable a rellenar.
     * @param lstCliente La lista de clientes a mostrar.
     * @return <b>true</b> La ejecución se ha completado correctamente.
     * <br><b>false</b> La ejecución ha terminado con problemas. Los detalles
     * se pueden recuperar en la propiedad {@link mensaje}.
     */
    public boolean listarCliente(JTable tabla, List<Cliente> lstCliente) {
        estado = false;
        mensaje = "listarCliente -> ";
        try {
            // "DefaultTableModel" sirve para definir el modelo de datos (columnas,
            // tipos de datos) que se mostrará en un JTable.
            DefaultTableModel dtm;
            String[] cabecera = {"ID", "NOMBRE"};
            dtm = new DefaultTableModel(null, cabecera);     

            // Definimos un array de "String" para almacenar todos los 
            // campos que queremos mostrar en nuestra tabla.
            String[] columnas = new String[2];
            for (Cliente e : lstCliente) {
                columnas[0] = e.getIdCliente().toString();
                columnas[1] = e.getNombreCliente() + " " + e.getApellidosCliente() + " (" + e.getClaveIdentificacion() + ")";
                // Añadimos una fila al "DefaultTableModel" que hemos definido
                // antes.
                dtm.addRow(columnas);
            }
            // Asignamos al "JTable" el "DefaultTableModel", que ya contiene
            // la información a mostrar.
            tabla.setModel(dtm);
            // Removemos la primera columna (ID) de la tabla, pero no así sus
            // datos (accesibles desde el modelo).
            tabla.removeColumn(tabla.getColumnModel().getColumn(0));
            
            estado = true;
            mensaje = mensaje + "OK: " + lstCliente.size() + " clientes recuperados.";
        } catch (Exception E) {
            mensaje = mensaje + "ERROR: " + cliente + "\n" + E.getMessage();
            System.out.println(mensaje);
        }
        return estado;
    }    
        
}
