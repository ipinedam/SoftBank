package view;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import model.entity.Cliente;
import model.entity.ProductoBancario;
import model.entity.Usuario;

import utilities.Constants.FormAction;
import utilities.Constants.PanelMode;
import utilities.Functions;

/**
 *
 * @author Ignacio Pineda Martín
 */
public class frmAppCliente extends javax.swing.JFrame {

    private Cliente cliente = null;
    private Usuario usuario = null;
    
    /**
     * Creates new form frmAppCliente
     */
    public frmAppCliente() {
        initComponents();
        // Título del formulario.
        this.setTitle("SoftBank - Cliente");
        // La posicionamos en el centro de la pantalla.
        this.setLocationRelativeTo(null);
    }

    public frmAppCliente(Usuario usuario) {
        this();
        this.usuario = usuario;
        this.cliente = usuario.getCliente();
        if (cliente.getApellidosCliente() == null)
            cliente.setApellidosCliente("");
        setStatusBarText(String.format("Conectado %s %s",
                cliente.getNombreCliente(), cliente.getApellidosCliente()));
        habilitarMenusYBotones();
    }

    private void habilitarMenusYBotones() {
        // Habillitamos / deshabilitamos botones y menús en función de los
        // productos del cliente.
        btnCuentasCorrientesMovimientosConsulta.setVisible(false);
        btnTarjetasCreditoMovimientosConsulta.setVisible(false);
        btnPrestamosMovimientosConsulta.setVisible(false);
        mnuCuentasCorrientes.setVisible(false);
        mnuTarjetasCredito.setVisible(false);
        mnuPrestamos.setVisible(false);
        for (ProductoBancario pb : cliente.getProductoBancarioList()) {
            // Comprobamos en primer lugar que el producto no esté cancelado.
            if (pb.getFecCancelacion() == null) {
                if (pb.getCuentaCorriente() != null) {
                    mnuCuentasCorrientes.setVisible(true);
                    btnCuentasCorrientesMovimientosConsulta.setVisible(true);
                }
                if (pb.getTarjetaCredito() != null) {
                    mnuTarjetasCredito.setVisible(true);
                    btnTarjetasCreditoMovimientosConsulta.setVisible(true);
                    changeGBC();
                }
                if (pb.getPrestamo() != null) {
                    mnuPrestamos.setVisible(true);
                    btnPrestamosMovimientosConsulta.setVisible(true);
                    changeGBC();
                }
            }
        }
    }

    /**
     * Modificamos la característica "weitghx" de los GridBagConstraints del
     * botón "btnCuentasCorrientesMovimientosConsulta". De esta forma, cuando
     * haya más de un producto en el formulario, aparecerán todos justificados a
     * la izquierda.
     */
    private void changeGBC() {
        GridBagLayout gbl = (GridBagLayout) pnlCliente.getLayout();
        GridBagConstraints gbc = gbl.getConstraints(btnCuentasCorrientesMovimientosConsulta);
        gbc.weightx = 0;
        gbl.setConstraints(btnCuentasCorrientesMovimientosConsulta, gbc);
    }
    
    private void setStatusBarText(String texto) {
        Format f = new SimpleDateFormat("HH:mm:ss");
        txtStatus.setText(f.format(new Date()) + " " + texto);
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

        pnlCliente = new javax.swing.JPanel();
        btnCuentasCorrientesMovimientosConsulta = new javax.swing.JButton();
        btnTarjetasCreditoMovimientosConsulta = new javax.swing.JButton();
        btnPrestamosMovimientosConsulta = new javax.swing.JButton();
        pnlStatusBar = new view.pnlAppSoftBank();
        txtStatus = new javax.swing.JLabel();
        mnuAppCliente = new javax.swing.JMenuBar();
        mnuCuentasCorrientes = new javax.swing.JMenu();
        mniCuentasCorrientesMovimientosConsulta = new javax.swing.JMenuItem();
        mnuTarjetasCredito = new javax.swing.JMenu();
        mniTarjetasCreditoMovimientosConsulta = new javax.swing.JMenuItem();
        mnuPrestamos = new javax.swing.JMenu();
        mniPrestamosMovimientosConsulta = new javax.swing.JMenuItem();
        mnuAplicacion = new javax.swing.JMenu();
        mniCerrarSesion = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setIconImage(new ImageIcon(Functions.getImage("icons/SoftBank 16px.png")).getImage());
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.Y_AXIS));

        pnlCliente.setMinimumSize(new java.awt.Dimension(400, 134));
        java.awt.GridBagLayout pnlClienteLayout = new java.awt.GridBagLayout();
        pnlClienteLayout.columnWidths = new int[] {0, 5, 0, 5, 0};
        pnlClienteLayout.rowHeights = new int[] {0};
        pnlCliente.setLayout(pnlClienteLayout);

        btnCuentasCorrientesMovimientosConsulta.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/cuenta-corriente 128px.png"))); // NOI18N
        btnCuentasCorrientesMovimientosConsulta.setToolTipText("Cuentas Corrientes: consulta de movimientos");
        btnCuentasCorrientesMovimientosConsulta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCuentasCorrientesMovimientosConsultaActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        pnlCliente.add(btnCuentasCorrientesMovimientosConsulta, gridBagConstraints);

        btnTarjetasCreditoMovimientosConsulta.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/tarjeta-credito 128px.png"))); // NOI18N
        btnTarjetasCreditoMovimientosConsulta.setToolTipText("Tarjetas de Crédito: consulta de movimientos");
        btnTarjetasCreditoMovimientosConsulta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTarjetasCreditoMovimientosConsultaActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        pnlCliente.add(btnTarjetasCreditoMovimientosConsulta, gridBagConstraints);

        btnPrestamosMovimientosConsulta.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/prestamo 128px.png"))); // NOI18N
        btnPrestamosMovimientosConsulta.setToolTipText("Préstamos: consulta de movimientos");
        btnPrestamosMovimientosConsulta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrestamosMovimientosConsultaActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        pnlCliente.add(btnPrestamosMovimientosConsulta, gridBagConstraints);

        getContentPane().add(pnlCliente);

        pnlStatusBar.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        pnlStatusBar.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        pnlStatusBar.setMinimumSize(new java.awt.Dimension(131, 100));
        pnlStatusBar.setName("pnlStatusBar"); // NOI18N
        pnlStatusBar.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        txtStatus.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtStatus.setText(" ");
        pnlStatusBar.add(txtStatus);

        getContentPane().add(pnlStatusBar);

        mnuCuentasCorrientes.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/cuenta-corriente 16px.png"))); // NOI18N
        mnuCuentasCorrientes.setText("Cuentas Corrientes");

        mniCuentasCorrientesMovimientosConsulta.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/cuenta-corriente 16px.png"))); // NOI18N
        mniCuentasCorrientesMovimientosConsulta.setText("Consulta de movimientos");
        mniCuentasCorrientesMovimientosConsulta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniCuentasCorrientesMovimientosConsultaActionPerformed(evt);
            }
        });
        mnuCuentasCorrientes.add(mniCuentasCorrientesMovimientosConsulta);

        mnuAppCliente.add(mnuCuentasCorrientes);

        mnuTarjetasCredito.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/tarjeta-credito 16px.png"))); // NOI18N
        mnuTarjetasCredito.setText("Tarjetas de Crédito");

        mniTarjetasCreditoMovimientosConsulta.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/tarjeta-credito 16px.png"))); // NOI18N
        mniTarjetasCreditoMovimientosConsulta.setText("Consulta de movimientos");
        mniTarjetasCreditoMovimientosConsulta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniTarjetasCreditoMovimientosConsultaActionPerformed(evt);
            }
        });
        mnuTarjetasCredito.add(mniTarjetasCreditoMovimientosConsulta);

        mnuAppCliente.add(mnuTarjetasCredito);

        mnuPrestamos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/prestamo 16px.png"))); // NOI18N
        mnuPrestamos.setText("Préstamos");

        mniPrestamosMovimientosConsulta.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/prestamo 16px.png"))); // NOI18N
        mniPrestamosMovimientosConsulta.setText("Consulta de movimientos");
        mniPrestamosMovimientosConsulta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniPrestamosMovimientosConsultaActionPerformed(evt);
            }
        });
        mnuPrestamos.add(mniPrestamosMovimientosConsulta);

        mnuAppCliente.add(mnuPrestamos);

        mnuAplicacion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/SoftBank 16px.png"))); // NOI18N
        mnuAplicacion.setText("Aplicación");

        mniCerrarSesion.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.ALT_DOWN_MASK));
        mniCerrarSesion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/exit 16px.png"))); // NOI18N
        mniCerrarSesion.setText("Cerrar sesión");
        mniCerrarSesion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniCerrarSesionActionPerformed(evt);
            }
        });
        mnuAplicacion.add(mniCerrarSesion);

        mnuAppCliente.add(mnuAplicacion);

        setJMenuBar(mnuAppCliente);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        mniCerrarSesion.doClick();
    }//GEN-LAST:event_formWindowClosing

    private void mniCerrarSesionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniCerrarSesionActionPerformed
        int opcion = JOptionPane.showConfirmDialog(this,
                                                   "¿Desea cerrar la sesión?",
                                                   this.getTitle(),
                                                   JOptionPane.YES_NO_OPTION);
        if (opcion == JOptionPane.YES_OPTION) {
            // Cerramos el formulario de "Persona" y volvemos al formulario de
            // "Login".
            frmLogin fl = new frmLogin();
            fl.setVisible(true);
            this.dispose();
        }
    }//GEN-LAST:event_mniCerrarSesionActionPerformed

    private void btnCuentasCorrientesMovimientosConsultaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCuentasCorrientesMovimientosConsultaActionPerformed
        mniCuentasCorrientesMovimientosConsulta.doClick();
    }//GEN-LAST:event_btnCuentasCorrientesMovimientosConsultaActionPerformed

    private void mniCuentasCorrientesMovimientosConsultaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniCuentasCorrientesMovimientosConsultaActionPerformed
        // Creamos el formulario.
        frmCuentaCorriente fcc = new frmCuentaCorriente();
        // Pasamos el usuario.
        fcc.setAppUser(usuario);
        // Modo "READ_ONLY" + "QUERY" (Visualización de cuenta y movimientos.) 
        // (requiere pasar información al formulario).
        fcc.setFormModeAndAction(PanelMode.READ_ONLY, FormAction.QUERY);
        // Pasamos información de un cliente (se mostrarán todas sus cuentas).
        fcc.setJPAObject(cliente);
        fcc.setVisible(true);
    }//GEN-LAST:event_mniCuentasCorrientesMovimientosConsultaActionPerformed

    private void mniTarjetasCreditoMovimientosConsultaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniTarjetasCreditoMovimientosConsultaActionPerformed
        // Creamos el formulario.
        frmTarjetaCredito ftc = new frmTarjetaCredito();
        // Pasamos el usuario.
        ftc.setAppUser(usuario);        
        // Modo "READ_ONLY" + "QUERY" (Visualización de cuenta y movimientos.) 
        // (requiere pasar información al formulario).
        ftc.setFormModeAndAction(PanelMode.READ_ONLY, FormAction.QUERY);
        // Pasamos información de un cliente (se mostrarán todas sus cuentas).
        ftc.setJPAObject(cliente);
        ftc.setVisible(true);
    }//GEN-LAST:event_mniTarjetasCreditoMovimientosConsultaActionPerformed

    private void btnTarjetasCreditoMovimientosConsultaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTarjetasCreditoMovimientosConsultaActionPerformed
        mniTarjetasCreditoMovimientosConsulta.doClick();
    }//GEN-LAST:event_btnTarjetasCreditoMovimientosConsultaActionPerformed

    private void mniPrestamosMovimientosConsultaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniPrestamosMovimientosConsultaActionPerformed
        // Creamos el formulario.
        frmPrestamo fp = new frmPrestamo();
        // Pasamos el usuario.
        fp.setAppUser(usuario);        
        // Modo "READ_ONLY" + "QUERY" (Visualización de cuenta y movimientos.) 
        // (requiere pasar información al formulario).
        fp.setFormModeAndAction(PanelMode.READ_ONLY, FormAction.QUERY);
        // Pasamos información de un cliente (se mostrarán todas sus cuentas).
        fp.setJPAObject(cliente);
        fp.setVisible(true);
    }//GEN-LAST:event_mniPrestamosMovimientosConsultaActionPerformed

    private void btnPrestamosMovimientosConsultaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrestamosMovimientosConsultaActionPerformed
        mniPrestamosMovimientosConsulta.doClick();
    }//GEN-LAST:event_btnPrestamosMovimientosConsultaActionPerformed

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
            java.util.logging.Logger.getLogger(frmAppCliente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(frmAppCliente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(frmAppCliente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(frmAppCliente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new frmAppCliente().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCuentasCorrientesMovimientosConsulta;
    private javax.swing.JButton btnPrestamosMovimientosConsulta;
    private javax.swing.JButton btnTarjetasCreditoMovimientosConsulta;
    private javax.swing.JMenuItem mniCerrarSesion;
    private javax.swing.JMenuItem mniCuentasCorrientesMovimientosConsulta;
    private javax.swing.JMenuItem mniPrestamosMovimientosConsulta;
    private javax.swing.JMenuItem mniTarjetasCreditoMovimientosConsulta;
    private javax.swing.JMenu mnuAplicacion;
    private javax.swing.JMenuBar mnuAppCliente;
    private javax.swing.JMenu mnuCuentasCorrientes;
    private javax.swing.JMenu mnuPrestamos;
    private javax.swing.JMenu mnuTarjetasCredito;
    private javax.swing.JPanel pnlCliente;
    private javax.swing.JPanel pnlStatusBar;
    private javax.swing.JLabel txtStatus;
    // End of variables declaration//GEN-END:variables
}
