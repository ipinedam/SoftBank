package model.DAO;

//<editor-fold defaultstate="collapsed" desc=" Librerías importadas... ">
import controller.CatalogoJpaController;
import controller.exceptions.NonexistentEntityException;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import model.entity.Catalogo;
//</editor-fold>

/**
 *
 * @author Ignacio Pineda Martín
 */
public class CatalogoDAO {
    
    private CatalogoJpaController sjc;
    private Catalogo catalogo = new Catalogo();
    
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
    
    public Catalogo getCatalogo() {
        return catalogo;
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

    public CatalogoDAO() {
    }

    public CatalogoDAO(EntityManagerFactory emf) {
        this.sjc = new CatalogoJpaController(emf);          
    }

    /**
     * Método para el alta de un nuevo {@link Catalogo}.
     * 
     * @param c El objeto {@link Catalogo} a crear.
     * @return <b>true</b> El {@link Catalogo} ha sido dada de alta.
     * <br><b>false</b> El {@link Catalogo} no ha sido dada de alta.
     */
    public boolean insertarCatalogo(Catalogo c) {
        estado = false;
        mensaje = "insertarCatalogo -> ";
        try {
            c.setIdCatalogo(null);
            sjc.create(c);
            estado = true;
            mensaje = mensaje + "OK: " + c;
            catalogo = c;
        } catch (Exception E) {
            mensaje = mensaje + "ERROR: " + c + "\n" + E.getMessage();
            System.out.println(mensaje);
        }      
        return estado;
    }

    /**
     * Método para la modificación de un {@link Catalogo}.
     * 
     * @param c El objeto {@link Catalogo} a modificar.
     * @return <b>true</b> El {@link Catalogo} ha sido modificada.
     * <br><b>false</b> El {@link Catalogo} no ha sido modificada.
     */
    public boolean actualizarCatalogo(Catalogo c) {
        estado = false;
        mensaje = "actualizarCatalogo -> ";
        try {
            // Antes de lanzar el "edit", comprobamos que el catalogo con 
            // el ID seleccionado existe. En caso contrario, lanzaremos una
            // excepción.
            if (sjc.findCatalogo(c.getIdCatalogo()) == null)
                throw new NonexistentEntityException("El Catalogo con id " + c.getIdCatalogo() + " no existe.");            
            sjc.edit(c);
            estado = true;
            mensaje = mensaje + "OK: " + c;
            catalogo = c;
        } catch (Exception E) {
            mensaje = mensaje + "ERROR: " + c + "\n" + E.getMessage();
            System.out.println(mensaje);
        }      
        return estado;
    }

    /**
     * Método para el borrado de un {@link Catalogo}.
     * 
     * @param c El objeto {@link Catalogo} a eliminar.
     * @return <b>true</b> El {@link Catalogo} ha sido eliminada.
     * <br><b>false</b> El {@link Catalogo} no ha sido eliminada.
     */
    public boolean eliminarCatalogo(Catalogo c) {
        return eliminarCatalogo(c.getIdCatalogo());
    }

    /**
     * Método para el borrado de un {@link Catalogo}.
     * 
     * @param idCatalogo El ID de el {@link Catalogo} a eliminar.
     * @return <b>true</b> El {@link Catalogo} ha sido eliminada.
     * <br><b>false</b> El {@link Catalogo} no ha sido eliminada.
     */
    public boolean eliminarCatalogo(Integer idCatalogo) {
        estado = false;
        mensaje = "eliminarCatalogo -> ";
        try {
            sjc.destroy(idCatalogo);
            estado = true;
            mensaje = mensaje + "OK: ID=" + idCatalogo.toString();
        } catch (Exception E) {
            mensaje = mensaje + "ERROR: ID=" + idCatalogo.toString()
                    + "\n" + E.getMessage();
            System.out.println(mensaje);
        }
        return estado;
    }
    
    /**
     * Búsqueda de {@link Catalogo} por su ID. Si se encuentra se devolverá
     * en la propiedad {@link catalogo}, accesible mediante su "getter"
     * {@link getCatalogo()}.
     * 
     * @param idCatalogo El ID del {@link Catalogo} a buscar.
     * @return <b>true</b> El {@link Catalogo} ha sido encontrado.
     * <br><b>false</b> El {@link Catalogo} no ha sido encontrado.
     */ 
    public boolean buscarCatalogo(Integer idCatalogo) {
        estado = false;
        mensaje = "buscarCatalogo -> ";
        try {
            catalogo = sjc.findCatalogo(idCatalogo);
            if (catalogo == null)
                throw new NonexistentEntityException("La Catalogo con id " + idCatalogo + " no existe.");
            estado = true;
            mensaje = mensaje + "OK: " + catalogo;
        } catch (Exception E) {
            mensaje = mensaje + "ERROR: ID=" + idCatalogo.toString() + "\n" + E.getMessage();
            System.out.println(mensaje);
        }      
        return estado;
    }

    /**
     * Búsqueda de los catalogos cuyo nombre coincida (parcialmente), con el 
     * nombre facilitado. En caso de que el nombre esté vacío, se devolverán todos
     * los catalogos.
     * 
     * @param nombreProducto El nombre (parcial) de el catalogo.
     * @return Lista de catalogos que cumplan el criterio de búsqueda.
     */
    private List<Catalogo> buscarCatalogo(String nombreProducto) {
        List<Catalogo> lstCatalogo = null;
        EntityManager em = sjc.getEntityManager();
        try {
            Query q = em.createQuery("SELECT c FROM Catalogo c " + 
                                     "WHERE c.nombreProducto LIKE :nombreProducto " +
                                     "ORDER BY c.codProducto");
            q.setParameter("nombreProducto", "%" + nombreProducto + "%");
            lstCatalogo = q.getResultList();
        } finally {
            em.close();
        }
        return lstCatalogo;
    }

    /**
     * Búsqueda de {@link Catalogo} por su código de catalogo. Si se encuentra 
     * se devolverá en la propiedad {@link catalogo}, accesible mediante 
     * su "getter" {@link getCatalogo()}.
     * 
     * @param codProducto El código de la {@link Catalogo} a buscar.
     * @return <b>true</b> El {@link Catalogo} ha sido encontrado.
     * <br><b>false</b> El {@link Catalogo} no ha sido encontrado.
     */
    public boolean buscarCodProducto(String codProducto) {
        estado = false;
        mensaje = "buscarCodProducto -> ";        
        EntityManager em = sjc.getEntityManager();
        try {
            Query q = em.createNamedQuery("Catalogo.findByCodProducto");
            q.setParameter("codProducto", codProducto);
            List<Catalogo> lstCatalogo = q.getResultList();
            if (lstCatalogo.isEmpty())
                throw new NonexistentEntityException("La Catalogo con código " + codProducto + " no existe.");
            catalogo = lstCatalogo.get(0);
            estado = true;
            mensaje = mensaje + "OK: " + catalogo;
        } catch (Exception E) {
            mensaje = mensaje + "ERROR: ID=" + codProducto + "\n" + E.getMessage();
            System.out.println(mensaje);
        } finally {
            em.close();
        }      
        return estado;
    }     

    /**
     * Lista el contenido de la tabla "Catalogo" en un JTable.
     *
     * @param tabla JTable a rellenar.
     * @param nombreProducto El nombre (parcial) de el catalogo a buscar. Si
     * este valor está en vacío, se devolverán todas los catalogos.
     * @return <b>true</b> La ejecución se ha completado correctamente.
     * <br><b>false</b> La ejecución ha terminado con problemas. Los detalles se
     * pueden recuperar en la propiedad {@link mensaje}.
     */
    public boolean listarCatalogo(JTable tabla, String nombreProducto) {
        estado = false;
        mensaje = "listarCatalogo -> ";
        try {
            // "DefaultTableModel" sirve para definir el modelo de datos (columnas,
            // tipos de datos) que se mostrará en un JTable.
            DefaultTableModel dtm;
            String[] cabecera = {"ID", "COD.PROD.", "NOMBRE PROD."};
            dtm = new DefaultTableModel(null, cabecera);
            // Recuperamos el contenido de la tabla "Catalogos" en un
            // objeto "List".
            // List<Catalogo> lstCatalogos = pjc.findCatalogoEntities();
            List<Catalogo> lstCatalogo = buscarCatalogo(nombreProducto);

            // Definimos un array de "String" para almacenar todos los 
            // campos que queremos mostrar en nuestra tabla.
            String[] columnas = new String[3];
            for (Catalogo c : lstCatalogo) {
                columnas[0] = c.getIdCatalogo().toString();
                columnas[1] = c.getCodProducto();
                columnas[2] = c.getNombreProducto();
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
            mensaje = mensaje + "OK: " + lstCatalogo.size() + " catalogos recuperados.";
        } catch (Exception E) {
            mensaje = mensaje + "ERROR: " + catalogo + "\n" + E.getMessage();
            System.out.println(mensaje);
        }
        return estado;
    }
 
    /**
     * Método para devolver la información de catálogo correspondiente a
     * Cuentas Corrientes.
     * @return Objeto {@link Catalogo} correspondiente a Cuentas Corrientes.
     */
    public Catalogo getCuentaCorriente() {
        if (buscarCodProducto("CCC")) {
            return catalogo;                
        } else {
            return new Catalogo();
        }
    }
    
    /**
     * Método para devolver la información de catálogo correspondiente a
     * Tarjetas de Crédito.
     *
     * @return Objeto {@link Catalogo} correspondiente a Tarjetas de Crédito.
     */
    public Catalogo getTarjetaCredito() {
        if (buscarCodProducto("TJC")) {
            return catalogo;
        } else {
            return new Catalogo();
        }
    }
    
    /**
     * Método para devolver la información de catálogo correspondiente a
     * Préstamos.
     *
     * @return Objeto {@link Catalogo} correspondiente a Préstamos.
     */
    public Catalogo getPrestamo() {
        if (buscarCodProducto("PTM")) {
            return catalogo;
        } else {
            return new Catalogo();
        }
    }
    
    /**
     * Método para devolver la información de catálogo correspondiente a
     * Avales.
     *
     * @return Objeto {@link Catalogo} correspondiente a Avales.
     */
    public Catalogo getAval() {
        if (buscarCodProducto("AVL")) {
            return catalogo;
        } else {
            return new Catalogo();
        }
    }
        
}
