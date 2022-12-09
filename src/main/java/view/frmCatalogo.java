package view;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import model.DAO.CatalogoDAO;
import model.entity.Catalogo;
import utilities.Constants.PanelMode;
import utilities.Functions;
import utilities.UpcaseFilter;

/**
 * Formulario para la gestión de Catalogoes y sus direcciones.
 * 
 * @author Ignacio Pineda Martín
 */
public class frmCatalogo extends frmAppSoftBank {

    private CatalogoDAO cdao;
    private Catalogo catalogo = new Catalogo();

    /**
     * Creates new form frmCatalogo
     */
    public frmCatalogo() {
        super();
        initComponents();
        // Título del formulario.
        this.setTitle("Catálogo");
        // Lo posicionamos en el centro de la pantalla.
        this.setLocationRelativeTo(null);
        // Asignamos este formulario como "padre" en los paneles.
        pnlCatalogo.setFrmParent(this);
        // Ponemos todos los JTextField en modo mayúsculas.
        UpcaseFilter.setFieldsToUpperCase(this);
        // Creamos los DAO's con su EntityManagerFactory.
        cdao = new CatalogoDAO(emf);
        // Iniciamos los componentes del formulario base
        // IMPORTANTE: esta llamada debe ser siempre la última del
        // contructor.
        super.inicializarComponentes();
    }
    
    @Override
    public void setFormMode(PanelMode formMode) {
        super.setFormMode(formMode);
        this.formMode = formMode;
        switch (this.formMode) {
            case CRUD:

                break;
            case SELECTION:
                pnlCatalogo.setPanelMode(PanelMode.SELECTION);
                break;
            case READ_ONLY:
                pnlCatalogo.setPanelMode(PanelMode.READ_ONLY);
                break;
            default:
                throw new AssertionError();
        }
        this.pack();       
    }
  
    @Override
    public void setJPAObject(Object jpaObject) {
        this.jpaObject = jpaObject;
        if (this.jpaObject instanceof Catalogo c) {
            this.catalogo = c;
            pnlCatalogo.setJPAObject(c);
        }
    }

    @Override
    protected void enableCRUDButtons() {
        super.enableCRUDButtons();
        if (formMode == PanelMode.CRUD) {
            if (this.catalogo.getIdCatalogo() == null) {
                btnModificar.setEnabled(false);
                btnEliminar.setEnabled(false);
            }
        }
    }     
    
    @Override
    protected void btnLimpiarActionPerformed(java.awt.event.ActionEvent evt) {                                           
        super.btnLimpiarActionPerformed(evt);
        // Limpiamos los paneles de "Catalogo" y "Dirección".
        // El último panel que se limpie será el que reciba el foco
        // del usuario en primer lugar.       
        pnlCatalogo.limpiarPanel();
    }                                          

    @Override    
    protected void btnInsertarActionPerformed(java.awt.event.ActionEvent evt) {                                            
        super.btnInsertarActionPerformed(evt);
        // Recuperamos los valores introducidos en los paneles.
        catalogo = pnlCatalogo.getJPAObject();
        
        // Comprobación prevía de datos clave de el producto.
        if (!checkProducto(true))
            return;
        
        // Del objeto Catalogo, limpiamos las relaciones que podrían haber
        // quedado guardadas.
        catalogo.setProductoBancarioList(null);
        
        // Creamos el producto.
        if (cdao.insertarCatalogo(catalogo)) {
            // Mostramos el mensaje resultante de la inserción.
            String mensaje = String.format("Catalogo %s - %s insertado correctamente.",
                    catalogo.getCodProducto(),
                    catalogo.getNombreProducto());
            setStatusBarText(mensaje);
            JOptionPane.showMessageDialog(this, mensaje, this.getTitle(), JOptionPane.INFORMATION_MESSAGE);
            // Refrescamos los paneles.
            pnlCatalogo.mostrarTabla("");
            pnlCatalogo.inicializarFormulario();
            // Refrescamos los objetos de los paneles.
            catalogo = cdao.getCatalogo();
            pnlCatalogo.setJPAObject(catalogo);
        } else {
            // Error inesperado.
            setStatusBarText(cdao.getMensaje());
            JOptionPane.showMessageDialog(this, cdao.getMensaje(), this.getTitle(), JOptionPane.ERROR_MESSAGE);
        }
    }                                           

    @Override    
    protected void btnModificarActionPerformed(java.awt.event.ActionEvent evt) {                                             
        super.btnModificarActionPerformed(evt);
        // Recuperamos los valores introducidos en los paneles.
        catalogo = pnlCatalogo.getJPAObject();

        // Comprobamos que podemos hacer modificaciones.
        if (catalogo.getIdCatalogo() == null || catalogo.getIdCatalogo() == 0) {
            JOptionPane.showMessageDialog(this,
                    "Debes elegir un producto ya existente para modificarlo.",
                    this.getTitle(), JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Comprobación prevía de datos clave de el producto.
        if (!checkProducto(false))
            return;        
        
        // Modificamos la catalogo.
        if (cdao.actualizarCatalogo(catalogo)) {
            // Mostramos el mensaje resultante de la modificación.
            String mensaje = String.format("Catalogo %s - %s modificado correctamente.",
                    catalogo.getCodProducto(),
                    catalogo.getNombreProducto());
            setStatusBarText(mensaje);
            JOptionPane.showMessageDialog(this, mensaje, this.getTitle(), JOptionPane.INFORMATION_MESSAGE);
            // Refrescamos los paneles.
            pnlCatalogo.mostrarTabla("");
            pnlCatalogo.inicializarFormulario();
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
        catalogo = pnlCatalogo.getJPAObject();

        // Comprobamos que podemos hacer eliminaciones.
        if (catalogo.getIdCatalogo() == null || catalogo.getIdCatalogo() == 0) {
            JOptionPane.showMessageDialog(this,
                    "Debes elegir un Catalogo para eliminarlo.",
                    this.getTitle(), JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Comprobamos que la catalogo no está asociada a un producto bancario.
        if (!catalogo.getProductoBancarioList().isEmpty()) {
            setStatusBarText(String.format("ERROR - Catalogo %s - %s NO eliminada.",
                    catalogo.getCodProducto(),
                    catalogo.getNombreProducto()));
            JOptionPane.showMessageDialog(this,
                    String.format("El catalogo %s - %s tiene productos asignados y no puede borrarse.",
                            catalogo.getCodProducto(),
                            catalogo.getNombreProducto()),
                    this.getTitle(), JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Eliminamos la catalogo.
        if (cdao.eliminarCatalogo(catalogo)) {
            // Limpiamos el formulario (hacer antes de poner Status).
            btnLimpiar.doClick();
            // Mostramos el mensaje resultante de la eliminación.
            String mensaje = String.format("Catalogo %s - %s eliminado correctamente.",
                    catalogo.getCodProducto(),
                    catalogo.getNombreProducto());
            setStatusBarText(mensaje);
            JOptionPane.showMessageDialog(this, mensaje, this.getTitle(), JOptionPane.INFORMATION_MESSAGE);            
        } else {
            // Error inesperado.
            setStatusBarText(cdao.getMensaje());
            JOptionPane.showMessageDialog(this, cdao.getMensaje(), this.getTitle(), JOptionPane.ERROR_MESSAGE);            
        }
    }

    /**
     * Comprobación de que el código de producto no existe.
     * 
     * @param insertar <b>true</b> para comprobar cuando vamos a insertar una
     * nueva producto.
     * <br><b>false</b> para comprobar en cuando vamos a modificar un producto.
     * 
     * @return <b>true</b> El código de producto no existe.
     *     <br><b>false</b> El código de producto ya existe.
     */     
    private boolean checkProducto(boolean insertar) {
        // Si vamos a insertar un producto, debemos inicializar los ID's
        // para hacer una correcta validación.
        if (insertar) {
            this.catalogo.setIdCatalogo(null);
        }

        // Comprobamos que el producto no exista.
        if (!pnlCatalogo.checkProductoNoExiste()) {
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

        pnlCatalogo = new view.pnlCatalogo(emf);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setIconImage(new ImageIcon(Functions.getImage("icons/SoftBank 16px.png")).getImage());
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.Y_AXIS));
        getContentPane().add(pnlCatalogo);

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
            java.util.logging.Logger.getLogger(frmCatalogo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(frmCatalogo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(frmCatalogo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(frmCatalogo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new frmCatalogo().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private view.pnlCatalogo pnlCatalogo;
    // End of variables declaration//GEN-END:variables
}
