package model.DAO;

import controller.TarjetaCreditoJpaController;
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
import model.entity.TarjetaCredito;

import utilities.Banking;

/**
 * Clase "DATA ACCESS OBJECT" para {@link TarjetaCredito}.
 * 
 * @author Ignacio Pineda Martín
 */
public class TarjetaCreditoDAO {

    private TarjetaCreditoJpaController tjc;
    private TarjetaCredito tarjetaCredito = new TarjetaCredito();
    
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
    
    public TarjetaCredito getTarjetaCredito() {
        return tarjetaCredito;
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
 
    public TarjetaCreditoDAO() {
    }

    public TarjetaCreditoDAO(EntityManagerFactory emf) {
        this.tjc = new TarjetaCreditoJpaController(emf);          
    }

    /**
     * Método para el alta de una nueva {@link TarjetaCredito}.
     * 
     * @param tc El objeto {@link TarjetaCredito} a crear.
     * @return <b>true</b> La {@link TarjetaCredito} ha sido dada de alta.
     * <br><b>false</b> La {@link TarjetaCredito} no ha sido dada de alta.
     */
    public boolean insertarTarjetaCredito(TarjetaCredito tc) {
        estado = false;
        mensaje = "insertarTarjetaCredito -> ";
        try {
            tc.setIdTarjetaCredito(null);
            tc.setNumeroTarjeta(getNuevoNumeroTarjeta());
            tc.setPan(Banking.getPAN(tc.getNumeroTarjeta()));
            tjc.create(tc);
            estado = true;
            mensaje = mensaje + "OK: " + tc;
            tarjetaCredito = tc;
        } catch (Exception E) {
            mensaje = mensaje + "ERROR: " + tc + "\n" + E.getMessage();
            System.out.println(mensaje);
        }      
        return estado;
    }
    
    /**
     * Método para obtener un nuevo número de tarjeta.
     * 
     * @return El nuevo número de tarjeta.
     */
    private long getNuevoNumeroTarjeta() {
        long numeroTarjeta = 0L;
        Object queryResult;        
        EntityManager em = tjc.getEntityManager();        
        try {
            Query q = em.createQuery("SELECT MAX(tc.numeroTarjeta) " +
                                     "FROM TarjetaCredito tc");
            queryResult = q.getSingleResult();
            // Comprobamos que el valor devuelto no sea nulo.
            if (queryResult != null) {
                // Incrementamos en 1 el número de tarjeta.
                numeroTarjeta = (long) queryResult;
                numeroTarjeta += 1L;
            } else {
                // Asignamos el primer número de tarjeta.
                numeroTarjeta = 4000000001L;
            }             
        } catch (Exception E) {
            mensaje = mensaje + "ERROR: \n" + E.getMessage();
            System.out.println(mensaje);   
        } finally {
            em.close();
        }
        return numeroTarjeta;
    }

    /**
     * Método para la modificación de una {@link TarjetaCredito}.
     * 
     * @param tc El objeto {@link TarjetaCredito} a modificar.
     * @return <b>true</b> El objeto {@link TarjetaCredito} ha sido modificado.
     * <br><b>false</b> El objeto {@link TarjetaCredito} no ha sido modificado.
     */
    public boolean actualizarTarjetaCredito(TarjetaCredito tc) {
        estado = false;
        mensaje = "actualizarTarjetaCredito -> ";
        try {
            // Antes de lanzar el "edit", comprobamos que el cliente con 
            // el ID seleccionado existe. En caso contrario, lanzaremos una
            // excepción.
            if (tjc.findTarjetaCredito(tc.getIdTarjetaCredito()) == null)
                throw new NonexistentEntityException("La TarjetaCredito con id " + tc.getIdTarjetaCredito()+ " no existe.");            
            tjc.edit(tc);
            estado = true;
            mensaje = mensaje + "OK: " + tc;
            tarjetaCredito = tc;
        } catch (Exception E) {
            mensaje = mensaje + "ERROR: " + tc + "\n" + E.getMessage();
            System.out.println(mensaje);
        }      
        return estado;
    }
    
    /**
     * Recupera todas las tarjetas de crédito almacenadas en la BB.DD.
     * 
     * @return lista de objetos {@link TarjetaCredito}.
     */
    public List<TarjetaCredito> buscarTarjetaCredito() {
        estado = false;
        mensaje = "buscarTarjetaCredito -> ";
        List<TarjetaCredito> lstTarjetaCredito = null;
        EntityManager em = tjc.getEntityManager();
        try {
            lstTarjetaCredito = tjc.findTarjetaCreditoEntities();
            if (lstTarjetaCredito == null)
                throw new NonexistentEntityException("No existen objetos TarjetaCredito (tabla vacía).");
            estado = true;
            mensaje = mensaje + "OK: " + lstTarjetaCredito.size() + " cuentas recuperadas.";
        } catch (Exception E) {
            mensaje = mensaje + "ERROR: \n" + E.getMessage();
            System.out.println(mensaje);            
        } finally {
            em.close();
        }
        return lstTarjetaCredito;
    }    

    /**
     * Búsqueda de {@link TarjetaCredito} por su ID. Si se encuentra se
     * devolverá en la propiedad {@link tarjetaCredito}, accesible mediante su
     * "getter" {@link getTarjetaCredito()}.
     *
     * @param idTarjetaCredito El ID de la {@link TarjetaCredito} a buscar.
     * @return <b>true</b> La {@link TarjetaCredito} ha sido encontrado.
     * <br><b>false</b> La {@link TarjetaCredito} no ha sido encontrado.
     */
    public boolean buscarTarjetaCredito(Integer idTarjetaCredito) {
        estado = false;
        mensaje = "buscarTarjetaCredito -> ";
        EntityManager em = tjc.getEntityManager();
        try {
            tarjetaCredito = tjc.findTarjetaCredito(idTarjetaCredito);
            if (tarjetaCredito == null)
                throw new NonexistentEntityException(("La TarjetaCredito con id " + idTarjetaCredito + " no existe."));
            estado = true;
            mensaje = mensaje + "OK: " + tarjetaCredito;
        } catch (Exception E) {
            mensaje = mensaje + "ERROR: \n" + E.getMessage();
            System.out.println(mensaje);            
        } finally {
            em.close();
        }
        return estado;
    }
    
    /**
     * Búsqueda de {@link TarjetaCredito} <b>activas</b> por número (parcial).
     * Si el parámetro está vacío, se devolveran todas las tarjetas.
     *
     * @param numeroTarjeta El número (parcial) de la {@link TarjetaCredito} a
     * buscar.
     * @return <b>true</b> La {@link TarjetaCredito} ha sido encontrado.
     * <br><b>false</b> La {@link TarjetaCredito} no ha sido encontrado.
     */
    private List<TarjetaCredito> buscarTarjetaCredito(String numeroTarjeta) {
        List<TarjetaCredito> lstTarjetaCredito = null;
        EntityManager em = tjc.getEntityManager();
        try {
            Query q = em.createQuery("SELECT tc FROM TarjetaCredito tc " + 
                                     "WHERE tc.numeroTarjeta LIKE :numeroTarjeta" + 
                                     "  AND tc.productoBancario.fecCancelacion IS NULL", TarjetaCredito.class);
            q.setParameter("numeroTarjeta", "%" + numeroTarjeta + "%");
            lstTarjetaCredito = q.getResultList();
        } finally {
            em.close();
        }
        return lstTarjetaCredito;
    }

    /**
     * Búsqueda de las {@link TarjetaCredito} <b>activas</b> asociadas a un
     * cliente.
     *
     * @param c El objeto {@link Cliente} de la(s) {@link TarjetaCredito} a
     * buscar.
     * @return <b>true</b> La {@link TarjetaCredito} ha sido encontrado.
     * <br><b>false</b> La {@link TarjetaCredito} no ha sido encontrado.
     */
    private List<TarjetaCredito> buscarTarjetaCredito(Cliente c) {
        List<TarjetaCredito> lstTarjetaCredito = null;
        EntityManager em = tjc.getEntityManager();
        try {
            Query q = em.createNativeQuery("SELECT TC.* "
                                         + "FROM SOFTBANK.TARJETA_CREDITO TC,"
                                         + "     SOFTBANK.PRODUCTO_BANCARIO PB,"
                                         + "     SOFTBANK.CLIENTE_PRODUCTO_BANCARIO CPB "
                                         + "WHERE TC.ID_PRODUCTO_BANCARIO = PB.ID_PRODUCTO_BANCARIO"
                                         + "  AND PB.ID_PRODUCTO_BANCARIO = CPB.ID_PRODUCTO_BANCARIO"
                                         + "  AND PB.FEC_CANCELACION IS NULL"
                                         + "  AND CPB.ID_CLIENTE = ? "
                                         + "ORDER BY TC.ID_TARJETA_CREDITO", TarjetaCredito.class);
            q.setParameter(1, c.getIdCliente());
            lstTarjetaCredito = (List<TarjetaCredito>) q.getResultList();
        } finally {
            em.close();
        }
        return lstTarjetaCredito;
    }

    /**
     * Búsqueda de las {@link TarjetaCredito} <b>activas</b> pendientes de
     * liquidación a una fecha.
     *
     * @param fecLiquidacion La fecha a la que se van a liquidar la(s)
     * {@link TarjetaCredito} a buscar.
     * @return Lista de {@link TarjetaCredito} a liquidar.
     */
    public List<TarjetaCredito> buscarTarjetaCredito(Date fecLiquidacion) {
        List<TarjetaCredito> lstTarjetaCredito = null;
        EntityManager em = tjc.getEntityManager();
        try {
            Query q = em.createQuery("SELECT tc FROM TarjetaCredito tc "
                    + "WHERE tc.productoBancario.fecCancelacion IS NULL"
                    + "  AND (tc.productoBancario.fecApertura <= :fecLiquidacion)"
                    + "  AND (tc.productoBancario.fecLiquidacion < :fecLiquidacion OR tc.productoBancario.fecLiquidacion IS NULL)", TarjetaCredito.class);
            q.setParameter("fecLiquidacion", fecLiquidacion, TemporalType.DATE);
            lstTarjetaCredito = q.getResultList();
        } finally {
            em.close();
        }
        return lstTarjetaCredito;
    }    
    
    /**
     * Lista las {@link TarjetaCredito} en un JTable que coincidan 
     * (parcialmente) con el número facilitado.
     *
     * @param tabla JTable a rellenar.
     * @param numeroTarjeta El número (parcial) de cuenta corriente a listar.
     * @return <b>true</b> La ejecución se ha completado correctamente.
     * <br><b>false</b> La ejecución ha terminado con problemas. Los detalles se
     * pueden recuperar en la propiedad {@link mensaje}.
     */
    public boolean listarTarjetaCreditoNumero(JTable tabla, String numeroTarjeta) {
        // Recuperamos el contenido de la tabla "CuentasCorrientes" en un
        // objeto "List".
        List<TarjetaCredito> lstTarjetaCredito = buscarTarjetaCredito(numeroTarjeta);
        // Mostramos los clientes recuperados.
        return listarTarjetaCredito(tabla, lstTarjetaCredito);        
    }

    /**
     * Lista las {@link TarjetaCredito} en un JTable que pertenezcan al cliente
     * facilitado.
     *
     * @param tabla JTable a rellenar.
     * @param cliente El {@link Cliente} al que pertenecen las tarjetas a
     * listar.
     * @return <b>true</b> La ejecución se ha completado correctamente.
     * <br><b>false</b> La ejecución ha terminado con problemas. Los detalles se
     * pueden recuperar en la propiedad {@link mensaje}.
     */
    public boolean listarTarjetaCreditoCliente(JTable tabla, Cliente cliente) {
        // Recuperamos el contenido de la tabla "TarjetaCredito" en un
        // objeto "List".
        List<TarjetaCredito> lstTarjetaCredito = buscarTarjetaCredito(cliente);
        // Mostramos los clientes recuperados.
        return listarTarjetaCredito(tabla, lstTarjetaCredito);
    }
    
    /**
     * Lista las {@link TarjetaCredito} en un JTable.
     *
     * @param tabla JTable a rellenar.
     * @param lstTarjetaCredito La lista de tarjetas de crédito a mostrar.
     * @return <b>true</b> La ejecución se ha completado correctamente.
     * <br><b>false</b> La ejecución ha terminado con problemas. Los detalles se
     * pueden recuperar en la propiedad {@link mensaje}.
     */
    public boolean listarTarjetaCredito(JTable tabla, List<TarjetaCredito> lstTarjetaCredito) {
        estado = false;
        mensaje = "listarTarjetaCredito -> ";
        try {
            // "DefaultTableModel" sirve para definir el modelo de datos (columnas,
            // tipos de datos) que se mostrará en un JTable.
            DefaultTableModel dtm;
            String[] cabecera = {"ID", "Nº CUENTA", "SUCURSAL"};
            dtm = new DefaultTableModel(null, cabecera); 

            // Definimos un array de "String" para almacenar todos los 
            // campos que queremos mostrar en nuestra tabla.
            String[] columnas = new String[3];
            for (TarjetaCredito c : lstTarjetaCredito) {
                columnas[0] = c.getIdTarjetaCredito().toString();
                columnas[1] = String.format("%10d", c.getNumeroTarjeta());
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
            mensaje = mensaje + "OK: " + lstTarjetaCredito.size() + " tarjetas recuperadas.";
        } catch (Exception E) {
            mensaje = mensaje + "ERROR: " + tarjetaCredito + "\n" + E.getMessage();
            System.out.println(mensaje);
        }
        return estado;
    }      
    
}
