package view;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import model.DAO.DireccionDAO;
import model.DAO.SucursalDAO;
import model.entity.Direccion;
import model.entity.Sucursal;
import utilities.Constants.PanelMode;
import utilities.Functions;
import utilities.UpcaseFilter;

/**
 * Formulario para la gestión de Sucursales y sus direcciones.
 * 
 * @author Ignacio Pineda Martín
 */
public class frmSucursal extends frmAppSoftBank {

    private SucursalDAO sdao;
    private DireccionDAO ddao;
    private Sucursal sucursal = new Sucursal();
    private Direccion direccion = new Direccion();

    /**
     * Creates new form frmSucursal
     */
    public frmSucursal() {
        super();
        initComponents();
        // Título del formulario.
        this.setTitle("Sucursales");
        // Lo posicionamos en el centro de la pantalla.
        this.setLocationRelativeTo(null);
        // Asignamos este formulario como "padre" en los paneles.
        pnlSucursal.setFrmParent(this);
        pnlDireccion.setFrmParent(this);
        // Ponemos todos los JTextField en modo mayúsculas.
        UpcaseFilter.setFieldsToUpperCase(this);
        // Creamos los DAO's con su EntityManagerFactory.
        sdao = new SucursalDAO(emf);
        ddao = new DireccionDAO(emf);
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
                pnlSucursal.setPanelMode(PanelMode.SELECTION);
                pnlDireccion.setPanelMode(PanelMode.READ_ONLY);
                break;
            case READ_ONLY:
                pnlSucursal.setPanelMode(PanelMode.READ_ONLY);
                pnlDireccion.setPanelMode(PanelMode.READ_ONLY);
                break;
            default:
                throw new AssertionError();
        }
        this.pack();       
    }
  
    @Override
    public void setJPAObject(Object jpaObject) {
        this.jpaObject = jpaObject;
        if (this.jpaObject instanceof Sucursal s) {
            this.sucursal = s;
            pnlSucursal.setJPAObject(s);
            pnlDireccion.setJPAObject(s.getDireccion());
        }
        if (this.jpaObject instanceof Direccion d) {
            this.sucursal.setDireccion(d);
        }        
    }

    @Override
    protected void enableCRUDButtons() {
        super.enableCRUDButtons();
        if (formMode == PanelMode.CRUD) {
            if (this.sucursal.getIdSucursal() == null) {
                btnModificar.setEnabled(false);
                btnEliminar.setEnabled(false);
            }
        }
    }  
    
    @Override
    protected void btnLimpiarActionPerformed(java.awt.event.ActionEvent evt) {                                           
        super.btnLimpiarActionPerformed(evt);
        // Limpiamos los paneles de "Sucursal" y "Dirección".
        // El último panel que se limpie será el que reciba el foco
        // del usuario en primer lugar.
        pnlDireccion.limpiarPanel();        
        pnlSucursal.limpiarPanel();
    }                                          

    @Override    
    protected void btnInsertarActionPerformed(java.awt.event.ActionEvent evt) {                                            
        super.btnInsertarActionPerformed(evt);
        // Recuperamos los valores introducidos en los paneles.
        sucursal = pnlSucursal.getJPAObject();
        direccion = pnlDireccion.getJPAObject();
        
        // Comprobación prevía de datos clave de la sucursal.
        if (!checkSucursal(true))
            return;
        
        // Del objeto Direccion, limpiamos las relaciones que podrían haber
        // quedado guardadas.
        direccion.setClienteList(null);
        direccion.setSucursalList(null);        
        
        // Si la dirección es nueva, se da de alta antes que la sucursal.
        if (direccion.getIdDireccion() == null) {
            if (ddao.insertarDireccion(direccion)) {
                sucursal.setDireccion(ddao.getDireccion());
            } else {
                setStatusBarText(ddao.getMensaje());
                JOptionPane.showMessageDialog(this, ddao.getMensaje(), this.getTitle(), JOptionPane.ERROR_MESSAGE);
            }
        } else {
            // Si la dirección no es nueva, se asigna directamente a 
            // la sucursal.
            sucursal.setDireccion(direccion);
        }

        // Del objeto Sucursal, limpiamos las relaciones que podrían haber
        // quedado guardadas.
        sucursal.setEmpleadoList(null);
        sucursal.setProductoBancarioList(null);
        
        // Creamos la sucursal.
        if (sdao.insertarSucursal(sucursal)) {
            // Mostramos el mensaje resultante de la inserción.
            String mensaje = String.format("Sucursal %s - %s insertada correctamente.",
                    sucursal.getCodSucursal(),
                    sucursal.getNombreSucursal());
            setStatusBarText(mensaje);
            JOptionPane.showMessageDialog(this, mensaje, this.getTitle(), JOptionPane.INFORMATION_MESSAGE);
            // Refrescamos los paneles.
            pnlSucursal.mostrarTabla("");
            pnlSucursal.inicializarFormulario();
            pnlDireccion.mostrarTabla("");
            // Refrescamos los objetos de los paneles.
            sucursal = sdao.getSucursal();
            pnlSucursal.setJPAObject(sucursal);
            pnlDireccion.setJPAObject(sucursal.getDireccion());           
        }
    }                                           

    @Override    
    protected void btnModificarActionPerformed(java.awt.event.ActionEvent evt) {                                             
        super.btnModificarActionPerformed(evt);
        // Recuperamos los valores introducidos en los paneles.
        sucursal = pnlSucursal.getJPAObject();
        direccion = pnlDireccion.getJPAObject();

        // Comprobamos que podemos hacer modificaciones.
        if (sucursal.getIdSucursal() == null || sucursal.getIdSucursal() == 0) {
            JOptionPane.showMessageDialog(this,
                    "Debes elegir una Sucursal para modificarla.",
                    this.getTitle(), JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Comprobación prevía de datos clave de la sucursal.
        if (!checkSucursal(false))
            return;
        
        // Asignamos la dirección a la sucursal.
        sucursal.setDireccion(direccion);

        // Modificamos la sucursal.
        if (sdao.actualizarSucursal(sucursal)) {
            // Mostramos el mensaje resultante de la modificación.
            String mensaje = String.format("Sucursal %s - %s modificada correctamente.",
                    sucursal.getCodSucursal(),
                    sucursal.getNombreSucursal());
            setStatusBarText(mensaje);
            JOptionPane.showMessageDialog(this, mensaje, this.getTitle(), JOptionPane.INFORMATION_MESSAGE);
            // Refrescamos los paneles.
            pnlSucursal.mostrarTabla("");
            pnlSucursal.inicializarFormulario();
            pnlDireccion.mostrarTabla("");
        }
    }                                            

    @Override
    protected void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {                                            
        super.btnEliminarActionPerformed(evt);
        // Recuperamos los valores introducidos en los paneles.
        sucursal = pnlSucursal.getJPAObject();

        // Comprobamos que podemos hacer eliminaciones.
        if (sucursal.getIdSucursal() == null || sucursal.getIdSucursal() == 0) {
            JOptionPane.showMessageDialog(this,
                    "Debes elegir una Sucursal para eliminarlo.",
                    this.getTitle(), JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Comprobamos que la sucursal no está asociada a un empleado y/o
        // producto bancario.
        if (!sucursal.getEmpleadoList().isEmpty()) {
            setStatusBarText(String.format("ERROR - Sucursal %s - %s NO eliminada.",
                    sucursal.getCodSucursal(),
                    sucursal.getNombreSucursal()));
            JOptionPane.showMessageDialog(this,
                    String.format("La sucursal %s - %s tiene empleados asignados y no puede borrarse.",
                            sucursal.getCodSucursal(),
                            sucursal.getNombreSucursal()),
                    this.getTitle(), JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!sucursal.getProductoBancarioList().isEmpty()) {
            setStatusBarText(String.format("ERROR - Sucursal %s - %s NO eliminada.",
                    sucursal.getCodSucursal(),
                    sucursal.getNombreSucursal()));
            JOptionPane.showMessageDialog(this,
                    String.format("La sucursal %s - %s tiene productos asignados y no puede borrarse.",
                            sucursal.getCodSucursal(),
                            sucursal.getNombreSucursal()),
                    this.getTitle(), JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Eliminamos la sucursal.
        if (sdao.eliminarSucursal(sucursal)) {
            // Limpiamos el formulario (hacer antes de poner Status).
            btnLimpiar.doClick();
            // Mostramos el mensaje resultante de la eliminación.
            String mensaje = String.format("Sucursal %s - %s eliminada correctamente.",
                    sucursal.getCodSucursal(),
                    sucursal.getNombreSucursal());
            setStatusBarText(mensaje);
            JOptionPane.showMessageDialog(this, mensaje, this.getTitle(), JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Comprobación de que el código de sucursal no existe.
     * 
     * @param insertar <b>true</b> para comprobar cuando vamos a insertar una
     * nueva sucursal.
     * <br><b>false</b> para comprobar en cuando vamos a modificar una sucursal.
     * 
     * @return <b>true</b> El código de sucursal no existe.
     *     <br><b>false</b> El código de sucursal ya existe.
     */     
    private boolean checkSucursal(boolean insertar) {
        // Si vamos a insertar una sucursal, debemos inicializar los ID's
        // para hacer una correcta validación.
        if (insertar) {
            this.sucursal.setIdSucursal(null);
        }

        // Comprobamos que la sucursal no exista.
        if (!pnlSucursal.checkSucursalNoExiste()) {
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

        pnlSucursal = new view.pnlSucursal(this.emf);
        pnlDireccion = new view.pnlDireccion(this.emf);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setIconImage(new ImageIcon(Functions.getImage("icons/SoftBank 16px.png")).getImage());
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.Y_AXIS));

        pnlSucursal.setMinimumSize(new java.awt.Dimension(500, 200));
        getContentPane().add(pnlSucursal);

        pnlDireccion.setMinimumSize(new java.awt.Dimension(500, 200));
        getContentPane().add(pnlDireccion);

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
            java.util.logging.Logger.getLogger(frmSucursal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(frmSucursal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(frmSucursal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(frmSucursal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new frmSucursal().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private view.pnlDireccion pnlDireccion;
    private view.pnlSucursal pnlSucursal;
    // End of variables declaration//GEN-END:variables
}
