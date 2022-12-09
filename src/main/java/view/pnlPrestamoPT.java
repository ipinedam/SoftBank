package view;

import java.util.Date;
import javax.persistence.EntityManagerFactory;
import javax.swing.JOptionPane;

import model.entity.Cliente;
import model.entity.CuentaCorriente;
import model.entity.Prestamo;
import utilities.Constants.FormAction;

import utilities.Functions;
import utilities.Item;

/**
 * Panel de datos para la clase {@link Prestamo}, derivado de la clase
 * {@link pnlPrestamo}.
 * 
 * @author Ignacio Pineda Martín
 */
public class pnlPrestamoPT extends pnlPrestamo {
  
    /**
     * Creates new form pnlPrestamoPT
     */
    public pnlPrestamoPT() {
        initComponents();
    }

    public pnlPrestamoPT(EntityManagerFactory emf) {
        super(emf);
    }    
 
    @Override
    public void setJPAObject(Object jpaObject) {
        this.jpaObject = jpaObject;
        /**
         * Tratamiento para cuando se recibe en el panel un objeto de tipo
         * {@link Prestamo}
         */
        if (this.jpaObject instanceof Prestamo tc) {
            super.setJPAObject(tc);
        }
        /**
         * Tratamiento para cuando se recibe un objeto de tipo {@link Cliente}
         */
        if (this.jpaObject instanceof Cliente c) {
            // 
            if (frmParent.getFormAction() == FormAction.NEW) {
                /**
                 * En el alta incluimos todas las cuentas corrientes del cliente
                 * en el comboBox de cuentas corrientes de pago, para elegir la
                 * deseada.
                 */
                rellenarCbxCuentaCorrientePago(c);
                cbxCuentaCorrientePago.setSelectedIndex(-1);
                // El cliente de préstamo debe tener, obligatoriamente,
                // al menos una cuenta corriente.
                if (cbxCuentaCorrientePago.getItemCount() == 0) {
                    vaciarCampos();
                    ((frmPrestamo) frmParent).pnlProductoBancario.limpiarPanel();
                    JOptionPane.showMessageDialog(frmParent, "El cliente no tiene todavía una cuenta corriente.", "Préstamos", JOptionPane.WARNING_MESSAGE);
                }              
            } else {
                // Mostramos en la tabla del panel los préstamos asociadas al 
                // cliente recibido.
                mostrarTabla(c);
                if (tblPrestamo.getRowCount() > 0) {
                    // Si se ha recuperado información en la tabla, se selecciona
                    // por defecto la primera fila.
                    tblPrestamo.changeSelection(0, 0, false, false);
                    // Simulamos que hemos hecho "click" en la primera fila, para
                    // que se disparen los eventos asociados que permiten rellenar
                    // todo el formulario.
                    java.awt.event.MouseEvent evt = null;
                    tblPrestamoMouseClicked(evt);
                    rellenarCampos();

                    if (frmParent.getFormAction() != null) {
                        switch (frmParent.getFormAction()) {
                            case NEW:

                                break;
                            case CANCEL:

                                break;
                            case MODIFY:
                                /**
                                 * En la modificación incluimos todas las
                                 * cuentas corrientes del cliente en el comboBox
                                 * de cuentas corrientes de pago, para elegir la
                                 * deseada, y dejamos selecionada la que ya
                                 * tenía.
                                 */
                                rellenarCbxCuentaCorrientePago(c);
                                cbxCuentaCorrientePago.setSelectedItem(new Item<CuentaCorriente>(prestamo.getCuentaCorrientePago(), String.format("%10d", prestamo.getCuentaCorrientePago().getNumeroCuenta())));
                                break;

                            case MOVEMENT:
                                break;

                            case QUERY:
                                if (tblPrestamo.getRowCount() > 1) {
                                    /**
                                     * (En modo "Consulta"/"Visualización",
                                     * mostramos la tabla de préstamos si hay
                                     * más de 1 préstamo.
                                     */
                                    pnlTabla.setVisible(true);
                                    frmParent.pack();
                                }
                                break;
                            default:
                                throw new AssertionError();
                        }
                    }
                } else {
                    frmParent.btnLimpiar.doClick();
                    JOptionPane.showMessageDialog(frmParent, "Cliente sin préstamo", "Préstamos", JOptionPane.WARNING_MESSAGE);
                }
            }
        }
    }
    
    /**
     * Método para recoger los campos del formulario en la propiedad
     * {@link prestamo}
     */
    @Override
    protected void recogerCampos() {
        Item cc;
        switch (frmParent.getFormAction()) {
            case NEW:
                cc = (Item) cbxCuentaCorrientePago.getSelectedItem();
                prestamo.setCuentaCorrientePago((CuentaCorriente) cc.getValue());
                prestamo.setNumeroPrestamo(0);
                prestamo.setTipoGarantia(cbxTipoGarantia.getSelectedItem().toString());
                prestamo.setTipoInteres(Functions.getBigDecimalFromFormattedNumber(txtTipoInteres.getText()));
                prestamo.setFecVencimiento((Date) txtFecVencimiento.getValue());
                prestamo.setImpConcedido(Functions.getBigDecimalFromFormattedNumber(txtImpConcedido.getText()));
                prestamo.setImpSaldoPendiente(Functions.getBigDecimalFromFormattedNumber(txtImpConcedido.getText()));
                break;

            case CANCEL:
                break;

            case MODIFY:
                prestamo.setTipoInteres(Functions.getBigDecimalFromFormattedNumber(txtTipoInteres.getText()));
                // prestamo.setImpConcedido(Functions.getBigDecimalFromFormattedNumber(txtImpConcedido.getText()));
                prestamo.setTipoGarantia(cbxTipoGarantia.getSelectedItem().toString());
                cc = (Item) cbxCuentaCorrientePago.getSelectedItem();
                prestamo.setCuentaCorrientePago((CuentaCorriente) cc.getValue());                
                break;
                
            case MOVEMENT:
                break;
                
            case QUERY:

                break;
            default:
                throw new AssertionError();
        }         
    }
    
    /**
     * Método para rellenar la tabla del panel.
     * 
     * @param c El {@link Cliente} del que se desean mostrar sus préstamos.
     */
    protected void mostrarTabla(Cliente c) {
        pdao.listarPrestamoCliente(tblPrestamo, c);
    }     
    
    /**
     * Comprobación de campos requeridos.
     * 
     * @return <b>true</b> Todos los campos requeridos son correctos.
     * <br><b>false</b> No todos los campos requeridos son correctos.
     */
    @Override
    protected boolean checkRequiredFields() {
        if (frmParent.getFormAction() != null) {
            switch (frmParent.getFormAction()) {
                case NEW:
                    // En el alta de un préstamo, son obligatorios
                    // el tipo de interes, el importe concedido, la fecha de
                    // vencimiento, el tipo de garantía y la cuenta corriente 
                    // de pago. El número de préstamo se asignará automáticamente.
                    return !txtTipoInteres.getText().equals("")
                            && !txtImpConcedido.getText().equals("")
                            && !(Functions.getNumberFromFormattedNumber(txtImpConcedido.getText()).doubleValue() >= 0.0)
                            && !txtFecVencimiento.getText().equals("")
                            && (cbxTipoGarantia.getSelectedIndex() != -1)
                            && (cbxCuentaCorrientePago.getSelectedIndex() != -1);

                case CANCEL:
                    // En la cancelación, no es obligatorio ningún campo
                    // del panel.
                    return true;

                case MODIFY:
                    // En la modificación de un Prétamo, son modificables el 
                    // tipo de interes, el tipo de garantía y la cuenta 
                    // corriente de pago. 
                    return !txtTipoInteres.getText().equals("")
                            && (cbxTipoGarantia.getSelectedIndex() != -1)
                            && (cbxCuentaCorrientePago.getSelectedIndex() != -1);

                case MOVEMENT:
                    // En la cancelación, no es obligatorio ningún campo
                    // del panel.
                    return true;

                case QUERY:

                    break;
                default:
                    throw new AssertionError();
            }
        }
        return false;
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
    protected void tblPrestamoMouseClicked(java.awt.event.MouseEvent evt) {                                                
        // "getSelectedRow()" nos devuelve el índice de la fila seleccionada.
        int fila = tblPrestamo.getSelectedRow();
        if (fila != -1) {
            prestamo.setIdPrestamo(Integer.valueOf(tblPrestamo.getModel().getValueAt(fila, 0).toString()));
            // Recuperamos toda la información del objeto "Prestamo" a través
            // de su ID.
            if (pdao.buscarPrestamo(prestamo.getIdPrestamo())) {
                // Obtenemos el objeto "ProductoBancario" asociado a la cuenta
                // y lo enviamos al panel de Producto Bancario.
                setJPAObject(pdao.getPrestamo());
                
                // Enviamos el cliente al panel de producto bancario.
                if (frmParent instanceof frmPrestamo fp) {
                    fp.pnlProductoBancario.setJPAObject(this.prestamo.getProductoBancario());
                    fp.pnlProductoBancario.sendJPAObjectToFrmParent();
                }
                // Llamada para intentar habilitar los botones CRUD.
                if (this.frmParent != null) {
                    frmParent.tryEnableCRUDButtons();
                }
            }
        }
        inicializarFormulario();        
    }     
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
