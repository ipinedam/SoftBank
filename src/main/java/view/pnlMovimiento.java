package view;

//<editor-fold defaultstate="collapsed" desc=" Librerías importadas... ">
import java.awt.Component;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import javax.persistence.EntityManagerFactory;
import javax.swing.JOptionPane;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import model.DAO.MovimientoDAO;
import model.entity.Movimiento;
import model.entity.ProductoBancario;
import pdf.ItxDocument;
import pdf.ItxDocumentParams;

import utilities.Constants.FormAction;
import utilities.Constants.PdfType;
import utilities.Functions;
//</editor-fold>

/**
 * Panel de datos para la clase {@link ProductoBancario}, derivado de la clase
 * {@link pnlAppSoftBank}.
 *
 * @author Ignacio Pineda Martín
 */
public class pnlMovimiento extends pnlAppSoftBank {
    
    protected ProductoBancario productoBancario = new ProductoBancario();
    protected MovimientoDAO mdao;
    protected Movimiento movimiento = new Movimiento();

    /**
     * Creates new form pnlProductoBancario
     */
    public pnlMovimiento() {
        initComponents();
    }
    
    public pnlMovimiento(EntityManagerFactory emf) {
        this();
        this.jpaObject = productoBancario;
        this.emf = emf;
        // Creamos el DAO con su EntityManagerFactory.
        mdao = new MovimientoDAO(emf);
        // Preparamos el panel en su estado inicial.
        limpiarPanel();
        // Hacemos que la tabla no sea editable.
        tblMovimiento.setDefaultEditor(Object.class, null);
    }
    
    @Override
    public void setJPAObject(Object jpaObject) {
        this.jpaObject = jpaObject;
        if (this.jpaObject instanceof ProductoBancario p) {
            this.productoBancario = p;
            iniciarPanel();
        }
        if (this.jpaObject instanceof Movimiento m) {
            this.movimiento = m;
            rellenarCampos();
        }
    }
    
    public ProductoBancario getJPAObject() {
        return this.productoBancario;
    }

    /**
     * Método para limpiar el panel al estado inicial. Necesario para ser
     * invocado desde el botón "Limpiar" del formulario contenedor.
     */
    protected void limpiarPanel() {
        vaciarCampos();
        mostrarTabla("");
        inicializarFormulario();
    }

    private void iniciarPanel() {
        if (this.productoBancario != null) {
            btnExtracto.setEnabled(true);
            jdcFecMovimiento.setEnabled(true);
            txtImpMovimiento.setEnabled(true);
            if (frmParent.getFormAction() == FormAction.QUERY) {
                jdcFecMovimiento.getCalendarButton().setEnabled(false);
                jdcFecMovimiento.setFocusable(false);
                txtImpMovimiento.setFocusable(false);
            } else {
                jdcFecMovimiento.getCalendarButton().setEnabled(true);                
                jdcFecMovimiento.setFocusable(true);
                txtImpMovimiento.setFocusable(true);
            }
            txtConcepto.setEnabled(true);
        }
        limpiarCampos();
        mostrarTabla("");
        inicializarFormulario();
    }    
    
    /**
     * Método para inicializar los campos del formulario y los objetos
     * asociados.
     */
    protected void vaciarCampos() {
        this.productoBancario = null;
        btnExtracto.setEnabled(false);
        jdcFecMovimiento.setEnabled(false);
        txtImpMovimiento.setEnabled(false);
        txtConcepto.setEnabled(false);
        limpiarCampos();
    }
    
    /**
     * Método para rellenar los campos del formulario con la propiedad
     * {@link Movimiento}
     */
    protected void rellenarCampos() {
        jdcFecMovimiento.setDate(movimiento.getFecMovimiento());
        txtConcepto.setText(movimiento.getConcepto());
        txtImpMovimiento.setValue(movimiento.getImpMovimiento());
    }
 
    /**
     * Método para recoger los campos del formulario en la propiedad
     * {@link movimiento}
     */    
    protected void recogerCampos() {
        movimiento.setFecMovimiento(jdcFecMovimiento.getDate());
        movimiento.setConcepto(txtConcepto.getText());
        movimiento.setImpMovimiento(Functions.getBigDecimalFromFormattedNumber(txtImpMovimiento.getText()));
    }

    /**
     * Método para preparar el formulario en un estado inicial.
     */
    protected void inicializarFormulario() {
        jdcFecMovimiento.requestFocus();
        // Llamada para intentar habilitar los botones CRUD.
        if (this.frmParent != null) {
            frmParent.tryEnableCRUDButtons();
        }        
    }

    /**
     * Método para rellenar la tabla del panel.
     *
     * @param concepto El concepto del movimiento que mostraremos en la tabla
     * (si el valor es "" se mostrarán todos).
     */
    protected void mostrarTabla(String concepto) {
        mdao.listarMovimientoConcepto(tblMovimiento, productoBancario, jdcFecInicio.getDate(), jdcFecFin.getDate(), concepto);
    }
    
    @Override
    protected boolean checkRequiredFields() {
        if (frmParent.getFormAction() != null) {
            switch (frmParent.getFormAction()) {
                case NEW:
                    return true;

                case CANCEL:
                    return true;

                case MODIFY:
                    return true;

                case MOVEMENT:
                    return (jdcFecMovimiento.getDate() != null)
                            && (jdcFecMovimiento.getDate().compareTo(jdcFecMovimiento.getMinSelectableDate()) >= 0) // mayor o igual que fecha mínima.
                            && (jdcFecMovimiento.getDate().compareTo(jdcFecMovimiento.getMaxSelectableDate()) <= 0) // menor o igual que fecha máxima.
                            && !txtImpMovimiento.getText().equals("")
                            && !txtConcepto.getText().equals("");

                case QUERY:
                    return true;

                default:
                    throw new AssertionError();
            }
        }
        return false;
    }
    
    private void deshabilitarBotones() {
        btnInsertar.setEnabled(false);
        btnModificar.setEnabled(false);
        btnEliminar.setEnabled(false);
    }
    
    private void habilitarBotones() {
        deshabilitarBotones();
        
        switch (frmParent.getFormAction()) {
            case NEW:
                break;

            case CANCEL:
                break;

            case MODIFY:
                break;

            case MOVEMENT:
                if (checkRequiredFields()) {
                    btnInsertar.setEnabled(true);
                    if (movimiento.getIdMovimiento() != null) {
                        btnModificar.setEnabled(true);
                    }
                }

            case QUERY:
                break;

            default:
                throw new AssertionError();
        }
    }
    
    private void limpiarCampos() {
        movimiento.setIdMovimiento(null);
        movimiento.setProductoBancario(productoBancario);

        limpiarPanelSeleccion();
        limpiarPanelDatos();
    }
    
    private void limpiarPanelSeleccion() {
        if (productoBancario != null) {
            jdcFecInicio.setDate(productoBancario.getFecApertura());
            jdcFecInicio.setMinSelectableDate(productoBancario.getFecApertura());
            jdcFecInicio.setMaxSelectableDate(Functions.getEndOfDay());
            jdcFecFin.setDate(Functions.getEndOfDay());
            jdcFecFin.setMinSelectableDate(productoBancario.getFecApertura());
            jdcFecFin.setMaxSelectableDate(Functions.getEndOfDay());
        } else {
            jdcFecInicio.setDate(null);
            jdcFecInicio.setMinSelectableDate(null);
            jdcFecInicio.setMaxSelectableDate(null);
            jdcFecFin.setDate(null);
            jdcFecFin.setMinSelectableDate(null);
            jdcFecFin.setMaxSelectableDate(null);
        }        
    }
    
    private void limpiarPanelDatos() {
        if (productoBancario != null) {
            jdcFecMovimiento.setDate(null);
            jdcFecMovimiento.setMinSelectableDate(productoBancario.getFecApertura());
            jdcFecMovimiento.setMaxSelectableDate(Functions.getEndOfDay());
        } else {
            jdcFecMovimiento.setDate(null);
            jdcFecMovimiento.setMinSelectableDate(null);
            jdcFecMovimiento.setMaxSelectableDate(null);
        }

        txtImpMovimiento.setText("");
        txtConcepto.setText("");

        deshabilitarBotones();
    }

    private boolean comprobarSaldo(ProductoBancario pb, BigDecimal impAnterior, BigDecimal impActual) {
        boolean resultado = false;
        BigDecimal impSaldoFinal = BigDecimal.ZERO;
        Movimiento saldoFinDia = mdao.saldoFinDia(pb, Functions.getEndOfDay());
        
        impSaldoFinal = impSaldoFinal.add(saldoFinDia.getImpMovimiento());
        impSaldoFinal = impSaldoFinal.subtract(impAnterior);
        impSaldoFinal = impSaldoFinal.add(impActual);
        // Comprobamos que la suma del movimiento más el saldo actual de la 
        // cuenta corriente es mayor o igual que 0
        if (frmParent instanceof frmCuentaCorriente) {
            if (impSaldoFinal.compareTo(BigDecimal.ZERO) >= 0) {
                resultado = true;
            }
        }
        // Comprobamos que la suma del movimiento más el saldo actual de la 
        // tarjeta es mayor que el saldo limite de la tarjeta
        if (frmParent instanceof frmTarjetaCredito) {
            if (impSaldoFinal.compareTo(pb.getTarjetaCredito().getImpLimiteTarjeta()) >= 0) {
                resultado = true;
            }
        }
        // Comprobamos que la suma del movimiento más el saldo actual de el
        // préstamo es mayor que el saldo limite de la tarjeta
        if (frmParent instanceof frmPrestamo) {
            if (impSaldoFinal.compareTo(pb.getPrestamo().getImpConcedido()) >= 0) {
                resultado = true;
            }
        }
        return resultado;
    }
    
    /**
     * Método para informar a los paneles cada vez que se inserta, modifica o
     * elimina un movimiento.
     */
    private void informarPaneles() {
        // Informamos al panel de Cuenta Corriente para que actualice
        // su saldo.
        if (frmParent instanceof frmCuentaCorriente fcc) {
            fcc.pnlCuentaCorriente.setJPAObject(this.movimiento.getProductoBancario().getCuentaCorriente());
        }
        // Informamos al panel de Tarjeta de Crédito para que actualice
        // su saldo.
        if (frmParent instanceof frmTarjetaCredito ftc) {
            ftc.pnlTarjetaCredito.setJPAObject(this.movimiento.getProductoBancario().getTarjetaCredito());
        }
        // Informamos al panel de Préstamo para que actualice su saldo.
        if (frmParent instanceof frmPrestamo fp) {
            fp.pnlPrestamo.setJPAObject(this.movimiento.getProductoBancario().getPrestamo());
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

        jTextField1 = new javax.swing.JTextField();
        pnlSeleccion = new javax.swing.JPanel();
        lblFecInicio = new javax.swing.JLabel();
        jdcFecInicio = new com.toedter.calendar.JDateChooser();
        lblFecFin = new javax.swing.JLabel();
        jdcFecFin = new com.toedter.calendar.JDateChooser();
        tlbSeleccion = new javax.swing.JToolBar();
        tlbSeleccion.setFloatable(false);
        btnExtracto = new javax.swing.JButton();
        btnRefrescar = new javax.swing.JButton();
        pnlDatos = new javax.swing.JPanel();
        lblFecMovimiento = new javax.swing.JLabel();
        jdcFecMovimiento = new com.toedter.calendar.JDateChooser();
        lblImpMovimiento = new javax.swing.JLabel();
        txtImpMovimiento = new javax.swing.JFormattedTextField();
        lblConcepto = new javax.swing.JLabel();
        txtConcepto = new javax.swing.JTextField();
        tlbAcciones = new javax.swing.JToolBar();
        tlbAcciones.setFloatable(false);
        btnInsertar = new javax.swing.JButton();
        btnModificar = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();
        btnLimpiar = new javax.swing.JButton();
        pnlTabla = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblMovimiento = new javax.swing.JTable() {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component component = super.prepareRenderer(renderer, row, column);
                int rendererWidth = component.getPreferredSize().width;
                TableColumn tableColumn = getColumnModel().getColumn(column);
                tableColumn.setPreferredWidth(Math.max(rendererWidth + getIntercellSpacing().width, tableColumn.getPreferredWidth()));
                return component;
            }
        };

        jTextField1.setText("jTextField1");

        setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Movimientos", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14))); // NOI18N

        pnlSeleccion.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        pnlSeleccion.setName("pnlDatos"); // NOI18N

        lblFecInicio.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblFecInicio.setText("Fecha inicio");

        jdcFecInicio.setDateFormatString("dd/MM/yyyy");
        jdcFecInicio.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jdcFecInicio.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jdcFecInicioPropertyChange(evt);
            }
        });

        lblFecFin.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblFecFin.setText("Fecha fin");

        jdcFecFin.setDateFormatString("dd/MM/yyyy");
        jdcFecFin.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jdcFecFin.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jdcFecFinPropertyChange(evt);
            }
        });

        tlbSeleccion.setRollover(true);
        tlbSeleccion.setName("tlbSeleccion"); // NOI18N

        btnExtracto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/download-pdf 16px.png"))); // NOI18N
        btnExtracto.setToolTipText("Generar extracto PDF");
        btnExtracto.setFocusable(false);
        btnExtracto.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnExtracto.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnExtracto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExtractoActionPerformed(evt);
            }
        });
        tlbSeleccion.add(btnExtracto);

        btnRefrescar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/refresh 16px.png"))); // NOI18N
        btnRefrescar.setToolTipText("Reiniciar selección de fechas");
        btnRefrescar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnRefrescar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnRefrescar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefrescarActionPerformed(evt);
            }
        });
        tlbSeleccion.add(btnRefrescar);

        javax.swing.GroupLayout pnlSeleccionLayout = new javax.swing.GroupLayout(pnlSeleccion);
        pnlSeleccion.setLayout(pnlSeleccionLayout);
        pnlSeleccionLayout.setHorizontalGroup(
            pnlSeleccionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSeleccionLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlSeleccionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblFecInicio, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jdcFecInicio, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlSeleccionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlSeleccionLayout.createSequentialGroup()
                        .addComponent(jdcFecFin, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(pnlSeleccionLayout.createSequentialGroup()
                        .addComponent(lblFecFin, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(tlbSeleccion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );
        pnlSeleccionLayout.setVerticalGroup(
            pnlSeleccionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSeleccionLayout.createSequentialGroup()
                .addGroup(pnlSeleccionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlSeleccionLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(pnlSeleccionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblFecInicio)
                            .addComponent(lblFecFin)))
                    .addComponent(tlbSeleccion, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8)
                .addGroup(pnlSeleccionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jdcFecInicio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jdcFecFin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlDatos.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        pnlDatos.setName("pnlDatos"); // NOI18N

        lblFecMovimiento.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblFecMovimiento.setText("Fecha movimiento");

        jdcFecMovimiento.setDateFormatString("dd/MM/yyyy");
        jdcFecMovimiento.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jdcFecMovimiento.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jdcFecMovimientoKeyReleased(evt);
            }
        });

        lblImpMovimiento.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblImpMovimiento.setText("Imp. movimiento");

        txtImpMovimiento.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtImpMovimiento.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtImpMovimiento.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtImpMovimientoKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtImpMovimientoKeyTyped(evt);
            }
        });

        lblConcepto.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblConcepto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/search 16px.png"))); // NOI18N
        lblConcepto.setText("Concepto");
        lblConcepto.setToolTipText("Campo de búsqueda");
        lblConcepto.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);

        txtConcepto.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtConcepto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtConceptoKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtConceptoKeyTyped(evt);
            }
        });

        tlbAcciones.setRollover(true);
        tlbAcciones.setName("tlbAcciones"); // NOI18N

        btnInsertar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/insert 16px.png"))); // NOI18N
        btnInsertar.setToolTipText("Insertar movimiento");
        btnInsertar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnInsertar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnInsertar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInsertarActionPerformed(evt);
            }
        });
        tlbAcciones.add(btnInsertar);

        btnModificar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/modify 16px.png"))); // NOI18N
        btnModificar.setToolTipText("Modificar movimiento");
        btnModificar.setFocusable(false);
        btnModificar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnModificar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnModificar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnModificarActionPerformed(evt);
            }
        });
        tlbAcciones.add(btnModificar);

        btnEliminar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/delete 16px.png"))); // NOI18N
        btnEliminar.setToolTipText("Eliminar movimiento");
        btnEliminar.setFocusable(false);
        btnEliminar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnEliminar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarActionPerformed(evt);
            }
        });
        tlbAcciones.add(btnEliminar);

        btnLimpiar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/clean 16px.png"))); // NOI18N
        btnLimpiar.setToolTipText("Limpiar panel");
        btnLimpiar.setFocusable(false);
        btnLimpiar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnLimpiar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnLimpiar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimpiarActionPerformed(evt);
            }
        });
        tlbAcciones.add(btnLimpiar);

        javax.swing.GroupLayout pnlDatosLayout = new javax.swing.GroupLayout(pnlDatos);
        pnlDatos.setLayout(pnlDatosLayout);
        pnlDatosLayout.setHorizontalGroup(
            pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDatosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlDatosLayout.createSequentialGroup()
                        .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jdcFecMovimiento, javax.swing.GroupLayout.DEFAULT_SIZE, 210, Short.MAX_VALUE)
                            .addComponent(lblFecMovimiento, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlDatosLayout.createSequentialGroup()
                                .addComponent(lblImpMovimiento, javax.swing.GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(tlbAcciones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pnlDatosLayout.createSequentialGroup()
                                .addComponent(txtImpMovimiento)
                                .addContainerGap())))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlDatosLayout.createSequentialGroup()
                        .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lblConcepto, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtConcepto))
                        .addContainerGap())))
        );
        pnlDatosLayout.setVerticalGroup(
            pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDatosLayout.createSequentialGroup()
                .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlDatosLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblFecMovimiento)
                            .addComponent(lblImpMovimiento)))
                    .addComponent(tlbAcciones, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jdcFecMovimiento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtImpMovimiento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblConcepto)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtConcepto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlTabla.setName("pnlTabla"); // NOI18N
        pnlTabla.setPreferredSize(new java.awt.Dimension(465, 126));

        tblMovimiento.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        tblMovimiento.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tblMovimiento.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblMovimiento.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblMovimientoMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tblMovimiento);

        javax.swing.GroupLayout pnlTablaLayout = new javax.swing.GroupLayout(pnlTabla);
        pnlTabla.setLayout(pnlTablaLayout);
        pnlTablaLayout.setHorizontalGroup(
            pnlTablaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTablaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlTablaLayout.setVerticalGroup(
            pnlTablaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 203, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlTabla, javax.swing.GroupLayout.DEFAULT_SIZE, 490, Short.MAX_VALUE)
            .addComponent(pnlDatos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(pnlSeleccion, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(pnlSeleccion, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlDatos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlTabla, javax.swing.GroupLayout.DEFAULT_SIZE, 203, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    protected void tblMovimientoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblMovimientoMouseClicked
        // "getSelectedRow()" nos devuelve el índice de la fila seleccionada.
        int fila = tblMovimiento.getSelectedRow();
        if (fila != -1) {
            Integer idMovimiento = Integer.valueOf(tblMovimiento.getModel().getValueAt(fila, 0).toString());
            // Recuperamos toda la información del objeto "Movimiento" a través
            // de su ID. Si el ID es -1 indica que es un movimiento "ficticio"
            // de saldo y no se mostrará en el panel de datos.
            if (idMovimiento.intValue() != -1) {
                if (mdao.buscarMovimiento(idMovimiento)) {
                    // Fijamos el objeto Movimiento en el panel.
                    setJPAObject(mdao.getMovimiento());
                    // Habilitamos los botones de modificación y eliminación.
                    if (frmParent.getFormAction() == FormAction.MOVEMENT) {
                        btnModificar.setEnabled(true);
                        btnEliminar.setEnabled(true);
                    }
                }
            }
        }
    }//GEN-LAST:event_tblMovimientoMouseClicked

    protected void btnInsertarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInsertarActionPerformed
        if (checkRequiredFields()) {
            recogerCampos();
            // Comprobamos que el nuevo movimiento cumple con las condiciones de
            // saldo de cuenta corriente o de tarjeta.
            if (!comprobarSaldo(productoBancario, BigDecimal.ZERO, movimiento.getImpMovimiento())) {
                // Preguntamos si queremos autorizar el movimiento.
                if (JOptionPane.showConfirmDialog(this, "Esta operación excede el saldo permitido. ¿Se autoriza este movimiento?", "Movimientos", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
                    return;
                }
            }
            // Creamos el movimiento.
            if (mdao.insertarMovimiento(movimiento)) {
                // Mostramos el mensaje resultante de la inserción.
                if (this.frmParent != null) {
                    String mensaje = String.format("Movimiento %s %s %s creado correctamente.",
                            Functions.formatDate(movimiento.getFecMovimiento()),
                            movimiento.getConcepto(),
                            Functions.formatAmount(movimiento.getImpMovimiento()));
                    ((frmAppSoftBank) frmParent).setStatusBarText(mensaje);
                    JOptionPane.showMessageDialog(this, mensaje, "Movimientos", JOptionPane.INFORMATION_MESSAGE);
                }
                // Informarmos a los paneles.
                informarPaneles();
                // Refrescamos el panel.
                iniciarPanel();
                // Desactivamos el botón "Insertar".
                btnInsertar.setEnabled(false);
            } else {
                // Error inesperado.
                if (this.frmParent != null) {
                    ((frmAppSoftBank) frmParent).setStatusBarText(mdao.getMensaje());
                }
                JOptionPane.showMessageDialog(this, mdao.getMensaje(), "Movimientos", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnInsertarActionPerformed

    private void btnRefrescarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefrescarActionPerformed
        iniciarPanel();
    }//GEN-LAST:event_btnRefrescarActionPerformed

    private void btnModificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModificarActionPerformed
        if (checkRequiredFields()) {
            BigDecimal impAnterior = movimiento.getImpMovimiento();
            recogerCampos();
            // Comprobamos que el nuevo movimiento cumple con las condiciones de
            // saldo de cuenta corriente o de tarjeta.
            if (!comprobarSaldo(productoBancario, impAnterior, movimiento.getImpMovimiento())) {
                // Preguntamos si queremos autorizar el movimiento.
                if (JOptionPane.showConfirmDialog(this, "Esta operación excede el saldo permitido. ¿Se autoriza este movimiento?", "Movimientos", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
                    return;
                }
            }
            // Eliminamos el movimiento.
            if (mdao.actualizarMovimiento(movimiento)) {
                // Mostramos el mensaje resultante de la inserción.
                 if (this.frmParent != null) {
                    String mensaje = String.format("Movimiento %s %s %s actualizado correctamente.",
                            Functions.formatDate(movimiento.getFecMovimiento()),
                            movimiento.getConcepto(),
                            Functions.formatAmount(movimiento.getImpMovimiento()));
                    ((frmAppSoftBank) frmParent).setStatusBarText(mensaje);
                    JOptionPane.showMessageDialog(this, mensaje, "Movimientos", JOptionPane.INFORMATION_MESSAGE);
                }
                // Informarmos a los paneles.
                informarPaneles();               
                // Refrescamos el panel.
                iniciarPanel();
                // Desactivamos el botón "Modificar".
                btnModificar.setEnabled(false);
            } else {
                // Error inesperado.
                if (this.frmParent != null) {
                    ((frmAppSoftBank) frmParent).setStatusBarText(mdao.getMensaje());
                }
                JOptionPane.showMessageDialog(this, mdao.getMensaje(), "Movimientos", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnModificarActionPerformed

    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed
        if (checkRequiredFields()) {
            recogerCampos();
            // Comprobamos que el nuevo movimiento cumple con las condiciones de
            // saldo de cuenta corriente o de tarjeta.
            if (!comprobarSaldo(productoBancario, movimiento.getImpMovimiento(), BigDecimal.ZERO)) {
                // Preguntamos si queremos autorizar el movimiento.
                if (JOptionPane.showConfirmDialog(this, "Esta operación excede el saldo permitido. ¿Se autoriza este movimiento?", "Movimientos", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
                    return;
                }
            }            
            // Eliminamos el movimiento.
            if (mdao.eliminarMovimiento(movimiento)) {
                // Mostramos el mensaje resultante de la inserción.
                 if (this.frmParent != null) {
                    String mensaje = String.format("Movimiento %s %s %s eliminado correctamente.",
                            Functions.formatDate(movimiento.getFecMovimiento()),
                            movimiento.getConcepto(),
                            Functions.formatAmount(movimiento.getImpMovimiento()));
                    ((frmAppSoftBank) frmParent).setStatusBarText(mensaje);
                    JOptionPane.showMessageDialog(this, mensaje, "Movimientos", JOptionPane.INFORMATION_MESSAGE);
                }
                // Informarmos a los paneles.
                informarPaneles();
                // Refrescamos el panel.
                iniciarPanel();
                // Desactivamos el botón "Eliminar".
                btnEliminar.setEnabled(false);
            } else {
                // Error inesperado.
                if (this.frmParent != null) {
                    ((frmAppSoftBank) frmParent).setStatusBarText(mdao.getMensaje());
                }
                JOptionPane.showMessageDialog(this, mdao.getMensaje(), "Movimientos", JOptionPane.ERROR_MESSAGE);                
            }
        }
    }//GEN-LAST:event_btnEliminarActionPerformed

    private void btnLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarActionPerformed
        limpiarPanelDatos();
        mostrarTabla("");
    }//GEN-LAST:event_btnLimpiarActionPerformed

    private void txtConceptoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtConceptoKeyReleased
        // Filtra la tabla por el nombre de cliente introducido.
        mostrarTabla(txtConcepto.getText());
        // Llamada para intentar habilitar los botones del Toolbar.
        habilitarBotones();
    }//GEN-LAST:event_txtConceptoKeyReleased

    private void txtConceptoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtConceptoKeyTyped
        // Límite de 50 carácteres.
        if (txtConcepto.getText().length() >= 50)
            evt.consume();
    }//GEN-LAST:event_txtConceptoKeyTyped

    private void txtImpMovimientoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtImpMovimientoKeyReleased
        // Llamada para intentar habilitar los botones del Toolbar.
        habilitarBotones();
    }//GEN-LAST:event_txtImpMovimientoKeyReleased

    private void txtImpMovimientoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtImpMovimientoKeyTyped
        // Límite de 18 números.
        if (txtImpMovimiento.getText().length() >= 18)
            evt.consume();
        else
            // Si no se ha llegado al límite de carácteres, comprobamos que
            // sólo sean números y la coma decimal.
            Functions.checkNumber(evt);
    }//GEN-LAST:event_txtImpMovimientoKeyTyped

    private void jdcFecMovimientoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jdcFecMovimientoKeyReleased
        // Llamada para intentar habilitar los botones del Toolbar.
        habilitarBotones();
    }//GEN-LAST:event_jdcFecMovimientoKeyReleased

    private void jdcFecInicioPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jdcFecInicioPropertyChange
        if ((jdcFecInicio.getDate() != null)
                && (jdcFecInicio.getDate().compareTo(jdcFecInicio.getMinSelectableDate()) >= 0) // mayor o igual que fecha mínima.
                && (jdcFecInicio.getDate().compareTo(jdcFecInicio.getMaxSelectableDate()) <= 0) // menor o igual que fecha máxima.

                && (jdcFecFin.getDate() != null)
                && (jdcFecFin.getDate().compareTo(jdcFecFin.getMinSelectableDate()) >= 0) // mayor o igual que fecha mínima.
                && (jdcFecFin.getDate().compareTo(jdcFecFin.getMaxSelectableDate()) <= 0) // menor o igual que fecha máxima.                
                ) {
            if (jdcFecInicio.getDate().compareTo(jdcFecFin.getDate()) <= 0) { // fecha inicio menor o igual que fecha fin
                mostrarTabla("");
            } else {
                JOptionPane.showMessageDialog(this, "La fecha de inicio no puede ser mayor que la fecha fin.", "Selección de fechas", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_jdcFecInicioPropertyChange

    private void jdcFecFinPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jdcFecFinPropertyChange
        if ((jdcFecInicio.getDate() != null)
                && (jdcFecInicio.getDate().compareTo(jdcFecInicio.getMinSelectableDate()) >= 0) // mayor o igual que fecha mínima.
                && (jdcFecInicio.getDate().compareTo(jdcFecInicio.getMaxSelectableDate()) <= 0) // menor o igual que fecha máxima.

                && (jdcFecFin.getDate() != null)
                && (jdcFecFin.getDate().compareTo(jdcFecFin.getMinSelectableDate()) >= 0) // mayor o igual que fecha mínima.
                && (jdcFecFin.getDate().compareTo(jdcFecFin.getMaxSelectableDate()) <= 0) // menor o igual que fecha máxima.                
                ) {
            if (jdcFecFin.getDate().compareTo(jdcFecInicio.getDate()) >= 0) { // fecha fin mayor o igual que fecha inicio
                mostrarTabla("");
            } else {
                JOptionPane.showMessageDialog(this, "La fecha de fin no puede ser menor que la fecha de inicio.", "Selección de fechas", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_jdcFecFinPropertyChange

    private void btnExtractoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExtractoActionPerformed
        List<Movimiento> lstMovimiento = mdao.listarMovimiento(productoBancario, jdcFecInicio.getDate(), jdcFecFin.getDate());
        if (mdao.isOK()) {
            try {
                ItxDocument itd = new ItxDocument(new ItxDocumentParams(PdfType.MOVEMENT, productoBancario, lstMovimiento));
            } catch (IOException E) {
                System.out.println(E.getMessage());
            }
        }
    }//GEN-LAST:event_btnExtractoActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected javax.swing.JButton btnEliminar;
    protected javax.swing.JButton btnExtracto;
    protected javax.swing.JButton btnInsertar;
    protected javax.swing.JButton btnLimpiar;
    protected javax.swing.JButton btnModificar;
    protected javax.swing.JButton btnRefrescar;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField jTextField1;
    protected com.toedter.calendar.JDateChooser jdcFecFin;
    protected com.toedter.calendar.JDateChooser jdcFecInicio;
    protected com.toedter.calendar.JDateChooser jdcFecMovimiento;
    protected javax.swing.JLabel lblConcepto;
    protected javax.swing.JLabel lblFecFin;
    protected javax.swing.JLabel lblFecInicio;
    protected javax.swing.JLabel lblFecMovimiento;
    protected javax.swing.JLabel lblImpMovimiento;
    protected javax.swing.JPanel pnlDatos;
    protected javax.swing.JPanel pnlSeleccion;
    protected javax.swing.JPanel pnlTabla;
    protected javax.swing.JTable tblMovimiento;
    protected javax.swing.JToolBar tlbAcciones;
    protected javax.swing.JToolBar tlbSeleccion;
    private javax.swing.JTextField txtConcepto;
    protected javax.swing.JFormattedTextField txtImpMovimiento;
    // End of variables declaration//GEN-END:variables
}
