package controller;

//<editor-fold defaultstate="collapsed" desc=" Librerías importadas... ">
import controller.exceptions.IllegalOrphanException;
import controller.exceptions.NonexistentEntityException;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import model.entity.CuentaCorriente;
import model.entity.Prestamo;
import model.entity.ProductoBancario;
//</editor-fold>

/**
 *
 * @author Ignacio Pineda Martín
 */
public class PrestamoJpaController implements Serializable {

    public PrestamoJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Prestamo prestamo) throws IllegalOrphanException {
        List<String> illegalOrphanMessages = null;
        ProductoBancario productoBancarioOrphanCheck = prestamo.getProductoBancario();
        if (productoBancarioOrphanCheck != null) {
            Prestamo oldPrestamoOfProductoBancario = productoBancarioOrphanCheck.getPrestamo();
            if (oldPrestamoOfProductoBancario != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("The ProductoBancario " + productoBancarioOrphanCheck + " already has an item of type Prestamo whose productoBancario column cannot be null. Please make another selection for the productoBancario field.");
            }
        }
        if (illegalOrphanMessages != null) {
            throw new IllegalOrphanException(illegalOrphanMessages);
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            CuentaCorriente cuentaCorrientePago = prestamo.getCuentaCorrientePago();
            if (cuentaCorrientePago != null) {
                cuentaCorrientePago = em.getReference(cuentaCorrientePago.getClass(), cuentaCorrientePago.getIdCuentaCorriente());
                prestamo.setCuentaCorrientePago(cuentaCorrientePago);
            }
            ProductoBancario productoBancario = prestamo.getProductoBancario();
            if (productoBancario != null) {
                productoBancario = em.getReference(productoBancario.getClass(), productoBancario.getIdProductoBancario());
                prestamo.setProductoBancario(productoBancario);
            }
            em.persist(prestamo);
            if (cuentaCorrientePago != null) {
                cuentaCorrientePago.getPrestamoList().add(prestamo);
                cuentaCorrientePago = em.merge(cuentaCorrientePago);
            }
            if (productoBancario != null) {
                productoBancario.setPrestamo(prestamo);
                productoBancario = em.merge(productoBancario);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Prestamo prestamo) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Prestamo persistentPrestamo = em.find(Prestamo.class, prestamo.getIdPrestamo());
            CuentaCorriente cuentaCorrientePagoOld = persistentPrestamo.getCuentaCorrientePago();
            CuentaCorriente cuentaCorrientePagoNew = prestamo.getCuentaCorrientePago();
            ProductoBancario productoBancarioOld = persistentPrestamo.getProductoBancario();
            ProductoBancario productoBancarioNew = prestamo.getProductoBancario();
            List<String> illegalOrphanMessages = null;
            if (productoBancarioNew != null && !productoBancarioNew.equals(productoBancarioOld)) {
                Prestamo oldPrestamoOfProductoBancario = productoBancarioNew.getPrestamo();
                if (oldPrestamoOfProductoBancario != null) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("The ProductoBancario " + productoBancarioNew + " already has an item of type Prestamo whose productoBancario column cannot be null. Please make another selection for the productoBancario field.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (cuentaCorrientePagoNew != null) {
                cuentaCorrientePagoNew = em.getReference(cuentaCorrientePagoNew.getClass(), cuentaCorrientePagoNew.getIdCuentaCorriente());
                prestamo.setCuentaCorrientePago(cuentaCorrientePagoNew);
            }
            if (productoBancarioNew != null) {
                productoBancarioNew = em.getReference(productoBancarioNew.getClass(), productoBancarioNew.getIdProductoBancario());
                prestamo.setProductoBancario(productoBancarioNew);
            }
            prestamo = em.merge(prestamo);
            if (cuentaCorrientePagoOld != null && !cuentaCorrientePagoOld.equals(cuentaCorrientePagoNew)) {
                cuentaCorrientePagoOld.getPrestamoList().remove(prestamo);
                cuentaCorrientePagoOld = em.merge(cuentaCorrientePagoOld);
            }
            if (cuentaCorrientePagoNew != null && !cuentaCorrientePagoNew.equals(cuentaCorrientePagoOld)) {
                cuentaCorrientePagoNew.getPrestamoList().add(prestamo);
                cuentaCorrientePagoNew = em.merge(cuentaCorrientePagoNew);
            }
            if (productoBancarioOld != null && !productoBancarioOld.equals(productoBancarioNew)) {
                productoBancarioOld.setPrestamo(null);
                productoBancarioOld = em.merge(productoBancarioOld);
            }
            if (productoBancarioNew != null && !productoBancarioNew.equals(productoBancarioOld)) {
                productoBancarioNew.setPrestamo(prestamo);
                productoBancarioNew = em.merge(productoBancarioNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = prestamo.getIdPrestamo();
                if (findPrestamo(id) == null) {
                    throw new NonexistentEntityException("The prestamo with id " + id + " no longer exists.");
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
            Prestamo prestamo;
            try {
                prestamo = em.getReference(Prestamo.class, id);
                prestamo.getIdPrestamo();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The prestamo with id " + id + " no longer exists.", enfe);
            }
            CuentaCorriente cuentaCorrientePago = prestamo.getCuentaCorrientePago();
            if (cuentaCorrientePago != null) {
                cuentaCorrientePago.getPrestamoList().remove(prestamo);
                cuentaCorrientePago = em.merge(cuentaCorrientePago);
            }
            ProductoBancario productoBancario = prestamo.getProductoBancario();
            if (productoBancario != null) {
                productoBancario.setPrestamo(null);
                productoBancario = em.merge(productoBancario);
            }
            em.remove(prestamo);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Prestamo> findPrestamoEntities() {
        return findPrestamoEntities(true, -1, -1);
    }

    public List<Prestamo> findPrestamoEntities(int maxResults, int firstResult) {
        return findPrestamoEntities(false, maxResults, firstResult);
    }

    private List<Prestamo> findPrestamoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Prestamo.class));
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

    public Prestamo findPrestamo(Integer id) {
        EntityManager em = getEntityManager();
        Prestamo p;
        try {
            p = em.find(Prestamo.class, id);
            if (p != null) 
                em.refresh(p); 
            return p;
        } finally {
            em.close();
        }
    }

    public int getPrestamoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Prestamo> rt = cq.from(Prestamo.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
