package model.DAO;

import controller.DireccionJpaController;
import controller.exceptions.NonexistentEntityException;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import model.entity.Direccion;

/**
 *
 * @author Ignacio Pineda Martín
 */
public class DireccionDAO {
    
    private EntityManagerFactory emf;
    private DireccionJpaController sjc;
    private Direccion direccion = new Direccion();
    
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
    
    public Direccion getDireccion() {
        return direccion;
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

    public DireccionDAO() {
    }

    public DireccionDAO(EntityManagerFactory emf) {
        this.emf = emf;
        this.sjc = new DireccionJpaController(emf);          
    }

    /**
     * Método para el alta de una nueva {@link Direccion}.
     * 
     * @param d El objeto {@link Direcion} a crear.
     * @return <b>true</b> La {@link Direccion} ha sido dada de alta.
     * <br><b>false</b> La {@link Direccion} no ha sido dada de alta.
     */
    public boolean insertarDireccion(Direccion d) {
        estado = false;
        mensaje = "insertarDirecion -> ";
        try {
            d.setIdDireccion(null);
            sjc.create(d);
            estado = true;
            mensaje = mensaje + "OK: " + d;
            direccion = d;
        } catch (Exception E) {
            mensaje = mensaje + "ERROR: " + d + "\n" + E.getMessage();
            System.out.println(mensaje);
        }      
        return estado;
    }
    
    /**
     * Búsqueda de {@link Direccion} por su ID. Si se encuentra se devolverá
     * en la propiedad {@link direccion}, accesible mediante su "getter"
     * {@link getDireccion()}.
     * 
     * @param idDireccion El ID del {@link Direccion} a buscar.
     * @return <b>true</b> La {@link Direccion} ha sido encontrado.
     * <br><b>false</b> La {@link Direccion} no ha sido encontrado.
     */ 
    public boolean buscarDireccion(Integer idDireccion) {
        estado = false;
        mensaje = "buscarDireccion -> ";
        try {
            direccion = sjc.findDireccion(idDireccion);
            if (direccion == null)
                throw new NonexistentEntityException("La Direccion con id " + idDireccion + " no existe.");
            estado = true;
            mensaje = mensaje + "OK: " + direccion;
        } catch (Exception E) {
            mensaje = mensaje + "ERROR: ID=" + idDireccion.toString() + "\n" + E.getMessage();
            System.out.println(mensaje);
        }      
        return estado;
    }

    /**
     * Búsqueda de las direcciones cuyo nombre coincida (parcialmente), con el 
     * nombre facilitado. En caso de que el nombre esté vacío, se devolverán todas
     * las direcciones.
     * 
     * @param nombreVia El nombre (parcial) de la direccion.
     * @return Lista de direcciones que cumplan el criterio de búsqueda.
     */
    private List<Direccion> buscarDireccion(String nombreVia) {
        List<Direccion> lstDireccion = null;
        EntityManager em = sjc.getEntityManager();
        try {
            Query q = em.createQuery("SELECT s FROM Direccion s " + 
                                      "WHERE s.nombreVia LIKE :nombreVia");
            q.setParameter("nombreVia", "%" + nombreVia + "%");
            lstDireccion = q.getResultList();
        } finally {
            em.close();
        }
        return lstDireccion;
    }    

    /**
     * Lista el contenido de la tabla {@link Direccion} en un JTable.
     * 
     * @param tabla JTable a rellenar.
     * @param nombreVia El nombre (parcial) de la dirección a buscar.
     * Si este valor está en vacío, se devolverán todas las dirección.
     * @return <b>true</b> La ejecución se ha completado correctamente.
     * <br><b>false</b> La ejecución ha terminado con problemas. Los detalles
     * se pueden recuperar en la propiedad {@link mensaje}.
     */    
    public boolean listarDireccion(JTable tabla, String nombreVia) {
        estado = false;
        mensaje = "listarDireccion -> ";
        try {
            // "DefaultTableModel" sirve para definir el modelo de datos (columnas,
            // tipos de datos) que se mostrará en un JTable.
            DefaultTableModel dtm;
            String[] cabecera = {"ID", "TIPO VIA", "NOMBRE VIA", "NUM.", "POBLACION", "COD.POSTAL", "PROVINCIA", "PAIS"};
            dtm = new DefaultTableModel(null, cabecera);
            // Recuperamos el contenido de la tabla "Direccions" en un
            // objeto "List".
            // List<Direccion> lstDireccions = pjc.findDireccionEntities();
            List<Direccion> lstDireccion = buscarDireccion(nombreVia);            

            // Definimos un array de "String" para almacenar todos los 
            // campos que queremos mostrar en nuestra tabla.
            String[] columnas = new String[8];
            for (Direccion d : lstDireccion) {
                columnas[0] = d.getIdDireccion().toString();
                columnas[1] = d.getTipoVia();
                columnas[2] = d.getNombreVia();
                columnas[3] = d.getNumero().stripTrailingZeros().toPlainString();
                columnas[4] = d.getPoblacion();
                columnas[5] = String.format("%05d", d.getCodPostal());
                columnas[6] = d.getProvinciaEstado();
                columnas[7] = d.getPais();
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
            mensaje = mensaje + "OK: " + lstDireccion.size() + " direcciones recuperadas.";
        } catch (Exception E) {
            mensaje = mensaje + "ERROR: " + direccion + "\n" + E.getMessage();
            System.out.println(mensaje);
        }
        return estado;
    }
    
}
