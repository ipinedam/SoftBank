package controller;

//<editor-fold defaultstate="collapsed" desc=" Librerías importadas... ">
import controller.exceptions.IllegalOrphanException;
import controller.exceptions.NonexistentEntityException;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import model.entity.CuentaCorriente;
import model.entity.Prestamo;
import model.entity.ProductoBancario;
import model.entity.TarjetaCredito;
//</editor-fold>

/**
 *
 * @author Ignacio Pineda Martín
 */
public class CuentaCorrienteJpaController implements Serializable {

    private EntityManagerFactory emf = null;
    
    public CuentaCorrienteJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(CuentaCorriente cuentaCorriente) throws IllegalOrphanException {
        if (cuentaCorriente.getPrestamoList() == null) {
            cuentaCorriente.setPrestamoList(new ArrayList<Prestamo>());
        }
        if (cuentaCorriente.getTarjetaCreditoList() == null) {
            cuentaCorriente.setTarjetaCreditoList(new ArrayList<TarjetaCredito>());
        }
        List<String> illegalOrphanMessages = null;
        ProductoBancario productoBancarioOrphanCheck = cuentaCorriente.getProductoBancario();
        if (productoBancarioOrphanCheck != null) {
            CuentaCorriente oldCuentaCorrienteOfProductoBancario = productoBancarioOrphanCheck.getCuentaCorriente();
            if (oldCuentaCorrienteOfProductoBancario != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("The ProductoBancario " + productoBancarioOrphanCheck + " already has an item of type CuentaCorriente whose productoBancario column cannot be null. Please make another selection for the productoBancario field.");
            }
        }
        if (illegalOrphanMessages != null) {
            throw new IllegalOrphanException(illegalOrphanMessages);
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            ProductoBancario productoBancario = cuentaCorriente.getProductoBancario();
            if (productoBancario != null) {
                productoBancario = em.getReference(productoBancario.getClass(), productoBancario.getIdProductoBancario());
                cuentaCorriente.setProductoBancario(productoBancario);
            }
            List<Prestamo> attachedPrestamoList = new ArrayList<Prestamo>();
            for (Prestamo prestamoListPrestamoToAttach : cuentaCorriente.getPrestamoList()) {
                prestamoListPrestamoToAttach = em.getReference(prestamoListPrestamoToAttach.getClass(), prestamoListPrestamoToAttach.getIdPrestamo());
                attachedPrestamoList.add(prestamoListPrestamoToAttach);
            }
            cuentaCorriente.setPrestamoList(attachedPrestamoList);
            List<TarjetaCredito> attachedTarjetaCreditoList = new ArrayList<TarjetaCredito>();
            for (TarjetaCredito tarjetaCreditoListTarjetaCreditoToAttach : cuentaCorriente.getTarjetaCreditoList()) {
                tarjetaCreditoListTarjetaCreditoToAttach = em.getReference(tarjetaCreditoListTarjetaCreditoToAttach.getClass(), tarjetaCreditoListTarjetaCreditoToAttach.getIdTarjetaCredito());
                attachedTarjetaCreditoList.add(tarjetaCreditoListTarjetaCreditoToAttach);
            }
            cuentaCorriente.setTarjetaCreditoList(attachedTarjetaCreditoList);
            em.persist(cuentaCorriente);
            if (productoBancario != null) {
                productoBancario.setCuentaCorriente(cuentaCorriente);
                productoBancario = em.merge(productoBancario);
            }
            for (Prestamo prestamoListPrestamo : cuentaCorriente.getPrestamoList()) {
                CuentaCorriente oldCuentaCorrientePagoOfPrestamoListPrestamo = prestamoListPrestamo.getCuentaCorrientePago();
                prestamoListPrestamo.setCuentaCorrientePago(cuentaCorriente);
                prestamoListPrestamo = em.merge(prestamoListPrestamo);
                if (oldCuentaCorrientePagoOfPrestamoListPrestamo != null) {
                    oldCuentaCorrientePagoOfPrestamoListPrestamo.getPrestamoList().remove(prestamoListPrestamo);
                    oldCuentaCorrientePagoOfPrestamoListPrestamo = em.merge(oldCuentaCorrientePagoOfPrestamoListPrestamo);
                }
            }
            for (TarjetaCredito tarjetaCreditoListTarjetaCredito : cuentaCorriente.getTarjetaCreditoList()) {
                CuentaCorriente oldCuentaCorrientePagoOfTarjetaCreditoListTarjetaCredito = tarjetaCreditoListTarjetaCredito.getCuentaCorrientePago();
                tarjetaCreditoListTarjetaCredito.setCuentaCorrientePago(cuentaCorriente);
                tarjetaCreditoListTarjetaCredito = em.merge(tarjetaCreditoListTarjetaCredito);
                if (oldCuentaCorrientePagoOfTarjetaCreditoListTarjetaCredito != null) {
                    oldCuentaCorrientePagoOfTarjetaCreditoListTarjetaCredito.getTarjetaCreditoList().remove(tarjetaCreditoListTarjetaCredito);
                    oldCuentaCorrientePagoOfTarjetaCreditoListTarjetaCredito = em.merge(oldCuentaCorrientePagoOfTarjetaCreditoListTarjetaCredito);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(CuentaCorriente cuentaCorriente) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            CuentaCorriente persistentCuentaCorriente = em.find(CuentaCorriente.class, cuentaCorriente.getIdCuentaCorriente());
            ProductoBancario productoBancarioOld = persistentCuentaCorriente.getProductoBancario();
            ProductoBancario productoBancarioNew = cuentaCorriente.getProductoBancario();
            List<Prestamo> prestamoListOld = persistentCuentaCorriente.getPrestamoList();
            List<Prestamo> prestamoListNew = cuentaCorriente.getPrestamoList();
            List<TarjetaCredito> tarjetaCreditoListOld = persistentCuentaCorriente.getTarjetaCreditoList();
            List<TarjetaCredito> tarjetaCreditoListNew = cuentaCorriente.getTarjetaCreditoList();
            List<String> illegalOrphanMessages = null;
            if (productoBancarioNew != null && !productoBancarioNew.equals(productoBancarioOld)) {
                CuentaCorriente oldCuentaCorrienteOfProductoBancario = productoBancarioNew.getCuentaCorriente();
                if (oldCuentaCorrienteOfProductoBancario != null) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("The ProductoBancario " + productoBancarioNew + " already has an item of type CuentaCorriente whose productoBancario column cannot be null. Please make another selection for the productoBancario field.");
                }
            }
            for (Prestamo prestamoListOldPrestamo : prestamoListOld) {
                if (!prestamoListNew.contains(prestamoListOldPrestamo)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Prestamo " + prestamoListOldPrestamo + " since its cuentaCorrientePago field is not nullable.");
                }
            }
            for (TarjetaCredito tarjetaCreditoListOldTarjetaCredito : tarjetaCreditoListOld) {
                if (!tarjetaCreditoListNew.contains(tarjetaCreditoListOldTarjetaCredito)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain TarjetaCredito " + tarjetaCreditoListOldTarjetaCredito + " since its cuentaCorrientePago field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (productoBancarioNew != null) {
                productoBancarioNew = em.getReference(productoBancarioNew.getClass(), productoBancarioNew.getIdProductoBancario());
                cuentaCorriente.setProductoBancario(productoBancarioNew);
            }
            List<Prestamo> attachedPrestamoListNew = new ArrayList<Prestamo>();
            for (Prestamo prestamoListNewPrestamoToAttach : prestamoListNew) {
                prestamoListNewPrestamoToAttach = em.getReference(prestamoListNewPrestamoToAttach.getClass(), prestamoListNewPrestamoToAttach.getIdPrestamo());
                attachedPrestamoListNew.add(prestamoListNewPrestamoToAttach);
            }
            prestamoListNew = attachedPrestamoListNew;
            cuentaCorriente.setPrestamoList(prestamoListNew);
            List<TarjetaCredito> attachedTarjetaCreditoListNew = new ArrayList<TarjetaCredito>();
            for (TarjetaCredito tarjetaCreditoListNewTarjetaCreditoToAttach : tarjetaCreditoListNew) {
                tarjetaCreditoListNewTarjetaCreditoToAttach = em.getReference(tarjetaCreditoListNewTarjetaCreditoToAttach.getClass(), tarjetaCreditoListNewTarjetaCreditoToAttach.getIdTarjetaCredito());
                attachedTarjetaCreditoListNew.add(tarjetaCreditoListNewTarjetaCreditoToAttach);
            }
            tarjetaCreditoListNew = attachedTarjetaCreditoListNew;
            cuentaCorriente.setTarjetaCreditoList(tarjetaCreditoListNew);
            cuentaCorriente = em.merge(cuentaCorriente);
            if (productoBancarioOld != null && !productoBancarioOld.equals(productoBancarioNew)) {
                productoBancarioOld.setCuentaCorriente(null);
                productoBancarioOld = em.merge(productoBancarioOld);
            }
            if (productoBancarioNew != null && !productoBancarioNew.equals(productoBancarioOld)) {
                productoBancarioNew.setCuentaCorriente(cuentaCorriente);
                productoBancarioNew = em.merge(productoBancarioNew);
            }
            for (Prestamo prestamoListNewPrestamo : prestamoListNew) {
                if (!prestamoListOld.contains(prestamoListNewPrestamo)) {
                    CuentaCorriente oldCuentaCorrientePagoOfPrestamoListNewPrestamo = prestamoListNewPrestamo.getCuentaCorrientePago();
                    prestamoListNewPrestamo.setCuentaCorrientePago(cuentaCorriente);
                    prestamoListNewPrestamo = em.merge(prestamoListNewPrestamo);
                    if (oldCuentaCorrientePagoOfPrestamoListNewPrestamo != null && !oldCuentaCorrientePagoOfPrestamoListNewPrestamo.equals(cuentaCorriente)) {
                        oldCuentaCorrientePagoOfPrestamoListNewPrestamo.getPrestamoList().remove(prestamoListNewPrestamo);
                        oldCuentaCorrientePagoOfPrestamoListNewPrestamo = em.merge(oldCuentaCorrientePagoOfPrestamoListNewPrestamo);
                    }
                }
            }
            for (TarjetaCredito tarjetaCreditoListNewTarjetaCredito : tarjetaCreditoListNew) {
                if (!tarjetaCreditoListOld.contains(tarjetaCreditoListNewTarjetaCredito)) {
                    CuentaCorriente oldCuentaCorrientePagoOfTarjetaCreditoListNewTarjetaCredito = tarjetaCreditoListNewTarjetaCredito.getCuentaCorrientePago();
                    tarjetaCreditoListNewTarjetaCredito.setCuentaCorrientePago(cuentaCorriente);
                    tarjetaCreditoListNewTarjetaCredito = em.merge(tarjetaCreditoListNewTarjetaCredito);
                    if (oldCuentaCorrientePagoOfTarjetaCreditoListNewTarjetaCredito != null && !oldCuentaCorrientePagoOfTarjetaCreditoListNewTarjetaCredito.equals(cuentaCorriente)) {
                        oldCuentaCorrientePagoOfTarjetaCreditoListNewTarjetaCredito.getTarjetaCreditoList().remove(tarjetaCreditoListNewTarjetaCredito);
                        oldCuentaCorrientePagoOfTarjetaCreditoListNewTarjetaCredito = em.merge(oldCuentaCorrientePagoOfTarjetaCreditoListNewTarjetaCredito);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = cuentaCorriente.getIdCuentaCorriente();
                if (findCuentaCorriente(id) == null) {
                    throw new NonexistentEntityException("The cuentaCorriente with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            CuentaCorriente cuentaCorriente;
            try {
                cuentaCorriente = em.getReference(CuentaCorriente.class, id);
                cuentaCorriente.getIdCuentaCorriente();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The cuentaCorriente with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Prestamo> prestamoListOrphanCheck = cuentaCorriente.getPrestamoList();
            for (Prestamo prestamoListOrphanCheckPrestamo : prestamoListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This CuentaCorriente (" + cuentaCorriente + ") cannot be destroyed since the Prestamo " + prestamoListOrphanCheckPrestamo + " in its prestamoList field has a non-nullable cuentaCorrientePago field.");
            }
            List<TarjetaCredito> tarjetaCreditoListOrphanCheck = cuentaCorriente.getTarjetaCreditoList();
            for (TarjetaCredito tarjetaCreditoListOrphanCheckTarjetaCredito : tarjetaCreditoListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This CuentaCorriente (" + cuentaCorriente + ") cannot be destroyed since the TarjetaCredito " + tarjetaCreditoListOrphanCheckTarjetaCredito + " in its tarjetaCreditoList field has a non-nullable cuentaCorrientePago field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            ProductoBancario productoBancario = cuentaCorriente.getProductoBancario();
            if (productoBancario != null) {
                productoBancario.setCuentaCorriente(null);
                productoBancario = em.merge(productoBancario);
            }
            em.remove(cuentaCorriente);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<CuentaCorriente> findCuentaCorrienteEntities() {
        return findCuentaCorrienteEntities(true, -1, -1);
    }

    public List<CuentaCorriente> findCuentaCorrienteEntities(int maxResults, int firstResult) {
        return findCuentaCorrienteEntities(false, maxResults, firstResult);
    }

    private List<CuentaCorriente> findCuentaCorrienteEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(CuentaCorriente.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public CuentaCorriente findCuentaCorriente(Integer id) {
        EntityManager em = getEntityManager();
        CuentaCorriente c;
        try {
            c = em.find(CuentaCorriente.class, id);
            if (c != null)
                em.refresh(c);
            return c;
        } finally {
            em.close();
        }
    }

    public int getCuentaCorrienteCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<CuentaCorriente> rt = cq.from(CuentaCorriente.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
