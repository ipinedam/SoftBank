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
import javax.persistence.Persistence;
import javax.persistence.Query;

import model.entity.Direccion;
import model.entity.Empleado;
import model.entity.ProductoBancario;
import model.entity.Sucursal;
//</editor-fold>

/**
 *
 * @author Ignacio Pineda Martín
 */
public class SucursalJpaController implements Serializable {
    
    private EntityManagerFactory emf = null;
    
    public SucursalJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    
    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Sucursal sucursal) {
        if (sucursal.getEmpleadoList() == null) {
            sucursal.setEmpleadoList(new ArrayList<Empleado>());
        }
        if (sucursal.getProductoBancarioList() == null) {
            sucursal.setProductoBancarioList(new ArrayList<ProductoBancario>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Direccion direccion = sucursal.getDireccion();
            if (direccion != null) {
                direccion = em.getReference(direccion.getClass(), direccion.getIdDireccion());
                sucursal.setDireccion(direccion);
            }
            List<Empleado> attachedEmpleadoList = new ArrayList<Empleado>();
            for (Empleado empleadoListEmpleadoToAttach : sucursal.getEmpleadoList()) {
                empleadoListEmpleadoToAttach = em.getReference(empleadoListEmpleadoToAttach.getClass(), empleadoListEmpleadoToAttach.getIdEmpleado());
                attachedEmpleadoList.add(empleadoListEmpleadoToAttach);
            }
            sucursal.setEmpleadoList(attachedEmpleadoList);
            List<ProductoBancario> attachedProductoBancarioList = new ArrayList<ProductoBancario>();
            for (ProductoBancario productoBancarioListProductoBancarioToAttach : sucursal.getProductoBancarioList()) {
                productoBancarioListProductoBancarioToAttach = em.getReference(productoBancarioListProductoBancarioToAttach.getClass(), productoBancarioListProductoBancarioToAttach.getIdProductoBancario());
                attachedProductoBancarioList.add(productoBancarioListProductoBancarioToAttach);
            }
            sucursal.setProductoBancarioList(attachedProductoBancarioList);
            em.persist(sucursal);
            if (direccion != null) {
                direccion.getSucursalList().add(sucursal);
                direccion = em.merge(direccion);
            }
            for (Empleado empleadoListEmpleado : sucursal.getEmpleadoList()) {
                Sucursal oldSucursalOfEmpleadoListEmpleado = empleadoListEmpleado.getSucursal();
                empleadoListEmpleado.setSucursal(sucursal);
                empleadoListEmpleado = em.merge(empleadoListEmpleado);
                if (oldSucursalOfEmpleadoListEmpleado != null) {
                    oldSucursalOfEmpleadoListEmpleado.getEmpleadoList().remove(empleadoListEmpleado);
                    oldSucursalOfEmpleadoListEmpleado = em.merge(oldSucursalOfEmpleadoListEmpleado);
                }
            }
            for (ProductoBancario productoBancarioListProductoBancario : sucursal.getProductoBancarioList()) {
                Sucursal oldSucursalOfProductoBancarioListProductoBancario = productoBancarioListProductoBancario.getSucursal();
                productoBancarioListProductoBancario.setSucursal(sucursal);
                productoBancarioListProductoBancario = em.merge(productoBancarioListProductoBancario);
                if (oldSucursalOfProductoBancarioListProductoBancario != null) {
                    oldSucursalOfProductoBancarioListProductoBancario.getProductoBancarioList().remove(productoBancarioListProductoBancario);
                    oldSucursalOfProductoBancarioListProductoBancario = em.merge(oldSucursalOfProductoBancarioListProductoBancario);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Sucursal sucursal) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Sucursal persistentSucursal = em.find(Sucursal.class, sucursal.getIdSucursal());
            Direccion direccionOld = persistentSucursal.getDireccion();
            Direccion direccionNew = sucursal.getDireccion();
            List<Empleado> empleadoListOld = persistentSucursal.getEmpleadoList();
            List<Empleado> empleadoListNew = sucursal.getEmpleadoList();
            List<ProductoBancario> productoBancarioListOld = persistentSucursal.getProductoBancarioList();
            List<ProductoBancario> productoBancarioListNew = sucursal.getProductoBancarioList();
            List<String> illegalOrphanMessages = null;
            for (Empleado empleadoListOldEmpleado : empleadoListOld) {
                if (!empleadoListNew.contains(empleadoListOldEmpleado)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Empleado " + empleadoListOldEmpleado + " since its sucursal field is not nullable.");
                }
            }
            for (ProductoBancario productoBancarioListOldProductoBancario : productoBancarioListOld) {
                if (!productoBancarioListNew.contains(productoBancarioListOldProductoBancario)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain ProductoBancario " + productoBancarioListOldProductoBancario + " since its sucursal field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (direccionNew != null) {
//                direccionNew = em.getReference(direccionNew.getClass(), direccionNew.getIdDireccion());
                sucursal.setDireccion(direccionNew);
            }
            List<Empleado> attachedEmpleadoListNew = new ArrayList<Empleado>();
            for (Empleado empleadoListNewEmpleadoToAttach : empleadoListNew) {
                empleadoListNewEmpleadoToAttach = em.getReference(empleadoListNewEmpleadoToAttach.getClass(), empleadoListNewEmpleadoToAttach.getIdEmpleado());
                attachedEmpleadoListNew.add(empleadoListNewEmpleadoToAttach);
            }
            empleadoListNew = attachedEmpleadoListNew;
            sucursal.setEmpleadoList(empleadoListNew);
            List<ProductoBancario> attachedProductoBancarioListNew = new ArrayList<ProductoBancario>();
            for (ProductoBancario productoBancarioListNewProductoBancarioToAttach : productoBancarioListNew) {
                productoBancarioListNewProductoBancarioToAttach = em.getReference(productoBancarioListNewProductoBancarioToAttach.getClass(), productoBancarioListNewProductoBancarioToAttach.getIdProductoBancario());
                attachedProductoBancarioListNew.add(productoBancarioListNewProductoBancarioToAttach);
            }
            productoBancarioListNew = attachedProductoBancarioListNew;
            sucursal.setProductoBancarioList(productoBancarioListNew);
            sucursal = em.merge(sucursal);
            if (direccionOld != null && !direccionOld.equals(direccionNew)) {
                direccionOld.getSucursalList().remove(sucursal);
                direccionOld = em.merge(direccionOld);
            }
            if (direccionNew != null && !direccionNew.equals(direccionOld)) {
                direccionNew.getSucursalList().add(sucursal);
                direccionNew = em.merge(direccionNew);
            }
            for (Empleado empleadoListNewEmpleado : empleadoListNew) {
                if (!empleadoListOld.contains(empleadoListNewEmpleado)) {
                    Sucursal oldSucursalOfEmpleadoListNewEmpleado = empleadoListNewEmpleado.getSucursal();
                    empleadoListNewEmpleado.setSucursal(sucursal);
                    empleadoListNewEmpleado = em.merge(empleadoListNewEmpleado);
                    if (oldSucursalOfEmpleadoListNewEmpleado != null && !oldSucursalOfEmpleadoListNewEmpleado.equals(sucursal)) {
                        oldSucursalOfEmpleadoListNewEmpleado.getEmpleadoList().remove(empleadoListNewEmpleado);
                        oldSucursalOfEmpleadoListNewEmpleado = em.merge(oldSucursalOfEmpleadoListNewEmpleado);
                    }
                }
            }
            for (ProductoBancario productoBancarioListNewProductoBancario : productoBancarioListNew) {
                if (!productoBancarioListOld.contains(productoBancarioListNewProductoBancario)) {
                    Sucursal oldSucursalOfProductoBancarioListNewProductoBancario = productoBancarioListNewProductoBancario.getSucursal();
                    productoBancarioListNewProductoBancario.setSucursal(sucursal);
                    productoBancarioListNewProductoBancario = em.merge(productoBancarioListNewProductoBancario);
                    if (oldSucursalOfProductoBancarioListNewProductoBancario != null && !oldSucursalOfProductoBancarioListNewProductoBancario.equals(sucursal)) {
                        oldSucursalOfProductoBancarioListNewProductoBancario.getProductoBancarioList().remove(productoBancarioListNewProductoBancario);
                        oldSucursalOfProductoBancarioListNewProductoBancario = em.merge(oldSucursalOfProductoBancarioListNewProductoBancario);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = sucursal.getIdSucursal();
                if (findSucursal(id) == null) {
                    throw new NonexistentEntityException("The sucursal with id " + id + " no longer exists.");
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
            Sucursal sucursal;
            try {
                sucursal = em.getReference(Sucursal.class, id);
                sucursal.getIdSucursal();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The sucursal with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Empleado> empleadoListOrphanCheck = sucursal.getEmpleadoList();
            for (Empleado empleadoListOrphanCheckEmpleado : empleadoListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Sucursal (" + sucursal + ") cannot be destroyed since the Empleado " + empleadoListOrphanCheckEmpleado + " in its empleadoList field has a non-nullable sucursal field.");
            }
            List<ProductoBancario> productoBancarioListOrphanCheck = sucursal.getProductoBancarioList();
            for (ProductoBancario productoBancarioListOrphanCheckProductoBancario : productoBancarioListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Sucursal (" + sucursal + ") cannot be destroyed since the ProductoBancario " + productoBancarioListOrphanCheckProductoBancario + " in its productoBancarioList field has a non-nullable sucursal field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Direccion direccion = sucursal.getDireccion();
            if (direccion != null) {
                direccion.getSucursalList().remove(sucursal);
                direccion = em.merge(direccion);
                // Eliminamos la dirección si no está asignada a ningún objeto.
                if (direccion.getClienteList().isEmpty() && direccion.getSucursalList().isEmpty())
                    em.remove(direccion);
            }
            em.remove(sucursal);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Sucursal> findSucursalEntities() {
        return findSucursalEntities(true, -1, -1);
    }

    public List<Sucursal> findSucursalEntities(int maxResults, int firstResult) {
        return findSucursalEntities(false, maxResults, firstResult);
    }

    private List<Sucursal> findSucursalEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Sucursal.class));
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

    public Sucursal findSucursal(Integer id) {
        EntityManager em = getEntityManager();
        Sucursal s;
        try {
            s = em.find(Sucursal.class, id);
            if (s != null)
                em.refresh(s);            
            return s;
        } finally {
            em.close();
        }
    }

    public int getSucursalCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Sucursal> rt = cq.from(Sucursal.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
