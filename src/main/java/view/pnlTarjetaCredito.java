package view;

//<editor-fold defaultstate="collapsed" desc=" Librerías importadas... ">
import java.awt.Component;

import javax.persistence.EntityManagerFactory;
import javax.swing.JOptionPane;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import model.DAO.TarjetaCreditoDAO;

import model.entity.Cliente;
import model.entity.CuentaCorriente;
import model.entity.ProductoBancario;
import model.entity.TarjetaCredito;

import utilities.Banking;
import utilities.Functions;
import utilities.Item;
//</editor-fold>

/**
 * Panel de datos para la clase {@link TarjetaCredito}, derivado de la clase
 * {@link pnlAppSoftBank}.
 *
 * @author Ignacio Pineda Martín
 */
public class pnlTarjetaCredito extends pnlAppSoftBank {
    
    protected TarjetaCreditoDAO tcdao;
    protected TarjetaCredito tarjetaCredito = new TarjetaCredito();

    /**
     * Creates new form pnlTarjetaCredito
     */
    public pnlTarjetaCredito() {
        initComponents();
    }
    
    public pnlTarjetaCredito(EntityManagerFactory emf) {
        this();
        this.jpaObject = tarjetaCredito;
        this.emf = emf;
        // Creamos el DAO con su EntityManagerFactory.
        tcdao = new TarjetaCreditoDAO(emf);
        // Preparamos el panel en su estado inicial.
        limpiarPanel();
        // Hacemos que la tabla no sea editable.
        tblTarjetaCredito.setDefaultEditor(Object.class, null);        
    }
    
    public void setJPAObject(TarjetaCredito jpaObject) {
        this.tarjetaCredito = jpaObject;
        rellenarCampos();
    }
    
    public TarjetaCredito getJPAObject() {
        recogerCampos();
        return this.tarjetaCredito;
    }
    
    @Override
    public void sendJPAObjectToFrmParent() {
        if (this.frmParent != null) {
            frmParent.setJPAObject(this.tarjetaCredito);            
        }        
    }
    
    /**
     * Método para limpiar el panel al estado inicial. Necesario para ser
     * invocado desde el botón "Limpiar" del formulario contenedor.
     */
    protected final void limpiarPanel() {
        vaciarCampos();
        mostrarTabla("");
        inicializarFormulario();
    }
    
    /**
     * Método para inicializar los campos del formulario.
     */
    protected void vaciarCampos() {
        tarjetaCredito.setIdTarjetaCredito(null);
        cbxCuentaCorrientePago.removeAllItems();
        txtNumeroTarjeta.setText("");
        lblTxtPAN.setText("");
        txtTipoInteres.setText("");
        txtImpLimiteTarjeta.setText("");
        lblTxtImpSaldoPendiente.setText("");
        cbxFormaPago.setSelectedIndex(-1);
    }
    
    /**
     * Método para rellenar los campos del formulario con la propiedad
     * {@link tarjetaCredito}
     */
    protected void rellenarCampos() {
        rellenarCbxCuentaCorrientePago(tarjetaCredito.getProductoBancario().getClienteList().get(0));
        cbxCuentaCorrientePago.setSelectedItem(new Item<CuentaCorriente>(tarjetaCredito.getCuentaCorrientePago(), String.format("%10d", tarjetaCredito.getCuentaCorrientePago().getNumeroCuenta())));
        txtNumeroTarjeta.setText(String.format("%10d", tarjetaCredito.getNumeroTarjeta()));
        lblTxtPAN.setText(Banking.fourDigitGroup(tarjetaCredito.getPan()));
        txtTipoInteres.setText(Functions.formatNumber(tarjetaCredito.getTipoInteres()));
        txtImpLimiteTarjeta.setText(Functions.formatNumber(tarjetaCredito.getImpLimiteTarjeta()));
        lblTxtImpSaldoPendiente.setText(Functions.formatAmount(tarjetaCredito.getImpSaldoPendiente()));
        cbxFormaPago.setSelectedItem(tarjetaCredito.getFormaPago());
    }
 
    /**
     * Método para rellenar el comboBox de cuenta corriente de pago con todas
     * las cuentas corrientes de el cliente proporcionado por parámetro.
     *
     * @param c el {@link Cliente} propietario de la tarjeta.
     */
    protected void rellenarCbxCuentaCorrientePago(Cliente c) {
        cbxCuentaCorrientePago.removeAllItems();
        for (ProductoBancario pb : c.getProductoBancarioList()) {
            // Añadimos las cuentas corrientes activas.
            if ((pb.getFecCancelacion() == null) && (pb.getCuentaCorriente() != null)) {
                cbxCuentaCorrientePago.addItem(new Item<CuentaCorriente>(pb.getCuentaCorriente(), String.format("%10d", pb.getCuentaCorriente().getNumeroCuenta())));
            }
        }
    }    
    
    /**
     * Método para recoger los campos del formulario en la propiedad
     * {@link tarjetaCredito}
     */    
    protected void recogerCampos() {
        tarjetaCredito.setNumeroTarjeta(Long.parseLong(txtNumeroTarjeta.getText()));
        tarjetaCredito.setTipoInteres(Functions.getBigDecimalFromFormattedNumber(txtTipoInteres.getText()));
        tarjetaCredito.setImpLimiteTarjeta(Functions.getBigDecimalFromFormattedNumber(txtImpLimiteTarjeta.getText()));
        tarjetaCredito.setFormaPago(cbxFormaPago.getSelectedItem().toString());
        Item cc = (Item) cbxCuentaCorrientePago.getSelectedItem();
        tarjetaCredito.setCuentaCorrientePago((CuentaCorriente) cc.getValue());
    }

    /**
     * Método para preparar el formulario en un estado inicial.
     */
    protected void inicializarFormulario() {
        txtNumeroTarjeta.requestFocus();
        // Llamada para intentar habilitar los botones CRUD.
        if (this.frmParent != null) {
            frmParent.tryEnableCRUDButtons();
        }        
    }

    /**
     * Método para rellenar la tabla del panel.
     * 
     * @param numeroTarjeta El número de la Tarjeta de Crédito que mostraremos
     * en la tabla (si el valor es "" se mostrarán todas).
     */
    protected void mostrarTabla(String numeroTarjeta) {
        tcdao.listarTarjetaCreditoNumero(tblTarjetaCredito, numeroTarjeta);
    }    
    
    @Override
    protected boolean checkRequiredFields() {
        return !txtNumeroTarjeta.getText().equals("") && 
               !txtTipoInteres.getText().equals("") &&
               !txtImpLimiteTarjeta.getText().equals("") &&
               !(Functions.getNumberFromFormattedNumber(txtImpLimiteTarjeta.getText()).doubleValue() >= 0.0) &&                 
               (cbxFormaPago.getSelectedIndex() != -1) &&  
               (cbxCuentaCorrientePago.getSelectedIndex() != -1);
    }
   
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlDatos = new javax.swing.JPanel();
        lblNumeroTarjeta = new javax.swing.JLabel();
        txtNumeroTarjeta = new javax.swing.JTextField();
        lblPAN = new javax.swing.JLabel();
        lblTxtPAN = new javax.swing.JLabel();
        lbImplLimiteTarjeta = new javax.swing.JLabel();
        txtImpLimiteTarjeta = new javax.swing.JFormattedTextField();
        lblTipoInteres = new javax.swing.JLabel();
        txtTipoInteres = new javax.swing.JTextField();
        lblCuentaCorrientePago = new javax.swing.JLabel();
        cbxCuentaCorrientePago = new javax.swing.JComboBox<Item<CuentaCorriente>>();
        lblFormaPago = new javax.swing.JLabel();
        cbxFormaPago = new javax.swing.JComboBox<>();
        lblImpSaldoPendiente = new javax.swing.JLabel();
        lblTxtImpSaldoPendiente = new javax.swing.JLabel();
        pnlTabla = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblTarjetaCredito = new javax.swing.JTable() {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component component = super.prepareRenderer(renderer, row, column);
                int rendererWidth = component.getPreferredSize().width;
                TableColumn tableColumn = getColumnModel().getColumn(column);
                tableColumn.setPreferredWidth(Math.max(rendererWidth + getIntercellSpacing().width, tableColumn.getPreferredWidth()));
                return component;
            }
        };

        setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Tarjeta de Crédito", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14))); // NOI18N

        pnlDatos.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        pnlDatos.setName("pnlDatos"); // NOI18N

        lblNumeroTarjeta.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblNumeroTarjeta.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/search 16px.png"))); // NOI18N
        lblNumeroTarjeta.setLabelFor(txtNumeroTarjeta);
        lblNumeroTarjeta.setText("Nº tarjeta");
        lblNumeroTarjeta.setToolTipText("Campo de búsqueda");
        lblNumeroTarjeta.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        lblNumeroTarjeta.setName("lblBusqueda"); // NOI18N

        txtNumeroTarjeta.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtNumeroTarjeta.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtNumeroTarjetaKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtNumeroTarjetaKeyTyped(evt);
            }
        });

        lblPAN.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblPAN.setLabelFor(lblTxtPAN);
        lblPAN.setText("PAN");

        lblTxtPAN.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        lbImplLimiteTarjeta.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lbImplLimiteTarjeta.setLabelFor(txtImpLimiteTarjeta);
        lbImplLimiteTarjeta.setText("Límite tarjeta");

        txtImpLimiteTarjeta.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtImpLimiteTarjeta.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtImpLimiteTarjeta.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtImpLimiteTarjetaFocusLost(evt);
            }
        });
        txtImpLimiteTarjeta.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtImpLimiteTarjetaKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtImpLimiteTarjetaKeyTyped(evt);
            }
        });

        lblTipoInteres.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblTipoInteres.setLabelFor(txtTipoInteres);
        lblTipoInteres.setText("% interes");

        txtTipoInteres.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtTipoInteres.setToolTipText("Tipo de interes de la cuenta");
        txtTipoInteres.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtTipoInteresKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtTipoInteresKeyTyped(evt);
            }
        });

        lblCuentaCorrientePago.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblCuentaCorrientePago.setLabelFor(cbxCuentaCorrientePago);
        lblCuentaCorrientePago.setText("Nº CC asociada");

        cbxCuentaCorrientePago.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        cbxCuentaCorrientePago.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbxCuentaCorrientePagoItemStateChanged(evt);
            }
        });
        cbxCuentaCorrientePago.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                cbxCuentaCorrientePagoPropertyChange(evt);
            }
        });

        lblFormaPago.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblFormaPago.setLabelFor(cbxFormaPago);
        lblFormaPago.setText("Forma de pago");

        cbxFormaPago.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        cbxFormaPago.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "CONTADO", "APLAZADO" }));
        cbxFormaPago.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbxFormaPagoItemStateChanged(evt);
            }
        });
        cbxFormaPago.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                cbxFormaPagoPropertyChange(evt);
            }
        });

        lblImpSaldoPendiente.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblImpSaldoPendiente.setLabelFor(lblTxtImpSaldoPendiente);
        lblImpSaldoPendiente.setText("Saldo pendiente");

        lblTxtImpSaldoPendiente.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        javax.swing.GroupLayout pnlDatosLayout = new javax.swing.GroupLayout(pnlDatos);
        pnlDatos.setLayout(pnlDatosLayout);
        pnlDatosLayout.setHorizontalGroup(
            pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDatosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlDatosLayout.createSequentialGroup()
                        .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblNumeroTarjeta, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtNumeroTarjeta, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblCuentaCorrientePago, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 118, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lblPAN, javax.swing.GroupLayout.DEFAULT_SIZE, 152, Short.MAX_VALUE)
                            .addComponent(lblFormaPago)
                            .addComponent(lblTxtPAN, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(pnlDatosLayout.createSequentialGroup()
                        .addComponent(cbxCuentaCorrientePago, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cbxFormaPago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(pnlDatosLayout.createSequentialGroup()
                        .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtImpLimiteTarjeta)
                            .addComponent(lblImpSaldoPendiente, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lbImplLimiteTarjeta, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtTipoInteres, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblTipoInteres)))
                    .addComponent(lblTxtImpSaldoPendiente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlDatosLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {lblTipoInteres, txtTipoInteres});

        pnlDatosLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cbxFormaPago, lblFormaPago});

        pnlDatosLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {lbImplLimiteTarjeta, txtImpLimiteTarjeta});

        pnlDatosLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {lblNumeroTarjeta, txtNumeroTarjeta});

        pnlDatosLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cbxCuentaCorrientePago, lblCuentaCorrientePago});

        pnlDatosLayout.setVerticalGroup(
            pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDatosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlDatosLayout.createSequentialGroup()
                        .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlDatosLayout.createSequentialGroup()
                                .addComponent(lbImplLimiteTarjeta)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtImpLimiteTarjeta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(lblTipoInteres)
                            .addGroup(pnlDatosLayout.createSequentialGroup()
                                .addGap(23, 23, 23)
                                .addComponent(txtTipoInteres, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblImpSaldoPendiente))
                    .addGroup(pnlDatosLayout.createSequentialGroup()
                        .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblNumeroTarjeta)
                            .addComponent(lblPAN))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblTxtPAN, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtNumeroTarjeta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblCuentaCorrientePago)
                            .addComponent(lblFormaPago))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(cbxCuentaCorrientePago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbxFormaPago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTxtImpSaldoPendiente))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlDatosLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {cbxCuentaCorrientePago, cbxFormaPago, lblTxtImpSaldoPendiente});

        pnlTabla.setName("pnlTabla"); // NOI18N
        pnlTabla.setPreferredSize(new java.awt.Dimension(465, 126));

        tblTarjetaCredito.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        tblTarjetaCredito.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tblTarjetaCredito.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblTarjetaCredito.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblTarjetaCreditoMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tblTarjetaCredito);

        javax.swing.GroupLayout pnlTablaLayout = new javax.swing.GroupLayout(pnlTabla);
        pnlTabla.setLayout(pnlTablaLayout);
        pnlTablaLayout.setHorizontalGroup(
            pnlTablaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTablaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2)
                .addContainerGap())
        );
        pnlTablaLayout.setVerticalGroup(
            pnlTablaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlTabla, javax.swing.GroupLayout.DEFAULT_SIZE, 608, Short.MAX_VALUE)
            .addComponent(pnlDatos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(pnlDatos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlTabla, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    protected void tblTarjetaCreditoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblTarjetaCreditoMouseClicked
        // "getSelectedRow()" nos devuelve el índice de la fila seleccionada.
        int fila = tblTarjetaCredito.getSelectedRow();
        if (fila != -1) {
            tarjetaCredito.setIdTarjetaCredito(Integer.valueOf(tblTarjetaCredito.getModel().getValueAt(fila, 0).toString()));
            // Recuperamos toda la información del objeto "TarjetaCredito" a través
            // de su ID.
            if (tcdao.buscarTarjetaCredito(tarjetaCredito.getIdTarjetaCredito())) {
                // Fijamos el objeto TarjetaCredito en el panel y lo enviamos
                // al formulario padre.
                setJPAObject(tcdao.getTarjetaCredito());
                sendJPAObjectToFrmParent();
            }
        }
        inicializarFormulario();        
    }//GEN-LAST:event_tblTarjetaCreditoMouseClicked

    private void txtNumeroTarjetaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNumeroTarjetaKeyReleased
        // Filtra la tabla por el número de cuenta introducido.
        mostrarTabla(txtNumeroTarjeta.getText());        
        // Llamada para intentar habilitar los botones CRUD.
        if (this.frmParent != null) {
            frmParent.tryEnableCRUDButtons();
        } 
    }//GEN-LAST:event_txtNumeroTarjetaKeyReleased

    private void txtNumeroTarjetaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNumeroTarjetaKeyTyped
        // Límite de 10 números
        if (txtNumeroTarjeta.getText().length() >= 10)
            evt.consume();
        else
            // Si no se ha llegado al límite de carácteres, comprobamos que
            // sólo sean números y el carácter "."
            Functions.checkNumber(evt);
    }//GEN-LAST:event_txtNumeroTarjetaKeyTyped

    private void txtTipoInteresKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTipoInteresKeyReleased
        // Llamada para intentar habilitar los botones CRUD.
        if (this.frmParent != null) {
            frmParent.tryEnableCRUDButtons();
        }
    }//GEN-LAST:event_txtTipoInteresKeyReleased

    private void txtTipoInteresKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTipoInteresKeyTyped
        // Límite de 5 números
        if (txtTipoInteres.getText().length() >= 5)
            evt.consume();
        else
            // Si no se ha llegado al límite de carácteres, comprobamos que
            // sólo sean números y el carácter "."
            Functions.checkNumber(evt);
    }//GEN-LAST:event_txtTipoInteresKeyTyped

    private void txtImpLimiteTarjetaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtImpLimiteTarjetaKeyReleased
        // Llamada para intentar habilitar los botones CRUD.
        if (this.frmParent != null) {
            frmParent.tryEnableCRUDButtons();
        }
    }//GEN-LAST:event_txtImpLimiteTarjetaKeyReleased

    private void txtImpLimiteTarjetaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtImpLimiteTarjetaKeyTyped
        // Límite de 15 números
        if (txtTipoInteres.getText().length() >= 15)
            evt.consume();
        else
            // Si no se ha llegado al límite de carácteres, comprobamos que
            // sólo sean números y el carácter "."
            Functions.checkNumber(evt);
    }//GEN-LAST:event_txtImpLimiteTarjetaKeyTyped

    private void cbxCuentaCorrientePagoPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_cbxCuentaCorrientePagoPropertyChange
        // Llamada para intentar habilitar los botones CRUD.
        if (this.frmParent != null) {
            frmParent.tryEnableCRUDButtons();
        }
    }//GEN-LAST:event_cbxCuentaCorrientePagoPropertyChange

    private void cbxFormaPagoPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_cbxFormaPagoPropertyChange
        // Llamada para intentar habilitar los botones CRUD.
        if (this.frmParent != null) {
            frmParent.tryEnableCRUDButtons();
        }
    }//GEN-LAST:event_cbxFormaPagoPropertyChange

    private void cbxCuentaCorrientePagoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbxCuentaCorrientePagoItemStateChanged
        // Llamada para intentar habilitar los botones CRUD.
        if (this.frmParent != null) {
            frmParent.tryEnableCRUDButtons();
        }
    }//GEN-LAST:event_cbxCuentaCorrientePagoItemStateChanged

    private void cbxFormaPagoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbxFormaPagoItemStateChanged
        // Llamada para intentar habilitar los botones CRUD.
        if (this.frmParent != null) {
            frmParent.tryEnableCRUDButtons();
        }
    }//GEN-LAST:event_cbxFormaPagoItemStateChanged

    private void txtImpLimiteTarjetaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtImpLimiteTarjetaFocusLost
        if ((!txtImpLimiteTarjeta.getText().equals("")) && 
                (Functions.getNumberFromFormattedNumber(txtImpLimiteTarjeta.getText()).doubleValue() >= 0.0)) {
            txtImpLimiteTarjeta.setText("");
            if (this.frmParent != null) {
                frmParent.tryEnableCRUDButtons();
            }         
            JOptionPane.showMessageDialog(this, "El importe debe ser negativo", "Límite tarjeta", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_txtImpLimiteTarjetaFocusLost

    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected javax.swing.JComboBox<Item<CuentaCorriente>> cbxCuentaCorrientePago;
    protected javax.swing.JComboBox<String> cbxFormaPago;
    private javax.swing.JScrollPane jScrollPane2;
    protected javax.swing.JLabel lbImplLimiteTarjeta;
    protected javax.swing.JLabel lblCuentaCorrientePago;
    protected javax.swing.JLabel lblFormaPago;
    protected javax.swing.JLabel lblImpSaldoPendiente;
    protected javax.swing.JLabel lblNumeroTarjeta;
    protected javax.swing.JLabel lblPAN;
    protected javax.swing.JLabel lblTipoInteres;
    protected javax.swing.JLabel lblTxtImpSaldoPendiente;
    protected javax.swing.JLabel lblTxtPAN;
    protected javax.swing.JPanel pnlDatos;
    protected javax.swing.JPanel pnlTabla;
    protected javax.swing.JTable tblTarjetaCredito;
    protected javax.swing.JFormattedTextField txtImpLimiteTarjeta;
    protected javax.swing.JTextField txtNumeroTarjeta;
    protected javax.swing.JTextField txtTipoInteres;
    // End of variables declaration//GEN-END:variables
}
