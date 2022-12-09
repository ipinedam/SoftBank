package model.DAO;

//<editor-fold defaultstate="collapsed" desc=" Librerías importadas... ">
import controller.MovimientoJpaController;
import controller.exceptions.NonexistentEntityException;

import java.awt.Color;
import java.awt.Component;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.ParameterMode;
import javax.persistence.Query;
import javax.persistence.StoredProcedureQuery;
import javax.persistence.TemporalType;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import model.entity.ProductoBancario;
import model.entity.Movimiento;

import utilities.Functions;
//</editor-fold>

/**
 * Clase "DATA ACCESS OBJECT" para {@link Movimiento}.
 * 
 * @author Ignacio Pineda Martín
 */
public class MovimientoDAO {

    private MovimientoJpaController mjc;
    private Movimiento movimiento = new Movimiento();
    
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
    
    public Movimiento getMovimiento() {
        return movimiento;
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
 
    public MovimientoDAO() {
    }

    public MovimientoDAO(EntityManagerFactory emf) {
        this.mjc = new MovimientoJpaController(emf);          
    }

    /**
     * Método para el alta de un nuevo {@link Movimiento}.
     * 
     * @param m El objeto {@link Movimiento} a crear.
     * @return <b>true</b> El {@link Movimiento} ha sido dada de alta.
     * <br><b>false</b> El {@link Movimiento} no ha sido dada de alta.
     */
    public boolean insertarMovimiento(Movimiento m) {
        estado = false;
        mensaje = "insertarMovimiento -> ";
        try {
            m.setIdMovimiento(null);
            mjc.create(m);
            estado = true;
            mensaje = mensaje + "OK: " + m;
            movimiento = m;
            // Llamamos al procedimiento para actualizar el saldo del producto
            // bancario asociado.
            actualizarSaldo(m);
        } catch (Exception E) {
            mensaje = mensaje + "ERROR: " + m + "\n" + E.getMessage();
            System.out.println(mensaje);
        }      
        return estado;
    }
    
    /**
     * Método para la modificación de un {@link Movimiento}.
     * 
     * @param m El objeto {@link Movimiento} a modificar.
     * @return <b>true</b> El objeto {@link Movimiento} ha sido modificado.
     * <br><b>false</b> El objeto {@link Movimiento} no ha sido modificado.
     */
    public boolean actualizarMovimiento(Movimiento m) {
        estado = false;
        mensaje = "actualizarMovimiento -> ";
        try {
            // Antes de lanzar el "edit", comprobamos que el movimiento con 
            // el ID seleccionado existe. En caso contrario, lanzaremos una
            // excepción.
            if (mjc.findMovimiento(m.getIdMovimiento()) == null)
                throw new NonexistentEntityException("El Movimiento con id " + m.getIdMovimiento()+ " no existe.");            
            mjc.edit(m);
            estado = true;
            mensaje = mensaje + "OK: " + m;
            movimiento = m;
            // Llamamos al procedimiento para actualizar el saldo del producto
            // bancario asociado.
            actualizarSaldo(m);            
        } catch (Exception E) {
            mensaje = mensaje + "ERROR: " + m + "\n" + E.getMessage();
            System.out.println(mensaje);
        }      
        return estado;
    }

    /**
     * Método para el borrado de un {@link Movimiento}.
     *
     * @param m El objeto {@link Movimiento} a eliminar.
     * @return <b>true</b> El {@link Movimiento} ha sido eliminada.
     * <br><b>false</b> El {@link Movimiento} no ha sido eliminada.
     */
    public boolean eliminarMovimiento(Movimiento m) {
        estado = eliminarMovimiento(m.getIdMovimiento());
        actualizarSaldo(m);
        return estado;
    }

    /**
     * Método para el borrado de un {@link Movimiento}.
     *
     * @param idMovimiento El ID de la {@link Movimiento} a eliminar.
     * @return <b>true</b> El {@link Movimiento} ha sido eliminada.
     * <br><b>false</b> El {@link Movimiento} no ha sido eliminada.
     */
    public boolean eliminarMovimiento(Integer idMovimiento) {
        estado = false;
        mensaje = "eliminarMovimiento -> ";
        try {
            mjc.destroy(idMovimiento);
            estado = true;
            mensaje = mensaje + "OK: ID=" + idMovimiento.toString();
        } catch (Exception E) {
            mensaje = mensaje + "ERROR: ID=" + idMovimiento.toString()
                    + "\n" + E.getMessage();
            System.out.println(mensaje);
        }
        return estado;
    }

    /**
     * Búsqueda de {@link Movimiento} por su ID. Si se encuentra se
     * devolverá en la propiedad {@link movimiento}, accesible mediante su
     * "getter" {@link getMovimiento()}.
     *
     * @param idMovimiento El ID del {@link Movimiento} a buscar.
     * @return <b>true</b> El {@link Movimiento} ha sido encontrado.
     * <br><b>false</b> El {@link Movimiento} no ha sido encontrado.
     */
    public boolean buscarMovimiento(Integer idMovimiento) {
        estado = false;
        mensaje = "buscarMovimiento -> ";
        EntityManager em = mjc.getEntityManager();
        try {
            movimiento = mjc.findMovimiento(idMovimiento);
            if (movimiento == null)
                throw new NonexistentEntityException(("El Movimiento con id " + idMovimiento + " no existe."));
            estado = true;
            mensaje = mensaje + "OK: " + movimiento;
        } catch (Exception E) {
            mensaje = mensaje + "ERROR: \n" + E.getMessage();
            System.out.println(mensaje);            
        } finally {
            em.close();
        }
        return estado;
    }
    
    /**
     * Búsqueda de los {@link Movimiento}, asociados a un
     * {@link ProductoBancario}, por el concepto (parcial). Si el parámetro está
     * vacío, se devolveran todos los movimientos.
     *
     * @param pb El {@link ProductoBancario} al que pertenecen los movimientos.
     * @param fecInicio La fecha inicial de la que capturar los movimientos.
     * @param fecFin La fecha final de la que capturar los movimientos.
     * @param concepto El concepto (parcial) del {@link Movimiento} a
     * buscar.
     * @return La lista de {@link Movimiento} recuperados.
     */
    private List<Movimiento> buscarMovimiento(ProductoBancario pb, Date fecInicio, Date fecFin, String concepto) {
        estado = false;
        mensaje = "buscarMovimiento -> ";
        List<Movimiento> lstMovimiento = null;
        EntityManager em = mjc.getEntityManager();
        try {
            Query q = em.createQuery("SELECT m FROM Movimiento m "
                    + "WHERE m.productoBancario.idProductoBancario = :idProductoBancario"
                    + "  AND m.concepto LIKE :concepto"
                    + "  AND m.fecMovimiento BETWEEN :fecInicio AND :fecFin "
                    + "ORDER BY m.fecMovimiento", Movimiento.class);
            q.setParameter("idProductoBancario", pb.getIdProductoBancario());
            q.setParameter("fecInicio", fecInicio, TemporalType.DATE);
            q.setParameter("fecFin", fecFin, TemporalType.DATE);
            q.setParameter("concepto", "%" + concepto + "%");
            lstMovimiento = q.getResultList();
            estado = true;
            mensaje = mensaje + "OK: " + lstMovimiento.size() + " movimientos recuperados.";
        } catch (Exception E) {
            mensaje = mensaje + "ERROR: \n" + E.getMessage();
            System.out.println(mensaje);            
        } finally {
            em.close();
        }
        return lstMovimiento;
    }

    /**
     * Devuelve la lista de los {@link Movimiento}, asociados a un
     * {@link Producto Bancario}, que esten comprendidos entre las fechas facilitadas.
     *
     * @param pb El {@link Producto Bancario} al que corresponden los
     * movimientos.
     * @param fecInicio La fecha de inicio de búsqueda de los movimientos.
     * @param fecFin La fecha final de búsqueda de los movimientos.
     * @return Lista de objetos {@link Movimiento} recuperados.
     */
    public List<Movimiento> listarMovimiento(ProductoBancario pb, Date fecInicio, Date fecFin) {
        // Recuperamos los movimientos entre dos fechas en un objeto "List".
        // Además, recuperamos el saldo de inicio y final como parte de dicha
        // lista.
        List<Movimiento> lstMovimiento = new ArrayList<Movimiento>();
        if (pb != null) {
            lstMovimiento.add(saldoInicioDia(pb, fecInicio));
            lstMovimiento.addAll(buscarMovimiento(pb, fecInicio, fecFin, ""));
            lstMovimiento.add(saldoFinDia(pb, fecFin));
        }
        // Devolvemos los movimientos recuperados.
        return lstMovimiento;
    }
    
    /**
     * Lista en un {@link JTable} los {@link Movimiento}, asociados a un
     * {@link Producto Bancario}, que coincidan (parcialmente) con el concepto
     * facilitado y que esten comprendidos entre las fechas facilitadas.
     *
     * @param tabla JTable a rellenar.
     * @param pb El {@link Producto Bancario} al que corresponden los
     * movimientos.
     * @param fecInicio La fecha de inicio de búsqueda de los movimientos.
     * @param fecFin La fecha final de búsqueda de los movimientos.
     * @param concepto El concepto(parcial) del movimiento a listar.
     * @return <b>true</b> La ejecución se ha completado correctamente.
     * <br><b>false</b> La ejecución ha terminado con problemas. Los detalles se
     * pueden recuperar en la propiedad {@link mensaje}.
     */
    public boolean listarMovimientoConcepto(JTable tabla, ProductoBancario pb, Date fecInicio, Date fecFin, String concepto) {
        // Recuperamos los movimientos entre dos fechas en un objeto "List".
        // Además, recuperamos el saldo de inicio y final como parte de dicha
        // lista.
        List<Movimiento> lstMovimiento = new ArrayList<Movimiento>();
        if (pb != null) {
            lstMovimiento.add(saldoInicioDia(pb, fecInicio));
            lstMovimiento.addAll(buscarMovimiento(pb, fecInicio, fecFin, concepto));
            lstMovimiento.add(saldoFinDia(pb, fecFin));
        }
        // Mostramos los movimientos recuperados.
        return listarMovimiento(tabla, lstMovimiento);
    }
   
    /**
     * Lista los {@link Movimiento} en un JTable.
     *
     * @param tabla JTable a rellenar.
     * @param lstMovimiento La lista de movimientos a mostrar.
     * @return <b>true</b> La ejecución se ha completado correctamente.
     * <br><b>false</b> La ejecución ha terminado con problemas. Los detalles se
     * pueden recuperar en la propiedad {@link mensaje}.
     */
    public boolean listarMovimiento(JTable tabla, List<Movimiento> lstMovimiento) {
        // Renderer para alinear el contenido de una columna a la DERECHA.
        DefaultTableCellRenderer dtcrColumnaImporte = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean arg2, boolean arg3, int row, int col) {
                Component tableCellRendererComponent = super.getTableCellRendererComponent(table, value, arg2, arg3, row, col);
                int align = DefaultTableCellRenderer.RIGHT;
                ((DefaultTableCellRenderer) tableCellRendererComponent).setHorizontalAlignment(align);              // Alineado a la derecha.
                if (Functions.getNumberFromFormattedAmount((String) value).doubleValue() < 0) {
                    ((DefaultTableCellRenderer) tableCellRendererComponent).setForeground(Color.red);               // Rojo
                } else {
                    ((DefaultTableCellRenderer) tableCellRendererComponent).setForeground(new Color(0, 129, 0));    // Verde
                }
                return tableCellRendererComponent;
            }
        };

        estado = false;
        mensaje = "listarMovimiento -> ";
        try {
            // "DefaultTableModel" sirve para definir el modelo de datos (columnas,
            // tipos de datos) que se mostrará en un JTable.
            DefaultTableModel dtm;
            String[] cabecera = {"ID", "FECHA", "CONCEPTO", "IMPORTE"};
            dtm = new DefaultTableModel(null, cabecera); 

            // Definimos un array de "String" para almacenar todos los 
            // campos que queremos mostrar en nuestra tabla.
            String[] columnas = new String[4];
            for (Movimiento m : lstMovimiento) {
                columnas[0] = m.getIdMovimiento().toString();
                columnas[1] = Functions.formatDate(m.getFecMovimiento());
                columnas[2] = m.getConcepto();
                columnas[3] = Functions.formatAmount(m.getImpMovimiento());
                // Añadimos una fila al "DefaultTableModel" que hemos definido
                // antes.
                dtm.addRow(columnas);
            }
            // Asignamos al "JTable" el "DefaultTableModel", que ya contiene
            // la información a mostrar.
            tabla.setModel(dtm);
            // Alineamos la columna de importes a la derecha.
            tabla.getColumnModel().getColumn(3).setCellRenderer(dtcrColumnaImporte);
            // Removemos la primera columna (ID) de la tabla, pero no así sus
            // datos (accesibles desde el modelo).
            tabla.removeColumn(tabla.getColumnModel().getColumn(0));

            estado = true;
            mensaje = mensaje + "OK: " + lstMovimiento.size() + " movimientos recuperadas.";
        } catch (Exception E) {
            mensaje = mensaje + "ERROR: " + movimiento + "\n" + E.getMessage();
            System.out.println(mensaje);
        }
        return estado;
    }
    
    /**
     * Obtención del saldo de un {@link ProductoBancario} al inicio de la fecha
     * indicada.
     *
     * @param pb El {@link ProductoBancario} al que pertenecen los movimientos.
     * @param fecSaldo La fecha a la que obtener el saldo de inicio del día.
     * @return Un movimiento (ficticio), con el saldo recuperado.
     */
    public Movimiento saldoInicioDia(ProductoBancario pb, Date fecSaldo) {
        Movimiento m = new Movimiento();
        m.setIdMovimiento(-1);
        m.setFecMovimiento(fecSaldo);
        m.setConcepto("SALDO INICIAL");
        EntityManager em = mjc.getEntityManager();
        try {
            Query q = em.createQuery("SELECT COALESCE(SUM(m.impMovimiento), 0) FROM Movimiento m "
                    + "WHERE m.productoBancario.idProductoBancario = :idProductoBancario"
                    + "  AND m.fecMovimiento < :fecSaldo", BigDecimal.class);
            q.setParameter("idProductoBancario", pb.getIdProductoBancario());
            q.setParameter("fecSaldo", fecSaldo, TemporalType.DATE);
            m.setImpMovimiento((BigDecimal) q.getSingleResult());
        } finally {
            em.close();
        }
        return m;
    }    
    
    /**
     * Obtención del saldo de un {@link ProductoBancario} al final de la fecha
     * indicada.
     *
     * @param pb El {@link ProductoBancario} al que pertenecen los movimientos.
     * @param fecSaldo La fecha a la que obtener el saldo de inicio del día.
     * @return Un movimiento (ficticio), con el saldo recuperado.
     */
    public Movimiento saldoFinDia(ProductoBancario pb, Date fecSaldo) {
        Movimiento m = new Movimiento();
        m.setIdMovimiento(-1);
        m.setFecMovimiento(fecSaldo);
        m.setConcepto("SALDO FINAL");
        EntityManager em = mjc.getEntityManager();
        try {
            Query q = em.createQuery("SELECT COALESCE(SUM(m.impMovimiento), 0) FROM Movimiento m "
                    + "WHERE m.productoBancario.idProductoBancario = :idProductoBancario"
                    + "  AND m.fecMovimiento <= :fecSaldo", BigDecimal.class);
            q.setParameter("idProductoBancario", pb.getIdProductoBancario());
            q.setParameter("fecSaldo", fecSaldo, TemporalType.DATE);
            m.setImpMovimiento((BigDecimal) q.getSingleResult());
        } finally {
            em.close();
        }
        return m;
    }
    
    /**
     * Actualización del saldo del producto bancario asociado al movimiento.
     *
     * @param m El movimiento originante de la actualización de saldo.
     */
    private void actualizarSaldo(Movimiento m) {
        ProductoBancario pb = m.getProductoBancario();
        if ((pb != null) && (pb.getIdProductoBancario() != null)) {
            EntityManager em = mjc.getEntityManager();
            try {
                // Actualización de saldo en Cuenta Corriente.
                if (pb.getCuentaCorriente() != null) {
                    ejecutarStoredProcedure(m, "softbank.ActualizarSaldoCC");
                }
                // Actualización de saldo en Tarjeta de Crédito.
                if (pb.getTarjetaCredito()!= null) {
                    ejecutarStoredProcedure(m, "softbank.ActualizarSaldoTC");
                }                
                // Actualización de saldo en Tarjeta de Crédito.
                if (pb.getPrestamo()!= null) {
                    ejecutarStoredProcedure(m, "softbank.ActualizarSaldoPT");
                }
            } finally {
                em.close();
            }
        }
    }
    
    /**
     * Ejecución de procedimiento almacenado y actualización del producto
     * bancario asociado al movimiento proporcionado.
     * 
     * @param m El movimiento que ha desencadenado la actualización de saldos.
     * @param sp El procedimiento almacenado a ejecutar.
     */
    private void ejecutarStoredProcedure(Movimiento m, String sp) {
        ProductoBancario pb = m.getProductoBancario();
        EntityManager em = mjc.getEntityManager();
        try {
            // Creamos una llamada al procedimiento almacenado.
            em.getTransaction().begin();
            StoredProcedureQuery spActualizarSaldo = em.createStoredProcedureQuery(sp);
            // Declaramos y rellenamos los parámetros.
            spActualizarSaldo.registerStoredProcedureParameter("idProductoBancario", Integer.class, ParameterMode.IN);
            spActualizarSaldo.setParameter("idProductoBancario", pb.getIdProductoBancario());
            // Ejecutamos el procedimiento almacenado.
            spActualizarSaldo.execute();
            // Refrescamos el producto bancario del movimiento.
            pb = em.find(ProductoBancario.class, pb.getIdProductoBancario());
            em.refresh(pb);
            m.setProductoBancario(pb);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

}
