package view;

import java.awt.Component;
import java.util.Objects;
import javax.persistence.EntityManagerFactory;
import javax.swing.JOptionPane;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import model.DAO.EmpleadoDAO;
import model.entity.Empleado;
import utilities.Constants.PanelMode;

/**
 *
 * @author Ignacio Pineda Martín
 */
public class pnlEmpleado extends pnlAppSoftBank {
    
    private EmpleadoDAO edao;
    private Empleado empleado = new Empleado();

    /**
     * Creates new form pnlEmpleado
     */
    public pnlEmpleado() {
        initComponents();
    }
    
    public pnlEmpleado(EntityManagerFactory emf) {
        this();
        this.jpaObject = empleado;
        this.emf = emf;
        // Creamos el DAO con su EntityManagerFactory.
        edao = new EmpleadoDAO(emf);
        // Preparamos el panel en su estado inicial.
        limpiarPanel();
        // Hacemos que la tabla no sea editable.
        tblEmpleado.setDefaultEditor(Object.class, null);        
    }
    
    public void setJPAObject(Empleado jpaObject) {
        this.empleado = jpaObject;
        rellenarCampos();
        btnInformacion.setEnabled(true);
    }
    
    public Empleado getJPAObject() {
        recogerCampos();
        return this.empleado;
    }
    
    @Override
    public void sendJPAObjectToFrmParent() {
        if (this.frmParent != null) {
            frmParent.setJPAObject(this.empleado);            
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
    private void vaciarCampos() {
        empleado.setIdEmpleado(null);
        txtNombreEmpleado.setText("");
        txtApellidosEmpleado.setText("");
        // Deshabilitamos el botón de "Información" de cliente.
        btnInformacion.setEnabled(false);
    }
    
    /**
     * Método para rellenar los campos del formulario con la propiedad
     * {@link empleado}
     */
    private void rellenarCampos() {
        txtNombreEmpleado.setText(empleado.getNombreEmpleado());        
        txtApellidosEmpleado.setText(empleado.getApellidosEmpleado());
    }
 
    /**
     * Método para recoger los campos del formulario en la propiedad
     * {@link empleado}
     */    
    private void recogerCampos() {
        empleado.setNombreEmpleado(txtNombreEmpleado.getText());        
        empleado.setApellidosEmpleado(txtApellidosEmpleado.getText());
    }

    /**
     * Método para preparar el formulario en un estado inicial.
     */
    protected void inicializarFormulario() {
        txtNombreEmpleado.requestFocus();
        // Llamada para intentar habilitar los botones CRUD.
        if (this.frmParent != null) {
            frmParent.tryEnableCRUDButtons();
        }        
    }

    /**
     * Método para rellenar la tabla del panel.
     * 
     * @param apellidosEmpleado Los apellidos del empleado que mostraremos en la tabla
     * (si el valor es "" se mostrarán todos).
     */
    protected void mostrarTabla(String apellidosEmpleado) {
        edao.listarEmpleado(tblEmpleado, apellidosEmpleado);
    }    
    
    @Override
    protected boolean checkRequiredFields() {
        return !txtNombreEmpleado.getText().equals("") && 
               !txtApellidosEmpleado.getText().equals("") &&
               checkEmpleado();
    }

    /**
     * Método para validar si el nombre y apellidos del empleado ya existen.
     * 
     * @return <b>true</b> El empleado no existe.
     * <br><b>false</b> El empleado ya existe.
     */
    protected boolean checkEmpleado() {
        // Comprobamos que el nombre y apellidos tengan texto.
        if (!txtNombreEmpleado.getText().equals("")
                && !txtApellidosEmpleado.getText().equals("") 
                && (this.panelMode == PanelMode.CRUD)) {
            // Comprobamos que el empleado no exista (comprobación mediante el
            // nombre y los apellidos).
            if (edao.buscarEmpleado(txtNombreEmpleado.getText(), txtApellidosEmpleado.getText())) {
                // Si el empleado ya existe, comprobamos que no sea el 
                // que ya está asignado en el panel.                
                if (!Objects.equals(this.empleado.getIdEmpleado(), edao.getEmpleado().getIdEmpleado())) {
                    return false;
                }
            }
        }
        return true;
    }
    
    /**
     * Método para validar si el nombre y apellidos del empleado ya existen,
     * mostrando un panel de error si así fuera.
     * 
     * @return <b>true</b> El empleado no existe.
     * <br><b>false</b> El empleado ya existe.
     */
    protected boolean checkEmpleadoNoExiste() {
        // Comprobamos si el empleado ya existe.
        if (!checkEmpleado()) {
            String mensaje = String.format("El empleado %s %s ya existe. Elija otro nombre para este empleado.",
                    txtNombreEmpleado.getText(),
                    txtApellidosEmpleado.getText());
            if (this.frmParent != null) {
                ((frmAppSoftBank) frmParent).setStatusBarText("ERROR - " + mensaje);
            }
            JOptionPane.showMessageDialog(this,
                    mensaje,
                    "Empleado", JOptionPane.ERROR_MESSAGE);
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
        lblNombreEmpleado = new javax.swing.JLabel();
        txtNombreEmpleado = new javax.swing.JTextField();
        lblApellidosEmpleado = new javax.swing.JLabel();
        txtApellidosEmpleado = new javax.swing.JTextField();
        tlbAccesorios = new javax.swing.JToolBar();
        tlbAccesorios.setFloatable(false);
        tlbAccesorios.setVisible(false);
        btnInformacion = new javax.swing.JButton();
        pnlTabla = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblEmpleado = new javax.swing.JTable() {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component component = super.prepareRenderer(renderer, row, column);
                int rendererWidth = component.getPreferredSize().width;
                TableColumn tableColumn = getColumnModel().getColumn(column);
                tableColumn.setPreferredWidth(Math.max(rendererWidth + getIntercellSpacing().width, tableColumn.getPreferredWidth()));
                return component;
            }
        };

        setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Empleado", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14))); // NOI18N

        pnlDatos.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        pnlDatos.setName("pnlDatos"); // NOI18N
        pnlDatos.setPreferredSize(new java.awt.Dimension(325, 112));

        lblNombreEmpleado.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblNombreEmpleado.setText("Nombre");

        txtNombreEmpleado.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtNombreEmpleado.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtNombreEmpleadoFocusLost(evt);
            }
        });
        txtNombreEmpleado.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtNombreEmpleadoKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtNombreEmpleadoKeyTyped(evt);
            }
        });

        lblApellidosEmpleado.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblApellidosEmpleado.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/search 16px.png"))); // NOI18N
        lblApellidosEmpleado.setText("Apellidos");
        lblApellidosEmpleado.setToolTipText("Campo de búsqueda");
        lblApellidosEmpleado.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        lblApellidosEmpleado.setName("lblBusqueda"); // NOI18N

        txtApellidosEmpleado.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtApellidosEmpleado.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtApellidosEmpleadoFocusLost(evt);
            }
        });
        txtApellidosEmpleado.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtApellidosEmpleadoKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtApellidosEmpleadoKeyTyped(evt);
            }
        });

        tlbAccesorios.setRollover(true);
        tlbAccesorios.setName("tlbAccesorios"); // NOI18N

        btnInformacion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/info 16px.png"))); // NOI18N
        btnInformacion.setToolTipText("Información del empleado");
        btnInformacion.setFocusable(false);
        btnInformacion.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnInformacion.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnInformacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInformacionActionPerformed(evt);
            }
        });
        tlbAccesorios.add(btnInformacion);

        javax.swing.GroupLayout pnlDatosLayout = new javax.swing.GroupLayout(pnlDatos);
        pnlDatos.setLayout(pnlDatosLayout);
        pnlDatosLayout.setHorizontalGroup(
            pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDatosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlDatosLayout.createSequentialGroup()
                        .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtNombreEmpleado)
                            .addComponent(txtApellidosEmpleado))
                        .addContainerGap())
                    .addGroup(pnlDatosLayout.createSequentialGroup()
                        .addComponent(lblApellidosEmpleado)
                        .addGap(0, 303, Short.MAX_VALUE))
                    .addGroup(pnlDatosLayout.createSequentialGroup()
                        .addComponent(lblNombreEmpleado)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(tlbAccesorios, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );
        pnlDatosLayout.setVerticalGroup(
            pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDatosLayout.createSequentialGroup()
                .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlDatosLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(lblNombreEmpleado))
                    .addComponent(tlbAccesorios, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtNombreEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblApellidosEmpleado)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtApellidosEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlDatosLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {txtApellidosEmpleado, txtNombreEmpleado});

        pnlTabla.setName("pnlTabla"); // NOI18N

        tblEmpleado.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        tblEmpleado.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tblEmpleado.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblEmpleado.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblEmpleadoMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tblEmpleado);

        javax.swing.GroupLayout pnlTablaLayout = new javax.swing.GroupLayout(pnlTabla);
        pnlTabla.setLayout(pnlTablaLayout);
        pnlTablaLayout.setHorizontalGroup(
            pnlTablaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTablaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 378, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlTablaLayout.setVerticalGroup(
            pnlTablaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 167, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlDatos, javax.swing.GroupLayout.DEFAULT_SIZE, 390, Short.MAX_VALUE)
            .addComponent(pnlTabla, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(pnlDatos, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnlTabla, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void tblEmpleadoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblEmpleadoMouseClicked
        // "getSelectedRow()" nos devuelve el índice de la fila seleccionada.
        int fila = tblEmpleado.getSelectedRow();
        if (fila != -1) {
            empleado.setIdEmpleado(Integer.valueOf(tblEmpleado.getModel().getValueAt(fila, 0).toString()));
            // Recuperamos toda la información del objeto "Empleado" a través
            // de su ID.
            if (edao.buscarEmpleado(empleado.getIdEmpleado())) {
                // Fijamos el objeto Empleado en el panel y lo enviamos
                // al formulario padre.
                setJPAObject(edao.getEmpleado());
                sendJPAObjectToFrmParent();
            }
        }
        inicializarFormulario();        
    }//GEN-LAST:event_tblEmpleadoMouseClicked

    private void txtApellidosEmpleadoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtApellidosEmpleadoKeyReleased
        // Filtra la tabla por los apellidos del empleado introducidos.
        mostrarTabla(txtApellidosEmpleado.getText());
        // Llamada para intentar habilitar los botones CRUD.
        if (this.frmParent != null) {
            frmParent.tryEnableCRUDButtons();
        } 
    }//GEN-LAST:event_txtApellidosEmpleadoKeyReleased

    private void txtNombreEmpleadoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNombreEmpleadoKeyReleased
        // Llamada para intentar habilitar los botones CRUD.
        if (this.frmParent != null) {
            frmParent.tryEnableCRUDButtons();
        } 
    }//GEN-LAST:event_txtNombreEmpleadoKeyReleased

    private void txtNombreEmpleadoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNombreEmpleadoKeyTyped
        // Límite de 50 carácteres.
        if (txtNombreEmpleado.getText().length() >= 50)
            evt.consume();
    }//GEN-LAST:event_txtNombreEmpleadoKeyTyped

    private void txtApellidosEmpleadoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtApellidosEmpleadoKeyTyped
        // Límite de 100 carácteres.
        if (txtApellidosEmpleado.getText().length() >= 100)
            evt.consume();
    }//GEN-LAST:event_txtApellidosEmpleadoKeyTyped

    private void txtNombreEmpleadoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtNombreEmpleadoFocusLost
        if (!checkEmpleadoNoExiste()) {
            // Llamada para intentar habilitar los botones CRUD.
            if (this.frmParent != null) {
                frmParent.tryEnableCRUDButtons();
            }
        }
    }//GEN-LAST:event_txtNombreEmpleadoFocusLost

    private void txtApellidosEmpleadoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtApellidosEmpleadoFocusLost
        if (!checkEmpleadoNoExiste()) {
            // Llamada para intentar habilitar los botones CRUD.
            if (this.frmParent != null) {
                frmParent.tryEnableCRUDButtons();
            }
        }
    }//GEN-LAST:event_txtApellidosEmpleadoFocusLost

    private void btnInformacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInformacionActionPerformed
        if (this.empleado != null) {
            frmEmpleado fe = new frmEmpleado();
            fe.setAppUser(getAppUser());
            fe.setJPAObject(this.empleado);
            fe.setFormMode(PanelMode.READ_ONLY);
            fe.setVisible(true);
        }
    }//GEN-LAST:event_btnInformacionActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected javax.swing.JButton btnInformacion;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblApellidosEmpleado;
    private javax.swing.JLabel lblNombreEmpleado;
    private javax.swing.JPanel pnlDatos;
    private javax.swing.JPanel pnlTabla;
    private javax.swing.JTable tblEmpleado;
    protected javax.swing.JToolBar tlbAccesorios;
    private javax.swing.JTextField txtApellidosEmpleado;
    private javax.swing.JTextField txtNombreEmpleado;
    // End of variables declaration//GEN-END:variables
}
