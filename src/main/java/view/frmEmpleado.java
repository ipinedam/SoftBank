package view;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import model.DAO.EmpleadoDAO;
import model.DAO.UsuarioDAO;
import model.DAO.SucursalDAO;
import model.entity.Empleado;
import model.entity.Usuario;
import model.entity.Sucursal;
import utilities.Constants.PanelMode;
import utilities.Functions;
import utilities.UpcaseFilter;

/**
 * Formulario para la gestión de Empleados.
 * 
 * @author Ignacio Pineda Martín
 */
public class frmEmpleado extends frmAppSoftBank {

    private EmpleadoDAO edao;
    private SucursalDAO sdao;
    private UsuarioDAO udao;
    private Empleado empleado = new Empleado();
    private Sucursal sucursal = new Sucursal();
    private Usuario usuario = new Usuario();

    /**
     * Creates new form frmEmpleado
     */
    public frmEmpleado() {
        super();
        initComponents();
        // Título del formulario.
        this.setTitle("Empleados");
        // Lo posicionamos en el centro de la pantalla.
        this.setLocationRelativeTo(null);
        // Asignamos este formulario como "padre" en los paneles.
        pnlEmpleado.setFrmParent(this);
        pnlSucursal.setFrmParent(this);
        pnlUsuario.setFrmParent(this);
        // Ponemos todos los JTextField en modo mayúsculas de los paneles
        // "Empleado" y "Sucursal".
        UpcaseFilter.setFieldsToUpperCase(pnlEmpleado);
        UpcaseFilter.setFieldsToUpperCase(pnlSucursal);
        // Creamos los DAO's con su EntityManagerFactory.
        edao = new EmpleadoDAO(emf);
        sdao = new SucursalDAO(emf);
        udao = new UsuarioDAO(emf);
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

                pnlSucursal.setPanelMode(PanelMode.SELECTION);                
                break;
            case SELECTION:
                pnlEmpleado.setPanelMode(PanelMode.SELECTION);                        
                pnlSucursal.setPanelMode(PanelMode.READ_ONLY);
                pnlUsuario.setPanelMode(PanelMode.READ_ONLY);
                break;
            case READ_ONLY:
                pnlEmpleado.setPanelMode(PanelMode.READ_ONLY);                 
                pnlSucursal.setPanelMode(PanelMode.READ_ONLY);
                pnlUsuario.setPanelMode(PanelMode.READ_ONLY);
                break;
            default:
                throw new AssertionError();
        }
        // Accesos rápidos de información adicional.        
        pnlSucursal.tlbAccesorios.setVisible(true);
        this.pack();      
    }
  
    @Override
    public void setJPAObject(Object jpaObject) {
        this.jpaObject = jpaObject;
        
        if (this.jpaObject instanceof Empleado e) {
            this.empleado = e;
            pnlEmpleado.setJPAObject(e);
            pnlSucursal.setJPAObject(e.getSucursal());
            pnlUsuario.setJPAObject(e.getUsuario());
        }        
        if (this.jpaObject instanceof Sucursal s) {
            this.sucursal = s;
            this.empleado.setSucursal(s);
        }
        if (this.jpaObject instanceof Usuario u) {
            this.empleado.setUsuario(u);
        }        
    }

    @Override
    protected void enableCRUDButtons() {
        super.enableCRUDButtons();
        if (formMode == PanelMode.CRUD) {
            if (this.empleado.getIdEmpleado() == null) {
                btnModificar.setEnabled(false);
                btnEliminar.setEnabled(false);
            }
        }
    }

    @Override
    protected void btnLimpiarActionPerformed(java.awt.event.ActionEvent evt) {                                           
        super.btnLimpiarActionPerformed(evt);
        // Limpiamos los paneles de "Empleado", "Sucursal" y "Usuario".
        // El último panel que se limpie será el que reciba el foco
        // del usuario en primer lugar.
        pnlUsuario.limpiarPanel();
        pnlSucursal.limpiarPanel();
        pnlEmpleado.limpiarPanel();
    }                                          

    @Override    
    protected void btnInsertarActionPerformed(java.awt.event.ActionEvent evt) {                                            
        super.btnInsertarActionPerformed(evt);
        // Recuperamos los valores introducidos en los paneles.
        empleado = pnlEmpleado.getJPAObject();
        sucursal = pnlSucursal.getJPAObject();
        usuario = pnlUsuario.getJPAObject();
        
        // Comprobación prevía de datos clave del empleado.
        if (!checkEmpleado(true))
            return;

        // Del objeto Sucursal, limpiamos las relaciones que podrían haber
        // quedado guardadas.
        sucursal.setEmpleadoList(null);
        sucursal.setProductoBancarioList(null);
        // Asignamos la sucursal al empleado.
        empleado.setSucursal(sucursal);
        
        // Del objeto Usuario, limpiamos las relaciones que podrían haber
        // quedado guardadas.
        usuario.setCliente(null);
        usuario.setEmpleado(null);
        
        // Creamos el usuario y lo asignamos al empleado.
        if (udao.insertarUsuario(usuario)) {
            empleado.setUsuario(udao.getUsuario());
        } else {
            // Ha ocurrido un error inesperado.
            setStatusBarText(String.format("ERROR - Empleado %s %s NO insertado.",
                    empleado.getNombreEmpleado(),
                    empleado.getApellidosEmpleado()));
            JOptionPane.showMessageDialog(this,
                    String.format("El usuario %s no ha podido ser creado. ERROR: %s",
                            usuario.getCodUsuario(),
                            udao.getMensaje()),
                    this.getTitle(), JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Del objeto Empleado, limpiamos las relaciones que podrían haber
        // quedado guardadas.
        empleado.setClienteList(null);

        // Creamos el empleado.
        if (edao.insertarEmpleado(empleado)) {
            // Mostramos el mensaje resultante de la inserción.
            String mensaje = String.format("Empleado %s %s insertado correctamente.",
                    empleado.getNombreEmpleado(),
                    empleado.getApellidosEmpleado());
            setStatusBarText(mensaje);
            JOptionPane.showMessageDialog(this, mensaje, this.getTitle(), JOptionPane.INFORMATION_MESSAGE);
            // Refrescamos los objetos de los paneles.
            empleado = edao.getEmpleado();
            pnlEmpleado.setJPAObject(empleado);
            pnlSucursal.setJPAObject(empleado.getSucursal());
            pnlUsuario.setJPAObject(empleado.getUsuario());
            
            // Refrescamos los paneles.
            pnlEmpleado.mostrarTabla("");
            pnlEmpleado.inicializarFormulario();
            pnlSucursal.mostrarTabla("");            
        } else {
            // Error inesperado.
            setStatusBarText(edao.getMensaje());
            JOptionPane.showMessageDialog(this, edao.getMensaje(), this.getTitle(), JOptionPane.ERROR_MESSAGE);
        }
    }                                           

    @Override    
    protected void btnModificarActionPerformed(java.awt.event.ActionEvent evt) {                                             
        super.btnModificarActionPerformed(evt);
        // Recuperamos los valores introducidos en los paneles.
        empleado = pnlEmpleado.getJPAObject();
        sucursal = pnlSucursal.getJPAObject();
        usuario = pnlUsuario.getJPAObject();

        // Comprobamos que podemos hacer modificaciones.
        if (empleado.getIdEmpleado() == null || empleado.getIdEmpleado() == 0) {
            JOptionPane.showMessageDialog(this,
                    "Debes elegir un Empleado ya existente para modificarlo.",
                    this.getTitle(), JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Comprobación prevía de datos clave del empleado.
        if (!checkEmpleado(false))
            return;
        
        // Asignamos la sucursal y el usuario al empleado.
        empleado.setSucursal(sucursal);
        empleado.setUsuario(usuario);

        // Modificamos el empleado.
        if (edao.actualizarEmpleado(empleado)) {
            // Mostramos el mensaje resultante de la modificación.
            String mensaje = String.format("Empleado %s %s modificado correctamente.",
                    empleado.getNombreEmpleado(),
                    empleado.getApellidosEmpleado());
            setStatusBarText(mensaje);
            JOptionPane.showMessageDialog(this, mensaje, this.getTitle(), JOptionPane.INFORMATION_MESSAGE);
            // Refrescamos los paneles.
            pnlEmpleado.mostrarTabla("");
            pnlEmpleado.inicializarFormulario();
            pnlSucursal.mostrarTabla("");
        } else {
            // Error inesperado.
            setStatusBarText(edao.getMensaje());
            JOptionPane.showMessageDialog(this, edao.getMensaje(), this.getTitle(), JOptionPane.ERROR_MESSAGE);
        }
    }                                            

    @Override
    protected void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {                                            
        super.btnEliminarActionPerformed(evt);
        // Recuperamos los valores introducidos en los paneles.
        empleado = pnlEmpleado.getJPAObject();

        // Comprobamos que podemos hacer eliminaciones.
        if (empleado.getIdEmpleado() == null || empleado.getIdEmpleado() == 0) {
            JOptionPane.showMessageDialog(this,
                    "Debes elegir un Empleado para eliminarlo.",
                    this.getTitle(), JOptionPane.ERROR_MESSAGE);
            return;
        }        
        
        // Comprobamos que el empleado no este asociado a ningún cliente.
        if (!empleado.getClienteList().isEmpty()) {
            setStatusBarText(String.format("ERROR - Empleado %s %s NO eliminado.",
                    empleado.getNombreEmpleado(),
                    empleado.getApellidosEmpleado()));
            JOptionPane.showMessageDialog(this,
                    String.format("El empleado %s %s tiene clientes asignados y no puede borrarse.",
                            empleado.getNombreEmpleado(),
                            empleado.getApellidosEmpleado()),
                    this.getTitle(), JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Eliminamos el empleado.
        if (edao.eliminarEmpleado(empleado)) {
            // Limpiamos el formulario (hacer antes de poner Status).       
            btnLimpiar.doClick();
            // Mostramos el mensaje resultante de la eliminación.
            String mensaje = String.format("Empleado %s %s eliminado correctamente.",
                    empleado.getNombreEmpleado(),
                    empleado.getApellidosEmpleado());
            setStatusBarText(mensaje);
            JOptionPane.showMessageDialog(this, mensaje, this.getTitle(), JOptionPane.INFORMATION_MESSAGE);
        } else {
            // Error inesperado.
            setStatusBarText(edao.getMensaje());
            JOptionPane.showMessageDialog(this, edao.getMensaje(), this.getTitle(), JOptionPane.ERROR_MESSAGE);
        }
    }
   
    /**
     * Comprobación de que el nombre del empleado y/o el usuario elegido 
     * no existen.
     * 
     * @param insertar <b>true</b> para comprobar cuando vamos a insertar un
     * nuevo empleado.
     * <br><b>false</b> para comprobar en cuando vamos a modificar un empleado.
     * 
     * @return <b>true</b> Ni el nombre del empleado ni el usuario existen.
     *     <br><b>false</b> El nombre del empleado y/o el usuario ya existen.
     */ 
    private boolean checkEmpleado(boolean insertar) {
        // Si vamos a insertar un empleado, debemos inicializar los ID's
        // para hacer una correcta validación.
        if (insertar)  {
            this.empleado.setIdEmpleado(null);
            this.usuario.setIdUsuario(null);
        } 
        
        // Comprobamos que el empleado no exista (comprobación mediante el
        // nombre y los apellidos).
        if (!pnlEmpleado.checkEmpleadoNoExiste()) {
            return false;
        }
        // Comprobamos que el usuario introducido no exista.      
        if (!pnlUsuario.checkCodUsuarioNoExiste()) {
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

        pnlEmpleado = new view.pnlEmpleado(this.emf);
        pnlSucursal = new view.pnlSucursal(this.emf);
        pnlUsuario = new view.pnlUsuario(this.emf);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setIconImage(new ImageIcon(Functions.getImage("icons/SoftBank 16px.png")).getImage());
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.Y_AXIS));
        getContentPane().add(pnlEmpleado);

        pnlSucursal.setMinimumSize(new java.awt.Dimension(500, 200));
        getContentPane().add(pnlSucursal);
        getContentPane().add(pnlUsuario);

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
            java.util.logging.Logger.getLogger(frmEmpleado.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(frmEmpleado.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(frmEmpleado.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(frmEmpleado.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new frmEmpleado().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private view.pnlEmpleado pnlEmpleado;
    private view.pnlSucursal pnlSucursal;
    private view.pnlUsuario pnlUsuario;
    // End of variables declaration//GEN-END:variables
}
