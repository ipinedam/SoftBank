package view;

//<editor-fold defaultstate="collapsed" desc=" Librerías importadas... ">
import java.awt.Component;
import java.io.IOException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManagerFactory;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import model.DAO.PrestamoDAO;

import model.entity.Cliente;
import model.entity.CuentaCorriente;
import model.entity.ProductoBancario;
import model.entity.Prestamo;

import pdf.ItxAmortization;
import pdf.ItxAmortizationParams;

import utilities.Functions;
import utilities.Item;
//</editor-fold>

/**
 * Panel de datos para la clase {@link Prestamo}, derivado de la clase
 * {@link pnlAppSoftBank}.
 *
 * @author Ignacio Pineda Martín
 */
public class pnlPrestamo extends pnlAppSoftBank {
    
    protected PrestamoDAO pdao;
    protected Prestamo prestamo = new Prestamo();

    /**
     * Creates new form pnlPrestamo
     */
    public pnlPrestamo() {
        initComponents();
    }
    
    public pnlPrestamo(EntityManagerFactory emf) {
        this();
        this.jpaObject = prestamo;
        this.emf = emf;
        // Creamos el DAO con su EntityManagerFactory.
        pdao = new PrestamoDAO(emf);
        // Preparamos el panel en su estado inicial.
        limpiarPanel();
        // Hacemos que la tabla no sea editable.
        tblPrestamo.setDefaultEditor(Object.class, null);        
    }
    
    public void setJPAObject(Prestamo jpaObject) {
        this.prestamo = jpaObject;
        rellenarCampos();
    }
    
    public Prestamo getJPAObject() {
        recogerCampos();
        return this.prestamo;
    }
    
    @Override
    public void sendJPAObjectToFrmParent() {
        if (this.frmParent != null) {
            frmParent.setJPAObject(this.prestamo);            
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
        btnCuadroAmortizacion.setEnabled(false);
        prestamo.setIdPrestamo(null);
        txtNumeroPrestamo.setText("");
        txtImpConcedido.setText("");
        spnPlazoPrestamo.setValue(((SpinnerNumberModel) spnPlazoPrestamo.getModel()).getMinimum());
        txtFecVencimiento.setText("");
        txtTipoInteres.setText("");
        cbxCuentaCorrientePago.removeAllItems();
        cbxTipoGarantia.setSelectedIndex(-1);
        lblTxtImpSaldoPendiente.setText("");
    }
    
    /**
     * Método para rellenar los campos del formulario con la propiedad
     * {@link prestamo}
     */
    protected void rellenarCampos() {
        txtNumeroPrestamo.setText(String.format("%10d", prestamo.getNumeroPrestamo()));
        txtImpConcedido.setText(Functions.formatNumber(prestamo.getImpConcedido()));
        // Calculamos los meses del préstamo.
        long meses = ChronoUnit.MONTHS.between(Functions.getLocalDateFromDate(prestamo.getProductoBancario().getFecApertura()),
                Functions.getLocalDateFromDate(prestamo.getFecVencimiento()));
        spnPlazoPrestamo.setValue(meses);
        txtFecVencimiento.setValue(prestamo.getFecVencimiento());
        txtTipoInteres.setText(Functions.formatNumber(prestamo.getTipoInteres()));
        // Cargamos en el ComboBox de cuentas todas las cuentas corrientes de 
        // los clientes que intervienen en el préstamo.
        rellenarCbxCuentaCorrientePago(prestamo.getProductoBancario().getClienteList());
        cbxCuentaCorrientePago.setSelectedItem(new Item<CuentaCorriente>(prestamo.getCuentaCorrientePago(), String.format("%10d", prestamo.getCuentaCorrientePago().getNumeroCuenta())));
        cbxTipoGarantia.setSelectedItem(prestamo.getTipoGarantia());
        lblTxtImpSaldoPendiente.setText(Functions.formatAmount(prestamo.getImpSaldoPendiente()));
        // Habilitamos botón de cuadro de amortización.
        btnCuadroAmortizacion.setEnabled(true);
    }
 
    /**
     * Método para rellenar el comboBox de cuenta corriente de pago con todas
     * las cuentas corrientes de la lista de clientes proporcionada por
     * parámetro.
     *
     * @param lstCliente La lista de clientes cuyas cuentas queremos añadir.
     */
    protected void rellenarCbxCuentaCorrientePago(List<Cliente> lstCliente) {
        cbxCuentaCorrientePago.removeAllItems();
        // Recorremos la lista de clientes.
        for (Cliente c : lstCliente) {
            rellenarCbxCuentaCorrientePago(c);
        }
    }
    
    /**
     * Método para rellenar el comboBox de cuenta corriente de pago con todas
     * las cuentas corrientes del cliente proporcionado por parámetro.
     *
     * @param c El cliente cuyas cuentas queremos añadir.
     */
    protected void rellenarCbxCuentaCorrientePago(Cliente c) {
        for (ProductoBancario pb : c.getProductoBancarioList()) {
            // Añadimos las cuentas corrientes activas.
            if ((pb.getFecCancelacion() == null) && (pb.getCuentaCorriente() != null)) {
                Item item = new Item<CuentaCorriente>(pb.getCuentaCorriente(), String.format("%10d", pb.getCuentaCorriente().getNumeroCuenta()));
                // Comprobamos que la cuenta no esté ya en la lista antes de añadirla.
                if (((DefaultComboBoxModel) cbxCuentaCorrientePago.getModel()).getIndexOf(item) < 0) {
                    cbxCuentaCorrientePago.addItem(item);
                }
            }
        }
    }
    
    /**
     * Método para recoger los campos del formulario en la propiedad
     * {@link prestamo}
     */    
    protected void recogerCampos() {
        Item cc = (Item) cbxCuentaCorrientePago.getSelectedItem();
        prestamo.setCuentaCorrientePago((CuentaCorriente) cc.getValue());
        prestamo.setNumeroPrestamo(Long.parseLong(txtNumeroPrestamo.getText()));
        prestamo.setTipoGarantia(cbxTipoGarantia.getSelectedItem().toString());
        prestamo.setTipoInteres(Functions.getBigDecimalFromFormattedNumber(txtTipoInteres.getText()));
        prestamo.setFecVencimiento((Date) txtFecVencimiento.getValue());
        prestamo.setImpConcedido(Functions.getBigDecimalFromFormattedNumber(txtImpConcedido.getText()));
        prestamo.setImpSaldoPendiente(Functions.getBigDecimalFromFormattedNumber(lblTxtImpSaldoPendiente.getText()));
    }

    /**
     * Método para preparar el formulario en un estado inicial.
     */
    protected void inicializarFormulario() {
        txtNumeroPrestamo.requestFocus();
        // Llamada para intentar habilitar los botones CRUD.
        if (this.frmParent != null) {
            frmParent.tryEnableCRUDButtons();
        }
        // Llamada para intentar habilitar el botón de cuadro de amortización
        tryEnableAmortizationButton();
    }

    /**
     * Método para rellenar la tabla del panel.
     * 
     * @param numeroPrestamo El número del Préstamo que mostraremos
     * en la tabla (si el valor es "" se mostrarán todas).
     */
    protected void mostrarTabla(String numeroPrestamo) {
        pdao.listarPrestamoNumero(tblPrestamo, numeroPrestamo);
    }    
    
    @Override
    protected boolean checkRequiredFields() {
        return !txtNumeroPrestamo.getText().equals("") && 
               !txtTipoInteres.getText().equals("") &&
               !txtImpConcedido.getText().equals("") &&
               !(Functions.getNumberFromFormattedNumber(txtImpConcedido.getText()).doubleValue() >= 0.0) && 
               !txtFecVencimiento.getText().equals("") &&
               (cbxTipoGarantia.getSelectedIndex() != -1) &&  
               (cbxCuentaCorrientePago.getSelectedIndex() != -1);
    }
    
    protected void tryEnableAmortizationButton() {
        btnCuadroAmortizacion.setEnabled(false);
        if ((frmParent != null) && (!txtImpConcedido.getText().equals(""))
                && (!spnPlazoPrestamo.getValue().equals(0))
                && (!txtTipoInteres.getText().equals(""))) {
            btnCuadroAmortizacion.setEnabled(true);
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

        pnlDatos = new javax.swing.JPanel();
        lblNumeroPrestamo = new javax.swing.JLabel();
        txtNumeroPrestamo = new javax.swing.JTextField();
        lbImpConcedido = new javax.swing.JLabel();
        txtImpConcedido = new javax.swing.JFormattedTextField();
        lbPlazoPrestamo = new javax.swing.JLabel();
        spnPlazoPrestamo = new javax.swing.JSpinner();
        lblFecVencimiento = new javax.swing.JLabel();
        txtFecVencimiento = new javax.swing.JFormattedTextField();
        lblTipoInteres = new javax.swing.JLabel();
        txtTipoInteres = new javax.swing.JTextField();
        lblCuentaCorrientePago = new javax.swing.JLabel();
        cbxCuentaCorrientePago = new javax.swing.JComboBox<Item<CuentaCorriente>>();
        lblTipoGarantia = new javax.swing.JLabel();
        cbxTipoGarantia = new javax.swing.JComboBox<>();
        lblImpSaldoPendiente = new javax.swing.JLabel();
        lblTxtImpSaldoPendiente = new javax.swing.JLabel();
        tlbAccesorios = new javax.swing.JToolBar();
        tlbAccesorios.setFloatable(false);
        btnCuadroAmortizacion = new javax.swing.JButton();
        pnlTabla = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblPrestamo = new javax.swing.JTable() {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component component = super.prepareRenderer(renderer, row, column);
                int rendererWidth = component.getPreferredSize().width;
                TableColumn tableColumn = getColumnModel().getColumn(column);
                tableColumn.setPreferredWidth(Math.max(rendererWidth + getIntercellSpacing().width, tableColumn.getPreferredWidth()));
                return component;
            }
        };

        setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Préstamo", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14))); // NOI18N

        pnlDatos.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        pnlDatos.setName("pnlDatos"); // NOI18N

        lblNumeroPrestamo.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblNumeroPrestamo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/search 16px.png"))); // NOI18N
        lblNumeroPrestamo.setLabelFor(txtNumeroPrestamo);
        lblNumeroPrestamo.setText("Nº préstamo");
        lblNumeroPrestamo.setToolTipText("Campo de búsqueda");
        lblNumeroPrestamo.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        lblNumeroPrestamo.setName("lblBusqueda"); // NOI18N

        txtNumeroPrestamo.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtNumeroPrestamo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtNumeroPrestamoKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtNumeroPrestamoKeyTyped(evt);
            }
        });

        lbImpConcedido.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lbImpConcedido.setLabelFor(txtImpConcedido);
        lbImpConcedido.setText("Importe préstamo");

        txtImpConcedido.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtImpConcedido.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtImpConcedido.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtImpConcedidoFocusLost(evt);
            }
        });
        txtImpConcedido.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtImpConcedidoKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtImpConcedidoKeyTyped(evt);
            }
        });

        lbPlazoPrestamo.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lbPlazoPrestamo.setLabelFor(spnPlazoPrestamo);
        lbPlazoPrestamo.setText("Meses");

        spnPlazoPrestamo.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        spnPlazoPrestamo.setModel(new javax.swing.SpinnerNumberModel(Long.valueOf(1L), Long.valueOf(1L), Long.valueOf(360L), Long.valueOf(1L)));
        spnPlazoPrestamo.setToolTipText("Duración préstamo (máximo 360 meses)");
        spnPlazoPrestamo.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnPlazoPrestamoStateChanged(evt);
            }
        });

        lblFecVencimiento.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblFecVencimiento.setText("Vencimiento");

        txtFecVencimiento.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter(new java.text.SimpleDateFormat("dd/MM/yyyy"))));
        txtFecVencimiento.setToolTipText("Fecha de vencimiento del préstamo");
        txtFecVencimiento.setFocusable(false);
        txtFecVencimiento.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtFecVencimiento.setName("txtFecVencimiento"); // NOI18N

        lblTipoInteres.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblTipoInteres.setLabelFor(txtTipoInteres);
        lblTipoInteres.setText("% interes");

        txtTipoInteres.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtTipoInteres.setToolTipText("Tipo de interes de el préstamo");
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

        lblTipoGarantia.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblTipoGarantia.setLabelFor(cbxTipoGarantia);
        lblTipoGarantia.setText("Tipo de garantía");

        cbxTipoGarantia.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        cbxTipoGarantia.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "PERSONAL", "HIPOTECARIA" }));
        cbxTipoGarantia.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbxTipoGarantiaItemStateChanged(evt);
            }
        });
        cbxTipoGarantia.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                cbxTipoGarantiaPropertyChange(evt);
            }
        });

        lblImpSaldoPendiente.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblImpSaldoPendiente.setLabelFor(lblTxtImpSaldoPendiente);
        lblImpSaldoPendiente.setText("Saldo pendiente");

        lblTxtImpSaldoPendiente.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        tlbAccesorios.setRollover(true);
        tlbAccesorios.setName("tlbAccesorios"); // NOI18N

        btnCuadroAmortizacion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/download-pdf 16px.png"))); // NOI18N
        btnCuadroAmortizacion.setToolTipText("Generar cuadro amortización PDF");
        btnCuadroAmortizacion.setFocusable(false);
        btnCuadroAmortizacion.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnCuadroAmortizacion.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnCuadroAmortizacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCuadroAmortizacionActionPerformed(evt);
            }
        });
        tlbAccesorios.add(btnCuadroAmortizacion);

        javax.swing.GroupLayout pnlDatosLayout = new javax.swing.GroupLayout(pnlDatos);
        pnlDatos.setLayout(pnlDatosLayout);
        pnlDatosLayout.setHorizontalGroup(
            pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlDatosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlDatosLayout.createSequentialGroup()
                        .addComponent(cbxCuentaCorrientePago, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cbxTipoGarantia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblTipoGarantia)))
                    .addGroup(pnlDatosLayout.createSequentialGroup()
                        .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblNumeroPrestamo, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtNumeroPrestamo, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblCuentaCorrientePago, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 118, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtImpConcedido)
                            .addComponent(lbImpConcedido, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlDatosLayout.createSequentialGroup()
                        .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lbPlazoPrestamo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(spnPlazoPrestamo, javax.swing.GroupLayout.PREFERRED_SIZE, 68, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtFecVencimiento, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblFecVencimiento))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtTipoInteres, javax.swing.GroupLayout.DEFAULT_SIZE, 68, Short.MAX_VALUE)
                            .addComponent(lblTipoInteres, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(lblTxtImpSaldoPendiente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblImpSaldoPendiente, javax.swing.GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
                .addComponent(tlbAccesorios, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pnlDatosLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {lblTipoInteres, txtTipoInteres});

        pnlDatosLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cbxTipoGarantia, lblTipoGarantia});

        pnlDatosLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {lbImpConcedido, txtImpConcedido});

        pnlDatosLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {lblNumeroPrestamo, txtNumeroPrestamo});

        pnlDatosLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cbxCuentaCorrientePago, lblCuentaCorrientePago});

        pnlDatosLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {lbPlazoPrestamo, spnPlazoPrestamo});

        pnlDatosLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {lblFecVencimiento, txtFecVencimiento});

        pnlDatosLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {lblImpSaldoPendiente, lblTxtImpSaldoPendiente});

        pnlDatosLayout.setVerticalGroup(
            pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDatosLayout.createSequentialGroup()
                .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tlbAccesorios, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlDatosLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlDatosLayout.createSequentialGroup()
                                .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblNumeroPrestamo)
                                    .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(lbImpConcedido)
                                        .addComponent(lbPlazoPrestamo)
                                        .addComponent(lblFecVencimiento)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtNumeroPrestamo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtImpConcedido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(spnPlazoPrestamo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtFecVencimiento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(pnlDatosLayout.createSequentialGroup()
                                .addGap(23, 23, 23)
                                .addComponent(txtTipoInteres, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(lblTipoInteres))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlDatosLayout.createSequentialGroup()
                                .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblImpSaldoPendiente)
                                    .addComponent(lblCuentaCorrientePago))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                    .addComponent(cbxCuentaCorrientePago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblTxtImpSaldoPendiente)))
                            .addGroup(pnlDatosLayout.createSequentialGroup()
                                .addComponent(lblTipoGarantia)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbxTipoGarantia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlDatosLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {cbxCuentaCorrientePago, cbxTipoGarantia, lblTxtImpSaldoPendiente});

        pnlDatosLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {spnPlazoPrestamo, txtFecVencimiento, txtImpConcedido, txtNumeroPrestamo, txtTipoInteres});

        pnlTabla.setName("pnlTabla"); // NOI18N
        pnlTabla.setPreferredSize(new java.awt.Dimension(465, 126));

        tblPrestamo.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        tblPrestamo.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tblPrestamo.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblPrestamo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblPrestamoMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tblPrestamo);

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

    protected void tblPrestamoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblPrestamoMouseClicked
        // "getSelectedRow()" nos devuelve el índice de la fila seleccionada.
        int fila = tblPrestamo.getSelectedRow();
        if (fila != -1) {
            prestamo.setIdPrestamo(Integer.valueOf(tblPrestamo.getModel().getValueAt(fila, 0).toString()));
            // Recuperamos toda la información del objeto "Prestamo" a través
            // de su ID.
            if (pdao.buscarPrestamo(prestamo.getIdPrestamo())) {
                // Fijamos el objeto Prestamo en el panel y lo enviamos
                // al formulario padre.
                setJPAObject(pdao.getPrestamo());
                sendJPAObjectToFrmParent();
            }
        }
        inicializarFormulario();        
    }//GEN-LAST:event_tblPrestamoMouseClicked

    private void txtNumeroPrestamoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNumeroPrestamoKeyReleased
        // Filtra la tabla por el número de cuenta introducido.
        mostrarTabla(txtNumeroPrestamo.getText());        
        // Llamada para intentar habilitar los botones CRUD.
        if (this.frmParent != null) {
            frmParent.tryEnableCRUDButtons();
        }
        // Llamada para intentar habilitar el botón de cuadro de amortización
        tryEnableAmortizationButton();
    }//GEN-LAST:event_txtNumeroPrestamoKeyReleased

    private void txtNumeroPrestamoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNumeroPrestamoKeyTyped
        // Límite de 10 números
        if (txtNumeroPrestamo.getText().length() >= 10)
            evt.consume();
        else
            // Si no se ha llegado al límite de carácteres, comprobamos que
            // sólo sean números y el carácter "."
            Functions.checkNumber(evt);
    }//GEN-LAST:event_txtNumeroPrestamoKeyTyped

    private void txtTipoInteresKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTipoInteresKeyReleased
        // Llamada para intentar habilitar los botones CRUD.
        if (this.frmParent != null) {
            frmParent.tryEnableCRUDButtons();
        }
        // Llamada para intentar habilitar el botón de cuadro de amortización
        tryEnableAmortizationButton();
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

    private void txtImpConcedidoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtImpConcedidoKeyReleased
        // Llamada para intentar habilitar los botones CRUD.
        if (this.frmParent != null) {
            frmParent.tryEnableCRUDButtons();
        }
        // Llamada para intentar habilitar el botón de cuadro de amortización
        tryEnableAmortizationButton();
    }//GEN-LAST:event_txtImpConcedidoKeyReleased

    private void txtImpConcedidoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtImpConcedidoKeyTyped
        // Límite de 15 números
        if (txtTipoInteres.getText().length() >= 15)
            evt.consume();
        else
            // Si no se ha llegado al límite de carácteres, comprobamos que
            // sólo sean números y el carácter "."
            Functions.checkNumber(evt);
    }//GEN-LAST:event_txtImpConcedidoKeyTyped

    private void cbxCuentaCorrientePagoPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_cbxCuentaCorrientePagoPropertyChange
        // Llamada para intentar habilitar los botones CRUD.
        if (this.frmParent != null) {
            frmParent.tryEnableCRUDButtons();
        }
        // Llamada para intentar habilitar el botón de cuadro de amortización
        tryEnableAmortizationButton();
    }//GEN-LAST:event_cbxCuentaCorrientePagoPropertyChange

    private void cbxTipoGarantiaPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_cbxTipoGarantiaPropertyChange
        // Llamada para intentar habilitar los botones CRUD.
        if (this.frmParent != null) {
            frmParent.tryEnableCRUDButtons();
        }
        // Llamada para intentar habilitar el botón de cuadro de amortización
        tryEnableAmortizationButton();        
    }//GEN-LAST:event_cbxTipoGarantiaPropertyChange

    private void btnCuadroAmortizacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCuadroAmortizacionActionPerformed
        // Generación del Cuadro de Amortización.
        try {
            ItxAmortizationParams iap = new ItxAmortizationParams();
            // Proporcionamos la información necesaria para generar el cuadro de
            // amortización.
            iap.setImpPrestamo(Functions.getBigDecimalFromFormattedNumber(txtImpConcedido.getText()).doubleValue() * -1);
            iap.setDuracionMeses((long) spnPlazoPrestamo.getValue());
            iap.setTipoInteres(Functions.getBigDecimalFromFormattedNumber(txtTipoInteres.getText()).doubleValue());
            // Llamamos al generador del cuadro.
            ItxAmortization ita = new ItxAmortization(iap);
        } catch (IOException E) {
            System.out.println(E.getMessage());
        }
    }//GEN-LAST:event_btnCuadroAmortizacionActionPerformed

    private void spnPlazoPrestamoStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnPlazoPrestamoStateChanged
        // Comprobamos en primer lugar si podemos acceder a la propiedad
        // "fecha de apertura" del préstamo.
        if (((frmPrestamo) frmParent).pnlProductoBancario.jdcFecApertura.getDate() != null) {
            // Para el cálculo de fechas, es mucho más versatil el tipo de 
            // Java "LocalDate". 
            LocalDate fecVencimiento = Functions.getLocalDateFromDate(((frmPrestamo) frmParent).pnlProductoBancario.jdcFecApertura.getDate());
            // Cálculo de fecha de vencimiento como suma de fecha de apertura más
            // número de meses indicados en "spnPlazoPrestamo".
            fecVencimiento = fecVencimiento.plusMonths((Long) spnPlazoPrestamo.getValue());
            // Convertimos la fecha calculada en tipo Java "Date" y la 
            // asignamos al campo de fecha de vencimiento.
            txtFecVencimiento.setValue(Functions.getDateFromLocalDate(fecVencimiento));
        }
        // Llamada para intentar habilitar los botones CRUD.
        if (this.frmParent != null) {
            frmParent.tryEnableCRUDButtons();
        }
        // Llamada para intentar habilitar el botón de cuadro de amortización
        tryEnableAmortizationButton();
    }//GEN-LAST:event_spnPlazoPrestamoStateChanged

    private void txtImpConcedidoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtImpConcedidoFocusLost
        if ((!txtImpConcedido.getText().equals("")) && 
                (Functions.getNumberFromFormattedNumber(txtImpConcedido.getText()).doubleValue() >= 0.0)) {
            txtImpConcedido.setText("");
            if (this.frmParent != null) {
                frmParent.tryEnableCRUDButtons();
            }
            // Llamada para intentar habilitar el botón de cuadro de amortización
            tryEnableAmortizationButton();
            JOptionPane.showMessageDialog(this, "El importe debe ser negativo", "Importe préstamo", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_txtImpConcedidoFocusLost

    private void cbxTipoGarantiaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbxTipoGarantiaItemStateChanged
        // Llamada para intentar habilitar los botones CRUD.
        if (this.frmParent != null) {
            frmParent.tryEnableCRUDButtons();
        }
        // Llamada para intentar habilitar el botón de cuadro de amortización
        tryEnableAmortizationButton();
    }//GEN-LAST:event_cbxTipoGarantiaItemStateChanged

    private void cbxCuentaCorrientePagoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbxCuentaCorrientePagoItemStateChanged
        // Llamada para intentar habilitar los botones CRUD.
        if (this.frmParent != null) {
            frmParent.tryEnableCRUDButtons();
        }
        // Llamada para intentar habilitar el botón de cuadro de amortización
        tryEnableAmortizationButton();
    }//GEN-LAST:event_cbxCuentaCorrientePagoItemStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected javax.swing.JButton btnCuadroAmortizacion;
    protected javax.swing.JComboBox<Item<CuentaCorriente>> cbxCuentaCorrientePago;
    protected javax.swing.JComboBox<String> cbxTipoGarantia;
    private javax.swing.JScrollPane jScrollPane2;
    protected javax.swing.JLabel lbImpConcedido;
    protected javax.swing.JLabel lbPlazoPrestamo;
    protected javax.swing.JLabel lblCuentaCorrientePago;
    private javax.swing.JLabel lblFecVencimiento;
    protected javax.swing.JLabel lblImpSaldoPendiente;
    protected javax.swing.JLabel lblNumeroPrestamo;
    protected javax.swing.JLabel lblTipoGarantia;
    protected javax.swing.JLabel lblTipoInteres;
    protected javax.swing.JLabel lblTxtImpSaldoPendiente;
    protected javax.swing.JPanel pnlDatos;
    protected javax.swing.JPanel pnlTabla;
    protected javax.swing.JSpinner spnPlazoPrestamo;
    protected javax.swing.JTable tblPrestamo;
    protected javax.swing.JToolBar tlbAccesorios;
    protected javax.swing.JFormattedTextField txtFecVencimiento;
    protected javax.swing.JFormattedTextField txtImpConcedido;
    protected javax.swing.JTextField txtNumeroPrestamo;
    protected javax.swing.JTextField txtTipoInteres;
    // End of variables declaration//GEN-END:variables
}
