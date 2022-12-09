package model.DAO;

import controller.CuentaCorrienteJpaController;
import controller.exceptions.NonexistentEntityException;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import model.entity.Cliente;
import model.entity.CuentaCorriente;

import utilities.Banking;

/**
 * Clase "DATA ACCESS OBJECT" para {@link CuentaCorriente}.
 * 
 * @author Ignacio Pineda Martín
 */
public class CuentaCorrienteDAO {

    private CuentaCorrienteJpaController cjc;
    private CuentaCorriente cuentaCorriente = new CuentaCorriente();
    
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
    
    public CuentaCorriente getCuentaCorriente() {
        return cuentaCorriente;
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
 
    public CuentaCorrienteDAO() {
    }

    public CuentaCorrienteDAO(EntityManagerFactory emf) {
        this.cjc = new CuentaCorrienteJpaController(emf);          
    }

    /**
     * Método para el alta de una nueva {@link CuentaCorriente}.
     * 
     * @param cc El objeto {@link CuentaCorriente} a crear.
     * @return <b>true</b> La {@link CuentaCorriente} ha sido dada de alta.
     * <br><b>false</b> La {@link CuentaCorriente} no ha sido dada de alta.
     */
    public boolean insertarCuentaCorriente(CuentaCorriente cc) {
        estado = false;
        mensaje = "insertarCuentaCorriente -> ";
        try {
            cc.setIdCuentaCorriente(null);
            cc.setNumeroCuenta(getNuevoNumeroCuenta());
            cc.setIban(Banking.getIBAN(cc.getProductoBancario().getSucursal().getCodSucursal(), cc.getNumeroCuenta()));
            cjc.create(cc);
            estado = true;
            mensaje = mensaje + "OK: " + cc;
            cuentaCorriente = cc;
        } catch (Exception E) {
            mensaje = mensaje + "ERROR: " + cc + "\n" + E.getMessage();
            System.out.println(mensaje);
        }      
        return estado;
    }
    
    /**
     * Método para obtener un nuevo número de cuenta.
     * 
     * @return El nuevo número de cuenta.
     */
    private int getNuevoNumeroCuenta() {
        Integer numeroCuenta = 0;
        Object queryResult;
        EntityManager em = cjc.getEntityManager();
        try {
            Query q = em.createQuery("SELECT MAX(cc.numeroCuenta) "
                    + "FROM CuentaCorriente cc");
            queryResult = q.getSingleResult();
            // Comprobamos que el valor devuelto no sea nulo.
            if (queryResult != null) {
                // Incrementamos en 1 el número de cuenta.
                numeroCuenta = (Integer) queryResult;
                numeroCuenta += 1;
            } else {
                // Asignamos el primer número de cuenta.
                numeroCuenta = 1000000001;
            }
        } catch (Exception E) {
            mensaje = mensaje + "ERROR: \n" + E.getMessage();
            System.out.println(mensaje);   
        } finally {
            em.close();
        }
        return numeroCuenta;
    }

    /**
     * Método para la modificación de una {@link CuentaCorriente}.
     * 
     * @param cc El objeto {@link CuentaCorriente} a modificar.
     * @return <b>true</b> El objeto {@link CuentaCorriente} ha sido modificado.
     * <br><b>false</b> El objeto {@link CuentaCorriente} no ha sido modificado.
     */
    public boolean actualizarCuentaCorriente(CuentaCorriente cc) {
        estado = false;
        mensaje = "actualizarCuentaCorriente -> ";
        try {
            // Antes de lanzar el "edit", comprobamos que el cliente con 
            // el ID seleccionado existe. En caso contrario, lanzaremos una
            // excepción.
            if (cjc.findCuentaCorriente(cc.getIdCuentaCorriente()) == null)
                throw new NonexistentEntityException("La CuentaCorriente con id " + cc.getIdCuentaCorriente()+ " no existe.");            
            cjc.edit(cc);
            estado = true;
            mensaje = mensaje + "OK: " + cc;
            cuentaCorriente = cc;
        } catch (Exception E) {
            mensaje = mensaje + "ERROR: " + cc + "\n" + E.getMessage();
            System.out.println(mensaje);
        }      
        return estado;
    }
    
    /**
     * Recupera todos las cuentas corrientes almacenadas en la BB.DD.
     * 
     * @return lista de objetos {@link CuentaCorriente}.
     */
    public List<CuentaCorriente> buscarCuentaCorriente() {
        estado = false;
        mensaje = "buscarCuentaCorriente -> ";
        List<CuentaCorriente> lstCuentaCorriente = null;
        EntityManager em = cjc.getEntityManager();
        try {
            lstCuentaCorriente = cjc.findCuentaCorrienteEntities();
            if (lstCuentaCorriente == null)
                throw new NonexistentEntityException("No existen CuentaCorrientes (tabla vacía).");
            estado = true;
            mensaje = mensaje + "OK: " + lstCuentaCorriente.size() + " cuentas recuperadas.";
        } catch (Exception E) {
            mensaje = mensaje + "ERROR: \n" + E.getMessage();
            System.out.println(mensaje);            
        } finally {
            em.close();
        }
        return lstCuentaCorriente;
    }    

    /**
     * Búsqueda de {@link CuentaCorriente} por su ID. Si se encuentra se
     * devolverá en la propiedad {@link cuentaCorriente}, accesible mediante su
     * "getter" {@link getCuentaCorriente()}.
     *
     * @param idCuentaCorriente El ID de la {@link CuentaCorriente} a buscar.
     * @return <b>true</b> La {@link CuentaCorriente} ha sido encontrado.
     * <br><b>false</b> La {@link CuentaCorriente} no ha sido encontrado.
     */
    public boolean buscarCuentaCorriente(Integer idCuentaCorriente) {
        estado = false;
        mensaje = "buscarCuentaCorriente -> ";
        EntityManager em = cjc.getEntityManager();
        try {
            cuentaCorriente = cjc.findCuentaCorriente(idCuentaCorriente);
            if (cuentaCorriente == null)
                throw new NonexistentEntityException(("La CuentaCorriente con id " + idCuentaCorriente + " no existe."));
            estado = true;
            mensaje = mensaje + "OK: " + cuentaCorriente;
        } catch (Exception E) {
            mensaje = mensaje + "ERROR: \n" + E.getMessage();
            System.out.println(mensaje);            
        } finally {
            em.close();
        }
        return estado;
    }
    
    /**
     * Búsqueda de {@link CuentaCorriente} <b>activas</b> por número (parcial).
     * Si el parámetro está vacío, se devolveran todas las cuentas.
     *
     * @param numeroCuenta El número (parcial) de la {@link CuentaCorriente} a
     * buscar.
     * @return <b>true</b> La {@link CuentaCorriente} ha sido encontrado.
     * <br><b>false</b> La {@link CuentaCorriente} no ha sido encontrado.
     */
    private List<CuentaCorriente> buscarCuentaCorriente(String numeroCuenta) {
        List<CuentaCorriente> lstCuentaCorriente = null;
        EntityManager em = cjc.getEntityManager();
        try {
            Query q = em.createQuery("SELECT cc FROM CuentaCorriente cc " + 
                                     "WHERE cc.numeroCuenta LIKE :numeroCuenta" + 
                                     "  AND cc.productoBancario.fecCancelacion IS NULL", CuentaCorriente.class);
            q.setParameter("numeroCuenta", "%" + numeroCuenta + "%");
            lstCuentaCorriente = q.getResultList();
        } finally {
            em.close();
        }
        return lstCuentaCorriente;
    }

    /**
     * Búsqueda de las {@link CuentaCorriente} <b>activas</b> asociadas a un
     * cliente.
     *
     * @param c El objeto {@link Cliente} de la(s) {@link CuentaCorriente} a
     * buscar.
     * @return <b>true</b> La {@link CuentaCorriente} ha sido encontrado.
     * <br><b>false</b> La {@link CuentaCorriente} no ha sido encontrado.
     */
    private List<CuentaCorriente> buscarCuentaCorriente(Cliente c) {
        List<CuentaCorriente> lstCuentaCorriente = null;
        EntityManager em = cjc.getEntityManager();
        try {
            Query q = em.createNativeQuery("SELECT CC.* "
                                         + "FROM SOFTBANK.CUENTA_CORRIENTE CC,"
                                         + "     SOFTBANK.PRODUCTO_BANCARIO PB,"
                                         + "     SOFTBANK.CLIENTE_PRODUCTO_BANCARIO CPB "
                                         + "WHERE CC.ID_PRODUCTO_BANCARIO = PB.ID_PRODUCTO_BANCARIO"
                                         + "  AND PB.ID_PRODUCTO_BANCARIO = CPB.ID_PRODUCTO_BANCARIO"
                                         + "  AND PB.FEC_CANCELACION IS NULL"
                                         + "  AND CPB.ID_CLIENTE = ? "
                                         + "ORDER BY CC.ID_CUENTA_CORRIENTE", CuentaCorriente.class);
            q.setParameter(1, c.getIdCliente());
            lstCuentaCorriente = (List<CuentaCorriente>) q.getResultList();
        } finally {
            em.close();
        }
        return lstCuentaCorriente;
    }

    /**
     * Lista las {@link CuentaCorriente} en un JTable que coincidan 
     * (parcialmente) con el número facilitado.
     *
     * @param tabla JTable a rellenar.
     * @param numeroCuenta El número (parcial) de cuenta corriente a listar.
     * @return <b>true</b> La ejecución se ha completado correctamente.
     * <br><b>false</b> La ejecución ha terminado con problemas. Los detalles se
     * pueden recuperar en la propiedad {@link mensaje}.
     */
    public boolean listarCuentaCorrienteNumero(JTable tabla, String numeroCuenta) {
        // Recuperamos el contenido de la tabla "CuentasCorrientes" en un
        // objeto "List".
        List<CuentaCorriente> lstCuentaCorriente = buscarCuentaCorriente(numeroCuenta);
        // Mostramos los clientes recuperados.
        return listarCuentaCorriente(tabla, lstCuentaCorriente);        
    }

    /**
     * Lista las {@link CuentaCorriente} en un JTable que pertenezcan al cliente
     * facilitado.
     *
     * @param tabla JTable a rellenar.
     * @param cliente El {@link Cliente} al que pertenecen las cuentas a listar.
     * @return <b>true</b> La ejecución se ha completado correctamente.
     * <br><b>false</b> La ejecución ha terminado con problemas. Los detalles se
     * pueden recuperar en la propiedad {@link mensaje}.
     */
    public boolean listarCuentaCorrienteCliente(JTable tabla, Cliente cliente) {
        // Recuperamos el contenido de la tabla "CuentasCorrientes" en un
        // objeto "List".
        List<CuentaCorriente> lstCuentaCorriente = buscarCuentaCorriente(cliente);
        // Mostramos los clientes recuperados.
        return listarCuentaCorriente(tabla, lstCuentaCorriente);
    }
    
    /**
     * Lista las {@link CuentaCorriente} en un JTable.
     *
     * @param tabla JTable a rellenar.
     * @param lstCuentaCorriente La lista de cuentas corrientes a mostrar.
     * @return <b>true</b> La ejecución se ha completado correctamente.
     * <br><b>false</b> La ejecución ha terminado con problemas. Los detalles se
     * pueden recuperar en la propiedad {@link mensaje}.
     */
    public boolean listarCuentaCorriente(JTable tabla, List<CuentaCorriente> lstCuentaCorriente) {
        estado = false;
        mensaje = "listarCuentaCorriente -> ";
        try {
            // "DefaultTableModel" sirve para definir el modelo de datos (columnas,
            // tipos de datos) que se mostrará en un JTable.
            DefaultTableModel dtm;
            String[] cabecera = {"ID", "Nº CUENTA", "SUCURSAL"};
            dtm = new DefaultTableModel(null, cabecera); 

            // Definimos un array de "String" para almacenar todos los 
            // campos que queremos mostrar en nuestra tabla.
            String[] columnas = new String[3];
            for (CuentaCorriente c : lstCuentaCorriente) {
                columnas[0] = c.getIdCuentaCorriente().toString();
                columnas[1] = String.format("%10d", c.getNumeroCuenta());
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
            mensaje = mensaje + "OK: " + lstCuentaCorriente.size() + " cuentas recuperadas.";
        } catch (Exception E) {
            mensaje = mensaje + "ERROR: " + cuentaCorriente + "\n" + E.getMessage();
            System.out.println(mensaje);
        }
        return estado;
    }      
    
}
