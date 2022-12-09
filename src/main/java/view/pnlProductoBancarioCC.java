package view;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.persistence.EntityManagerFactory;
import javax.swing.table.DefaultTableModel;
import model.DAO.ClienteDAO;
import model.entity.Cliente;
import model.entity.ProductoBancario;

/**
 * Panel de datos para la clase {@link ProductoBancario}, derivado de la clase
 * {@link pnlProductoBancario}.
 *
 * @author Ignacio Pineda Martín
 */
public class pnlProductoBancarioCC extends pnlProductoBancario {

    private ClienteDAO cdao;

    /**
     * Creates new form pnlProductoBancarioCC
     */
    public pnlProductoBancarioCC() {
        super();
        initComponents();
    }

    public pnlProductoBancarioCC(EntityManagerFactory emf) {
        super(emf);
        // Creamos el DAO con su EntityManagerFactory.
        cdao = new ClienteDAO(emf);    
    }

    @Override
    public void setJPAObject(Object jpaObject) {
        this.jpaObject = jpaObject;
        if (this.jpaObject instanceof ProductoBancario p) {
            this.productoBancario = p;
        }
        // Si se recibe un cliente en el panel, es para añadirlo a la 
        // lista de clientes.
        if (this.jpaObject instanceof Cliente c) {
            List<Cliente> lstCliente = new ArrayList<Cliente>();
            // Si el producto bancario ya tiene una lista de cliente, los
            // recogemos. En caso contrario, inicializamos la lista.
            if (this.productoBancario.getClienteList() != null) {
                lstCliente.addAll(this.productoBancario.getClienteList());
                this.productoBancario.getClienteList().clear();
            } else {
                this.productoBancario.setClienteList(new ArrayList<Cliente>());
            }
            // Si el cliente no esta en la lista, lo añadimos.
            if (!lstCliente.contains(c))
                lstCliente.add(c);
            // Guardamos la nueva lista de clientes en el producto bancario.
            this.productoBancario.setClienteList(lstCliente);
        }        
        rellenarCampos();
    }    
    
    /**
     * Método para limpiar el panel al estado inicial. Necesario para ser
     * invocado desde el botón "Limpiar" del formulario contenedor.
     */
    @Override
    protected final void limpiarPanel() {
        vaciarCampos();

        // Vaciamos la lista de clientes.
        this.productoBancario.setClienteList(new ArrayList<Cliente>());
        mostrarTabla();

        inicializarFormulario();

        // Preparamos elementos visuales.
        btnEliminar.setEnabled(false);
    }

    /**
     * Método para rellenar los campos del formulario con la propiedad
     * {@link productoBancario}
     */
    @Override
    protected void rellenarCampos() {
        super.rellenarCampos();

        switch (frmParent.getFormAction()) {
            case NEW:
                vaciarCampos();
                mostrarTabla();
                break;
            case CANCEL:
                mostrarTabla();
                break;
            case MODIFY:
                mostrarTabla();
                break;
            case MOVEMENT:
                mostrarTabla();
                break;
            case QUERY:
                mostrarTabla();                
                break;
            default:
                throw new AssertionError();
        }
    }    
    
    /**
     * Método para rellenar la tabla del panel con los clientes asociados al
     * producto.
     */
    @Override
    protected void mostrarTabla() {
        pdao.listarClientesProductoBancario(tblProductoBancario, this.productoBancario);
    } 
    
    @Override
    protected boolean checkRequiredFields() {
        // La comprobación de campos requeridos incluye que la lista de 
        // clientes en el producto no esté vacía.
        if (this.productoBancario.getClienteList() != null) {
            return super.checkRequiredFields()
                    && (!this.productoBancario.getClienteList().isEmpty());
        } else {
            return false;
        }
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    @Override
    protected void tblProductoBancarioMouseClicked(java.awt.event.MouseEvent evt) {
        // En este evento hacemos la comunicación entre el
        // panel de ProductoBancario (este) y el panel de Cliente.
        Integer idCliente;
        // "getSelectedRow()" nos devuelve el índice de la fila seleccionada.
        int fila = tblProductoBancario.getSelectedRow();
        if (fila != -1) {
            idCliente = Integer.valueOf(tblProductoBancario.getModel().getValueAt(fila, 0).toString());
            // Recuperamos toda la información del objeto "Cliente" a través
            // de su ID.
            if (cdao.buscarCliente(idCliente)) {
                // Fijamos el objeto Cliente en el panel de Cliente.
                if (frmParent instanceof frmCuentaCorriente fcc) {
                    fcc.pnlCliente.setJPAObject(cdao.getCliente());
                }
                if (frmParent instanceof frmTarjetaCredito ftc) {
                    ftc.pnlCliente.setJPAObject(cdao.getCliente());
                }
                if (frmParent instanceof frmPrestamo fp) {
                    fp.pnlCliente.setJPAObject(cdao.getCliente());
                }
            } else {
                System.out.println("ERROR INESPERADO");
            }
            btnEliminar.setEnabled(true);
        } else {
            btnEliminar.setEnabled(false);
        }
        inicializarFormulario();
    }
    
    @Override
    protected void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {
        // Eliminamos el cliente seleccionado de la lista de clientes del
        // producto bancario.
        Integer idCliente;
        int fila = tblProductoBancario.getSelectedRow();
        if (fila != -1) {
            idCliente = Integer.valueOf(tblProductoBancario.getModel().getValueAt(fila, 0).toString());
            
            // Copiamos la lista de clientes almacenada a una nueva lista.
            List<Cliente> lstCliente = new ArrayList<Cliente>();
            lstCliente.addAll(this.productoBancario.getClienteList());
            this.productoBancario.getClienteList().clear();

            // Recorremos la lista de clientes creada y eliminamos el
            // seleccionado.
            Iterator<Cliente> itr = lstCliente.iterator();
            while (itr.hasNext()) {
                Cliente c = itr.next();
                if (c.getIdCliente().intValue() == idCliente.intValue()) {
                    itr.remove();
                }     
            }
            
            // Guardamos la nueva lista de clientes en el producto bancario.
            this.productoBancario.setClienteList(lstCliente);

            // Eliminamos la fila de la tabla.
            DefaultTableModel dtm = (DefaultTableModel) tblProductoBancario.getModel();
            dtm.removeRow(fila);

            // Desactivamos el botón de borrar.
            btnEliminar.setEnabled(false);
            
            // Si estamos en la pantalla de préstamos, volvemos a cargar
            // la lista de cuentas corrientes asociadas a los clientes.
            if (frmParent instanceof frmPrestamo fp) {
                fp.pnlPrestamo.rellenarCbxCuentaCorrientePago(lstCliente);
            }
        }
        // Llamada para intentar habilitar los botones CRUD.
        if (this.frmParent != null) {
            frmParent.tryEnableCRUDButtons();
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}