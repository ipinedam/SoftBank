package view;

import java.math.BigDecimal;
import javax.persistence.EntityManagerFactory;
import javax.swing.JOptionPane;

import model.entity.Cliente;
import model.entity.CuentaCorriente;
import model.entity.TarjetaCredito;
import utilities.Constants.FormAction;

import utilities.Functions;
import utilities.Item;

/**
 * Panel de datos para la clase {@link TarjetaCredito}, derivado de la clase
 * {@link pnlTarjetaCredito}.
 * 
 * @author Ignacio Pineda Martín
 */
public class pnlTarjetaCreditoTC extends pnlTarjetaCredito {
  
    /**
     * Creates new form pnlTarjetaCreditoTC
     */
    public pnlTarjetaCreditoTC() {
        initComponents();
    }

    public pnlTarjetaCreditoTC(EntityManagerFactory emf) {
        super(emf);
    }    
 
    @Override
    public void setJPAObject(Object jpaObject) {
        this.jpaObject = jpaObject;
        /**
         * Tratamiento para cuando se recibe en el panel un objeto de tipo
         * {@link TarjetaCredito}
         */
        if (this.jpaObject instanceof TarjetaCredito tc) {
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
                // El cliente de una tarjeta de crédito debe tener, obligatoriamente,
                // al menos una cuenta corriente.
                if (cbxCuentaCorrientePago.getItemCount() == 0) {
                    vaciarCampos();
                    ((frmTarjetaCredito) frmParent).pnlProductoBancario.limpiarPanel();
                    JOptionPane.showMessageDialog(frmParent, "El cliente no tiene todavía una cuenta corriente.", "Tarjetas de Crédito", JOptionPane.WARNING_MESSAGE);
                }              
            } else {
                // Mostramos en la tabla del panel las tarjetas de crédito
                // asociadas al cliente recibido.
                mostrarTabla(c);
                if (tblTarjetaCredito.getRowCount() > 0) {
                    // Si se ha recuperado información en la tabla, se selecciona
                    // por defecto la primera fila.
                    tblTarjetaCredito.changeSelection(0, 0, false, false);
                    // Simulamos que hemos hecho "click" en la primera fila, para
                    // que se disparen los eventos asociados que permiten rellenar
                    // todo el formulario.
                    java.awt.event.MouseEvent evt = null;
                    tblTarjetaCreditoMouseClicked(evt);
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
                                cbxCuentaCorrientePago.setSelectedItem(new Item<CuentaCorriente>(tarjetaCredito.getCuentaCorrientePago(), String.format("%10d", tarjetaCredito.getCuentaCorrientePago().getNumeroCuenta())));
                                break;

                            case MOVEMENT:
                                break;

                            case QUERY:
                                if (tblTarjetaCredito.getRowCount() > 1) {
                                    /**
                                     * (En modo "Consulta"/"Visualización",
                                     * mostramos la tabla de tarjetas si hay más
                                     * de 1 tarjeta.
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
                    JOptionPane.showMessageDialog(frmParent, "Cliente sin tarjeta de crédito", "Tarjetas de Crédito", JOptionPane.WARNING_MESSAGE);
                }
            }
        }
    }
    
    /**
     * Método para recoger los campos del formulario en la propiedad
     * {@link tarjetaCredito}
     */
    @Override
    protected void recogerCampos() {
        Item cc;
        switch (frmParent.getFormAction()) {
            case NEW:
                tarjetaCredito.setNumeroTarjeta(0);
                tarjetaCredito.setTipoInteres(Functions.getBigDecimalFromFormattedNumber(txtTipoInteres.getText()));
                tarjetaCredito.setImpLimiteTarjeta(Functions.getBigDecimalFromFormattedNumber(txtImpLimiteTarjeta.getText()));
                tarjetaCredito.setImpSaldoPendiente(BigDecimal.ZERO);
                tarjetaCredito.setFormaPago(cbxFormaPago.getSelectedItem().toString());
                cc = (Item) cbxCuentaCorrientePago.getSelectedItem();
                tarjetaCredito.setCuentaCorrientePago((CuentaCorriente) cc.getValue());
                break;

            case CANCEL:
                break;

            case MODIFY:
                tarjetaCredito.setTipoInteres(Functions.getBigDecimalFromFormattedNumber(txtTipoInteres.getText()));
                tarjetaCredito.setImpLimiteTarjeta(Functions.getBigDecimalFromFormattedNumber(txtImpLimiteTarjeta.getText()));
                tarjetaCredito.setFormaPago(cbxFormaPago.getSelectedItem().toString());
                cc = (Item) cbxCuentaCorrientePago.getSelectedItem();
                tarjetaCredito.setCuentaCorrientePago((CuentaCorriente) cc.getValue());                
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
     * @param c El {@link Cliente} del que se desean mostrar sus tarjetas.
     */
    protected void mostrarTabla(Cliente c) {
        tcdao.listarTarjetaCreditoCliente(tblTarjetaCredito, c);
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
                    // En el alta de una Tarjeta de Crédito, son obligatorios
                    // el tipo de interes, el límite de la tarjeta, la forma de
                    // pago y la cuenta corriente de pago. El número de tarjeta 
                    // se asignará automáticamente.
                    return !txtTipoInteres.getText().equals("")
                            && !txtImpLimiteTarjeta.getText().equals("")
                            && !(Functions.getNumberFromFormattedNumber(txtImpLimiteTarjeta.getText()).doubleValue() >= 0.0)                            
                            && (cbxFormaPago.getSelectedIndex() != -1)
                            && (cbxCuentaCorrientePago.getSelectedIndex() != -1);

                case CANCEL:
                    // En la cancelación, no es obligatorio ningún campo
                    // del panel.
                    return true;

                case MODIFY:
                    // En la modificación de una Tarjeta de Crédito, son
                    // modificables el tipo de interes, el límite de la tarjeta,
                    // la forma de pago y la cuenta corriente de pago. 
                    return !txtTipoInteres.getText().equals("")
                            && !txtImpLimiteTarjeta.getText().equals("")
                            && (cbxFormaPago.getSelectedIndex() != -1)
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
    protected void tblTarjetaCreditoMouseClicked(java.awt.event.MouseEvent evt) {                                                
        // "getSelectedRow()" nos devuelve el índice de la fila seleccionada.
        int fila = tblTarjetaCredito.getSelectedRow();
        if (fila != -1) {
            tarjetaCredito.setIdTarjetaCredito(Integer.valueOf(tblTarjetaCredito.getModel().getValueAt(fila, 0).toString()));
            // Recuperamos toda la información del objeto "TarjetaCredito" a través
            // de su ID.
            if (tcdao.buscarTarjetaCredito(tarjetaCredito.getIdTarjetaCredito())) {
                // Obtenemos el objeto "ProductoBancario" asociado a la cuenta
                // y lo enviamos al panel de Producto Bancario.
                setJPAObject(tcdao.getTarjetaCredito());
                
                // Enviamos el cliente al panel de producto bancario.
                if (frmParent instanceof frmTarjetaCredito fcc) {
                    fcc.pnlProductoBancario.setJPAObject(this.tarjetaCredito.getProductoBancario());
                    fcc.pnlProductoBancario.sendJPAObjectToFrmParent();
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
