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

import model.entity.Aval;
import model.entity.ProductoBancario;
//</editor-fold>

/**
 *
 * @author Ignacio Pineda Martín
 */
public class AvalJpaController implements Serializable {
    
    private EntityManagerFactory emf = null;
    
    public AvalJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Aval aval) throws IllegalOrphanException {
        List<String> illegalOrphanMessages = null;
        ProductoBancario productoBancarioOrphanCheck = aval.getProductoBancario();
        if (productoBancarioOrphanCheck != null) {
            Aval oldAvalOfProductoBancario = productoBancarioOrphanCheck.getAval();
            if (oldAvalOfProductoBancario != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("The ProductoBancario " + productoBancarioOrphanCheck + " already has an item of type Aval whose productoBancario column cannot be null. Please make another selection for the productoBancario field.");
            }
        }
        if (illegalOrphanMessages != null) {
            throw new IllegalOrphanException(illegalOrphanMessages);
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            ProductoBancario productoBancario = aval.getProductoBancario();
            if (productoBancario != null) {
                productoBancario = em.getReference(productoBancario.getClass(), productoBancario.getIdProductoBancario());
                aval.setProductoBancario(productoBancario);
            }
            em.persist(aval);
            if (productoBancario != null) {
                productoBancario.setAval(aval);
                productoBancario = em.merge(productoBancario);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Aval aval) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Aval persistentAval = em.find(Aval.class, aval.getIdAval());
            ProductoBancario productoBancarioOld = persistentAval.getProductoBancario();
            ProductoBancario productoBancarioNew = aval.getProductoBancario();
            List<String> illegalOrphanMessages = null;
            if (productoBancarioNew != null && !productoBancarioNew.equals(productoBancarioOld)) {
                Aval oldAvalOfProductoBancario = productoBancarioNew.getAval();
                if (oldAvalOfProductoBancario != null) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("The ProductoBancario " + productoBancarioNew + " already has an item of type Aval whose productoBancario column cannot be null. Please make another selection for the productoBancario field.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (productoBancarioNew != null) {
                productoBancarioNew = em.getReference(productoBancarioNew.getClass(), productoBancarioNew.getIdProductoBancario());
                aval.setProductoBancario(productoBancarioNew);
            }
            aval = em.merge(aval);
            if (productoBancarioOld != null && !productoBancarioOld.equals(productoBancarioNew)) {
                productoBancarioOld.setAval(null);
                productoBancarioOld = em.merge(productoBancarioOld);
            }
            if (productoBancarioNew != null && !productoBancarioNew.equals(productoBancarioOld)) {
                productoBancarioNew.setAval(aval);
                productoBancarioNew = em.merge(productoBancarioNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = aval.getIdAval();
                if (findAval(id) == null) {
                    throw new NonexistentEntityException("The aval with id " + id + " no longer exists.");
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
            Aval aval;
            try {
                aval = em.getReference(Aval.class, id);
                aval.getIdAval();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The aval with id " + id + " no longer exists.", enfe);
            }
            ProductoBancario productoBancario = aval.getProductoBancario();
            if (productoBancario != null) {
                productoBancario.setAval(null);
                productoBancario = em.merge(productoBancario);
            }
            em.remove(aval);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Aval> findAvalEntities() {
        return findAvalEntities(true, -1, -1);
    }

    public List<Aval> findAvalEntities(int maxResults, int firstResult) {
        return findAvalEntities(false, maxResults, firstResult);
    }

    private List<Aval> findAvalEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Aval.class));
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

    public Aval findAval(Integer id) {
        EntityManager em = getEntityManager();
        Aval a;
        try {
            a = em.find(Aval.class, id);
            if (a != null) 
                em.refresh(a);
            return a;
        } finally {
            em.close();
        }
    }

    public int getAvalCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Aval> rt = cq.from(Aval.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
