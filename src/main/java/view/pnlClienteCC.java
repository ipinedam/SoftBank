package view;

import javax.persistence.EntityManagerFactory;
import javax.swing.JOptionPane;
import model.entity.ProductoBancario;

/**
 *
 * @author Ignacio Pineda Martín
 */
public class pnlClienteCC extends pnlCliente {

    /**
     * Creates new form pnlClienteCC
     */
    public pnlClienteCC() {
        super();
        initComponents();
    }
    
    public pnlClienteCC(EntityManagerFactory emf) {
        super(emf);
    }

    /**
     * Método para limpiar el panel al estado inicial. Necesario para ser
     * invocado desde el botón "Limpiar" del formulario contenedor.
     */
    @Override
    protected final void limpiarPanel() {
        super.limpiarPanel();
        // Preparamos elementos visuales.        
        btnInsertar.setEnabled(false);
    }

    @Override
    protected boolean checkRequiredFields() {
        // En el alta/modificación/cancelación de una Cuenta Corriente, el
        // contenido del panel de cliente es irrelevante.
        return true;
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
    protected void tblClienteMouseClicked(java.awt.event.MouseEvent evt) {
        Integer idCliente;
        // "getSelectedRow()" nos devuelve el índice de la fila seleccionada.
        int fila = tblCliente.getSelectedRow();
        if (fila != -1) {
            idCliente = Integer.valueOf(tblCliente.getModel().getValueAt(fila, 0).toString());
            // Recuperamos toda la información del objeto "Cliente" a través
            // de su ID.
            if (cdao.buscarCliente(idCliente)) {
                this.cliente = cdao.getCliente();
                // Fijamos el objeto Cliente en el panel.
                setJPAObject(this.cliente);
            } else {
                System.out.println("ERROR INESPERADO");
            }
        }
        
        switch (frmParent.getFormAction()) {
            case NEW:
                // Habilitamos/Deshabilitamos el botón de insertar del 
                // ToolBar.
                if (fila != -1) {
                    if (frmParent instanceof frmCuentaCorriente) {
                        btnInsertar.setEnabled(true);
                    }
                    if (frmParent instanceof frmTarjetaCredito ftc) {
                        // Para las Tarjetas de crédito, sólo se permite un
                        // titular.
                        ProductoBancario pb = ftc.pnlProductoBancario.getJPAObject();
                        if ((pb != null) && (pb.getClienteList() != null) && (pb.getClienteList().isEmpty())) {
                            btnInsertar.setEnabled(true);
                        } else {
                            btnInsertar.setEnabled(false);
                            JOptionPane.showMessageDialog(frmParent, "Sólo se permite 1 titular.", "Tarjetas de Crédito", JOptionPane.WARNING_MESSAGE);
                        }
                    }
                    if (frmParent instanceof frmPrestamo) {
                        btnInsertar.setEnabled(true);
                    }
                } else {
                    btnInsertar.setEnabled(false);
                }
                break;

            case CANCEL:
                // Enviamos el Cliente al panel de Cuentas Corrientes
                if (fila != -1) {
                    // Informamos a los paneles afectados.
                    informarPaneles();
                }
                break;

            case MODIFY:
                // Enviamos el Cliente al panel de Cuentas Corrientes
                if (fila != -1) {
                    // Informamos a los paneles afectados.
                    informarPaneles();
                }                
                break;
                
            case MOVEMENT:
                // Enviamos el Cliente al panel de Cuentas Corrientes
                if (fila != -1) {
                    // Informamos a los paneles afectados.
                    informarPaneles();                   
                }                
                break;                

            case QUERY:

                break;
            default:
                throw new AssertionError();
        }      
        
        inicializarFormulario();
    }

    /**
     * Método para informar a los paneles cada vez que se selecciona a
     * un cliente.
     */
    private void informarPaneles() {
        if (frmParent instanceof frmCuentaCorriente fcc) {
            fcc.pnlCuentaCorriente.setJPAObject(this.cliente);
        }
        if (frmParent instanceof frmTarjetaCredito ftc) {
            ftc.pnlTarjetaCredito.setJPAObject(this.cliente);
        }
        if (frmParent instanceof frmPrestamo fp) {
            fp.pnlPrestamo.setJPAObject(this.cliente);
        }
    }

    @Override
    protected void btnInsertarActionPerformed(java.awt.event.ActionEvent evt) {
        // Enviamos el cliente al panel de producto bancario.
        if (frmParent instanceof frmCuentaCorriente fcc) {
            fcc.pnlProductoBancario.setJPAObject(this.cliente);
        }
        // En las tarjetas de crédito, enviamos la información del cliente
        // al panel para capturar sus cuentas corrientes y así poderlas elegir
        // como cuenta de pago.
        if (frmParent instanceof frmTarjetaCredito ftc) {
            ftc.pnlProductoBancario.setJPAObject(this.cliente);
            ftc.pnlTarjetaCredito.setJPAObject(this.cliente);                   
        }
        // En los préstamos, enviamos la información del cliente
        // al panel para capturar sus cuentas corrientes y así poderlas elegir
        // como cuenta de pago.
        if (frmParent instanceof frmPrestamo fp) {
            fp.pnlProductoBancario.setJPAObject(this.cliente);
            fp.pnlPrestamo.setJPAObject(this.cliente);                   
        }        
        // Llamada para intentar habilitar los botones CRUD.
        if (this.frmParent != null) {
            frmParent.tryEnableCRUDButtons();
        }
    }

    @Override
    protected void btnLimpiarActionPerformed(java.awt.event.ActionEvent evt) {
        super.btnLimpiarActionPerformed(evt);
        btnInsertar.setEnabled(false);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
