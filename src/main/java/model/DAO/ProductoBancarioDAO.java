package model.DAO;

//<editor-fold defaultstate="collapsed" desc=" Librerías importadas... ">
import controller.exceptions.NonexistentEntityException;
import controller.ProductoBancarioJpaController;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import model.entity.Cliente;
import model.entity.ProductoBancario;
import utilities.Functions;
//</editor-fold>

/**
 * Clase "DATA ACCESS OBJECT" para {@link ProductoBancario}.
 * 
 * @author Ignacio Pineda Martín
 */
public class ProductoBancarioDAO {

    private ProductoBancarioJpaController pjc;
    private ProductoBancario productoBancario = new ProductoBancario();
    
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
    
    public ProductoBancario getProductoBancario() {
        return productoBancario;
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
 
    public ProductoBancarioDAO() {
    }

    public ProductoBancarioDAO(EntityManagerFactory emf) {
        this.pjc = new ProductoBancarioJpaController(emf);          
    }

    /**
     * Método para el alta de un nuevo {@link ProductoBancario}.
     * 
     * @param pb El objeto {@link ProductoBancario} a crear.
     * @return <b>true</b> El {@link ProductoBancario} ha sido dada de alta.
     * <br><b>false</b> El {@link ProductoBancario} no ha sido dada de alta.
     */
    public boolean insertarProductoBancario(ProductoBancario pb) {
        estado = false;
        mensaje = "insertarProductoBancario -> ";
        try {
            pb.setIdProductoBancario(null);
            pjc.create(pb);
            estado = true;
            mensaje = mensaje + "OK: " + pb;
            productoBancario = pb;
        } catch (Exception E) {
            mensaje = mensaje + "ERROR: " + pb + "\n" + E.getMessage();
            System.out.println(mensaje);
        }      
        return estado;
    }
    
    /**
     * Método para la modificación de un {@link ProductoBancario}.
     * 
     * @param pb El objeto {@link ProductoBancario} a modificar.
     * @return <b>true</b> El {@link ProductoBancario} ha sido modificado.
     * <br><b>false</b> El {@link ProductoBancario} no ha sido modificado.
     */
    public boolean actualizarProductoBancario(ProductoBancario pb) {
        estado = false;
        mensaje = "actualizarProductoBancario -> ";
        try {
            // Antes de lanzar el "edit", comprobamos que el producto bancario
            // con el ID seleccionado existe. En caso contrario, lanzaremos una
            // excepción.
            if (pjc.findProductoBancario(pb.getIdProductoBancario()) == null)
                throw new NonexistentEntityException("El ProductoBancario con id " + pb.getIdProductoBancario()+ " no existe.");            
            pjc.edit(pb);
            estado = true;
            mensaje = mensaje + "OK: " + pb;
            productoBancario = pb;
        } catch (Exception E) {
            mensaje = mensaje + "ERROR: " + pb + "\n" + E.getMessage();
            System.out.println(mensaje);
        }      
        return estado;
    }    
    
    /**
     * Recupera todos los productos bancarios almacenados en la BB.DD.
     * 
     * @return lista de objetos {@link ProductoBancario}.
     */
    public List<ProductoBancario> buscarProductoBancario() {
        estado = false;
        mensaje = "buscarProductoBancario -> ";
        List<ProductoBancario> lstProductoBancario = null;
        EntityManager em = pjc.getEntityManager();
        try {
            lstProductoBancario = pjc.findProductoBancarioEntities();
            if (lstProductoBancario == null)
                throw new NonexistentEntityException("No existen ProductoBancarios (tabla vacía).");
            estado = true;
            mensaje = mensaje + "OK: " + lstProductoBancario.size() + " productos recuperados.";
        } catch (Exception E) {
            mensaje = mensaje + "ERROR: \n" + E.getMessage();
            System.out.println(mensaje);            
        } finally {
            em.close();
        }
        return lstProductoBancario;
    }    

    /**
     * Búsqueda de {@link ProductoBancario} por su ID. Si se encuentra se
     * devolverá en la propiedad {@link productoBancario}, accesible mediante su
     * "getter" {@link getProductoBancario()}.
     *
     * @param idProductoBancario El ID del {@link ProductoBancario} a buscar.
     * @return <b>true</b> El {@link ProductoBancario} ha sido encontrado.
     * <br><b>false</b> El {@link ProductoBancario} no ha sido encontrado.
     */
    public boolean buscarProductoBancario(Integer idProductoBancario) {
        estado = false;
        mensaje = "buscarProductoBancario -> ";
        EntityManager em = pjc.getEntityManager();
        try {
            productoBancario = pjc.findProductoBancario(idProductoBancario);
            if (productoBancario == null)
                throw new NonexistentEntityException(("El ProductoBancario con id " + idProductoBancario + " no existe."));
            estado = true;
            mensaje = mensaje + "OK: " + productoBancario;
        } catch (Exception E) {
            mensaje = mensaje + "ERROR: \n" + E.getMessage();
            System.out.println(mensaje);            
        } finally {
            em.close();
        }
        return estado;
    }     
    
    /**
     * Lista las entidades {@link Cliente} de un {@link ProductoBancario} en un
     * JTable.
     *
     * @param tabla JTable a rellenar.
     * @param productoBancario El producto bancario con clientes a mostrar.
     * @return <b>true</b> La ejecución se ha completado correctamente.
     * <br><b>false</b> La ejecución ha terminado con problemas. Los detalles se
     * pueden recuperar en la propiedad {@link mensaje}.
     */
    public boolean listarClientesProductoBancario(JTable tabla, ProductoBancario productoBancario) {
        estado = false;
        mensaje = "listarClientesProductoBancario -> ";
        try {
            // "DefaultTableModel" sirve para definir el modelo de datos (columnas,
            // tipos de datos) que se mostrará en un JTable.
            DefaultTableModel dtm;
            String[] cabecera = {"ID", "NOMBRE"};
            dtm = new DefaultTableModel(null, cabecera);

            if (productoBancario.getClienteList() != null) {
                // Definimos un array de "String" para almacenar todos los 
                // campos que queremos mostrar en nuestra tabla.
                String[] columnas = new String[2];
                for (Cliente e : productoBancario.getClienteList()) {
                    columnas[0] = e.getIdCliente().toString();
                    columnas[1] = e.getNombreCliente() + " " + e.getApellidosCliente() + " (" + e.getClaveIdentificacion() + ")";
                    // Añadimos una fila al "DefaultTableModel" que hemos definido
                    // antes.
                    dtm.addRow(columnas);
                }
                estado = true;
                mensaje = mensaje + "OK: " + productoBancario.getClienteList().size() + " clientes recuperados.";
            } else {
                mensaje = mensaje + "ERROR: productoBancario.getClienteList() es null.";
            }
            
            // Asignamos al "JTable" el "DefaultTableModel", que ya contiene
            // la información a mostrar, o estará vacío si no había nada que
            // listar.
            tabla.setModel(dtm);
            // Removemos la primera columna (ID) de la tabla, pero no así sus
            // datos (accesibles desde el modelo).
            tabla.removeColumn(tabla.getColumnModel().getColumn(0));

        } catch (Exception E) {
            mensaje = mensaje + "ERROR: " + productoBancario + "\n" + E.getMessage();
            System.out.println(mensaje);
        }
        return estado;
    }
    
    /**
     * Lista las entidades {@link ProductoBancario} de un objeto {@link Cliente}
     * en un JTable.
     *
     * @param tabla JTable a rellenar.
     * @param cliente El cliente con productos bancarios a mostrar.
     * @return <b>true</b> La ejecución se ha completado correctamente.
     * <br><b>false</b> La ejecución ha terminado con problemas. Los detalles se
     * pueden recuperar en la propiedad {@link mensaje}.
     */
    public boolean listarProductoBancario(JTable tabla, Cliente cliente) {
        estado = false;
        mensaje = "listarProductoBancario -> ";
        try {
            // "DefaultTableModel" sirve para definir el modelo de datos (columnas,
            // tipos de datos) que se mostrará en un JTable.
            DefaultTableModel dtm;
            String[] cabecera = {"ID", "PRODUCTO", "NUMERO", "FEC.APERTURA"};
            dtm = new DefaultTableModel(null, cabecera);

            if (cliente.getProductoBancarioList() != null) {
                // Definimos un array de "String" para almacenar todos los 
                // campos que queremos mostrar en nuestra tabla.
                String[] columnas = new String[4];
                for (ProductoBancario pb : cliente.getProductoBancarioList()) {
                    // Sólo mostramos los productos activos.
                    if (pb.getFecCancelacion() == null) {
                        columnas[0] = pb.getIdProductoBancario().toString();
                        columnas[1] = pb.getCatalogo().getNombreProducto();
                        if (pb.getCuentaCorriente() != null) {
                            columnas[2] = String.format("%10d", pb.getCuentaCorriente().getNumeroCuenta());
                        }
                        if (pb.getTarjetaCredito() != null) {
                            columnas[2] = String.format("%10d", pb.getTarjetaCredito().getNumeroTarjeta());
                        }
                        if (pb.getPrestamo() != null) {
                            columnas[2] = String.format("%10d", pb.getPrestamo().getNumeroPrestamo());
                        }
                        if (pb.getAval() != null) {
                            columnas[2] = String.format("%10d", pb.getAval().getNumeroAval());
                        }
                        columnas[3] = Functions.formatDate(pb.getFecApertura());
                        // Añadimos una fila al "DefaultTableModel" que hemos definido
                        // antes.
                        dtm.addRow(columnas);
                    }
                }
                estado = true;
                mensaje = mensaje + "OK: " + cliente.getProductoBancarioList().size() + " productos recuperados.";
            } else {
                mensaje = mensaje + "ERROR: cliente.getProductoBancarioList() es null.";
            }
            
            // Asignamos al "JTable" el "DefaultTableModel", que ya contiene
            // la información a mostrar, o estará vacío si no había nada que
            // listar.
            tabla.setModel(dtm);
            // Removemos la primera columna (ID) de la tabla, pero no así sus
            // datos (accesibles desde el modelo).
            tabla.removeColumn(tabla.getColumnModel().getColumn(0));

        } catch (Exception E) {
            mensaje = mensaje + "ERROR: " + cliente + "\n" + E.getMessage();
            System.out.println(mensaje);
        }
        return estado;
    }    
   
}
