package model.DAO;

import controller.exceptions.NonexistentEntityException;
import controller.EmpleadoJpaController;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import model.entity.Empleado;

/**
 *
 * @author Ignacio Pineda Martín
 */
public class EmpleadoDAO {
    
    private EntityManagerFactory emf;
    private EmpleadoJpaController ejc;
    private Empleado empleado = new Empleado();
    
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
    
    public Empleado getEmpleado() {
        return empleado;
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

    public EmpleadoDAO() {
    }

    public EmpleadoDAO(EntityManagerFactory emf) {
        this.emf = emf;
        this.ejc = new EmpleadoJpaController(emf);          
    }

    /**
     * Método para el alta de un nuevo {@link Empleado}.
     * 
     * @param e El objeto {@link Empleado} a crear.
     * @return <b>true</b> El {@link Empleado} ha sido dada de alta.
     * <br><b>false</b> El {@link Empleado} no ha sido dada de alta.
     */
    public boolean insertarEmpleado(Empleado e) {
        estado = false;
        mensaje = "insertarEmpleado -> ";
        try {
            e.setIdEmpleado(null);
            ejc.create(e);
            estado = true;
            mensaje = mensaje + "OK: " + e;
            empleado = e;
        } catch (Exception E) {
            mensaje = mensaje + "ERROR: " + e + "\n" + E.getMessage();
            System.out.println(mensaje);
        }      
        return estado;
    }

    /**
     * Método para la modificación de un {@link Empleado}.
     * 
     * @param e El objeto {@link Empleado} a modificar.
     * @return <b>true</b> El {@link Empleado} ha sido modificada.
     * <br><b>false</b> El {@link Empleado} no ha sido modificada.
     */
    public boolean actualizarEmpleado(Empleado e) {
        estado = false;
        mensaje = "actualizarEmpleado -> ";
        try {
            // Antes de lanzar el "edit", comprobamos que el empleado con 
            // el ID seleccionado existe. En caso contrario, lanzaremos una
            // excepción.
            if (ejc.findEmpleado(e.getIdEmpleado()) == null)
                throw new NonexistentEntityException("El Empleado con id " + e.getIdEmpleado() + " no existe.");            
            ejc.edit(e);
            estado = true;
            mensaje = mensaje + "OK: " + e;
            empleado = e;
        } catch (Exception E) {
            mensaje = mensaje + "ERROR: " + e + "\n" + E.getMessage();
            System.out.println(mensaje);
        }      
        return estado;
    }

    /**
     * Método para el borrado de un {@link Empleado}.
     * 
     * @param e El objeto {@link Empleado} a eliminar.
     * @return <b>true</b> El {@link Empleado} ha sido eliminada.
     * <br><b>false</b> El {@link Empleado} no ha sido eliminada.
     */
    public boolean eliminarEmpleado(Empleado e) {
        return eliminarEmpleado(e.getIdEmpleado());
    }

    /**
     * Método para el borrado de un {@link Empleado}.
     * 
     * @param idEmpleado El ID de la {@link Empleado} a eliminar.
     * @return <b>true</b> El {@link Empleado} ha sido eliminada.
     * <br><b>false</b> El {@link Empleado} no ha sido eliminada.
     */
    public boolean eliminarEmpleado(Integer idEmpleado) {
        estado = false;
        mensaje = "eliminarEmpleado -> ";
        try {
            ejc.destroy(idEmpleado);
            estado = true;
            mensaje = mensaje + "OK: ID=" + idEmpleado.toString();
        } catch (Exception E) {
            mensaje = mensaje + "ERROR: ID=" + idEmpleado.toString()
                    + "\n" + E.getMessage();
            System.out.println(mensaje);
        }
        return estado;
    }
    
    /**
     * Búsqueda de {@link Empleado} por su ID. Si se encuentra se devolverá
     * en la propiedad {@link empleado}, accesible mediante su "getter"
     * {@link getEmpleado()}.
     * 
     * @param idEmpleado El ID del {@link Empleado} a buscar.
     * @return <b>true</b> El {@link Empleado} ha sido encontrado.
     * <br><b>false</b> El {@link Empleado} no ha sido encontrado.
     */ 
    public boolean buscarEmpleado(Integer idEmpleado) {
        estado = false;
        mensaje = "buscarEmpleado -> ";
        try {
            empleado = ejc.findEmpleado(idEmpleado);
            if (empleado == null)
                throw new NonexistentEntityException("El Empleado con ID " + idEmpleado + " no existe.");
            estado = true;
            mensaje = mensaje + "OK: " + empleado;
        } catch (Exception E) {
            mensaje = mensaje + "ERROR: ID=" + idEmpleado.toString() + "\n" + E.getMessage();
            System.out.println(mensaje);
        }      
        return estado;
    }

    /**
     * Búsqueda de los empleados cuyos apellidos coincidan (parcialmente) con
     * los apellidos facilitados. En caso de que los apellidos estén vacíos, se
     * devolverán todos los empleadoss.
     *
     * @param apellidosEmpleado Los apellidos (parciales) del empleado.
     * @return Lista de empleados que cumplan el criterio de búsqueda.
     */
    private List<Empleado> buscarEmpleado(String apellidosEmpleado) {
        List<Empleado> lstEmpleado = null;
        EntityManager em = ejc.getEntityManager();
        try {
            Query q = em.createQuery("SELECT e FROM Empleado e "
                    + "WHERE e.apellidosEmpleado LIKE :apellidosEmpleado");
            q.setParameter("apellidosEmpleado", "%" + apellidosEmpleado + "%");
            lstEmpleado = q.getResultList();
        } finally {
            em.close();
        }
        return lstEmpleado;
    }
    
    /**
     * Búsqueda de los empleados cuyo nombre y apellidos coincidan con los
     * facilitados. 
     * 
     * @param nombreEmpleado El nombre del empleado.
     * @param apellidosEmpleado Los apellidos del empleado.
     * @return Lista de empleados que cumplan el criterio de búsqueda.
     */
    public boolean buscarEmpleado(String nombreEmpleado, String apellidosEmpleado) {
        estado = false;
        mensaje = "buscarEmpleado -> ";        
        EntityManager em = ejc.getEntityManager();
        try {
            Query q = em.createQuery("SELECT e FROM Empleado e " +
                                     "WHERE e.nombreEmpleado = :nombreEmpleado" +
                                     "  AND e.apellidosEmpleado = :apellidosEmpleado");
            q.setParameter("nombreEmpleado", nombreEmpleado);
            q.setParameter("apellidosEmpleado", apellidosEmpleado);
            List<Empleado> lstEmpleado = q.getResultList();
            if (lstEmpleado.isEmpty())
                throw new NonexistentEntityException("El Empleado " + nombreEmpleado + " " + apellidosEmpleado + " no existe.");
            empleado = lstEmpleado.get(0);
            estado = true;
            mensaje = mensaje + "OK: " + empleado;
        } catch (Exception E) {
            mensaje = mensaje + "ERROR: Empleado=" + nombreEmpleado + " " + apellidosEmpleado + "\n" + E.getMessage();
            System.out.println(mensaje);
        } finally {
            em.close();
        }      
        return estado;
    }

    /**
     * Lista el contenido de la tabla "Empleado" en un JTable.
     *
     * @param tabla JTable a rellenar.
     * @param apellidosEmpleado Los apellidos (parciales) del empleado a buscar.
     * Si este valor está en vacío, se devolverán todos los empleados.
     * @return <b>true</b> La ejecución se ha completado correctamente.
     * <br><b>false</b> La ejecución ha terminado con problemas. Los detalles se
     * pueden recuperar en la propiedad {@link mensaje}.
     */
    public boolean listarEmpleado(JTable tabla, String apellidosEmpleado) {
        estado = false;
        mensaje = "listarEmpleado -> ";
        try {
            // "DefaultTableModel" sirve para definir el modelo de datos (columnas,
            // tipos de datos) que se mostrará en un JTable.
            DefaultTableModel dtm;
            String[] cabecera = {"ID", "NOMBRE"};
            dtm = new DefaultTableModel(null, cabecera);
            // Recuperamos el contenido de la tabla "Empleados" en un
            // objeto "List".
            // List<Empleado> lstEmpleados = pjc.findEmpleadoEntities();
            List<Empleado> lstEmpleado = buscarEmpleado(apellidosEmpleado);            

            // Definimos un array de "String" para almacenar todos los 
            // campos que queremos mostrar en nuestra tabla.
            String[] columnas = new String[2];
            for (Empleado e : lstEmpleado) {
                columnas[0] = e.getIdEmpleado().toString();
                columnas[1] = e.getNombreEmpleado() + " " + e.getApellidosEmpleado();
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
            mensaje = mensaje + "OK: " + lstEmpleado.size() + " empleados recuperados.";
        } catch (Exception E) {
            mensaje = mensaje + "ERROR: " + empleado + "\n" + E.getMessage();
            System.out.println(mensaje);
        }
        return estado;
    }    
        
}
