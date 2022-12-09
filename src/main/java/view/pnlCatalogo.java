package view;

import java.awt.Component;
import java.util.Objects;
import javax.persistence.EntityManagerFactory;
import javax.swing.JOptionPane;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import model.DAO.CatalogoDAO;
import model.entity.Catalogo;
import utilities.Constants.PanelMode;

/**
 *
 * @author Ignacio Pineda Martín
 */
public class pnlCatalogo extends pnlAppSoftBank {
    
    private CatalogoDAO cdao;
    private Catalogo catalogo = new Catalogo();

    /**
     * Creates new form pnlCatalogo
     */
    public pnlCatalogo() {
        initComponents();
    }
    
    public pnlCatalogo(EntityManagerFactory emf) {
        this();
        this.jpaObject = catalogo;
        this.emf = emf;
        // Creamos el DAO con su EntityManagerFactory.
        cdao = new CatalogoDAO(emf);
        // Preparamos el panel en su estado inicial.
        limpiarPanel();
        // Hacemos que la tabla no sea editable.
        tblCatalogo.setDefaultEditor(Object.class, null);        
    }
    
    public void setJPAObject(Catalogo jpaObject) {
        this.catalogo = jpaObject;
        rellenarCampos();
    }
    
    public Catalogo getJPAObject() {
        recogerCampos();
        return this.catalogo;
    }
    
    @Override
    public void sendJPAObjectToFrmParent() {
        if (this.frmParent != null) {
            frmParent.setJPAObject(this.catalogo);            
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
        catalogo.setIdCatalogo(null);
        txtCodProducto.setText("");
        txtNombreProducto.setText("");
    }
    
    /**
     * Método para rellenar los campos del formulario con la propiedad
     * {@link catalogo}
     */
    private void rellenarCampos() {
        txtCodProducto.setText(catalogo.getCodProducto());
        txtNombreProducto.setText(catalogo.getNombreProducto());
    }
 
    /**
     * Método para recoger los campos del formulario en la propiedad
     * {@link catalogo}
     */    
    private void recogerCampos() {
        catalogo.setCodProducto(txtCodProducto.getText());
        catalogo.setNombreProducto(txtNombreProducto.getText());
    }

    /**
     * Método para preparar el formulario en un estado inicial.
     */
    protected void inicializarFormulario() {
        txtCodProducto.requestFocus();
        // Llamada para intentar habilitar los botones CRUD.
        if (this.frmParent != null) {
            frmParent.tryEnableCRUDButtons();
        }        
    }

    /**
     * Método para rellenar la tabla del panel.
     * 
     * @param nombreProducto El nombre de la catalogo que mostraremos en la tabla
     * (si el valor es "" se mostrarán todas).
     */
    protected void mostrarTabla(String nombreProducto) {
        cdao.listarCatalogo(tblCatalogo, nombreProducto);
    }    
    
    @Override
    protected boolean checkRequiredFields() {
        return !txtCodProducto.getText().equals("") && 
               !txtNombreProducto.getText().equals("") &&
               checkProducto();
    }

    /**
     * Método para validar si el producto ya existe en el catálogo.
     *
     * @return <b>true</b> El producto no existe.
     * <br><b>false</b> El producto ya existe.
     */
    protected boolean checkProducto() {
        // Comprobamos que el campo de código de producto tenga texto.
        if (!txtCodProducto.getText().equals("") && (this.panelMode == PanelMode.CRUD)) {
            // Comprobamos que el código de producto no esté duplicado.
            if (cdao.buscarCodProducto(txtCodProducto.getText())) {
                // Si el producto ya existe, comprobamos que no sea el que ya
                // está asignado en el panel.                 
                if (!Objects.equals(this.catalogo.getIdCatalogo(), cdao.getCatalogo().getIdCatalogo())) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Método para validar si el producto ya existe en el catálogo, mostrando un
     * panel de error si así fuera.
     *
     * @return <b>true</b> El producto no existe.
     * <br><b>false</b> El producto ya existe.
     */
    protected boolean checkProductoNoExiste() {
        // Comprobamos si el producto ya existe (sólo en modo CRUD).
        if (!checkProducto()) {
            String mensaje = String.format("El código de producto %s ya existe. Elija otro código para este producto.",
                    txtCodProducto.getText());
            if (this.frmParent != null) {
                ((frmAppSoftBank) frmParent).setStatusBarText("ERROR - " + mensaje);
            }
            JOptionPane.showMessageDialog(this,
                    mensaje,
                    "Catálogo", JOptionPane.ERROR_MESSAGE);
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
        lblCodProducto = new javax.swing.JLabel();
        txtCodProducto = new javax.swing.JTextField();
        lblNombreProducto = new javax.swing.JLabel();
        txtNombreProducto = new javax.swing.JTextField();
        pnlTabla = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblCatalogo = new javax.swing.JTable() {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component component = super.prepareRenderer(renderer, row, column);
                int rendererWidth = component.getPreferredSize().width;
                TableColumn tableColumn = getColumnModel().getColumn(column);
                tableColumn.setPreferredWidth(Math.max(rendererWidth + getIntercellSpacing().width, tableColumn.getPreferredWidth()));
                return component;
            }
        };

        setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Catálogo", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14))); // NOI18N

        pnlDatos.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        pnlDatos.setName("pnlDatos"); // NOI18N

        lblCodProducto.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblCodProducto.setText("Cód. Producto");

        txtCodProducto.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtCodProducto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCodProductoKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtCodProductoKeyTyped(evt);
            }
        });

        lblNombreProducto.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblNombreProducto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/search 16px.png"))); // NOI18N
        lblNombreProducto.setText("Nombre Producto");
        lblNombreProducto.setToolTipText("Campo de búsqueda");
        lblNombreProducto.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        lblNombreProducto.setName("lblBusqueda"); // NOI18N

        txtNombreProducto.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtNombreProducto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtNombreProductoKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtNombreProductoKeyTyped(evt);
            }
        });

        javax.swing.GroupLayout pnlDatosLayout = new javax.swing.GroupLayout(pnlDatos);
        pnlDatos.setLayout(pnlDatosLayout);
        pnlDatosLayout.setHorizontalGroup(
            pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDatosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblCodProducto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtCodProducto))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlDatosLayout.createSequentialGroup()
                        .addComponent(lblNombreProducto)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(txtNombreProducto))
                .addContainerGap())
        );
        pnlDatosLayout.setVerticalGroup(
            pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDatosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCodProducto)
                    .addComponent(lblNombreProducto))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCodProducto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtNombreProducto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlTabla.setName("pnlTabla"); // NOI18N
        pnlTabla.setPreferredSize(new java.awt.Dimension(465, 126));

        tblCatalogo.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        tblCatalogo.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tblCatalogo.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblCatalogo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblCatalogoMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tblCatalogo);

        javax.swing.GroupLayout pnlTablaLayout = new javax.swing.GroupLayout(pnlTabla);
        pnlTabla.setLayout(pnlTablaLayout);
        pnlTablaLayout.setHorizontalGroup(
            pnlTablaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTablaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 477, Short.MAX_VALUE)
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
            .addComponent(pnlTabla, javax.swing.GroupLayout.DEFAULT_SIZE, 489, Short.MAX_VALUE)
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

    private void tblCatalogoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblCatalogoMouseClicked
        // "getSelectedRow()" nos devuelve el índice de la fila seleccionada.
        int fila = tblCatalogo.getSelectedRow();
        if (fila != -1) {
            catalogo.setIdCatalogo(Integer.valueOf(tblCatalogo.getModel().getValueAt(fila, 0).toString()));
            // Recuperamos toda la información del objeto "Catalogo" a través
            // de su ID.
            if (cdao.buscarCatalogo(catalogo.getIdCatalogo())) {
                // Fijamos el objeto Catalogo en el panel y lo enviamos
                // al formulario padre.
                setJPAObject(cdao.getCatalogo());
                sendJPAObjectToFrmParent();
            }
        }
        inicializarFormulario();        
    }//GEN-LAST:event_tblCatalogoMouseClicked

    private void txtNombreProductoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNombreProductoKeyReleased
        // Filtra la tabla por el nombre de catalogo introducido.
        mostrarTabla(txtNombreProducto.getText());
        // Llamada para intentar habilitar los botones CRUD.
        if (this.frmParent != null) {
            frmParent.tryEnableCRUDButtons();
        } 
    }//GEN-LAST:event_txtNombreProductoKeyReleased

    private void txtCodProductoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCodProductoKeyReleased
        checkProductoNoExiste();
        // Llamada para intentar habilitar los botones CRUD.
        if (this.frmParent != null) {
            frmParent.tryEnableCRUDButtons();
        } 
    }//GEN-LAST:event_txtCodProductoKeyReleased

    private void txtCodProductoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCodProductoKeyTyped
        // Límite de 50 caracteres
        if (txtCodProducto.getText().length() >= 50)
            evt.consume();
    }//GEN-LAST:event_txtCodProductoKeyTyped

    private void txtNombreProductoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNombreProductoKeyTyped
        // Límite de 100 carácteres.
        if (txtNombreProducto.getText().length() >= 100)
            evt.consume();
    }//GEN-LAST:event_txtNombreProductoKeyTyped

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblCodProducto;
    private javax.swing.JLabel lblNombreProducto;
    private javax.swing.JPanel pnlDatos;
    private javax.swing.JPanel pnlTabla;
    private javax.swing.JTable tblCatalogo;
    private javax.swing.JTextField txtCodProducto;
    private javax.swing.JTextField txtNombreProducto;
    // End of variables declaration//GEN-END:variables
}
