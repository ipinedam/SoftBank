package view;

import javax.persistence.EntityManagerFactory;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import model.DAO.UsuarioDAO;
import utilities.DBConnection;
import utilities.Functions;

/**
 *
 * @author Ignacio Pineda Martín
 */
public class frmLogin extends javax.swing.JFrame {
    
    protected EntityManagerFactory emf;
    private UsuarioDAO udao;

    /**
     * Creates new form frmLogin
     */
    public frmLogin() {
        initComponents();
        // Título del formulario.
        this.setTitle("Login");
        // Posicionamos el formulario en el centro de la pantalla.
        this.setLocationRelativeTo(null);
        // Creamos el EntityManagerFactory.
        emf = new DBConnection().getEntityManagerFactory();
        // Creamos los DAO's con su EntityManagerFactory.
        udao = new UsuarioDAO(emf);
        vaciarCampos();
        inicializarFormulario();
    }
   
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        rbgTipoUsuario = new javax.swing.ButtonGroup();
        pnlLogin = new javax.swing.JPanel();
        lblTitulo = new javax.swing.JLabel();
        lblUsuario = new javax.swing.JLabel();
        lblPassword = new javax.swing.JLabel();
        txtUsuario = new javax.swing.JTextField();
        txtPassword = new javax.swing.JPasswordField();
        rbCliente = new javax.swing.JRadioButton();
        rbEmpleado = new javax.swing.JRadioButton();
        btnIniciarSesion = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setIconImage(new ImageIcon(Functions.getImage("icons/SoftBank 16px.png")).getImage());
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        lblTitulo.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        lblTitulo.setText("LOGIN");

        lblUsuario.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        lblUsuario.setLabelFor(txtUsuario);
        lblUsuario.setText("Usuario");

        lblPassword.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        lblPassword.setLabelFor(txtPassword);
        lblPassword.setText("Contraseña");

        txtUsuario.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        txtUsuario.setNextFocusableComponent(txtPassword);
        txtUsuario.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtUsuarioKeyReleased(evt);
            }
        });

        txtPassword.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        txtPassword.setNextFocusableComponent(rbCliente);
        txtPassword.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtPasswordKeyReleased(evt);
            }
        });

        rbgTipoUsuario.add(rbCliente);
        rbCliente.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        rbCliente.setText("Cliente");
        rbCliente.setActionCommand("Cliente");
        rbCliente.setNextFocusableComponent(rbEmpleado);
        rbCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbClienteActionPerformed(evt);
            }
        });

        rbgTipoUsuario.add(rbEmpleado);
        rbEmpleado.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        rbEmpleado.setText("Empleado");
        rbEmpleado.setActionCommand("Empleado");
        rbEmpleado.setNextFocusableComponent(btnIniciarSesion);
        rbEmpleado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbEmpleadoActionPerformed(evt);
            }
        });

        btnIniciarSesion.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        btnIniciarSesion.setText("Iniciar sesión");
        btnIniciarSesion.setEnabled(false);
        btnIniciarSesion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnIniciarSesionActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlLoginLayout = new javax.swing.GroupLayout(pnlLogin);
        pnlLogin.setLayout(pnlLoginLayout);
        pnlLoginLayout.setHorizontalGroup(
            pnlLoginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlLoginLayout.createSequentialGroup()
                .addGroup(pnlLoginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pnlLoginLayout.createSequentialGroup()
                        .addGap(50, 50, 50)
                        .addComponent(rbCliente)
                        .addGap(90, 90, 90)
                        .addComponent(rbEmpleado))
                    .addGroup(pnlLoginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(pnlLoginLayout.createSequentialGroup()
                            .addGap(160, 160, 160)
                            .addComponent(lblTitulo))
                        .addGroup(pnlLoginLayout.createSequentialGroup()
                            .addGap(60, 60, 60)
                            .addComponent(lblUsuario)
                            .addGap(20, 20, 20)
                            .addComponent(txtUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(pnlLoginLayout.createSequentialGroup()
                            .addGap(20, 20, 20)
                            .addComponent(lblPassword)
                            .addGap(20, 20, 20)
                            .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(0, 40, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlLoginLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnIniciarSesion)
                .addGap(102, 102, 102))
        );
        pnlLoginLayout.setVerticalGroup(
            pnlLoginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlLoginLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(lblTitulo)
                .addGap(18, 18, 18)
                .addGroup(pnlLoginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblUsuario)
                    .addComponent(txtUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(35, 35, 35)
                .addGroup(pnlLoginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblPassword)
                    .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pnlLoginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rbCliente)
                    .addComponent(rbEmpleado))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 30, Short.MAX_VALUE)
                .addComponent(btnIniciarSesion)
                .addGap(19, 19, 19))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlLogin, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlLogin, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnIniciarSesionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnIniciarSesionActionPerformed
        String sTipoUsuario = rbgTipoUsuario.getSelection().getActionCommand();
        System.out.println(sTipoUsuario);        
        // Invocamos el método de validación de login del DAO de "Usuario".
        if ( udao.login(txtUsuario.getText(), 
                        String.valueOf(txtPassword.getPassword()),
                        sTipoUsuario) ) {
            // Mostramos el formulario de "Cliente" o "Empleado" y cerramos 
            // la pantalla de "Login".
            if (sTipoUsuario.equals("Cliente")) {
                frmAppCliente fac = new frmAppCliente(udao.getUsuario());
                fac.setVisible(true);
            } else {
                frmAppEmpleado fae = new frmAppEmpleado(udao.getUsuario());
                fae.setVisible(true);               
            }
            this.dispose();
        } else {
            vaciarCampos();
            inicializarFormulario();
            JOptionPane.showMessageDialog(this, udao.getMensaje(), 
                                          this.getTitle(), 
                                          JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnIniciarSesionActionPerformed

    private void txtUsuarioKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtUsuarioKeyReleased
        habilitarIniciarSesion();
    }//GEN-LAST:event_txtUsuarioKeyReleased

    private void txtPasswordKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPasswordKeyReleased
        habilitarIniciarSesion();        
    }//GEN-LAST:event_txtPasswordKeyReleased

    private void rbClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbClienteActionPerformed
        habilitarIniciarSesion();       
    }//GEN-LAST:event_rbClienteActionPerformed

    private void rbEmpleadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbEmpleadoActionPerformed
        habilitarIniciarSesion();
    }//GEN-LAST:event_rbEmpleadoActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        int opcion = JOptionPane.showConfirmDialog(this,
                                                   "¿Desea cerrar la aplicación?",
                                                   this.getTitle(),
                                                   JOptionPane.YES_NO_OPTION);
        if (opcion == JOptionPane.YES_OPTION) {
            // Cerramos el formulario de "Login".
            this.dispose();
        }
    }//GEN-LAST:event_formWindowClosing

    /**
     * Método para habilitar/deshabilitar el botón {@link btnIniciarSesion}.
     */
    private void habilitarIniciarSesion() {
        char[] password = txtPassword.getPassword();
        
        if (txtUsuario.getText().isEmpty() || (password.length == 0)
                || (rbgTipoUsuario.getSelection() == null)) {
            btnIniciarSesion.setEnabled(false);
        } else {
            btnIniciarSesion.setEnabled(true);
        }
    }
    
    /**
     * Método para inicializar los campos del formulario.
     */
    private void vaciarCampos() {
        txtUsuario.setText("");
        txtPassword.setText("");
        rbgTipoUsuario.clearSelection();
    }

    /**
     * Método para preparar el formulario en un estado inicial.
     */
    private void inicializarFormulario() {
        txtUsuario.requestFocus();
        btnIniciarSesion.setEnabled(false);
    }
    
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
//                if ("Nimbus".equals(info.getName())) {
                if ("Windows".equals(info.getName())) {                    
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(frmLogin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(frmLogin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(frmLogin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(frmLogin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new frmLogin().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnIniciarSesion;
    private javax.swing.JLabel lblPassword;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JLabel lblUsuario;
    private javax.swing.JPanel pnlLogin;
    private javax.swing.JRadioButton rbCliente;
    private javax.swing.JRadioButton rbEmpleado;
    private javax.swing.ButtonGroup rbgTipoUsuario;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JTextField txtUsuario;
    // End of variables declaration//GEN-END:variables
}
