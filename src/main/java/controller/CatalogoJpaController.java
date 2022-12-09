package controller;

//<editor-fold defaultstate="collapsed" desc=" Librerías importadas... ">
import controller.exceptions.IllegalOrphanException;
import controller.exceptions.NonexistentEntityException;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;

import model.entity.Catalogo;
import model.entity.ProductoBancario;
//</editor-fold>

/**
 *
 * @author Ignacio Pineda Martín
 */
@SuppressWarnings("serial")
public class CatalogoJpaController implements Serializable {
    
    private EntityManagerFactory emf = null;

    public CatalogoJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Catalogo catalogo) {
        if (catalogo.getProductoBancarioList() == null) {
            catalogo.setProductoBancarioList(new ArrayList<ProductoBancario>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<ProductoBancario> attachedProductoBancarioList = new ArrayList<ProductoBancario>();
            for (ProductoBancario productoBancarioListProductoBancarioToAttach : catalogo.getProductoBancarioList()) {
                productoBancarioListProductoBancarioToAttach = em.getReference(productoBancarioListProductoBancarioToAttach.getClass(), productoBancarioListProductoBancarioToAttach.getIdProductoBancario());
                attachedProductoBancarioList.add(productoBancarioListProductoBancarioToAttach);
            }
            catalogo.setProductoBancarioList(attachedProductoBancarioList);
            em.persist(catalogo);
            for (ProductoBancario productoBancarioListProductoBancario : catalogo.getProductoBancarioList()) {
                Catalogo oldCatalogoOfProductoBancarioListProductoBancario = productoBancarioListProductoBancario.getCatalogo();
                productoBancarioListProductoBancario.setCatalogo(catalogo);
                productoBancarioListProductoBancario = em.merge(productoBancarioListProductoBancario);
                if (oldCatalogoOfProductoBancarioListProductoBancario != null) {
                    oldCatalogoOfProductoBancarioListProductoBancario.getProductoBancarioList().remove(productoBancarioListProductoBancario);
                    oldCatalogoOfProductoBancarioListProductoBancario = em.merge(oldCatalogoOfProductoBancarioListProductoBancario);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Catalogo catalogo) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Catalogo persistentCatalogo = em.find(Catalogo.class, catalogo.getIdCatalogo());
            List<ProductoBancario> productoBancarioListOld = persistentCatalogo.getProductoBancarioList();
            List<ProductoBancario> productoBancarioListNew = catalogo.getProductoBancarioList();
            List<String> illegalOrphanMessages = null;
            for (ProductoBancario productoBancarioListOldProductoBancario : productoBancarioListOld) {
                if (!productoBancarioListNew.contains(productoBancarioListOldProductoBancario)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain ProductoBancario " + productoBancarioListOldProductoBancario + " since its catalogo field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<ProductoBancario> attachedProductoBancarioListNew = new ArrayList<ProductoBancario>();
            for (ProductoBancario productoBancarioListNewProductoBancarioToAttach : productoBancarioListNew) {
                productoBancarioListNewProductoBancarioToAttach = em.getReference(productoBancarioListNewProductoBancarioToAttach.getClass(), productoBancarioListNewProductoBancarioToAttach.getIdProductoBancario());
                attachedProductoBancarioListNew.add(productoBancarioListNewProductoBancarioToAttach);
            }
            productoBancarioListNew = attachedProductoBancarioListNew;
            catalogo.setProductoBancarioList(productoBancarioListNew);
            catalogo = em.merge(catalogo);
            for (ProductoBancario productoBancarioListNewProductoBancario : productoBancarioListNew) {
                if (!productoBancarioListOld.contains(productoBancarioListNewProductoBancario)) {
                    Catalogo oldCatalogoOfProductoBancarioListNewProductoBancario = productoBancarioListNewProductoBancario.getCatalogo();
                    productoBancarioListNewProductoBancario.setCatalogo(catalogo);
                    productoBancarioListNewProductoBancario = em.merge(productoBancarioListNewProductoBancario);
                    if (oldCatalogoOfProductoBancarioListNewProductoBancario != null && !oldCatalogoOfProductoBancarioListNewProductoBancario.equals(catalogo)) {
                        oldCatalogoOfProductoBancarioListNewProductoBancario.getProductoBancarioList().remove(productoBancarioListNewProductoBancario);
                        oldCatalogoOfProductoBancarioListNewProductoBancario = em.merge(oldCatalogoOfProductoBancarioListNewProductoBancario);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = catalogo.getIdCatalogo();
                if (findCatalogo(id) == null) {
                    throw new NonexistentEntityException("The catalogo with id " + id + " no longer exists.");
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
            Catalogo catalogo;
            try {
                catalogo = em.getReference(Catalogo.class, id);
                catalogo.getIdCatalogo();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The catalogo with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<ProductoBancario> productoBancarioListOrphanCheck = catalogo.getProductoBancarioList();
            for (ProductoBancario productoBancarioListOrphanCheckProductoBancario : productoBancarioListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Catalogo (" + catalogo + ") cannot be destroyed since the ProductoBancario " + productoBancarioListOrphanCheckProductoBancario + " in its productoBancarioList field has a non-nullable catalogo field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(catalogo);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Catalogo> findCatalogoEntities() {
        return findCatalogoEntities(true, -1, -1);
    }

    public List<Catalogo> findCatalogoEntities(int maxResults, int firstResult) {
        return findCatalogoEntities(false, maxResults, firstResult);
    }

    private List<Catalogo> findCatalogoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Catalogo.class));
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

    public Catalogo findCatalogo(Integer id) {
        EntityManager em = getEntityManager();
        Catalogo c;
        try {
            c = em.find(Catalogo.class, id);
            if (c != null) 
                em.refresh(c);            
            return c;
        } finally {
            em.close();
        }
    }

    public int getCatalogoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Catalogo> rt = cq.from(Catalogo.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
