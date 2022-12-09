package model.DAO;

import controller.exceptions.NonexistentEntityException;
import controller.SucursalJpaController;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import model.entity.Sucursal;

/**
 *
 * @author Ignacio Pineda Martín
 */
public class SucursalDAO {
    
    private EntityManagerFactory emf;
    private SucursalJpaController sjc;
    private Sucursal sucursal = new Sucursal();
    
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
    
    public Sucursal getSucursal() {
        return sucursal;
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

    public SucursalDAO() {
    }

    public SucursalDAO(EntityManagerFactory emf) {
        this.emf = emf;
        this.sjc = new SucursalJpaController(emf);          
    }

    /**
     * Método para el alta de una nueva {@link Sucursal}.
     * 
     * @param s El objeto {@link Sucursal} a crear.
     * @return <b>true</b> La {@link Sucursal} ha sido dada de alta.
     * <br><b>false</b> La {@link Sucursal} no ha sido dada de alta.
     */
    public boolean insertarSucursal(Sucursal s) {
        estado = false;
        mensaje = "insertarSucursal -> ";
        try {
            s.setIdSucursal(null);
            sjc.create(s);
            estado = true;
            mensaje = mensaje + "OK: " + s;
            sucursal = s;
        } catch (Exception E) {
            mensaje = mensaje + "ERROR: " + s + "\n" + E.getMessage();
            System.out.println(mensaje);
        }      
        return estado;
    }

    /**
     * Método para la modificación de una {@link Sucursal}.
     * 
     * @param s El objeto {@link Sucursal} a modificar.
     * @return <b>true</b> La {@link Sucursal} ha sido modificada.
     * <br><b>false</b> La {@link Sucursal} no ha sido modificada.
     */
    public boolean actualizarSucursal(Sucursal s) {
        estado = false;
        mensaje = "actualizarSucursal -> ";
        try {
            // Antes de lanzar el "edit", comprobamos que la sucursal con 
            // el ID seleccionado existe. En caso contrario, lanzaremos una
            // excepción.
            if (sjc.findSucursal(s.getIdSucursal()) == null)
                throw new NonexistentEntityException("La Sucursal con id " + s.getIdSucursal() + " no existe.");            
            sjc.edit(s);
            estado = true;
            mensaje = mensaje + "OK: " + s;
            sucursal = s;
        } catch (Exception E) {
            mensaje = mensaje + "ERROR: " + s + "\n" + E.getMessage();
            System.out.println(mensaje);
        }      
        return estado;
    }

    /**
     * Método para el borrado de una {@link Sucursal}.
     * 
     * @param s El objeto {@link Sucursal} a eliminar.
     * @return <b>true</b> La {@link Sucursal} ha sido eliminada.
     * <br><b>false</b> La {@link Sucursal} no ha sido eliminada.
     */
    public boolean eliminarSucursal(Sucursal s) {
        return eliminarSucursal(s.getIdSucursal());
    }

    /**
     * Método para el borrado de una {@link Sucursal}.
     * 
     * @param idSucursal El ID de la {@link Sucursal} a eliminar.
     * @return <b>true</b> La {@link Sucursal} ha sido eliminada.
     * <br><b>false</b> La {@link Sucursal} no ha sido eliminada.
     */
    public boolean eliminarSucursal(Integer idSucursal) {
        estado = false;
        mensaje = "eliminarSucursal -> ";
        try {
            sjc.destroy(idSucursal);
            estado = true;
            mensaje = mensaje + "OK: ID=" + idSucursal.toString();
        } catch (Exception E) {
            mensaje = mensaje + "ERROR: ID=" + idSucursal.toString()
                    + "\n" + E.getMessage();
            System.out.println(mensaje);
        }
        return estado;
    }
    
    /**
     * Búsqueda de {@link Sucursal} por su ID. Si se encuentra se devolverá
     * en la propiedad {@link sucursal}, accesible mediante su "getter"
     * {@link getSucursal()}.
     * 
     * @param idSucursal El ID del {@link Sucursal} a buscar.
     * @return <b>true</b> La {@link Sucursal} ha sido encontrado.
     * <br><b>false</b> La {@link Sucursal} no ha sido encontrado.
     */ 
    public boolean buscarSucursal(Integer idSucursal) {
        estado = false;
        mensaje = "buscarSucursal -> ";
        try {
            sucursal = sjc.findSucursal(idSucursal);
            if (sucursal == null)
                throw new NonexistentEntityException("La Sucursal con id " + idSucursal + " no existe.");
            estado = true;
            mensaje = mensaje + "OK: " + sucursal;
        } catch (Exception E) {
            mensaje = mensaje + "ERROR: ID=" + idSucursal.toString() + "\n" + E.getMessage();
            System.out.println(mensaje);
        }      
        return estado;
    }

    /**
     * Búsqueda de las sucursales cuyo nombre coincida (parcialmente), con el 
     * nombre facilitado. En caso de que el nombre esté vacío, se devolverán todas
     * las sucursales.
     * 
     * @param nombreSucursal El nombre (parcial) de la sucursal.
     * @return Lista de sucursales que cumplan el criterio de búsqueda.
     */
    private List<Sucursal> buscarSucursal(String nombreSucursal) {
        List<Sucursal> lstSucursal = null;
        EntityManager em = sjc.getEntityManager();
        try {
            Query q = em.createQuery("SELECT s FROM Sucursal s " + 
                                     "WHERE s.nombreSucursal LIKE :nombreSucursal " +
                                     "ORDER BY s.codSucursal");
            q.setParameter("nombreSucursal", "%" + nombreSucursal + "%");
            lstSucursal = q.getResultList();
        } finally {
            em.close();
        }
        return lstSucursal;
    }

    /**
     * Búsqueda de {@link Sucursal} por su código de sucursal. Si se encuentra 
     * se devolverá en la propiedad {@link sucursal}, accesible mediante 
     * su "getter" {@link getSucursal()}.
     * 
     * @param codSucursal El código de la {@link Sucursal} a buscar.
     * @return <b>true</b> La {@link Sucursal} ha sido encontrado.
     * <br><b>false</b> La {@link Sucursal} no ha sido encontrado.
     */
    public boolean buscarCodSucursal(int codSucursal) {
        estado = false;
        mensaje = "buscarCodSucursal -> ";        
        EntityManager em = sjc.getEntityManager();
        try {
            Query q = em.createNamedQuery("Sucursal.findByCodSucursal");
            q.setParameter("codSucursal", codSucursal);
            List<Sucursal> lstSucursal = q.getResultList();
            if (lstSucursal.isEmpty())
                throw new NonexistentEntityException("La Sucursal con código " + codSucursal + " no existe.");
            sucursal = lstSucursal.get(0);
            estado = true;
            mensaje = mensaje + "OK: " + sucursal;
        } catch (Exception E) {
            mensaje = mensaje + "ERROR: ID=" + codSucursal + "\n" + E.getMessage();
            System.out.println(mensaje);
        } finally {
            em.close();
        }      
        return estado;
    }     

    /**
     * Lista el contenido de la tabla "Sucursal" en un JTable.
     * 
     * @param tabla JTable a rellenar.
     * @param nombreSucursal El nombre (parcial) de la sucursal a buscar.
     * Si este valor está en vacío, se devolverán todas las sucursales.
     * @return <b>true</b> La ejecución se ha completado correctamente.
     * <br><b>false</b> La ejecución ha terminado con problemas. Los detalles
     * se pueden recuperar en la propiedad {@link mensaje}.
     */
    public boolean listarSucursal(JTable tabla, String nombreSucursal) {
        estado = false;
        mensaje = "listarSucursal -> ";
        try {
            // "DefaultTableModel" sirve para definir el modelo de datos (columnas,
            // tipos de datos) que se mostrará en un JTable.
            DefaultTableModel dtm;
            String[] cabecera = {"ID", "COD.SUC.", "NOMBRE SUC."};
            dtm = new DefaultTableModel(null, cabecera);
            // Recuperamos el contenido de la tabla "Sucursals" en un
            // objeto "List".
            // List<Sucursal> lstSucursals = pjc.findSucursalEntities();
            List<Sucursal> lstSucursal = buscarSucursal(nombreSucursal);            

            // Definimos un array de "String" para almacenar todos los 
            // campos que queremos mostrar en nuestra tabla.
            String[] columnas = new String[3];
            for (Sucursal s : lstSucursal) {
                columnas[0] = s.getIdSucursal().toString();
                columnas[1] = Integer.toString(s.getCodSucursal());
                columnas[2] = s.getNombreSucursal();
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
            mensaje = mensaje + "OK: " + lstSucursal.size() + " sucursales recuperadas.";
        } catch (Exception E) {
            mensaje = mensaje + "ERROR: " + sucursal + "\n" + E.getMessage();
            System.out.println(mensaje);
        }
        return estado;
    }    
        
}
