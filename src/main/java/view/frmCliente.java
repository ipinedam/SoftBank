package view;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import model.DAO.ClienteDAO;
import model.DAO.EmpleadoDAO;
import model.DAO.UsuarioDAO;
import model.DAO.DireccionDAO;
import model.entity.Cliente;
import model.entity.Empleado;
import model.entity.Usuario;
import model.entity.Direccion;
import utilities.Constants.PanelMode;
import utilities.Functions;
import utilities.UpcaseFilter;

/**
 * Formulario para la gestión de Empleados.
 * 
 * @author Ignacio Pineda Martín
 */
public class frmCliente extends frmAppSoftBank {

    private ClienteDAO cdao;
    private DireccionDAO ddao;
    private EmpleadoDAO edao;
    private UsuarioDAO udao;
    private Cliente cliente = new Cliente();
    private Direccion direccion = new Direccion();
    private Empleado empleado = new Empleado();
    private Usuario usuario = new Usuario();

    /**
     * Creates new form frmEmpleado
     */
    public frmCliente() {
        super();
        initComponents();
        // Título del formulario.
        this.setTitle("Clientes");
        // Lo posicionamos en el centro de la pantalla.
        this.setLocationRelativeTo(null);
        // Asignamos este formulario como "padre" en los paneles.
        pnlCliente.setFrmParent(this);
        pnlDireccion.setFrmParent(this);
        pnlEmpleado.setFrmParent(this);
        pnlUsuario.setFrmParent(this);
        pnlProductoBancario.setFrmParent(this);
        // Ponemos todos los JTextField en modo mayúsculas de los paneles
        // "Empleado" y "Direccion".
        UpcaseFilter.setFieldsToUpperCase(pnlCliente);
        UpcaseFilter.setFieldsToUpperCase(pnlDireccion);
        UpcaseFilter.setFieldsToUpperCase(pnlEmpleado);        
        UpcaseFilter.setFieldsToUpperCase(pnlProductoBancario); 
        // Creamos los DAO's con su EntityManagerFactory.
        cdao = new ClienteDAO(emf);
        ddao = new DireccionDAO(emf);
        edao = new EmpleadoDAO(emf);        
        udao = new UsuarioDAO(emf);
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
    
    @Override
    public void setFormMode(PanelMode formMode) {
        super.setFormMode(formMode);
        this.formMode = formMode;
        switch (this.formMode) {
            case CRUD:

                pnlDireccion.setPanelMode(PanelMode.SELECTION);
                pnlEmpleado.setPanelMode(PanelMode.SELECTION);
                break;
            case SELECTION:
                pnlCliente.setPanelMode(PanelMode.SELECTION);
                pnlDireccion.setPanelMode(PanelMode.READ_ONLY);
                pnlEmpleado.setPanelMode(PanelMode.READ_ONLY);
                pnlUsuario.setPanelMode(PanelMode.READ_ONLY);
                pnlProductoBancario.setPanelMode(PanelMode.READ_ONLY);
                pnlProductoBancario.setVisible(true);
                pnlProductoBancario.pnlTabla.setVisible(true);
                pnlProductoBancario.pnlTabla.setEnabled(true);
                btnLimpiar.doClick();
                break;
            case READ_ONLY:
                pnlCliente.setPanelMode(PanelMode.READ_ONLY);
                pnlDireccion.setPanelMode(PanelMode.READ_ONLY);
                pnlEmpleado.setPanelMode(PanelMode.READ_ONLY);
                pnlUsuario.setPanelMode(PanelMode.READ_ONLY);
                pnlProductoBancario.setPanelMode(PanelMode.READ_ONLY);
                pnlProductoBancario.setVisible(true);
                pnlProductoBancario.pnlTabla.setVisible(true);
                pnlProductoBancario.pnlTabla.setEnabled(true);            
                break;
            default:
                throw new AssertionError();
        }
        // Accesos rápidos de información adicional.        
        pnlEmpleado.tlbAccesorios.setVisible(true);        
        this.pack();       
    }
  
    @Override
    public void setJPAObject(Object jpaObject) {
        this.jpaObject = jpaObject;
        
        if (this.jpaObject instanceof Cliente c) {
            this.cliente = c;
            pnlCliente.setJPAObject(c);
            pnlDireccion.setJPAObject(c.getDireccion());
            pnlEmpleado.setJPAObject(c.getEmpleado());            
            pnlUsuario.setJPAObject(c.getUsuario());
            pnlProductoBancario.setJPAObject(c);
        }        
        if (this.jpaObject instanceof Direccion s) {
            this.direccion = s;
            this.cliente.setDireccion(s);
        }
        if (this.jpaObject instanceof Empleado e) {
            this.empleado = e;
            this.cliente.setEmpleado(e);
        }
        if (this.jpaObject instanceof Usuario u) {
            this.usuario = u;
            this.cliente.setUsuario(u);
        }        
    }

    @Override
    protected void enableCRUDButtons() {
        super.enableCRUDButtons();
        if (formMode == PanelMode.CRUD) {
            if (this.cliente.getIdCliente() == null) {
                btnModificar.setEnabled(false);
                btnEliminar.setEnabled(false);
            }
        }
    }

    @Override
    protected void btnLimpiarActionPerformed(java.awt.event.ActionEvent evt) {                                           
        super.btnLimpiarActionPerformed(evt);
        // Limpiamos los paneles de "Cliente", "Direccion", "Empleado" y 
        // "Usuario".
        // El último panel que se limpie será el que reciba el foco
        // del usuario en primer lugar.
        pnlProductoBancario.limpiarPanel();        
        pnlUsuario.limpiarPanel();
        pnlEmpleado.limpiarPanel();
        pnlDireccion.limpiarPanel();
        pnlCliente.limpiarPanel();
    }                                          

    @Override    
    protected void btnInsertarActionPerformed(java.awt.event.ActionEvent evt) {                                            
        super.btnInsertarActionPerformed(evt);
        // Recuperamos los valores introducidos en los paneles.
        cliente = pnlCliente.getJPAObject();
        direccion = pnlDireccion.getJPAObject();
        empleado = pnlEmpleado.getJPAObject();
        usuario = pnlUsuario.getJPAObject();
   
        // Comprobación prevía de datos clave del cliente.
        if (!checkCliente(true))
            return;
        
        // Del objeto Direccion, limpiamos las relaciones que podrían haber
        // quedado guardadas.
        direccion.setClienteList(null);
        direccion.setSucursalList(null);

        // Si la dirección es nueva, se da de alta antes que el cliente.
        if (direccion.getIdDireccion() == null) {
            if (ddao.insertarDireccion(direccion)) {
                cliente.setDireccion(ddao.getDireccion());
            } else {
                setStatusBarText(ddao.getMensaje());
                JOptionPane.showMessageDialog(this, ddao.getMensaje(), this.getTitle(), JOptionPane.ERROR_MESSAGE);
            }
        } else {
            // Si la dirección no es nueva, se asigna directamente a 
            // el cliente.
            cliente.setDireccion(direccion);
        }

        // Del objeto Empleado, limpiamos las relaciones que podrían haber
        // quedado guardadas.
        empleado.setClienteList(null);
        cliente.setEmpleado(empleado);
        
        // Del objeto Usuario, limpiamos las relaciones que podrían haber
        // quedado guardadas.
        usuario.setCliente(null);
        usuario.setEmpleado(null);
        
        // Creamos el usuario y lo asignamos al cliente.
        if (udao.insertarUsuario(usuario)) {
            cliente.setUsuario(udao.getUsuario());
        } else {
            // Ha ocurrido un error inesperado.
            setStatusBarText(String.format("ERROR - Cliente %s %s NO insertado.",
                    cliente.getNombreCliente(),
                    cliente.getApellidosCliente()));
            JOptionPane.showMessageDialog(this,
                    String.format("El usuario %s no ha podido ser creado. ERROR: %s",
                            usuario.getCodUsuario(),
                            udao.getMensaje()),
                    this.getTitle(), JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Del objeto Cliente, limpiamos las relaciones que podrían haber
        // quedado guardadas.
        cliente.setProductoBancarioList(null);

        // Creamos el cliente.
        if (cdao.insertarCliente(cliente)) {
            // Mostramos el mensaje resultante de la inserción.
            String mensaje = String.format("Cliente %s %s insertado correctamente.",
                    cliente.getNombreCliente(),
                    cliente.getApellidosCliente());
            setStatusBarText(mensaje);
            JOptionPane.showMessageDialog(this, mensaje, this.getTitle(), JOptionPane.INFORMATION_MESSAGE);
            // Refrescamos los objetos de los paneles.
            cliente = cdao.getCliente();
            pnlDireccion.setJPAObject(cliente.getDireccion());
            pnlEmpleado.setJPAObject(cliente.getEmpleado());
            pnlUsuario.setJPAObject(cliente.getUsuario());
            
            // Refrescamos los paneles.
            pnlEmpleado.mostrarTabla("");
            pnlDireccion.mostrarTabla("");
            pnlCliente.mostrarTabla("");
            pnlCliente.inicializarFormulario();
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
        cliente = pnlCliente.getJPAObject();
        direccion = pnlDireccion.getJPAObject();
        empleado = pnlEmpleado.getJPAObject();
        usuario = pnlUsuario.getJPAObject();

        // Comprobamos que podemos hacer modificaciones.
        if (cliente.getIdCliente() == null || cliente.getIdCliente() == 0) {
            JOptionPane.showMessageDialog(this,
                    "Debes elegir un Cliente ya existente para modificarlo.",
                    this.getTitle(), JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Comprobación prevía de datos clave del cliente.
        if (!checkCliente(false))
            return;
        
        // Asignamos la direccion y el usuario al cliente.
        cliente.setDireccion(direccion);
        cliente.setEmpleado(empleado);
        cliente.setUsuario(usuario);

        // Modificamos el cliente.
        if (cdao.actualizarCliente(cliente)) {
            // Mostramos el mensaje resultante de la modificación.
            String mensaje = String.format("Cliente %s %s modificado correctamente.",
                    cliente.getNombreCliente(),
                    cliente.getApellidosCliente());
            setStatusBarText(mensaje);
            JOptionPane.showMessageDialog(this, mensaje, this.getTitle(), JOptionPane.INFORMATION_MESSAGE);            
            // Refrescamos los paneles.
            pnlEmpleado.mostrarTabla("");
            pnlDireccion.mostrarTabla("");
            pnlCliente.mostrarTabla("");
            pnlCliente.inicializarFormulario();
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
        cliente = pnlCliente.getJPAObject();

        // Comprobamos que podemos hacer eliminaciones.
        if (cliente.getIdCliente() == null || cliente.getIdCliente() == 0) {
            JOptionPane.showMessageDialog(this,
                    "Debes elegir un Cliente para eliminarlo.",
                    this.getTitle(), JOptionPane.ERROR_MESSAGE);
            return;
        }        
        
        // Comprobamos que el cliente no este asociado a ningún producto 
        // bancario.
        if (!cliente.getProductoBancarioList().isEmpty()) {
            setStatusBarText(String.format("ERROR - Cliente %s %s NO eliminado.",
                    cliente.getNombreCliente(),
                    cliente.getApellidosCliente()));
            JOptionPane.showMessageDialog(this,
                    String.format("El cliente %s %s tiene productos asignados y no puede borrarse.",
                            cliente.getNombreCliente(),
                            cliente.getApellidosCliente()),
                    this.getTitle(), JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Eliminamos el cliente.
        if (cdao.eliminarCliente(cliente)) {
            // Limpiamos el formulario (hacer antes de poner Status).       
            btnLimpiar.doClick();
            // Mostramos el mensaje resultante de la eliminación.
            String mensaje = String.format("Cliente %s %s eliminado correctamente.",
                    cliente.getNombreCliente(),
                    cliente.getApellidosCliente());
            setStatusBarText(mensaje);
            JOptionPane.showMessageDialog(this, mensaje, this.getTitle(), JOptionPane.INFORMATION_MESSAGE);
        } else {
            // Error inesperado.
            setStatusBarText(cdao.getMensaje());
            JOptionPane.showMessageDialog(this, cdao.getMensaje(), this.getTitle(), JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Comprobación de que el documento de identificación del cliente y/o el
     * usuario elegido no existen.
     * 
     * @param insertar <b>true</b> para comprobar cuando vamos a insertar un
     * nuevo cliente.
     * <br><b>false</b> para comprobar en cuando vamos a modificar un cliente.
     * 
     * @return <b>true</b> Ni el documento ni el usuario existen.
     *     <br><b>false</b> El documento y/o el usuario ya existen.
     */ 
    private boolean checkCliente(boolean insertar) {
        // Si vamos a insertar un cliente, debemos inicializar los ID's
        // para hacer una correcta validación.
        if (insertar)  {
            this.cliente.setIdCliente(null);
            this.usuario.setIdUsuario(null);
        }        
        
        // Comprobamos que el cliente no exista (comprobación mediante la
        // clave de identificación).
        if (!pnlCliente.checkClaveIdentificacionNoExiste()) {
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
        java.awt.GridBagConstraints gridBagConstraints;

        pnlContenedor = new javax.swing.JPanel();
        pnlCliente = new view.pnlCliente(this.emf);
        pnlDireccion = new view.pnlDireccion(this.emf);
        pnlEmpleado = new view.pnlEmpleado(this.emf);
        pnlUsuario = new view.pnlUsuario(this.emf);
        pnlProductoBancario = new view.pnlProductoBancario(this.emf);
        pnlProductoBancario.setVisible(false);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setIconImage(new ImageIcon(Functions.getImage("icons/SoftBank 16px.png")).getImage());

        pnlContenedor.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        pnlContenedor.add(pnlCliente, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        pnlContenedor.add(pnlDireccion, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        pnlContenedor.add(pnlEmpleado, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        pnlContenedor.add(pnlUsuario, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        pnlContenedor.add(pnlProductoBancario, gridBagConstraints);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlContenedor, javax.swing.GroupLayout.DEFAULT_SIZE, 1049, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlContenedor, javax.swing.GroupLayout.PREFERRED_SIZE, 607, javax.swing.GroupLayout.PREFERRED_SIZE)
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
            java.util.logging.Logger.getLogger(frmCliente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(frmCliente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(frmCliente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(frmCliente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new frmCliente().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private view.pnlCliente pnlCliente;
    private javax.swing.JPanel pnlContenedor;
    private view.pnlDireccion pnlDireccion;
    private view.pnlEmpleado pnlEmpleado;
    private view.pnlProductoBancario pnlProductoBancario;
    private view.pnlUsuario pnlUsuario;
    // End of variables declaration//GEN-END:variables
}
