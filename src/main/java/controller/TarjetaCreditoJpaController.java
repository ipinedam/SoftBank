package controller;

//<editor-fold defaultstate="collapsed" desc=" Librerías importadas... ">
import controller.exceptions.IllegalOrphanException;
import controller.exceptions.NonexistentEntityException;

import java.io.Serializable;

import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import model.entity.CuentaCorriente;
import model.entity.ProductoBancario;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import model.entity.TarjetaCredito;
//</editor-fold>

/**
 *
 * @author Ignacio Pineda Martín
 */
public class TarjetaCreditoJpaController implements Serializable {

    public TarjetaCreditoJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(TarjetaCredito tarjetaCredito) throws IllegalOrphanException {
        List<String> illegalOrphanMessages = null;
        ProductoBancario productoBancarioOrphanCheck = tarjetaCredito.getProductoBancario();
        if (productoBancarioOrphanCheck != null) {
            TarjetaCredito oldTarjetaCreditoOfProductoBancario = productoBancarioOrphanCheck.getTarjetaCredito();
            if (oldTarjetaCreditoOfProductoBancario != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("The ProductoBancario " + productoBancarioOrphanCheck + " already has an item of type TarjetaCredito whose productoBancario column cannot be null. Please make another selection for the productoBancario field.");
            }
        }
        if (illegalOrphanMessages != null) {
            throw new IllegalOrphanException(illegalOrphanMessages);
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            CuentaCorriente cuentaCorrientePago = tarjetaCredito.getCuentaCorrientePago();
            if (cuentaCorrientePago != null) {
                cuentaCorrientePago = em.getReference(cuentaCorrientePago.getClass(), cuentaCorrientePago.getIdCuentaCorriente());
                tarjetaCredito.setCuentaCorrientePago(cuentaCorrientePago);
            }
            ProductoBancario productoBancario = tarjetaCredito.getProductoBancario();
            if (productoBancario != null) {
                productoBancario = em.getReference(productoBancario.getClass(), productoBancario.getIdProductoBancario());
                tarjetaCredito.setProductoBancario(productoBancario);
            }
            em.persist(tarjetaCredito);
            if (cuentaCorrientePago != null) {
                cuentaCorrientePago.getTarjetaCreditoList().add(tarjetaCredito);
                cuentaCorrientePago = em.merge(cuentaCorrientePago);
            }
            if (productoBancario != null) {
                productoBancario.setTarjetaCredito(tarjetaCredito);
                productoBancario = em.merge(productoBancario);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(TarjetaCredito tarjetaCredito) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            TarjetaCredito persistentTarjetaCredito = em.find(TarjetaCredito.class, tarjetaCredito.getIdTarjetaCredito());
            CuentaCorriente cuentaCorrientePagoOld = persistentTarjetaCredito.getCuentaCorrientePago();
            CuentaCorriente cuentaCorrientePagoNew = tarjetaCredito.getCuentaCorrientePago();
            ProductoBancario productoBancarioOld = persistentTarjetaCredito.getProductoBancario();
            ProductoBancario productoBancarioNew = tarjetaCredito.getProductoBancario();
            List<String> illegalOrphanMessages = null;
            if (productoBancarioNew != null && !productoBancarioNew.equals(productoBancarioOld)) {
                TarjetaCredito oldTarjetaCreditoOfProductoBancario = productoBancarioNew.getTarjetaCredito();
                if (oldTarjetaCreditoOfProductoBancario != null) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("The ProductoBancario " + productoBancarioNew + " already has an item of type TarjetaCredito whose productoBancario column cannot be null. Please make another selection for the productoBancario field.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (cuentaCorrientePagoNew != null) {
                cuentaCorrientePagoNew = em.getReference(cuentaCorrientePagoNew.getClass(), cuentaCorrientePagoNew.getIdCuentaCorriente());
                tarjetaCredito.setCuentaCorrientePago(cuentaCorrientePagoNew);
            }
            if (productoBancarioNew != null) {
                productoBancarioNew = em.getReference(productoBancarioNew.getClass(), productoBancarioNew.getIdProductoBancario());
                tarjetaCredito.setProductoBancario(productoBancarioNew);
            }
            tarjetaCredito = em.merge(tarjetaCredito);
            if (cuentaCorrientePagoOld != null && !cuentaCorrientePagoOld.equals(cuentaCorrientePagoNew)) {
                cuentaCorrientePagoOld.getTarjetaCreditoList().remove(tarjetaCredito);
                cuentaCorrientePagoOld = em.merge(cuentaCorrientePagoOld);
            }
            if (cuentaCorrientePagoNew != null && !cuentaCorrientePagoNew.equals(cuentaCorrientePagoOld)) {
                cuentaCorrientePagoNew.getTarjetaCreditoList().add(tarjetaCredito);
                cuentaCorrientePagoNew = em.merge(cuentaCorrientePagoNew);
            }
            if (productoBancarioOld != null && !productoBancarioOld.equals(productoBancarioNew)) {
                productoBancarioOld.setTarjetaCredito(null);
                productoBancarioOld = em.merge(productoBancarioOld);
            }
            if (productoBancarioNew != null && !productoBancarioNew.equals(productoBancarioOld)) {
                productoBancarioNew.setTarjetaCredito(tarjetaCredito);
                productoBancarioNew = em.merge(productoBancarioNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = tarjetaCredito.getIdTarjetaCredito();
                if (findTarjetaCredito(id) == null) {
                    throw new NonexistentEntityException("The tarjetaCredito with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            TarjetaCredito tarjetaCredito;
            try {
                tarjetaCredito = em.getReference(TarjetaCredito.class, id);
                tarjetaCredito.getIdTarjetaCredito();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The tarjetaCredito with id " + id + " no longer exists.", enfe);
            }
            CuentaCorriente cuentaCorrientePago = tarjetaCredito.getCuentaCorrientePago();
            if (cuentaCorrientePago != null) {
                cuentaCorrientePago.getTarjetaCreditoList().remove(tarjetaCredito);
                cuentaCorrientePago = em.merge(cuentaCorrientePago);
            }
            ProductoBancario productoBancario = tarjetaCredito.getProductoBancario();
            if (productoBancario != null) {
                productoBancario.setTarjetaCredito(null);
                productoBancario = em.merge(productoBancario);
            }
            em.remove(tarjetaCredito);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<TarjetaCredito> findTarjetaCreditoEntities() {
        return findTarjetaCreditoEntities(true, -1, -1);
    }

    public List<TarjetaCredito> findTarjetaCreditoEntities(int maxResults, int firstResult) {
        return findTarjetaCreditoEntities(false, maxResults, firstResult);
    }

    private List<TarjetaCredito> findTarjetaCreditoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(TarjetaCredito.class));
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

    public TarjetaCredito findTarjetaCredito(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(TarjetaCredito.class, id);
        } finally {
            em.close();
        }
    }

    public int getTarjetaCreditoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<TarjetaCredito> rt = cq.from(TarjetaCredito.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
