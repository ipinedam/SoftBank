package view;

import java.awt.Component;
import java.util.Objects;
import javax.persistence.EntityManagerFactory;
import javax.swing.JOptionPane;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import model.DAO.SucursalDAO;
import model.entity.Sucursal;
import utilities.Constants.PanelMode;
import utilities.Functions;

/**
 *
 * @author Ignacio Pineda Martín
 */
public class pnlSucursal extends pnlAppSoftBank {
    
    private SucursalDAO sdao;
    private Sucursal sucursal = new Sucursal();

    /**
     * Creates new form pnlSucursal
     */
    public pnlSucursal() {
        initComponents();
    }
    
    public pnlSucursal(EntityManagerFactory emf) {
        this();
        this.jpaObject = sucursal;
        this.emf = emf;
        // Creamos el DAO con su EntityManagerFactory.
        sdao = new SucursalDAO(emf);
        // Preparamos el panel en su estado inicial.
        limpiarPanel();
        // Hacemos que la tabla no sea editable.
        tblSucursal.setDefaultEditor(Object.class, null);        
    }
    
    public void setJPAObject(Sucursal jpaObject) {
        this.sucursal = jpaObject;
        rellenarCampos();
        btnInformacion.setEnabled(true);        
    }
    
    public Sucursal getJPAObject() {
        recogerCampos();
        return this.sucursal;
    }
    
    @Override
    public void sendJPAObjectToFrmParent() {
        if (this.frmParent != null) {
            frmParent.setJPAObject(this.sucursal);            
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
        sucursal.setIdSucursal(null);
        txtCodSucursal.setText("");
        txtNombreSucursal.setText("");
        // Deshabilitamos el botón de "Información" de la sucursal.
        btnInformacion.setEnabled(false);        
    }
    
    /**
     * Método para rellenar los campos del formulario con la propiedad
     * {@link sucursal}
     */
    private void rellenarCampos() {
        txtCodSucursal.setText(Integer.toString(sucursal.getCodSucursal()));
        txtNombreSucursal.setText(sucursal.getNombreSucursal());
    }
 
    /**
     * Método para recoger los campos del formulario en la propiedad
     * {@link sucursal}
     */    
    private void recogerCampos() {
        sucursal.setCodSucursal(Integer.parseInt(txtCodSucursal.getText()));
        sucursal.setNombreSucursal(txtNombreSucursal.getText());
    }

    /**
     * Método para preparar el formulario en un estado inicial.
     */
    protected void inicializarFormulario() {
        txtCodSucursal.requestFocus();
        // Llamada para intentar habilitar los botones CRUD.
        if (this.frmParent != null) {
            frmParent.tryEnableCRUDButtons();
        }        
    }

    /**
     * Método para rellenar la tabla del panel.
     * 
     * @param nombreSucursal El nombre de la sucursal que mostraremos en la tabla
     * (si el valor es "" se mostrarán todas).
     */
    protected void mostrarTabla(String nombreSucursal) {
        sdao.listarSucursal(tblSucursal, nombreSucursal);
    }    
    
    @Override
    protected boolean checkRequiredFields() {
        return !txtCodSucursal.getText().equals("") && 
               !txtNombreSucursal.getText().equals("") &&
               checkSucursal();
    }
    
    /**
     * Método para validar si la sucursal ya existe.
     * 
     * @return <b>true</b> La sucursal no existe.
     * <br><b>false</b> La sucursal ya existe.
     */
    protected boolean checkSucursal() {
        // Comprobamos que el campo de código de sucursal tenga texto.
        if (!txtCodSucursal.getText().equals("") && (this.panelMode == PanelMode.CRUD)) {
            // Comprobamos que el código de sucursal no esté duplicado.
            if (sdao.buscarCodSucursal(Integer.parseInt(txtCodSucursal.getText()))) {
                // Si la sucursal ya existe, comprobamos que no sea la que ya
                // está asignada en el panel.                 
                if (!Objects.equals(this.sucursal.getIdSucursal(), sdao.getSucursal().getIdSucursal())) {
                    return false;
                }
            }
        }
        return true;
    }    
    
    /**
     * Método para validar si la sucursal ya existe, mostrando un panel de error
     * si así fuera.
     *
     * @return <b>true</b> La sucursal no existe.
     * <br><b>false</b> La sucursal ya existe.
     */
    protected boolean checkSucursalNoExiste() {
        // Comprobamos si la sucursal ya existe (sólo en modo CRUD).
        if (!checkSucursal()) {
            String mensaje = String.format("El código de sucursal %s ya existe. Elija otro código para esta sucursal.",
                    txtCodSucursal.getText());
            if (this.frmParent != null) {
                ((frmAppSoftBank) frmParent).setStatusBarText("ERROR - " + mensaje);
            }
            JOptionPane.showMessageDialog(this,
                    mensaje,
                    "Sucursal", JOptionPane.ERROR_MESSAGE);
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
        lblCodSucursal = new javax.swing.JLabel();
        txtCodSucursal = new javax.swing.JTextField();
        lblNombreSucursal = new javax.swing.JLabel();
        txtNombreSucursal = new javax.swing.JTextField();
        tlbAccesorios = new javax.swing.JToolBar();
        tlbAccesorios.setFloatable(false);
        tlbAccesorios.setVisible(false);
        btnInformacion = new javax.swing.JButton();
        pnlTabla = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblSucursal = new javax.swing.JTable() {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component component = super.prepareRenderer(renderer, row, column);
                int rendererWidth = component.getPreferredSize().width;
                TableColumn tableColumn = getColumnModel().getColumn(column);
                tableColumn.setPreferredWidth(Math.max(rendererWidth + getIntercellSpacing().width, tableColumn.getPreferredWidth()));
                return component;
            }
        };

        setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Sucursal", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14))); // NOI18N

        pnlDatos.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        pnlDatos.setName("pnlDatos"); // NOI18N

        lblCodSucursal.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblCodSucursal.setText("Cód. sucursal");

        txtCodSucursal.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtCodSucursal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCodSucursalKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtCodSucursalKeyTyped(evt);
            }
        });

        lblNombreSucursal.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblNombreSucursal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/search 16px.png"))); // NOI18N
        lblNombreSucursal.setText("Nombre sucursal");
        lblNombreSucursal.setToolTipText("Campo de búsqueda");
        lblNombreSucursal.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        lblNombreSucursal.setName("lblBusqueda"); // NOI18N

        txtNombreSucursal.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtNombreSucursal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtNombreSucursalKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtNombreSucursalKeyTyped(evt);
            }
        });

        tlbAccesorios.setRollover(true);
        tlbAccesorios.setName("tlbAccesorios"); // NOI18N

        btnInformacion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/info 16px.png"))); // NOI18N
        btnInformacion.setToolTipText("Información de la sucursal");
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
                .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblCodSucursal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtCodSucursal))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlDatosLayout.createSequentialGroup()
                        .addComponent(lblNombreSucursal)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(tlbAccesorios, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlDatosLayout.createSequentialGroup()
                        .addComponent(txtNombreSucursal)
                        .addContainerGap())))
        );
        pnlDatosLayout.setVerticalGroup(
            pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDatosLayout.createSequentialGroup()
                .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlDatosLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblCodSucursal)
                            .addComponent(lblNombreSucursal)))
                    .addComponent(tlbAccesorios, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCodSucursal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtNombreSucursal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlTabla.setName("pnlTabla"); // NOI18N
        pnlTabla.setPreferredSize(new java.awt.Dimension(465, 126));

        tblSucursal.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        tblSucursal.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tblSucursal.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblSucursal.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblSucursalMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tblSucursal);

        javax.swing.GroupLayout pnlTablaLayout = new javax.swing.GroupLayout(pnlTabla);
        pnlTabla.setLayout(pnlTablaLayout);
        pnlTablaLayout.setHorizontalGroup(
            pnlTablaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTablaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
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
            .addComponent(pnlTabla, javax.swing.GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(pnlDatos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlTabla, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void tblSucursalMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblSucursalMouseClicked
        // "getSelectedRow()" nos devuelve el índice de la fila seleccionada.
        int fila = tblSucursal.getSelectedRow();
        if (fila != -1) {
            sucursal.setIdSucursal(Integer.valueOf(tblSucursal.getModel().getValueAt(fila, 0).toString()));
            // Recuperamos toda la información del objeto "Sucursal" a través
            // de su ID.
            if (sdao.buscarSucursal(sucursal.getIdSucursal())) {
                // Fijamos el objeto Sucursal en el panel y lo enviamos
                // al formulario padre.
                setJPAObject(sdao.getSucursal());
                sendJPAObjectToFrmParent();
            }
        }
        inicializarFormulario();        
    }//GEN-LAST:event_tblSucursalMouseClicked

    private void txtNombreSucursalKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNombreSucursalKeyReleased
        // Filtra la tabla por el nombre de sucursal introducido.
        mostrarTabla(txtNombreSucursal.getText());
        // Llamada para intentar habilitar los botones CRUD.
        if (this.frmParent != null) {
            frmParent.tryEnableCRUDButtons();
        } 
    }//GEN-LAST:event_txtNombreSucursalKeyReleased

    private void txtCodSucursalKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCodSucursalKeyReleased
        checkSucursalNoExiste();
        // Llamada para intentar habilitar los botones CRUD.
        if (this.frmParent != null) {
            frmParent.tryEnableCRUDButtons();
        } 
    }//GEN-LAST:event_txtCodSucursalKeyReleased

    private void txtCodSucursalKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCodSucursalKeyTyped
        // Límite de 4 números
        if (txtCodSucursal.getText().length() >= 4)
            evt.consume();
        else
            // Si no se ha llegado al límite de carácteres, comprobamos que
            // sólo sean números y el carácter "."
            Functions.checkNumber(evt);
    }//GEN-LAST:event_txtCodSucursalKeyTyped

    private void txtNombreSucursalKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNombreSucursalKeyTyped
        // Límite de 100 carácteres.
        if (txtNombreSucursal.getText().length() >= 100)
            evt.consume();
    }//GEN-LAST:event_txtNombreSucursalKeyTyped

    private void btnInformacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInformacionActionPerformed
        if (this.sucursal != null) {
            frmSucursal fs = new frmSucursal();
            fs.setJPAObject(this.sucursal);
            fs.setFormMode(PanelMode.READ_ONLY);
            fs.setVisible(true);
        }
    }//GEN-LAST:event_btnInformacionActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected javax.swing.JButton btnInformacion;
    protected javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblCodSucursal;
    private javax.swing.JLabel lblNombreSucursal;
    protected javax.swing.JPanel pnlDatos;
    protected javax.swing.JPanel pnlTabla;
    protected javax.swing.JTable tblSucursal;
    protected javax.swing.JToolBar tlbAccesorios;
    private javax.swing.JTextField txtCodSucursal;
    private javax.swing.JTextField txtNombreSucursal;
    // End of variables declaration//GEN-END:variables
}
