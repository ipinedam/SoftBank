package model.DAO;

import controller.PrestamoJpaController;
import controller.exceptions.NonexistentEntityException;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import model.entity.Cliente;
import model.entity.Prestamo;

/**
 * Clase "DATA ACCESS OBJECT" para {@link Prestamo}.
 * 
 * @author Ignacio Pineda Martín
 */
public class PrestamoDAO {

    private PrestamoJpaController pjc;
    private Prestamo prestamo = new Prestamo();
    
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
    
    public Prestamo getPrestamo() {
        return prestamo;
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
 
    public PrestamoDAO() {
    }

    public PrestamoDAO(EntityManagerFactory emf) {
        this.pjc = new PrestamoJpaController(emf);          
    }

    /**
     * Método para el alta de un nuevo {@link Prestamo}.
     * 
     * @param p El objeto {@link Prestamo} a crear.
     * @return <b>true</b> El {@link Prestamo} ha sido dada de alta.
     * <br><b>false</b> El {@link Prestamo} no ha sido dada de alta.
     */
    public boolean insertarPrestamo(Prestamo p) {
        estado = false;
        mensaje = "insertarPrestamo -> ";
        try {
            p.setIdPrestamo(null);
            p.setNumeroPrestamo(getNuevoNumeroPrestamo());
            pjc.create(p);
            estado = true;
            mensaje = mensaje + "OK: " + p;
            prestamo = p;
        } catch (Exception E) {
            mensaje = mensaje + "ERROR: " + p + "\n" + E.getMessage();
            System.out.println(mensaje);
        }      
        return estado;
    }
    
    /**
     * Método para obtener un nuevo número de préstamo.
     *
     * @return El nuevo número de préstamo.
     */
    private long getNuevoNumeroPrestamo() {
        long numeroPrestamo = 0L;
        Object queryResult;
        EntityManager em = pjc.getEntityManager();
        try {
            Query q = em.createQuery("SELECT MAX(p.numeroPrestamo) "
                                   + "FROM Prestamo p");
            queryResult = q.getSingleResult();
            // Comprobamos que el valor devuelto no sea nulo.
            if (queryResult != null) {
                // Incrementamos en 1 el número de préstamo.
                numeroPrestamo = (long) queryResult;
                numeroPrestamo += 1L;
            } else {
                // Asignamos el primer número de préstamo.
                numeroPrestamo = 3000000001L;
            }
        } catch (Exception E) {
            mensaje = mensaje + "ERROR: \n" + E.getMessage();
            System.out.println(mensaje);
        } finally {
            em.close();
        }
        return numeroPrestamo;
    }

    /**
     * Método para la modificación de una {@link Prestamo}.
     * 
     * @param tc El objeto {@link Prestamo} a modificar.
     * @return <b>true</b> El objeto {@link Prestamo} ha sido modificado.
     * <br><b>false</b> El objeto {@link Prestamo} no ha sido modificado.
     */
    public boolean actualizarPrestamo(Prestamo tc) {
        estado = false;
        mensaje = "actualizarPrestamo -> ";
        try {
            // Antes de lanzar el "edit", comprobamos que el cliente con 
            // el ID seleccionado existe. En caso contrario, lanzaremos una
            // excepción.
            if (pjc.findPrestamo(tc.getIdPrestamo()) == null)
                throw new NonexistentEntityException("El Prestamo con id " + tc.getIdPrestamo()+ " no existe.");            
            pjc.edit(tc);
            estado = true;
            mensaje = mensaje + "OK: " + tc;
            prestamo = tc;
        } catch (Exception E) {
            mensaje = mensaje + "ERROR: " + tc + "\n" + E.getMessage();
            System.out.println(mensaje);
        }      
        return estado;
    }
    
    /**
     * Recupera todos los préstamos almacenados en la BB.DD.
     * 
     * @return lista de objetos {@link Prestamo}.
     */
    public List<Prestamo> buscarPrestamo() {
        estado = false;
        mensaje = "buscarPrestamo -> ";
        List<Prestamo> lstPrestamo = null;
        EntityManager em = pjc.getEntityManager();
        try {
            lstPrestamo = pjc.findPrestamoEntities();
            if (lstPrestamo == null)
                throw new NonexistentEntityException("No existen objetos Prestamo (tabla vacía).");
            estado = true;
            mensaje = mensaje + "OK: " + lstPrestamo.size() + " préstamos recuperados.";
        } catch (Exception E) {
            mensaje = mensaje + "ERROR: \n" + E.getMessage();
            System.out.println(mensaje);            
        } finally {
            em.close();
        }
        return lstPrestamo;
    }    

    /**
     * Búsqueda de {@link Prestamo} por su ID. Si se encuentra se
     * devolverá en la propiedad {@link prestamo}, accesible mediante su
     * "getter" {@link getPrestamo()}.
     *
     * @param idPrestamo El ID del {@link Prestamo} a buscar.
     * @return <b>true</b> El {@link Prestamo} ha sido encontrado.
     * <br><b>false</b> El {@link Prestamo} no ha sido encontrado.
     */
    public boolean buscarPrestamo(Integer idPrestamo) {
        estado = false;
        mensaje = "buscarPrestamo -> ";
        EntityManager em = pjc.getEntityManager();
        try {
            prestamo = pjc.findPrestamo(idPrestamo);
            if (prestamo == null)
                throw new NonexistentEntityException(("El Prestamo con id " + idPrestamo + " no existe."));
            estado = true;
            mensaje = mensaje + "OK: " + prestamo;
        } catch (Exception E) {
            mensaje = mensaje + "ERROR: \n" + E.getMessage();
            System.out.println(mensaje);            
        } finally {
            em.close();
        }
        return estado;
    }
    
    /**
     * Búsqueda de {@link Prestamo} <b>activos</b> por número (parcial).
     * Si el parámetro está vacío, se devolveran todos los prestamos.
     *
     * @param numeroPrestamo El número (parcial) de la {@link Prestamo} a
     * buscar.
     * @return <b>true</b> El {@link Prestamo} ha sido encontrado.
     * <br><b>false</b> El {@link Prestamo} no ha sido encontrado.
     */
    private List<Prestamo> buscarPrestamo(String numeroPrestamo) {
        List<Prestamo> lstPrestamo = null;
        EntityManager em = pjc.getEntityManager();
        try {
            Query q = em.createQuery("SELECT p FROM Prestamo p " + 
                                     "WHERE p.numeroPrestamo LIKE :numeroPrestamo" + 
                                     "  AND p.productoBancario.fecCancelacion IS NULL", Prestamo.class);
            q.setParameter("numeroPrestamo", "%" + numeroPrestamo + "%");
            lstPrestamo = q.getResultList();
        } finally {
            em.close();
        }
        return lstPrestamo;
    }

    /**
     * Búsqueda de los {@link Prestamo} <b>activos</b> asociados a un
     * cliente.
     *
     * @param c El objeto {@link Cliente} de el/los {@link Prestamo} a
     * buscar.
     * @return <b>true</b> El {@link Prestamo} ha sido encontrado.
     * <br><b>false</b> El {@link Prestamo} no ha sido encontrado.
     */
    private List<Prestamo> buscarPrestamo(Cliente c) {
        List<Prestamo> lstPrestamo = null;
        EntityManager em = pjc.getEntityManager();
        try {
            Query q = em.createNativeQuery("SELECT P.* "
                                         + "FROM SOFTBANK.PRESTAMO P,"
                                         + "     SOFTBANK.PRODUCTO_BANCARIO PB,"
                                         + "     SOFTBANK.CLIENTE_PRODUCTO_BANCARIO CPB "
                                         + "WHERE P.ID_PRODUCTO_BANCARIO = PB.ID_PRODUCTO_BANCARIO"
                                         + "  AND PB.ID_PRODUCTO_BANCARIO = CPB.ID_PRODUCTO_BANCARIO"
                                         + "  AND PB.FEC_CANCELACION IS NULL"
                                         + "  AND CPB.ID_CLIENTE = ? "
                                         + "ORDER BY P.ID_PRESTAMO", Prestamo.class);
            q.setParameter(1, c.getIdCliente());
            lstPrestamo = (List<Prestamo>) q.getResultList();
        } finally {
            em.close();
        }
        return lstPrestamo;
    }

    /**
     * Búsqueda de los {@link Prestamo} <b>activos</b> pendientes de liquidación
     * a una fecha.
     *
     * @param fecLiquidacion La fecha a la que se van a liquidar el/los
     * {@link Prestamo} a buscar.
     * @return Lista de {@link Prestamo} a liquidar.
     */
    public List<Prestamo> buscarPrestamo(Date fecLiquidacion) {
        List<Prestamo> lstPrestamo = null;
        EntityManager em = pjc.getEntityManager();
        try {
            Query q = em.createQuery("SELECT p FROM Prestamo p "
                    + "WHERE p.productoBancario.fecCancelacion IS NULL"
                    + "  AND (p.productoBancario.fecApertura <= :fecLiquidacion)"
                    + "  AND (p.productoBancario.fecLiquidacion < :fecLiquidacion OR p.productoBancario.fecLiquidacion IS NULL)", Prestamo.class);
            q.setParameter("fecLiquidacion", fecLiquidacion, TemporalType.DATE);
            lstPrestamo = q.getResultList();
        } finally {
            em.close();
        }
        return lstPrestamo;
    }    
    
    /**
     * Lista lps {@link Prestamo} en un JTable que coincidan 
     * (parcialmente) con el número facilitado.
     *
     * @param tabla JTable a rellenar.
     * @param numeroPrestamo El número (parcial) de cuenta corriente a listar.
     * @return <b>true</b> La ejecución se ha completado correctamente.
     * <br><b>false</b> La ejecución ha terminado con problemas. Los detalles se
     * pueden recuperar en la propiedad {@link mensaje}.
     */
    public boolean listarPrestamoNumero(JTable tabla, String numeroPrestamo) {
        // Recuperamos el contenido de la tabla "CuentasCorrientes" en un
        // objeto "List".
        List<Prestamo> lstPrestamo = buscarPrestamo(numeroPrestamo);
        // Mostramos los clientes recuperados.
        return listarPrestamo(tabla, lstPrestamo);        
    }

    /**
     * Lista los {@link Prestamo} en un JTable que pertenezcan al cliente
     * facilitado.
     *
     * @param tabla JTable a rellenar.
     * @param cliente El {@link Cliente} al que pertenecen los préstamos a
     * listar.
     * @return <b>true</b> La ejecución se ha completado correctamente.
     * <br><b>false</b> La ejecución ha terminado con problemas. Los detalles se
     * pueden recuperar en la propiedad {@link mensaje}.
     */
    public boolean listarPrestamoCliente(JTable tabla, Cliente cliente) {
        // Recuperamos el contenido de la tabla "Prestamo" en un
        // objeto "List".
        List<Prestamo> lstPrestamo = buscarPrestamo(cliente);
        // Mostramos los clientes recuperados.
        return listarPrestamo(tabla, lstPrestamo);
    }
    
    /**
     * Lista los {@link Prestamo} en un JTable.
     *
     * @param tabla JTable a rellenar.
     * @param lstPrestamo La lista de préstamos a mostrar.
     * @return <b>true</b> La ejecución se ha completado correctamente.
     * <br><b>false</b> La ejecución ha terminado con problemas. Los detalles se
     * pueden recuperar en la propiedad {@link mensaje}.
     */
    public boolean listarPrestamo(JTable tabla, List<Prestamo> lstPrestamo) {
        estado = false;
        mensaje = "listarPrestamo -> ";
        try {
            // "DefaultTableModel" sirve para definir el modelo de datos (columnas,
            // tipos de datos) que se mostrará en un JTable.
            DefaultTableModel dtm;
            String[] cabecera = {"ID", "Nº CUENTA", "SUCURSAL"};
            dtm = new DefaultTableModel(null, cabecera); 

            // Definimos un array de "String" para almacenar todos los 
            // campos que queremos mostrar en nuestra tabla.
            String[] columnas = new String[3];
            for (Prestamo c : lstPrestamo) {
                columnas[0] = c.getIdPrestamo().toString();
                columnas[1] = String.format("%10d", c.getNumeroPrestamo());
                columnas[2] = String.format("%04d - %s", c.getProductoBancario().getSucursal().getCodSucursal(), c.getProductoBancario().getSucursal().getNombreSucursal());
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
            mensaje = mensaje + "OK: " + lstPrestamo.size() + " présamos recuperados.";
        } catch (Exception E) {
            mensaje = mensaje + "ERROR: " + prestamo + "\n" + E.getMessage();
            System.out.println(mensaje);
        }
        return estado;
    }      
    
}
