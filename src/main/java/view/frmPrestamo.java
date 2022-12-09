package view;

//<editor-fold defaultstate="collapsed" desc=" Librerías importadas... ">
import java.math.BigDecimal;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import model.DAO.CatalogoDAO;
import model.DAO.ClienteDAO;
import model.DAO.MovimientoDAO;
import model.DAO.PrestamoDAO;
import model.DAO.ProductoBancarioDAO;

import model.entity.Catalogo;
import model.entity.Cliente;
import model.entity.Movimiento;
import model.entity.ProductoBancario;
import model.entity.Sucursal;
import model.entity.Prestamo;
import model.entity.Usuario;

import utilities.Constants.FormAction;
import utilities.Constants.PanelMode;
import utilities.Functions;
import utilities.UpcaseFilter;
//</editor-fold>

/**
 * Formulario para la gestión de Sucursals.
 * 
 * @author Ignacio Pineda Martín
 */
public final class frmPrestamo extends frmAppSoftBank {

    private CatalogoDAO cadao;
    private ClienteDAO cdao;
    private MovimientoDAO mdao;
    private ProductoBancarioDAO pbdao;
    private PrestamoDAO pdao;
    private Catalogo catalogo = new Catalogo();
    private Sucursal sucursal = new Sucursal();
    private Prestamo prestamo = new Prestamo();
    private ProductoBancario productoBancario = new ProductoBancario();

    /**
     * Creates new form frmPrestamo
     */
    public frmPrestamo() {
        super();
        initComponents();
        // Título del formulario.
        this.setTitle("Préstamos");
        // Lo posicionamos en el centro de la pantalla.
        this.setLocationRelativeTo(null);
        // Asignamos este formulario como "padre" en los paneles.
        // *** Puede hacerse en el constructor de los objetos. ***
        pnlCatalogo.setFrmParent(this);
        pnlSucursal.setFrmParent(this);
        pnlCliente.setFrmParent(this);        
        pnlProductoBancario.setFrmParent(this);
        pnlPrestamo.setFrmParent(this);
        pnlMovimiento.setFrmParent(this);
        // Ponemos todos los JTextField en modo mayúsculas de los paneles
        // "Catalogo", "Sucursal", "Cliente" y "ProductoBancario".
        UpcaseFilter.setFieldsToUpperCase(pnlCatalogo);
        UpcaseFilter.setFieldsToUpperCase(pnlSucursal);
        UpcaseFilter.setFieldsToUpperCase(pnlCliente);
        UpcaseFilter.setFieldsToUpperCase(pnlProductoBancario);
        UpcaseFilter.setFieldsToUpperCase(pnlPrestamo);        
        UpcaseFilter.setFieldsToUpperCase(pnlMovimiento);
        // Creamos los DAO's con su EntityManagerFactory.
        cadao = new CatalogoDAO(emf);
        pbdao = new ProductoBancarioDAO(emf);
        cdao = new ClienteDAO(emf);
        mdao = new MovimientoDAO(emf);
        pdao = new PrestamoDAO(emf);
        // Ponemos la pantalla en modo "Préstamos".
        this.setJPAObject(cadao.getPrestamo());
        // Iniciamos los componentes del formulario base
        // IMPORTANTE: esta llamada debe ser siempre la última del
        // contructor.
        super.inicializarComponentes();
    }

    @Override
    public void setAppUser(Usuario appUser) {
        super.setAppUser(appUser);
        pnlCliente.setAppUser(appUser);
        pnlProductoBancario.setAppUser(appUser);
    }
    
    /**
     * Asignación simultánea de {@link formMode} y {@link formAction}
     *
     * @param formMode {@link formMode} del formulario.
     * @param formAction {@link formAction} del formulario.
     */
    public void setFormModeAndAction(PanelMode formMode, FormAction formAction) {
        this.formMode = formMode;
        this.formAction = formAction;
        setFormMode(formMode);
        setFormAction(formAction);
    }
    
    @Override
    public void setFormMode(PanelMode formMode) {
        super.setFormMode(formMode);
        this.formMode = formMode;
        switch (this.formMode) {
            case CRUD:
                pnlCatalogo.setPanelMode(PanelMode.READ_ONLY);
                pnlSucursal.setPanelMode(PanelMode.SELECTION);
                pnlCliente.setPanelMode(PanelMode.SELECTION);
                pnlProductoBancario.setPanelMode(PanelMode.READ_ONLY);
                pnlPrestamo.setPanelMode(PanelMode.CRUD);
                pnlMovimiento.setPanelMode(PanelMode.READ_ONLY);
                break;
            case SELECTION:
                pnlCatalogo.setPanelMode(PanelMode.READ_ONLY);
                pnlSucursal.setPanelMode(PanelMode.READ_ONLY);
                pnlCliente.setPanelMode(PanelMode.SELECTION);
                pnlProductoBancario.setPanelMode(PanelMode.READ_ONLY);
                pnlPrestamo.setPanelMode(PanelMode.SELECTION);
                pnlMovimiento.setPanelMode(PanelMode.CRUD);
                break;
            case READ_ONLY:
                pnlCatalogo.setPanelMode(PanelMode.READ_ONLY);
                pnlSucursal.setPanelMode(PanelMode.READ_ONLY);
                pnlProductoBancario.setPanelMode(PanelMode.READ_ONLY);
                pnlCliente.setPanelMode(PanelMode.READ_ONLY);
                pnlPrestamo.setPanelMode(PanelMode.READ_ONLY);
                pnlMovimiento.setPanelMode(PanelMode.SELECTION);
                break;
            default:
                throw new AssertionError();
        }
        // Accesos rápidos de información adicional.
        pnlSucursal.tlbAccesorios.setVisible(true);
        pnlCliente.tlbAccesorios.setVisible(true);
        pnlPrestamo.tlbAccesorios.setVisible(true);
        this.pack();   
    }

    @Override
    public void setFormAction(FormAction formAction) {
        this.formAction = formAction;
        switch (this.formAction) {
            case NEW:
                this.setTitle("Préstamos (Alta)");

                pnlCliente.tlbAcciones.setVisible(true);

                pnlProductoBancario.tlbAcciones.setVisible(true);
                pnlProductoBancario.pnlTabla.setVisible(true);

                pnlPrestamo.pnlTabla.setVisible(false);
                pnlPrestamo.txtNumeroPrestamo.setFocusable(false);
                pnlPrestamo.txtFecVencimiento.setFocusable(false);

                pnlMovimiento.setVisible(false);

                // Hacemos invisibles los botones del panel inferior que
                // no nos interesan.
                btnModificar.setVisible(false);
                btnEliminar.setVisible(false);
                break;
            case CANCEL:
                this.setTitle("Préstamos (Cancelación)");

                pnlProductoBancario.pnlTabla.setVisible(true);

                pnlPrestamo.txtImpConcedido.setFocusable(false);
                pnlPrestamo.spnPlazoPrestamo.setEnabled(false);
                pnlPrestamo.txtFecVencimiento.setFocusable(false);
                pnlPrestamo.txtTipoInteres.setFocusable(false);
                pnlPrestamo.cbxCuentaCorrientePago.setEnabled(false);
                pnlPrestamo.cbxTipoGarantia.setEnabled(false);

                pnlMovimiento.setVisible(false);

                // Hacemos visibles los botones del panel inferior que
                // nos interesan.
                btnEliminar.setVisible(true);
                btnEliminar.setText("Cancelar");
                pnlStatusBar.setVisible(true);
                break;
            case MODIFY:
                this.setTitle("Préstamos (Modificación)");

                pnlProductoBancario.pnlTabla.setVisible(true);

                pnlPrestamo.txtImpConcedido.setFocusable(false);
                pnlPrestamo.spnPlazoPrestamo.setEnabled(false);
                pnlPrestamo.txtFecVencimiento.setFocusable(false);

                pnlMovimiento.setVisible(false);

                // Hacemos visibles los botones del panel inferior que
                // nos interesan.
                btnModificar.setVisible(true);
                pnlStatusBar.setVisible(true);
                break;

            case MOVEMENT:
                this.setTitle("Préstamos (Movimientos)");

                pnlProductoBancario.pnlTabla.setVisible(true);

                pnlPrestamo.txtImpConcedido.setFocusable(false);
                pnlPrestamo.spnPlazoPrestamo.setEnabled(false);
                pnlPrestamo.txtFecVencimiento.setFocusable(false);
                pnlPrestamo.txtTipoInteres.setFocusable(false);
                pnlPrestamo.cbxCuentaCorrientePago.setEnabled(false);
                pnlPrestamo.cbxTipoGarantia.setEnabled(false);

                pnlMovimiento.setVisible(true);

                pnlStatusBar.setVisible(true);
                break;           
                
            case QUERY:
                this.setTitle("Préstamos (Visualización)");

                pnlProductoBancario.pnlTabla.setVisible(true);

                pnlMovimiento.setVisible(true);
                pnlMovimiento.tlbAcciones.setVisible(true);
                break;
            default:
                throw new AssertionError();
        }
        this.pack();
    }

    @Override
    public void setJPAObject(Object jpaObject) {
        this.jpaObject = jpaObject;
        
        if (this.jpaObject instanceof ProductoBancario pb) {
            this.productoBancario = pb;
            pnlCatalogo.setJPAObject(pb.getCatalogo());
            pnlSucursal.setJPAObject(pb.getSucursal());            
            pnlProductoBancario.setJPAObject(pb);
            pnlCliente.setJPAObject(pb.getClienteList().get(0));
            pnlPrestamo.setJPAObject(pb.getPrestamo());
            pnlMovimiento.setJPAObject(pb);
        }        
        if (this.jpaObject instanceof Catalogo ca) {
            this.catalogo = ca;
            this.productoBancario.setCatalogo(ca);
            pnlCatalogo.setJPAObject(ca);
        }
        if (this.jpaObject instanceof Sucursal s) {
            this.sucursal = s;
            this.productoBancario.setSucursal(s);
        }
        if (this.jpaObject instanceof Cliente c) {
            pnlPrestamo.setJPAObject(c);
        }
        if (this.jpaObject instanceof Prestamo p) {
            this.prestamo = p;
            this.productoBancario.setPrestamo(p);
        }        
    }

    @Override
    protected void enableCRUDButtons() {
        super.enableCRUDButtons();
        if (formMode == PanelMode.CRUD) {
            if (this.productoBancario.getIdProductoBancario() == null) {
                btnModificar.setEnabled(false);
                btnEliminar.setEnabled(false);
            }
        }
        if (formMode == PanelMode.SELECTION) {
            switch (this.formAction) {
                case CANCEL:
                    btnEliminar.setEnabled(true);
                    break;
                case MODIFY:
                    btnModificar.setEnabled(true);
                    break;
            }
        }
    }
    
    @Override
    protected void disableCRUDButtons() {
        super.disableCRUDButtons();
        if (formMode == PanelMode.SELECTION) {
            btnModificar.setEnabled(false);
            btnEliminar.setEnabled(false);
        }
    }
    
    @Override
    protected void btnLimpiarActionPerformed(java.awt.event.ActionEvent evt) {                                           
        super.btnLimpiarActionPerformed(evt);
        // Limpiamos los paneles de "Sucursal", "Cliente", "ProductoBancario" y 
        // "Prestamo".
        // El último panel que se limpie será el que reciba el foco
        // en primer lugar.
        pnlSucursal.limpiarPanel();
        pnlPrestamo.limpiarPanel();
        pnlProductoBancario.limpiarPanel();
        pnlCliente.limpiarPanel();
        pnlMovimiento.limpiarPanel();
    }                                          

    @Override    
    protected void btnInsertarActionPerformed(java.awt.event.ActionEvent evt) {                                            
        super.btnInsertarActionPerformed(evt);
        // Recuperamos los valores introducidos en los paneles.
        catalogo = pnlCatalogo.getJPAObject();
        sucursal = pnlSucursal.getJPAObject();
        productoBancario = pnlProductoBancario.getJPAObject();
        prestamo = pnlPrestamo.getJPAObject();

        // Asignamos el tipo de producto (catálogp) y la sucursal.
        productoBancario.setCatalogo(catalogo);
        productoBancario.setSucursal(sucursal);

        // Del objeto ProductoBancario, limpiamos las relaciones que no
        // nos interesan.
        productoBancario.setAval(null);
        productoBancario.setCuentaCorriente(null);
        productoBancario.setPrestamo(null);
        productoBancario.setTarjetaCredito(null);

        // Creamos el producto bancario antes de insertar el préstamo.
        if (pbdao.insertarProductoBancario(productoBancario)) {
            // Asignamos al préstamo el producto bancario creado.
            prestamo.setProductoBancario(pbdao.getProductoBancario());
        } else {
            setStatusBarText(pbdao.getMensaje());
            JOptionPane.showMessageDialog(this, pdao.getMensaje(), this.getTitle(), JOptionPane.ERROR_MESSAGE);
        }

        // Creamos el préstamo.
        if (pdao.insertarPrestamo(prestamo)) {
            // Componemos el mensaje resultante de la creación.            
            String mensaje = String.format("Préstamo nº %s creado correctamente.",
                    prestamo.getNumeroPrestamo());
            
            // Creamos el movimiento inicial de cargo en préstamo.
            insertarMovimiento(prestamo.getProductoBancario().getFecApertura(), "PRÉSTAMO GAR. " + prestamo.getTipoGarantia() + " CONCEDIDO", prestamo.getImpConcedido(), prestamo.getProductoBancario());
            // Creamos el movimiento de abono en cuenta corriente.
            insertarMovimiento(prestamo.getProductoBancario().getFecApertura(), "PRÉSTAMO GAR. " + prestamo.getTipoGarantia() + " CONCEDIDO", (prestamo.getImpConcedido().multiply(new BigDecimal(-1))), prestamo.getCuentaCorrientePago().getProductoBancario());

            // Refrescamos los objetos de los paneles.
            pnlPrestamo.setJPAObject(prestamo);

            // Refrescamos los paneles.
            pnlCliente.inicializarFormulario();

            // Desactivamos el botón "Insertar".
            btnInsertar.setEnabled(false);

            // Mostramos el mensaje resultante de la creación.
            setStatusBarText(mensaje);
            JOptionPane.showMessageDialog(this, mensaje, this.getTitle(), JOptionPane.INFORMATION_MESSAGE);
        } else {
            // Error inesperado.
            setStatusBarText(pdao.getMensaje());
            JOptionPane.showMessageDialog(this, pdao.getMensaje(), this.getTitle(), JOptionPane.ERROR_MESSAGE);
        }
    }         

    private void insertarMovimiento(Date fecMovimiento, String concepto, BigDecimal impMovimiento, ProductoBancario pb) {
        // Comprobamos que el movimiento no sea de importe cero.
        if (!impMovimiento.equals(BigDecimal.ZERO)) {
            // Creamos el movimiento con la información recibida y lo grabamos.
            Movimiento movimiento = new Movimiento(null, fecMovimiento, concepto, impMovimiento);
            movimiento.setProductoBancario(pb);
            if (!mdao.insertarMovimiento(movimiento)) {
                String mensaje = String.format("Error al grabar el movimiento %s %s %s",
                        Functions.formatDate(fecMovimiento), concepto, Functions.formatAmount(impMovimiento));
                setStatusBarText(mensaje);
                JOptionPane.showMessageDialog(this, mensaje, this.getTitle(), JOptionPane.ERROR_MESSAGE);
            }
        }
    } 
    
    @Override    
    protected void btnModificarActionPerformed(java.awt.event.ActionEvent evt) {                                             
        super.btnModificarActionPerformed(evt);
        // Recuperamos los valores introducidos en los paneles.
        productoBancario = pnlProductoBancario.getJPAObject();
        prestamo = pnlPrestamo.getJPAObject();
        
        // Modificamos el préstamo (tipo de interes, tipo de garantía, cuenta
        // corriente de cargo).
        if (pdao.actualizarPrestamo(prestamo)) {
            // Componemos el mensaje resultante de la modificación.
            String mensaje = String.format("Préstamo nº %s modificado correctamente.",
                    prestamo.getNumeroPrestamo());

            // Refrescamos los objetos de los paneles.
            pnlPrestamo.setJPAObject(prestamo);

            // Desactivamos el botón "Modificar".
            btnModificar.setEnabled(false);

            // Mostramos el mensaje resultante de la modificación.
            setStatusBarText(mensaje);
            JOptionPane.showMessageDialog(this, mensaje, this.getTitle(), JOptionPane.INFORMATION_MESSAGE);
        } else {
            // Error inesperado.
            setStatusBarText(cdao.getMensaje());
            JOptionPane.showMessageDialog(this, cdao.getMensaje(), this.getTitle(), JOptionPane.ERROR_MESSAGE);
        }
    }                                            

    @Override
    protected void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {                                            
        super.btnEliminarActionPerformed(evt);
        // Recuperamos los valores introducidos en los paneles.
        productoBancario = pnlProductoBancario.getJPAObject();
        prestamo = pnlPrestamo.getJPAObject();

        // Comprobación de que el préstamo es cancelable.
        boolean cancelable = true;
        String mensaje = "";
        
        // Comprobamos que el saldo del préstamo es cero.
        if (prestamo.getImpSaldoPendiente().compareTo(BigDecimal.ZERO) != 0) {
            mensaje = mensaje + "- El préstamo no tiene su saldo a 0.\n";
            cancelable = false;
        }
        
        if (!cancelable) {
            mensaje = "El préstamo " + prestamo.getNumeroPrestamo() + " no puede cancelarse porque:\n" + mensaje;
            setStatusBarText(String.format("El Préstamo nº %s no puede cancelarse.",
                    prestamo.getNumeroPrestamo()));
            JOptionPane.showMessageDialog(this, mensaje, this.getTitle(), JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Asignamos la fecha de hoy como fecha de cancelación del préstamo.
        productoBancario.setFecCancelacion(new java.sql.Date(System.currentTimeMillis()));
        // Modificamos el producto bancario.
        if (pbdao.actualizarProductoBancario(productoBancario)) {
            // Componemos el mensaje resultante de la cancelación.
            mensaje = String.format("Préstamo nº %s cancelado correctamente.",
                    prestamo.getNumeroPrestamo());

            // Refrescamos los objetos de los paneles.
            pnlProductoBancario.setJPAObject(productoBancario);
            pnlPrestamo.setJPAObject(prestamo);

            // Desactivamos el botón "Eliminar".
            // (Se mostrará como "Cancelar" en pantalla).
            btnEliminar.setEnabled(false);

            // Mostramos el mensaje resultante de la cancelación.
            setStatusBarText(mensaje);
            JOptionPane.showMessageDialog(this, mensaje, this.getTitle(), JOptionPane.INFORMATION_MESSAGE);
        } else {
            // Error inesperado.
            setStatusBarText(cdao.getMensaje());
            JOptionPane.showMessageDialog(this, cdao.getMensaje(), this.getTitle(), JOptionPane.ERROR_MESSAGE);
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
        java.awt.GridBagConstraints gridBagConstraints;

        pnlContenedor = new javax.swing.JPanel();
        pnlCatalogo = new view.pnlCatalogo(this.emf);
        pnlSucursal = new view.pnlSucursal(this.emf);
        pnlCliente = new view.pnlClienteCC(this.emf);
        pnlProductoBancario = new view.pnlProductoBancarioCC(this.emf);
        pnlPrestamo = new view.pnlPrestamoPT(this.emf);
        pnlMovimiento = new view.pnlMovimiento(this.emf);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setIconImage(new ImageIcon(Functions.getImage("icons/SoftBank 16px.png")).getImage());

        pnlContenedor.setLayout(new java.awt.GridBagLayout());

        pnlCatalogo.setMinimumSize(new java.awt.Dimension(258, 90));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        pnlContenedor.add(pnlCatalogo, gridBagConstraints);

        pnlSucursal.setMinimumSize(new java.awt.Dimension(242, 90));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        pnlContenedor.add(pnlSucursal, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        pnlContenedor.add(pnlCliente, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        pnlContenedor.add(pnlProductoBancario, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        pnlContenedor.add(pnlPrestamo, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlContenedor.add(pnlMovimiento, gridBagConstraints);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnlContenedor, javax.swing.GroupLayout.DEFAULT_SIZE, 970, Short.MAX_VALUE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlContenedor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(frmPrestamo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(frmPrestamo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(frmPrestamo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(frmPrestamo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new frmPrestamo().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private view.pnlCatalogo pnlCatalogo;
    protected view.pnlClienteCC pnlCliente;
    private javax.swing.JPanel pnlContenedor;
    private view.pnlMovimiento pnlMovimiento;
    protected view.pnlPrestamoPT pnlPrestamo;
    protected view.pnlProductoBancarioCC pnlProductoBancario;
    private view.pnlSucursal pnlSucursal;
    // End of variables declaration//GEN-END:variables
}
