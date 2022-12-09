package view;

import java.awt.Component;
import javax.persistence.EntityManagerFactory;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import model.DAO.ProductoBancarioDAO;
import model.entity.Cliente;
import model.entity.ProductoBancario;
import utilities.Constants.FormAction;
import utilities.Constants.PanelMode;

/**
 * Panel de datos para la clase {@link ProductoBancario}, derivado de la clase
 * {@link pnlAppSoftBank}.
 *
 * @author Ignacio Pineda Martín
 */
public class pnlProductoBancario extends pnlAppSoftBank {
    
    private Cliente cliente;
    
    protected ProductoBancarioDAO pdao;
    protected ProductoBancario productoBancario = new ProductoBancario();

    /**
     * Creates new form pnlProductoBancario
     */
    public pnlProductoBancario() {
        initComponents();
    }
    
    public pnlProductoBancario(EntityManagerFactory emf) {
        this();
        this.jpaObject = productoBancario;
        this.emf = emf;
        // Creamos el DAO con su EntityManagerFactory.
        pdao = new ProductoBancarioDAO(emf);
        // Preparamos el panel en su estado inicial.
        limpiarPanel();
        // Hacemos que la tabla no sea editable.
        tblProductoBancario.setDefaultEditor(Object.class, null);        
    }
    
    /**
     * Tratamiento para el objecto de tipo {@link ProductoBancario} o de tipo
     * {@link Cliente} recibidos.
     *
     * @param jpaObject El objeto recibido.
     */
    @Override
    public void setJPAObject(Object jpaObject) {
        this.jpaObject = jpaObject;
        if (this.jpaObject instanceof ProductoBancario p) {
            this.productoBancario = p;
            rellenarCampos();
        }
        // Si se recibe un cliente en el panel, es para mostrar la lista de
        // productos bancarios asociados al cliente.
        if (this.jpaObject instanceof Cliente c) {
            // ¡¡¡ IMPORTANTE !!! Debemos limpiar el panel antes de asignar
            // el objeto cliente recibido.
            limpiarPanel();
            this.cliente = c;
            // Si el cliente tiene productos bancarios, seleccionamos el 
            // primero que no esté cancelado.
            if (!cliente.getProductoBancarioList().isEmpty()) {
                for (ProductoBancario pb : cliente.getProductoBancarioList()) {
                    if (pb.getFecCancelacion() == null) {
                        this.productoBancario = cliente.getProductoBancarioList().get(0);
                        rellenarCampos();
                        mostrarTabla();
                        break;
                    }
                }
            }
        }        
    }
    
    public ProductoBancario getJPAObject() {
        recogerCampos();
        return this.productoBancario;
    }
    
    @Override
    public void sendJPAObjectToFrmParent() {
        if (this.frmParent != null) {
            frmParent.setJPAObject(this.productoBancario);            
        }        
    }
    
    /**
     * Método para limpiar el panel al estado inicial. Necesario para ser
     * invocado desde el botón "Limpiar" del formulario contenedor.
     */
    protected void limpiarPanel() {
        vaciarCampos();
        mostrarTabla();
        inicializarFormulario();
    }
    
    /**
     * Método para inicializar los campos del formulario.
     */
    protected void vaciarCampos() {
        productoBancario.setIdProductoBancario(null);
        jdcFecApertura.setDate(new java.sql.Date(System.currentTimeMillis()));
        jdcFecApertura.setMaxSelectableDate(new java.sql.Date(System.currentTimeMillis()));
        jdcFecCancelacion.setDate(null);
        jdcFecCancelacion.setMaxSelectableDate(new java.sql.Date(System.currentTimeMillis()));
        // Si el panel está dentro de la pantalla de cliente, la fecha de 
        // apertura no debe mostrar ningún valor por defecto. 
        // Además, inicializamos la propiedad "cliente".
        if ((this.frmParent != null) && (this.frmParent instanceof frmCliente)) {
            jdcFecApertura.setDate(null);
            cliente = null;
        }
    }
    
    /**
     * Método para rellenar los campos del formulario con la propiedad
     * {@link productoBancario}
     */
    protected void rellenarCampos() {
        jdcFecApertura.setDate(productoBancario.getFecApertura());
        jdcFecCancelacion.setDate(productoBancario.getFecCancelacion());
    }
 
    /**
     * Método para recoger los campos del formulario en la propiedad
     * {@link productoBancario}
     */    
    protected void recogerCampos() {
        productoBancario.setFecApertura(jdcFecApertura.getDate());
        productoBancario.setFecCancelacion(jdcFecCancelacion.getDate());
    }

    /**
     * Método para preparar el formulario en un estado inicial.
     */
    protected void inicializarFormulario() {
        jdcFecApertura.requestFocus();
        // Llamada para intentar habilitar los botones CRUD.
        if (this.frmParent != null) {
            frmParent.tryEnableCRUDButtons();
        }        
    }

    /**
     * Método para rellenar la tabla del panel con los productos asociados a un
     * cliente.
     */
    protected void mostrarTabla() {
        if (cliente != null) {
            pdao.listarProductoBancario(tblProductoBancario, this.cliente);
        } else {
            ((DefaultTableModel) tblProductoBancario.getModel()).setNumRows(0);
        }
    }
    
    @Override
    protected boolean checkRequiredFields() {
        // Si el panel está dentro de la pantalla de cliente, la validacion
        // de campos no debe realizarse.
        if ((this.frmParent != null) && (this.frmParent instanceof frmCliente)) {
            return true;
        } else {
            return (jdcFecApertura.getDate() != null)
                    && (jdcFecApertura.getDate().compareTo(jdcFecApertura.getMaxSelectableDate()) <= 0) // menor o igual que fecha máxima.
                    && ((jdcFecCancelacion.getDate() == null) || (jdcFecCancelacion.getDate().compareTo(jdcFecCancelacion.getMaxSelectableDate()) <= 0));
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
        lblFecApertura = new javax.swing.JLabel();
        jdcFecApertura = new com.toedter.calendar.JDateChooser();
        lblFecCancelacion = new javax.swing.JLabel();
        jdcFecCancelacion = new com.toedter.calendar.JDateChooser();
        tlbAcciones = new javax.swing.JToolBar();
        tlbAcciones.setFloatable(false);
        tlbAcciones.setVisible(false);
        btnEliminar = new javax.swing.JButton();
        pnlTabla = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblProductoBancario = new javax.swing.JTable() {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component component = super.prepareRenderer(renderer, row, column);
                int rendererWidth = component.getPreferredSize().width;
                TableColumn tableColumn = getColumnModel().getColumn(column);
                tableColumn.setPreferredWidth(Math.max(rendererWidth + getIntercellSpacing().width, tableColumn.getPreferredWidth()));
                return component;
            }
        };

        setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Producto Bancario", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14))); // NOI18N

        pnlDatos.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        pnlDatos.setName("pnlDatos"); // NOI18N

        lblFecApertura.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblFecApertura.setText("Fecha apertura");

        jdcFecApertura.setDateFormatString("dd/MM/yyyy");
        jdcFecApertura.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        lblFecCancelacion.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblFecCancelacion.setText("Fecha cancelación");

        jdcFecCancelacion.setDateFormatString("dd/MM/yyyy");
        jdcFecCancelacion.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        tlbAcciones.setRollover(true);
        tlbAcciones.setName("tlbAcciones"); // NOI18N

        btnEliminar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/delete 16px.png"))); // NOI18N
        btnEliminar.setToolTipText("Eliminar cliente");
        btnEliminar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnEliminar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarActionPerformed(evt);
            }
        });
        tlbAcciones.add(btnEliminar);

        javax.swing.GroupLayout pnlDatosLayout = new javax.swing.GroupLayout(pnlDatos);
        pnlDatos.setLayout(pnlDatosLayout);
        pnlDatosLayout.setHorizontalGroup(
            pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDatosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblFecApertura, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jdcFecApertura, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jdcFecCancelacion, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblFecCancelacion))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(tlbAcciones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pnlDatosLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jdcFecCancelacion, lblFecCancelacion});

        pnlDatosLayout.setVerticalGroup(
            pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDatosLayout.createSequentialGroup()
                .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlDatosLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblFecApertura)
                            .addComponent(lblFecCancelacion)))
                    .addComponent(tlbAcciones, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(pnlDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jdcFecApertura, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jdcFecCancelacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlTabla.setName("pnlTabla"); // NOI18N
        pnlTabla.setPreferredSize(new java.awt.Dimension(465, 126));

        tblProductoBancario.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        tblProductoBancario.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tblProductoBancario.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblProductoBancario.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblProductoBancarioMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tblProductoBancario);

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
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 123, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlTabla, javax.swing.GroupLayout.DEFAULT_SIZE, 398, Short.MAX_VALUE)
            .addComponent(pnlDatos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(pnlDatos, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlTabla, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

    protected void tblProductoBancarioMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblProductoBancarioMouseClicked
        // En este evento mostramos, en un formulario separado de CONSULTA, la
        // información completa del producto elegido.
        Integer idProductoBancario;
        // "getSelectedRow()" nos devuelve el índice de la fila seleccionada.
        int fila = tblProductoBancario.getSelectedRow();
        if (fila != -1) {
            idProductoBancario = Integer.valueOf(tblProductoBancario.getModel().getValueAt(fila, 0).toString());
            // Recuperamos toda la información del objeto "Cliente" a través
            // de su ID.
            if (pdao.buscarProductoBancario(idProductoBancario)) {
                this.setJPAObject(pdao.getProductoBancario());
                // Llamamos al formulario que pueda mostrar el producto 
                // seleccionado.
                if (pdao.getProductoBancario().getCuentaCorriente() != null) {
                    frmCuentaCorriente fcc = new frmCuentaCorriente();
                    fcc.setAppUser(getAppUser());
                    fcc.setFormModeAndAction(PanelMode.READ_ONLY, FormAction.QUERY);
                    fcc.setJPAObject(this.productoBancario);
                    fcc.setVisible(true);
                }
                if (pdao.getProductoBancario().getTarjetaCredito() != null) {
                    frmTarjetaCredito ftc = new frmTarjetaCredito();
                    ftc.setAppUser(getAppUser());                    
                    ftc.setFormModeAndAction(PanelMode.READ_ONLY, FormAction.QUERY);
                    ftc.setJPAObject(this.productoBancario);
                    ftc.setVisible(true);
                }
                if (pdao.getProductoBancario().getPrestamo()!= null) {
                    frmPrestamo fp = new frmPrestamo();
                    fp.setAppUser(getAppUser());                    
                    fp.setFormModeAndAction(PanelMode.READ_ONLY, FormAction.QUERY);
                    fp.setJPAObject(this.productoBancario);
                    fp.setVisible(true);
                }
                if (pdao.getProductoBancario().getAval()!= null) {
                    // TODO Visualización de Avales.
                }
            } else {
                System.out.println("ERROR INESPERADO");
            }
        }
        inicializarFormulario();
    }//GEN-LAST:event_tblProductoBancarioMouseClicked

    protected void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed
        // *** NO ELIMINAR ESTE EVENTO ***
    }//GEN-LAST:event_btnEliminarActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected javax.swing.JButton btnEliminar;
    private javax.swing.JScrollPane jScrollPane2;
    protected com.toedter.calendar.JDateChooser jdcFecApertura;
    protected com.toedter.calendar.JDateChooser jdcFecCancelacion;
    protected javax.swing.JLabel lblFecApertura;
    protected javax.swing.JLabel lblFecCancelacion;
    protected javax.swing.JPanel pnlDatos;
    protected javax.swing.JPanel pnlTabla;
    protected javax.swing.JTable tblProductoBancario;
    protected javax.swing.JToolBar tlbAcciones;
    // End of variables declaration//GEN-END:variables
}
