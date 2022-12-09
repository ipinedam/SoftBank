package view;

import java.awt.Component;
import java.math.BigDecimal;
import javax.persistence.EntityManagerFactory;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import model.DAO.DireccionDAO;
import model.entity.Direccion;
import utilities.Functions;

/**
 *
 * @author Ignacio Pineda Martín
 */
public class pnlDireccion extends pnlAppSoftBank {
    
    private DireccionDAO ddao;
    private Direccion direccion = new Direccion();

    /**
     * Creates new form pnlDireccion
     */
    public pnlDireccion() {
        initComponents();
    }
    
    public pnlDireccion(EntityManagerFactory emf) {
        this();
        this.jpaObject = direccion;
        this.emf = emf;
        // Creamos el DAO con su EntityManagerFactory.
        ddao = new DireccionDAO(emf);
        // Preparamos el panel en su estado inicial.
        limpiarPanel();
        // Hacemos que la tabla no sea editable.
        tblDireccion.setDefaultEditor(Object.class, null);        
    }

    public void setJPAObject(Direccion jpaObject) {
        this.direccion = jpaObject;
        rellenarCampos();
    }
    
    public Direccion getJPAObject() {
        recogerCampos();
        return this.direccion;
    }    
    
    @Override
    public void sendJPAObjectToFrmParent() {
        if (this.frmParent != null) {
            frmParent.setJPAObject(this.direccion);            
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
        direccion.setIdDireccion(null);
        cbxTipoVia.setSelectedIndex(-1);
        txtNombreVia.setText("");
        txtNumero.setText("");
        txtPoblacion.setText("");
        txtCodPostal.setText("");
        txtProvinciaEstado.setText("");
        txtPais.setText("");
    }
    
    /**
     * Método para rellenar los campos del formulario con la propiedad
     * {@link sucursal}
     */
    private void rellenarCampos() {
        cbxTipoVia.setSelectedItem(direccion.getTipoVia());
        txtNombreVia.setText(direccion.getNombreVia());
        txtNumero.setText(direccion.getNumero().stripTrailingZeros().toPlainString());
        txtPoblacion.setText(direccion.getPoblacion());
        txtCodPostal.setText(String.format("%05d", direccion.getCodPostal()));
        txtProvinciaEstado.setText(direccion.getProvinciaEstado());
        txtPais.setText(direccion.getPais());
    }
    
    /**
     * Método para recoger los campos del formulario en la propiedad
     * {@link Direccion}
     */    
    private void recogerCampos() {
        direccion.setTipoVia(cbxTipoVia.getSelectedItem().toString());
        direccion.setNombreVia(txtNombreVia.getText());
        direccion.setNumero(new BigDecimal(txtNumero.getText()));
        direccion.setPoblacion(txtPoblacion.getText());
        direccion.setCodPostal(Integer.parseInt(txtCodPostal.getText()));
        direccion.setProvinciaEstado(txtProvinciaEstado.getText());
        direccion.setPais(txtPais.getText());
    }

    /**
     * Método para preparar el formulario en un estado inicial.
     */
    protected void inicializarFormulario() {
        cbxTipoVia.requestFocus();
        
        if (this.frmParent != null) {
            frmParent.tryEnableCRUDButtons();
        }
    }

    /**
     * Método para rellenar la tabla del panel.
     * 
     * @param nombreVia El nombre de la vía que mostraremos en la tabla
     * (si el valor es "" se mostrarán todas).
     */
    protected void mostrarTabla(String nombreVia) {
        ddao.listarDireccion(tblDireccion, nombreVia);
    }
    
    @Override
    protected boolean checkRequiredFields() {
        return (cbxTipoVia.getSelectedIndex() != -1) &&                
               !txtNombreVia.getText().equals("") && 
               !txtNumero.getText().equals("") &&
               !txtPoblacion.getText().equals("") &&
               !txtCodPostal.getText().equals("") &&
               !txtProvinciaEstado.getText().equals("") &&
               !txtPais.getText().equals("");
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
        lblTipoVia = new javax.swing.JLabel();
        cbxTipoVia = new javax.swing.JComboBox<>();
        lblNombreVia = new javax.swing.JLabel();
        txtNombreVia = new javax.swing.JTextField();
        lblNumero = new javax.swing.JLabel();
        txtNumero = new javax.swing.JTextField();
        lblPoblacion = new javax.swing.JLabel();
        txtPoblacion = new javax.swing.JTextField();
        lblCodPostal = new javax.swing.JLabel();
        txtCodPostal = new javax.swing.JTextField();
        lblProvinciaEstado = new javax.swing.JLabel();
        txtProvinciaEstado = new javax.swing.JTextField();
        lblPais = new javax.swing.JLabel();
        txtPais = new javax.swing.JTextField();
        pnlTabla = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblDireccion = new javax.swing.JTable() {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component component = super.prepareRenderer(renderer, row, column);
                int rendererWidth = component.getPreferredSize().width;
                TableColumn tableColumn = getColumnModel().getColumn(column);
                tableColumn.setPreferredWidth(Math.max(rendererWidth + getIntercellSpacing().width, tableColumn.getPreferredWidth()));
                return component;
            }
        };

        setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Dirección", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14))); // NOI18N

        pnlDatos.setMinimumSize(new java.awt.Dimension(450, 160));
        pnlDatos.setName("pnlDatos"); // NOI18N

        lblTipoVia.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblTipoVia.setLabelFor(cbxTipoVia);
        lblTipoVia.setText("Tipo de vía");

        cbxTipoVia.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        cbxTipoVia.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "CALLE", "AVENIDA", "PASEO", "PLAZA", "CARRETERA", "TRAVESÍA" }));

        lblNombreVia.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblNombreVia.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/search 16px.png"))); // NOI18N
        lblNombreVia.setLabelFor(txtNombreVia);
        lblNombreVia.setText("Nombre");
        lblNombreVia.setToolTipText("Campo de búsqueda");
        lblNombreVia.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        lblNombreVia.setName("lblBusqueda"); // NOI18N

        txtNombreVia.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtNombreVia.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtNombreViaKeyReleased(evt);
            }
        });

        lblNumero.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblNumero.setLabelFor(txtNumero);
        lblNumero.setText("Número");

        txtNumero.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtNumero.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtNumeroKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtNumeroKeyTyped(evt);
            }
        });

        lblPoblacion.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblPoblacion.setLabelFor(txtPoblacion);
        lblPoblacion.setText("Población");

        txtPoblacion.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtPoblacion.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtPoblacionKeyReleased(evt);
            }
        });

        lblCodPostal.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblCodPostal.setLabelFor(txtCodPostal);
        lblCodPostal.setText("Cód. Postal");

        txtCodPostal.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtCodPostal.setName(""); // NOI18N
        txtCodPostal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCodPostalKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtCodPostalKeyTyped(evt);
            }
        });

        lblProvinciaEstado.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblProvinciaEstado.setLabelFor(txtProvinciaEstado);
        lblProvinciaEstado.setText("Provincia/Estado");

        txtProvinciaEstado.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtProvinciaEstado.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtProvinciaEstadoKeyReleased(evt);
            }
        });

        lblPais.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblPais.setLabelFor(txtPais);
        lblPais.setText("País");

        txtPais.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtPais.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtPaisKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout pnlDatosLayout = new javax.swing.GroupLayout(pnlDatos);
        pnlDatos.setLayout(pnlDatosLayout);
        pnlDatosLayout.setHorizontalGroup(
            pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDatosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlDatosLayout.createSequentialGroup()
                        .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlDatosLayout.createSequentialGroup()
                                .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblTipoVia)
                                    .addComponent(cbxTipoVia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(pnlDatosLayout.createSequentialGroup()
                                        .addComponent(lblNombreVia)
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addComponent(txtNombreVia)))
                            .addGroup(pnlDatosLayout.createSequentialGroup()
                                .addComponent(lblPoblacion)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(txtPoblacion))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblNumero)
                            .addComponent(txtNumero, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblCodPostal)
                            .addComponent(txtCodPostal)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlDatosLayout.createSequentialGroup()
                        .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblProvinciaEstado)
                            .addComponent(txtProvinciaEstado, javax.swing.GroupLayout.PREFERRED_SIZE, 243, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlDatosLayout.createSequentialGroup()
                                .addComponent(lblPais)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(txtPais))))
                .addContainerGap())
        );

        pnlDatosLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtCodPostal, txtNumero});

        pnlDatosLayout.setVerticalGroup(
            pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDatosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTipoVia)
                    .addComponent(lblNombreVia)
                    .addComponent(lblNumero))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbxTipoVia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtNombreVia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtNumero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPoblacion)
                    .addComponent(lblCodPostal))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCodPostal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPoblacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pnlDatosLayout.createSequentialGroup()
                        .addComponent(lblProvinciaEstado)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtProvinciaEstado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlDatosLayout.createSequentialGroup()
                        .addComponent(lblPais)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtPais, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlTabla.setName("pnlTabla"); // NOI18N

        tblDireccion.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        tblDireccion.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tblDireccion.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblDireccion.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblDireccionMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tblDireccion);

        javax.swing.GroupLayout pnlTablaLayout = new javax.swing.GroupLayout(pnlTabla);
        pnlTabla.setLayout(pnlTablaLayout);
        pnlTablaLayout.setHorizontalGroup(
            pnlTablaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTablaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 545, Short.MAX_VALUE)
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
            .addComponent(pnlTabla, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(pnlDatos, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnlDatos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlTabla, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void tblDireccionMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblDireccionMouseClicked
        // "getSelectedRow()" nos devuelve el índice de la fila seleccionada.
        int fila = tblDireccion.getSelectedRow();
        if (fila != -1) {
            direccion.setIdDireccion(Integer.valueOf(tblDireccion.getModel().getValueAt(fila, 0).toString()));
            // Recuperamos toda la información del objeto "Sucursal" a través
            // de su ID.
            if (ddao.buscarDireccion(direccion.getIdDireccion())) {
                // Fijamos el objeto Sucursal en el panel y lo enviamos
                // al formulario padre.
                setJPAObject(ddao.getDireccion());
                sendJPAObjectToFrmParent();
            }
        }
        inicializarFormulario();
    }//GEN-LAST:event_tblDireccionMouseClicked

    private void txtNumeroKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNumeroKeyTyped
        // Sólo permitimos números y el carácter "."
        Functions.checkNumber(evt);
    }//GEN-LAST:event_txtNumeroKeyTyped

    private void txtCodPostalKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCodPostalKeyTyped
        // Límite de 5 números
        if (txtCodPostal.getText().length() >= 5)
            evt.consume();
        else
            // Si no se ha llegado al límite de carácteres, comprobamos que
            // sólo sean números y el carácter "."
            Functions.checkNumber(evt);
    }//GEN-LAST:event_txtCodPostalKeyTyped

    private void txtNombreViaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNombreViaKeyReleased
        // Filtra la tabla por el nombre de vía introducido.
        mostrarTabla(txtNombreVia.getText());
        // Llamada para intentar habilitar los botones CRUD.
        if (this.frmParent != null) {
            frmParent.tryEnableCRUDButtons();
        }
    }//GEN-LAST:event_txtNombreViaKeyReleased

    private void txtNumeroKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNumeroKeyReleased
        // Llamada para intentar habilitar los botones CRUD.
        if (this.frmParent != null) {
            frmParent.tryEnableCRUDButtons();
        } 
    }//GEN-LAST:event_txtNumeroKeyReleased

    private void txtPoblacionKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPoblacionKeyReleased
        // Llamada para intentar habilitar los botones CRUD.
        if (this.frmParent != null) {
            frmParent.tryEnableCRUDButtons();
        }
    }//GEN-LAST:event_txtPoblacionKeyReleased

    private void txtCodPostalKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCodPostalKeyReleased
        // Llamada para intentar habilitar los botones CRUD.
        if (this.frmParent != null) {
            frmParent.tryEnableCRUDButtons();
        }
    }//GEN-LAST:event_txtCodPostalKeyReleased

    private void txtProvinciaEstadoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtProvinciaEstadoKeyReleased
        // Llamada para intentar habilitar los botones CRUD.
        if (this.frmParent != null) {
            frmParent.tryEnableCRUDButtons();
        }
    }//GEN-LAST:event_txtProvinciaEstadoKeyReleased

    private void txtPaisKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPaisKeyReleased
        // Llamada para intentar habilitar los botones CRUD.
        if (this.frmParent != null) {
            frmParent.tryEnableCRUDButtons();
        }
    }//GEN-LAST:event_txtPaisKeyReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> cbxTipoVia;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblCodPostal;
    private javax.swing.JLabel lblNombreVia;
    private javax.swing.JLabel lblNumero;
    private javax.swing.JLabel lblPais;
    private javax.swing.JLabel lblPoblacion;
    private javax.swing.JLabel lblProvinciaEstado;
    private javax.swing.JLabel lblTipoVia;
    private javax.swing.JPanel pnlDatos;
    private javax.swing.JPanel pnlTabla;
    private javax.swing.JTable tblDireccion;
    private javax.swing.JTextField txtCodPostal;
    private javax.swing.JTextField txtNombreVia;
    private javax.swing.JTextField txtNumero;
    private javax.swing.JTextField txtPais;
    private javax.swing.JTextField txtPoblacion;
    private javax.swing.JTextField txtProvinciaEstado;
    // End of variables declaration//GEN-END:variables
}
