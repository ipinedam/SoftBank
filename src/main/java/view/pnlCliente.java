package view;

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.util.Objects;
import javax.persistence.EntityManagerFactory;
import javax.swing.JOptionPane;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import model.DAO.ClienteDAO;
import model.entity.Cliente;
import utilities.CheckIdDocuments;
import utilities.CheckIdDocuments.IdDocumentType;
import utilities.Constants.PanelMode;

/**
 *
 * @author Ignacio Pineda Martín
 */
public class pnlCliente extends pnlAppSoftBank {

    protected ClienteDAO cdao;
    protected Cliente cliente = new Cliente();

    /**
     * Creates new form pnlCliente
     */
    public pnlCliente() {
        initComponents();
    }

    public pnlCliente(EntityManagerFactory emf) {
        this();
        this.jpaObject = cliente;
        this.emf = emf;
        // Creamos el DAO con su EntityManagerFactory.
        cdao = new ClienteDAO(emf);
        // Preparamos el panel en su estado inicial.
        limpiarPanel();
        // Hacemos que la tabla no sea editable.
        tblCliente.setDefaultEditor(Object.class, null);
    }

    public void setJPAObject(Cliente jpaObject) {
        this.cliente = jpaObject;
        rellenarCampos();
        // Habilitamos el botón de información si ocurre alguna de estas 
        // opciones:
        // - La consulta la está realizando un empleado.
        // - La consulta la está realizando el cliente cuyo usuario
        //   coincide con el que está conectado.
        if ( (getAppUser().isEmpleado() || this.cliente.getUsuario().equals(getAppUser())) )
            btnInformacion.setEnabled(true);
        else
            btnInformacion.setEnabled(false);
    }

    public Cliente getJPAObject() {
        recogerCampos();
        return this.cliente;
    }

    @Override
    public void sendJPAObjectToFrmParent() {
        if (this.frmParent != null) {
            frmParent.setJPAObject(this.cliente);
        }
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

    /**
     * Método para inicializar los campos del formulario.
     */
    private void vaciarCampos() {
//        cliente.setIdCliente(null);
        cliente = new Cliente();
        txtNombreCliente.setText("");
        txtApellidosCliente.setText("");
        cbxTipoIdentificacion.setSelectedIndex(-1);
        txtClaveIdentificacion.setText("");
        txtNacionalidad.setText("");
        jdcFecNacimiento.setDate(null);
        jdcFecNacimiento.setMaxSelectableDate(new java.sql.Date(System.currentTimeMillis()));
        // Deshabilitamos el botón de "Información" de cliente.
        btnInformacion.setEnabled(false);
    }

    /**
     * Método para rellenar los campos del formulario con la propiedad
     * {@link cliente}
     */
    private void rellenarCampos() {
        txtNombreCliente.setText(cliente.getNombreCliente());
        txtApellidosCliente.setText(cliente.getApellidosCliente());
        cbxTipoIdentificacion.setSelectedItem(cliente.getTipoIdentificacion());
        txtClaveIdentificacion.setText(cliente.getClaveIdentificacion());
        txtNacionalidad.setText(cliente.getNacionalidad());
        jdcFecNacimiento.setDate(cliente.getFecNacimiento());
    }

    /**
     * Método para recoger los campos del formulario en la propiedad
     * {@link cliente}
     */
    private void recogerCampos() {
        cliente.setNombreCliente(txtNombreCliente.getText());
        cliente.setApellidosCliente(txtApellidosCliente.getText());
        cliente.setTipoIdentificacion(cbxTipoIdentificacion.getSelectedItem().toString());
        cliente.setClaveIdentificacion(txtClaveIdentificacion.getText());
        cliente.setNacionalidad(txtNacionalidad.getText());
        cliente.setFecNacimiento(jdcFecNacimiento.getDate());
    }

    /**
     * Método para preparar el formulario en un estado inicial.
     */
    protected void inicializarFormulario() {
        txtNombreCliente.requestFocus();
        // Llamada para intentar habilitar los botones CRUD.
        if (this.frmParent != null) {
            frmParent.tryEnableCRUDButtons();
        }
    }

    /**
     * Método para rellenar la tabla del panel.
     *
     * @param nombreCliente El nombre del cliente que mostraremos en la tabla
     * (si el valor es "" se mostrarán todas).
     */
    protected void mostrarTabla(String nombreCliente) {
        cdao.listarClienteNombre(tblCliente, nombreCliente);
    }
    
    /**
     * Método para rellenar la tabla del panel.
     *
     * @param apellidosCliente Los apellidos del cliente que mostraremos en la
     * tabla (si el valor es "" se mostrarán todas las personas físicas y las
     * personas jurídicas (empresas) no aparecerían).
     */
    protected void mostrarTablaApellidos(String apellidosCliente) {
        cdao.listarClienteApellidos(tblCliente, apellidosCliente);
    }
    
    /**
     * Método para rellenar la tabla del panel.
     *
     * @param claveIdentificacion La clave de identificación del cliente que 
     * mostraremos en la tabla.
     */
    protected void mostrarTablaID(String claveIdentificacion) {
        cdao.listarClienteID(tblCliente, claveIdentificacion);
    }

    @Override
    protected boolean checkRequiredFields() {
        return !txtNombreCliente.getText().equals("")
                && checkApellidosCliente()
                && (cbxTipoIdentificacion.getSelectedIndex() != -1)
                && !txtClaveIdentificacion.getText().equals("")
                && !txtNacionalidad.getText().equals("")
                && (jdcFecNacimiento.getDate() != null)
                && (jdcFecNacimiento.getDate().compareTo(jdcFecNacimiento.getMinSelectableDate()) > 0)
                && (jdcFecNacimiento.getDate().compareTo(jdcFecNacimiento.getMaxSelectableDate()) < 0)
                && checkClaveIdentificacionValida()
                && checkClaveIdentificacion();
    }

    /**
     * Método para validar el contenido del campo txtApellidosCliente.
     * <p>Devolverá "True" (apellidos correctos) cuando:<ul style="list-style-type:none">
     * <li> 1. El campo de apellidos no esté vacío.
     * <li> 2. El campo de apellidos esté vacío, pero el cliente
     *         sea una empresa (tipo de documento = CIF).
     * </ul>
     * 
     * @return <b>true</b> El contenido es correcto.
     * <br><b>false</b> El contenido NO es correcto.
     */
    private boolean checkApellidosCliente() {
        // Capturamos los campos de apellidos y tipo de documento.
        String apellidos = txtApellidosCliente.getText();
        String tipoDocumento = cbxTipoIdentificacion.getSelectedIndex() == -1 ? "" : cbxTipoIdentificacion.getSelectedItem().toString();

        return !apellidos.equals("")                                                                    // 1.
                || ((apellidos.equals("")) && (tipoDocumento.equals(IdDocumentType.CIF.toString())));   // 2.
    }
    
    /**
     * Método para validar si el documento de identificación es válido.
     * La validación se realiza con la clase {@link CheckIdDocuments}.
     * 
     * @return <b>true</b> El documento de identificación es válido.
     * <br><b>false</b> El documento de identificación NO es válido.
     */
    private boolean checkClaveIdentificacionValida() {
        // La validación sólo se hará cuando se haya seleccionado tipo de
        // de documento y el número de documento no esté vacío.
        if ((cbxTipoIdentificacion.getSelectedIndex() != -1)
                && !txtClaveIdentificacion.getText().equals("")) {
            return CheckIdDocuments.CheckIdDocument(cbxTipoIdentificacion.getSelectedItem().toString(), txtClaveIdentificacion.getText());
        } else {
            return true;
        }
    }

    /**
     * Método para validar si el documento de identificación no existe
     * en otro cliente.
     * 
     * @return <b>true</b> El documento de identificación no existe.
     * <br><b>false</b> El documento de identificación ya existe.
     */
    protected boolean checkClaveIdentificacion() {
        if (!txtClaveIdentificacion.getText().equals("") && (this.panelMode == PanelMode.CRUD)) {
            if (cdao.buscarClaveCliente(txtClaveIdentificacion.getText())) {
                // Si el documento ya existe, comprobamos que no esté asignado a
                // otro cliente.
                if (!Objects.equals(this.cliente.getIdCliente(), cdao.getCliente().getIdCliente())) {
                    return false;
                }
            }
        }
        return true;
    }
    
    /**
     * Método para validar si el documento de identificación no existe
     * en otro cliente, mostrando un panel de error si así fuera.
     * 
     * @return <b>true</b> El documento de identificación no existe.
     * <br><b>false</b> El documento de identificación ya existe.
     */
    protected boolean checkClaveIdentificacionNoExiste() {
        // Comprobamos si el documento ya existe.
        if (!checkClaveIdentificacion()) {
            String mensaje = String.format("Ya existe otro cliente con documento número %s.",
                    txtClaveIdentificacion.getText());
            if (this.frmParent != null) {
                ((frmAppSoftBank) frmParent).setStatusBarText("ERROR - " + mensaje);
            }
            JOptionPane.showMessageDialog(this,
                    mensaje,
                    "Nº documento", JOptionPane.ERROR_MESSAGE);
            txtClaveIdentificacion.setText("");
            return false;
        }
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

        pnlDatos = new javax.swing.JPanel();
        lblNombreCliente = new javax.swing.JLabel();
        txtNombreCliente = new javax.swing.JTextField();
        lblApellidosCliente = new javax.swing.JLabel();
        txtApellidosCliente = new javax.swing.JTextField();
        lblTipoIdentificacion = new javax.swing.JLabel();
        cbxTipoIdentificacion = new javax.swing.JComboBox<>();
        lblClaveIdentifiacion = new javax.swing.JLabel();
        txtClaveIdentificacion = new javax.swing.JTextField();
        lblNacionalidad = new javax.swing.JLabel();
        txtNacionalidad = new javax.swing.JTextField();
        lblFecNacimiento = new javax.swing.JLabel();
        jdcFecNacimiento = new com.toedter.calendar.JDateChooser();
        tlbAccesorios = new javax.swing.JToolBar();
        tlbAccesorios.setFloatable(false);
        tlbAccesorios.setVisible(false);
        btnInformacion = new javax.swing.JButton();
        tlbAcciones = new javax.swing.JToolBar();
        tlbAcciones.setFloatable(false);
        tlbAcciones.setVisible(false);
        btnInsertar = new javax.swing.JButton();
        btnLimpiar = new javax.swing.JButton();
        pnlTabla = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblCliente = new javax.swing.JTable() {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component component = super.prepareRenderer(renderer, row, column);
                int rendererWidth = component.getPreferredSize().width;
                TableColumn tableColumn = getColumnModel().getColumn(column);
                tableColumn.setPreferredWidth(Math.max(rendererWidth + getIntercellSpacing().width, tableColumn.getPreferredWidth()));
                return component;
            }
        };

        setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Cliente", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14))); // NOI18N

        pnlDatos.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        pnlDatos.setName("pnlDatos"); // NOI18N

        lblNombreCliente.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblNombreCliente.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/search 16px.png"))); // NOI18N
        lblNombreCliente.setLabelFor(txtNombreCliente);
        lblNombreCliente.setText("Nombre / Empresa");
        lblNombreCliente.setToolTipText("Campo de búsqueda");
        lblNombreCliente.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        lblNombreCliente.setName("lblBusqueda"); // NOI18N

        txtNombreCliente.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtNombreCliente.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtNombreClienteKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtNombreClienteKeyTyped(evt);
            }
        });

        lblApellidosCliente.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblApellidosCliente.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/search 16px.png"))); // NOI18N
        lblApellidosCliente.setLabelFor(txtApellidosCliente);
        lblApellidosCliente.setText("Apellidos");
        lblApellidosCliente.setToolTipText("Campo de búsqueda");
        lblApellidosCliente.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        lblApellidosCliente.setName("lblBusqueda"); // NOI18N

        txtApellidosCliente.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtApellidosCliente.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtApellidosClienteKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtApellidosClienteKeyTyped(evt);
            }
        });

        lblTipoIdentificacion.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblTipoIdentificacion.setLabelFor(cbxTipoIdentificacion);
        lblTipoIdentificacion.setText("Tipo de docum.");

        cbxTipoIdentificacion.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        cbxTipoIdentificacion.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "DNI", "CIF", "NIE", "PASAPORTE" }));
        cbxTipoIdentificacion.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbxTipoIdentificacionItemStateChanged(evt);
            }
        });

        lblClaveIdentifiacion.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblClaveIdentifiacion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/search 16px.png"))); // NOI18N
        lblClaveIdentifiacion.setLabelFor(txtClaveIdentificacion);
        lblClaveIdentifiacion.setText("Nº docum.");
        lblClaveIdentifiacion.setToolTipText("Campo de búsqueda");
        lblClaveIdentifiacion.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        lblClaveIdentifiacion.setName("lblBusqueda"); // NOI18N

        txtClaveIdentificacion.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtClaveIdentificacion.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtClaveIdentificacionFocusLost(evt);
            }
        });
        txtClaveIdentificacion.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtClaveIdentificacionKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtClaveIdentificacionKeyTyped(evt);
            }
        });

        lblNacionalidad.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblNacionalidad.setLabelFor(txtNacionalidad);
        lblNacionalidad.setText("Nacionalidad");

        txtNacionalidad.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtNacionalidad.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtNacionalidadKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtNacionalidadKeyTyped(evt);
            }
        });

        lblFecNacimiento.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblFecNacimiento.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        lblFecNacimiento.setLabelFor(jdcFecNacimiento);
        lblFecNacimiento.setText("Fecha nacimiento");

        jdcFecNacimiento.setBackground(new java.awt.Color(70, 73, 75));
        jdcFecNacimiento.setToolTipText("Fecha de nacimiento del cliente");
        jdcFecNacimiento.setDateFormatString("dd/MM/yyyy");
        jdcFecNacimiento.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jdcFecNacimiento.setMinSelectableDate(new java.util.Date(-2177452800000L));
        jdcFecNacimiento.setMinimumSize(new java.awt.Dimension(82, 27));
        jdcFecNacimiento.setPreferredSize(new java.awt.Dimension(113, 27));
        jdcFecNacimiento.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jdcFecNacimientoPropertyChange(evt);
            }
        });

        tlbAccesorios.setRollover(true);
        tlbAccesorios.setName("tlbAccesorios"); // NOI18N

        btnInformacion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/info 16px.png"))); // NOI18N
        btnInformacion.setToolTipText("Información del cliente");
        btnInformacion.setFocusable(false);
        btnInformacion.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnInformacion.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnInformacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInformacionActionPerformed(evt);
            }
        });
        tlbAccesorios.add(btnInformacion);

        tlbAcciones.setRollover(true);
        tlbAcciones.setName("tlbAcciones"); // NOI18N

        btnInsertar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/insert 16px.png"))); // NOI18N
        btnInsertar.setToolTipText("Añadir cliente");
        btnInsertar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnInsertar.setName(""); // NOI18N
        btnInsertar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnInsertar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInsertarActionPerformed(evt);
            }
        });
        tlbAcciones.add(btnInsertar);

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
                    .addComponent(txtApellidosCliente)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlDatosLayout.createSequentialGroup()
                        .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(cbxTipoIdentificacion, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblTipoIdentificacion, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlDatosLayout.createSequentialGroup()
                                .addComponent(lblClaveIdentifiacion, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(lblNacionalidad))
                            .addGroup(pnlDatosLayout.createSequentialGroup()
                                .addComponent(txtClaveIdentificacion, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtNacionalidad))))
                    .addGroup(pnlDatosLayout.createSequentialGroup()
                        .addComponent(lblFecNacimiento, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jdcFecNacimiento, javax.swing.GroupLayout.DEFAULT_SIZE, 149, Short.MAX_VALUE))
                    .addComponent(txtNombreCliente)
                    .addGroup(pnlDatosLayout.createSequentialGroup()
                        .addComponent(lblApellidosCliente)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(pnlDatosLayout.createSequentialGroup()
                        .addComponent(lblNombreCliente)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(tlbAccesorios, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tlbAcciones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        pnlDatosLayout.setVerticalGroup(
            pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDatosLayout.createSequentialGroup()
                .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlDatosLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(lblNombreCliente))
                    .addComponent(tlbAcciones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tlbAccesorios, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtNombreCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblApellidosCliente)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtApellidosCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTipoIdentificacion)
                    .addComponent(lblClaveIdentifiacion)
                    .addComponent(lblNacionalidad))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbxTipoIdentificacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtClaveIdentificacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtNacionalidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jdcFecNacimiento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblFecNacimiento))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlDatosLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {txtApellidosCliente, txtNombreCliente});

        pnlTabla.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        pnlTabla.setName("pnlTabla"); // NOI18N
        pnlTabla.setPreferredSize(new java.awt.Dimension(129, 126));

        tblCliente.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        tblCliente.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tblCliente.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblCliente.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblClienteMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tblCliente);

        javax.swing.GroupLayout pnlTablaLayout = new javax.swing.GroupLayout(pnlTabla);
        pnlTabla.setLayout(pnlTablaLayout);
        pnlTablaLayout.setHorizontalGroup(
            pnlTablaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlTablaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlTablaLayout.setVerticalGroup(
            pnlTablaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 126, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlDatos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(pnlTabla, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 409, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnlDatos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlTabla, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    protected void tblClienteMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblClienteMouseClicked
        Integer idCliente;
        // "getSelectedRow()" nos devuelve el índice de la fila seleccionada.
        int fila = tblCliente.getSelectedRow();
        if (fila != -1) {
            idCliente = (Integer.valueOf(tblCliente.getModel().getValueAt(fila, 0).toString()));
            // Recuperamos toda la información del objeto "Cliente" a través
            // de su ID.
            if (cdao.buscarCliente(idCliente)) {
                // Fijamos el objeto Cliente en el panel y lo enviamos
                // al formulario padre.
                setJPAObject(cdao.getCliente());
                sendJPAObjectToFrmParent();
            }
        }
        inicializarFormulario();
    }//GEN-LAST:event_tblClienteMouseClicked

    private void txtApellidosClienteKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtApellidosClienteKeyReleased
        // Filtra la tabla por los apellidos de cliente introducidos.
        mostrarTablaApellidos(txtApellidosCliente.getText());
        // Llamada para intentar habilitar los botones CRUD.
        if (this.frmParent != null) {
            frmParent.tryEnableCRUDButtons();
        }
    }//GEN-LAST:event_txtApellidosClienteKeyReleased

    private void txtNombreClienteKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNombreClienteKeyReleased
        // Filtra la tabla por el nombre de cliente introducido.
        mostrarTabla(txtNombreCliente.getText());
        // Llamada para intentar habilitar los botones CRUD.
        if (this.frmParent != null) {
            frmParent.tryEnableCRUDButtons();
        }
    }//GEN-LAST:event_txtNombreClienteKeyReleased

    private void txtNombreClienteKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNombreClienteKeyTyped
        // Límite de 50 carácteres.
        if (txtNombreCliente.getText().length() >= 50)
            evt.consume();
    }//GEN-LAST:event_txtNombreClienteKeyTyped

    private void txtApellidosClienteKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtApellidosClienteKeyTyped
        // Límite de 100 carácteres.
        if (txtApellidosCliente.getText().length() >= 100)
            evt.consume();
    }//GEN-LAST:event_txtApellidosClienteKeyTyped

    private void txtClaveIdentificacionKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtClaveIdentificacionKeyReleased
        mostrarTablaID(txtClaveIdentificacion.getText());                
        // Llamada para intentar habilitar los botones CRUD.
        if (this.frmParent != null) {
            frmParent.tryEnableCRUDButtons();
        }
    }//GEN-LAST:event_txtClaveIdentificacionKeyReleased

    private void txtNacionalidadKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNacionalidadKeyReleased
        // Llamada para intentar habilitar los botones CRUD.
        if (this.frmParent != null) {
            frmParent.tryEnableCRUDButtons();
        }
    }//GEN-LAST:event_txtNacionalidadKeyReleased

    private void cbxTipoIdentificacionItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbxTipoIdentificacionItemStateChanged
        // Cuando seleccionamos "CIF", estamos ante una empresa.
        // Por tanto, no hay necesidad de rellenar el campo "Apellidos".
        // Además, la fecha de nacimiento se convierte en fecha de constitución
        // de la empresa.
        if ((evt.getStateChange() == ItemEvent.SELECTED) 
            && (cbxTipoIdentificacion.getSelectedItem().toString().equals(IdDocumentType.CIF.toString()))) {
                lblApellidosCliente.setText(" ");
                txtApellidosCliente.setEnabled(false);
                lblFecNacimiento.setText("Fec. Constitución");
            } else {
                lblApellidosCliente.setText("Apellidos");
                txtApellidosCliente.setEnabled(true);
                lblFecNacimiento.setText("Fecha nacimiento");
            }      
    }//GEN-LAST:event_cbxTipoIdentificacionItemStateChanged

    private void txtClaveIdentificacionFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtClaveIdentificacionFocusLost
        // Validamos el documento de identificación.
        if (!checkClaveIdentificacionValida()) {
            String mensaje = String.format("El %s número %s no es válido.",
                    cbxTipoIdentificacion.getSelectedItem().toString(),
                    txtClaveIdentificacion.getText());
            if (this.frmParent != null) {
                ((frmAppSoftBank) frmParent).setStatusBarText("ERROR - " + mensaje);
                frmParent.tryEnableCRUDButtons();
            }
            JOptionPane.showMessageDialog(this, mensaje,
                    "Nº documento", JOptionPane.ERROR_MESSAGE);
            txtClaveIdentificacion.setText("");
        } else if (!checkClaveIdentificacionNoExiste()) {
            // Llamada para intentar habilitar los botones CRUD.
            if (this.frmParent != null) {
                frmParent.tryEnableCRUDButtons();
            }
        }
    }//GEN-LAST:event_txtClaveIdentificacionFocusLost

    private void txtClaveIdentificacionKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtClaveIdentificacionKeyTyped
        // Límite de 9 carácteres.
        if (((javax.swing.JTextField) evt.getSource()).getText().length() >= 9)
            evt.consume();
    }//GEN-LAST:event_txtClaveIdentificacionKeyTyped

    private void txtNacionalidadKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNacionalidadKeyTyped
        // Límite de 30 carácteres.
        if (((javax.swing.JTextField) evt.getSource()).getText().length() >= 30)
            evt.consume();
    }//GEN-LAST:event_txtNacionalidadKeyTyped

    private void jdcFecNacimientoPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jdcFecNacimientoPropertyChange
        // Llamada para intentar habilitar los botones CRUD.
        if (this.frmParent != null) {
            frmParent.tryEnableCRUDButtons();
        }
    }//GEN-LAST:event_jdcFecNacimientoPropertyChange

    protected void btnInsertarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInsertarActionPerformed
        // *** NO ELIMINAR ESTE EVENTO ***
    }//GEN-LAST:event_btnInsertarActionPerformed

    protected void btnLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarActionPerformed
        limpiarPanel();
    }//GEN-LAST:event_btnLimpiarActionPerformed

    private void btnInformacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInformacionActionPerformed
        if (this.cliente != null) {
            frmCliente fc = new frmCliente();
            fc.setAppUser(getAppUser());
            fc.setJPAObject(this.cliente);
            fc.setFormMode(PanelMode.READ_ONLY);
            fc.setVisible(true);
        }
    }//GEN-LAST:event_btnInformacionActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected javax.swing.JButton btnInformacion;
    protected javax.swing.JButton btnInsertar;
    protected javax.swing.JButton btnLimpiar;
    protected javax.swing.JComboBox<String> cbxTipoIdentificacion;
    protected javax.swing.JScrollPane jScrollPane2;
    protected com.toedter.calendar.JDateChooser jdcFecNacimiento;
    protected javax.swing.JLabel lblApellidosCliente;
    protected javax.swing.JLabel lblClaveIdentifiacion;
    protected javax.swing.JLabel lblFecNacimiento;
    protected javax.swing.JLabel lblNacionalidad;
    protected javax.swing.JLabel lblNombreCliente;
    protected javax.swing.JLabel lblTipoIdentificacion;
    protected javax.swing.JPanel pnlDatos;
    protected javax.swing.JPanel pnlTabla;
    protected javax.swing.JTable tblCliente;
    protected javax.swing.JToolBar tlbAccesorios;
    protected javax.swing.JToolBar tlbAcciones;
    protected javax.swing.JTextField txtApellidosCliente;
    protected javax.swing.JTextField txtClaveIdentificacion;
    protected javax.swing.JTextField txtNacionalidad;
    protected javax.swing.JTextField txtNombreCliente;
    // End of variables declaration//GEN-END:variables
}
