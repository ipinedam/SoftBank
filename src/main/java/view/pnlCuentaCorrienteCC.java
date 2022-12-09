package view;

import java.math.BigDecimal;
import javax.persistence.EntityManagerFactory;
import javax.swing.JOptionPane;

import model.entity.Cliente;
import model.entity.CuentaCorriente;

import utilities.Functions;

/**
 * Panel de datos para la clase {@link CuentaCorriente}, derivado de la clase
 * {@link pnlCuentaCorriente}.
 * 
 * @author Ignacio Pineda Martín
 */
public class pnlCuentaCorrienteCC extends pnlCuentaCorriente {
  
    /**
     * Creates new form pnlCuentaCorrienteCC
     */
    public pnlCuentaCorrienteCC() {
        initComponents();
    }

    public pnlCuentaCorrienteCC(EntityManagerFactory emf) {
        super(emf);
    }    
 
    @Override
    public void setJPAObject(Object jpaObject) {
        this.jpaObject = jpaObject;
        /**
         * Tratamiento para cuando se recibe en el panel un objeto de tipo
         * {@link CuentaCorriente}
         */
        if (this.jpaObject instanceof CuentaCorriente cc) {
            this.cuentaCorriente = cc;
            rellenarCampos();
        }
        /**
         * Tratamiento para cuando se recibe un objeto de tipo
         * {@link Cliente}
         */
        if (this.jpaObject instanceof Cliente c) {
            // Mostramos en la tabla del panel las cuentas corrientes
            // asociadas al cliente recibido.
            mostrarTabla(c);
            if (tblCuentaCorriente.getRowCount() > 0) {
                // Si se ha recuperado información en la tabla, se selecciona
                // por defecto la primera fila.
                tblCuentaCorriente.changeSelection(0, 0, false, false);
                // Simulamos que hemos hecho "click" en la primera fila, para
                // que se disparen los eventos asociados que permiten rellenar
                // todo el formulario.
                java.awt.event.MouseEvent evt = null;
                tblCuentaCorrienteMouseClicked(evt);
                rellenarCampos();

                if (frmParent.getFormAction() != null) {
                    switch (frmParent.getFormAction()) {
                        case NEW:

                            break;
                        case CANCEL:

                            break;
                        case MODIFY:
                            break;
                            
                        case MOVEMENT:
                            break;
                            
                        case QUERY:
                            if (tblCuentaCorriente.getRowCount() > 1) {
                                /**
                                 * (En modo "Consulta"/"Visualización",
                                 * mostramos la tabla de cuentas si hay más de 1
                                 * cuenta.
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
                JOptionPane.showMessageDialog(frmParent, "Cliente sin cuenta corriente", "Cuentas Corrientes", JOptionPane.WARNING_MESSAGE);
            }
        }
    }
    
    /**
     * Método para recoger los campos del formulario en la propiedad
     * {@link cuentaCorriente}
     */
    @Override
    protected void recogerCampos() {
        switch (frmParent.getFormAction()) {
            case NEW:
                cuentaCorriente.setNumeroCuenta(0);
                cuentaCorriente.setTipoInteres(Functions.getBigDecimalFromFormattedNumber(txtTipoInteres.getText()));
                cuentaCorriente.setImpSaldoActual(BigDecimal.ZERO);
                break;

            case CANCEL:
                break;

            case MODIFY:
                cuentaCorriente.setTipoInteres(Functions.getBigDecimalFromFormattedNumber(txtTipoInteres.getText()));
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
     * @param c El {@link Cliente} del que se desean mostrar sus cuentas.
     */
    protected void mostrarTabla(Cliente c) {
        cdao.listarCuentaCorrienteCliente(tblCuentaCorriente, c);
    }     
    
    /**
     * Comprobación de campos requeridos.
     * 
     * @return <b>true</b> Todos los campos requeridos son correctos.
     * <br><b>false</b> No todos los campos requeridos son correctos.
     */
    @Override
    protected boolean checkRequiredFields() {
        switch (frmParent.getFormAction()) {
            case NEW:
                // En el alta de una Cuenta Corriente, sólo es obligatorio
                // el tipo de interes. El número de cuenta se asignará
                // automáticamente.
                return !txtTipoInteres.getText().equals("");

            case CANCEL:
                // En la cancelación, no es obligatorio ningún campo
                // del panel.
                return true;

            case MODIFY:
                // En la modificación de una Cuenta Corriente, sólo es 
                // modificable el tipo de interes. 
                return !txtTipoInteres.getText().equals("");

            case MOVEMENT:
                // En la cancelación, no es obligatorio ningún campo
                // del panel.
                return true;
                
            case QUERY:

                break;
            default:
                throw new AssertionError();
        }
        // Esta sentencia nunca debería alcanzarse.
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
    protected void tblCuentaCorrienteMouseClicked(java.awt.event.MouseEvent evt) {                                                
        // "getSelectedRow()" nos devuelve el índice de la fila seleccionada.
        int fila = tblCuentaCorriente.getSelectedRow();
        if (fila != -1) {
            cuentaCorriente.setIdCuentaCorriente(Integer.valueOf(tblCuentaCorriente.getModel().getValueAt(fila, 0).toString()));
            // Recuperamos toda la información del objeto "CuentaCorriente" a través
            // de su ID.
            if (cdao.buscarCuentaCorriente(cuentaCorriente.getIdCuentaCorriente())) {
                // Obtenemos el objeto "ProductoBancario" asociado a la cuenta
                // y lo enviamos al panel de Producto Bancario.
                setJPAObject(cdao.getCuentaCorriente());
                
                // Enviamos el cliente al panel de producto bancario.
                if (frmParent instanceof frmCuentaCorriente fcc) {
                    fcc.pnlProductoBancario.setJPAObject(this.cuentaCorriente.getProductoBancario());
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
