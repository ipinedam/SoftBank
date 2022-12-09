package view;

import com.toedter.calendar.JDateChooser;

import java.awt.Component;

import javax.persistence.EntityManagerFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JSpinner;

import model.entity.Usuario;

import utilities.Constants.PanelMode;
import utilities.Functions;

/**
 * <p>
 * Clase padre para todos los {@link javax.swing.JPanel JPanel} de la
 * aplicación.</p>
 *
 * <p>
 * Incluye la funcionalidad necesaría para activar/desactivar los elementos del
 * panel, en función del {@link PanelMode modo} seleccionado.</p>
 *
 * @author Ignacio Pineda Martín
 */
public class pnlAppSoftBank extends javax.swing.JPanel {

    private Usuario appUser;    
    protected PanelMode panelMode = PanelMode.CRUD;
    protected Object jpaObject;
    protected EntityManagerFactory emf;
    protected frmAppSoftBank frmParent;
    
    /**
     * Creates new form pnlAppSoftBank
     */
    public pnlAppSoftBank() {
        initComponents();
    }

    public void setAppUser(Usuario appUser) {
        this.appUser = appUser;
    }
            
    public Usuario getAppUser() {
        return this.appUser;
    }    
    
    /**
     * Gestión de los paneles en función de los modos de visualización.
     * 
     * @param panelMode Modo de visualización seleccionado 
     * (ver {@link PanelMode}).
     */
    public void setPanelMode(PanelMode panelMode) {
        this.panelMode = panelMode;
        switch (this.panelMode) {
            case CRUD:
                for (Component c : Functions.getComponents(this)) {
                    if (c instanceof JComboBox jcb) {
                        jcb.setEnabled(true);
                    }
                    if (c instanceof JDateChooser jdc) {
                        jdc.getCalendarButton().setEnabled(true);
                    }
                    if (c instanceof JSpinner jsp) {
                        jsp.setEnabled(true);
                    }                    
                    c.setFocusable(true);
                    // Las etiquetas nunca serán "focusables".
                    if (c instanceof JLabel lbl) {
                        lbl.setFocusable(false);
                    }
                    c.setVisible(true);
                    // Hacemos invisibles componentes con nombres específicos.                    
                    if (!(c.getName() == null)) {
                        if ((c.getName().equals("tlbAccesorios"))) {
                            c.setVisible(false);
                        }
                    }
                }
                break;
            case SELECTION:
                for (Component c : Functions.getComponents(this)) {
                    if (c instanceof JComboBox jcb) {
                        jcb.setEnabled(true);
                    }
                    if (c instanceof JDateChooser jdc) {
                        jdc.getCalendarButton().setEnabled(true);
                    }                    
                    if (c instanceof JSpinner jsp) {
                        jsp.setEnabled(true);
                    }
                    c.setFocusable(true);
                    // Las etiquetas nunca serán "focusables".                    
                    if (c instanceof JLabel lbl) {
                        lbl.setFocusable(false);
                    }                    
                    c.setVisible(true);
                    // Hacemos invisibles componentes con nombres específicos.                    
                    if (!(c.getName() == null)) {
                        if ((c.getName().equals("pnlStatusBar"))
                                || (c.getName().equals("btnInsertar"))
                                || (c.getName().equals("btnModificar"))
                                || (c.getName().equals("btnEliminar"))
                                || (c.getName().equals("tlbAccesorios"))
                                || (c.getName().equals("tlbAcciones"))) {
                            c.setVisible(false);
                        }
                    }
                }
                break;
            case READ_ONLY:
                for (Component c : Functions.getComponents(this)) {
                    if (c instanceof JComboBox jcb) {
                        jcb.setEnabled(false);
                    }
                    if (c instanceof JDateChooser jdc) {
                        jdc.getCalendarButton().setEnabled(false);
                    }                    
                    if (c instanceof JSpinner jsp) {
                        jsp.setEnabled(false);
                    }
                    if (c instanceof JLabel jlb) {
                        // Quitamos el icono de "búsqueda" en aquellas
                        // etiquetas no accesibles.
                        if ((jlb.getName() != null) && (jlb.getName().equals("lblBusqueda"))) {
                            jlb.setIcon(null);
                        }
                    }
                    c.setFocusable(false);
                    c.setVisible(true);
                    // Hacemos invisibles componentes con nombres específicos.
                    if (!(c.getName() == null)) {
                        if ((c.getName().equals("pnlTabla"))
                                || (c.getName().equals("pnlStatusBar"))
                                || (c.getName().equals("pnlBotones"))
                                || (c.getName().equals("tlbAccesorios"))                                
                                || (c.getName().equals("tlbAcciones"))) {
                            c.setVisible(false);
                        }
                    }
                }
                break;
            default:
                throw new AssertionError();
        }
    }

    public void setJPAObject(Object jpaObject) {
        this.jpaObject = jpaObject;
    }    
    
    public void setFrmParent(frmAppSoftBank frmParent) {
        this.frmParent = frmParent;
    }
    
    public frmAppSoftBank getFrmParent() {
        return this.frmParent;
    }

    public void sendJPAObjectToFrmParent() {
        if (this.frmParent != null) {
            frmParent.setJPAObject(this.jpaObject);            
        }        
    }
    
    protected boolean checkRequiredFields() {
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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
