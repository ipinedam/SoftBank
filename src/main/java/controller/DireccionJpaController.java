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

import model.entity.Cliente;
import model.entity.Direccion;
import model.entity.Sucursal;
//</editor-fold>

/**
 *
 * @author Ignacio Pineda Martín
 */
public class DireccionJpaController implements Serializable {

    private EntityManagerFactory emf = null;

    public DireccionJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }    
    
    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Direccion direccion) {
        if (direccion.getClienteList() == null) {
            direccion.setClienteList(new ArrayList<Cliente>());
        }
        if (direccion.getSucursalList() == null) {
            direccion.setSucursalList(new ArrayList<Sucursal>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Cliente> attachedClienteList = new ArrayList<Cliente>();
            for (Cliente clienteListClienteToAttach : direccion.getClienteList()) {
                clienteListClienteToAttach = em.getReference(clienteListClienteToAttach.getClass(), clienteListClienteToAttach.getIdCliente());
                attachedClienteList.add(clienteListClienteToAttach);
            }
            direccion.setClienteList(attachedClienteList);
            List<Sucursal> attachedSucursalList = new ArrayList<Sucursal>();
            for (Sucursal sucursalListSucursalToAttach : direccion.getSucursalList()) {
                sucursalListSucursalToAttach = em.getReference(sucursalListSucursalToAttach.getClass(), sucursalListSucursalToAttach.getIdSucursal());
                attachedSucursalList.add(sucursalListSucursalToAttach);
            }
            direccion.setSucursalList(attachedSucursalList);
            em.persist(direccion);
            for (Cliente clienteListCliente : direccion.getClienteList()) {
                Direccion oldDireccionOfClienteListCliente = clienteListCliente.getDireccion();
                clienteListCliente.setDireccion(direccion);
                clienteListCliente = em.merge(clienteListCliente);
                if (oldDireccionOfClienteListCliente != null) {
                    oldDireccionOfClienteListCliente.getClienteList().remove(clienteListCliente);
                    oldDireccionOfClienteListCliente = em.merge(oldDireccionOfClienteListCliente);
                }
            }
            for (Sucursal sucursalListSucursal : direccion.getSucursalList()) {
                Direccion oldDireccionOfSucursalListSucursal = sucursalListSucursal.getDireccion();
                sucursalListSucursal.setDireccion(direccion);
                sucursalListSucursal = em.merge(sucursalListSucursal);
                if (oldDireccionOfSucursalListSucursal != null) {
                    oldDireccionOfSucursalListSucursal.getSucursalList().remove(sucursalListSucursal);
                    oldDireccionOfSucursalListSucursal = em.merge(oldDireccionOfSucursalListSucursal);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Direccion direccion) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Direccion persistentDireccion = em.find(Direccion.class, direccion.getIdDireccion());
            List<Cliente> clienteListOld = persistentDireccion.getClienteList();
            List<Cliente> clienteListNew = direccion.getClienteList();
            List<Sucursal> sucursalListOld = persistentDireccion.getSucursalList();
            List<Sucursal> sucursalListNew = direccion.getSucursalList();
            List<String> illegalOrphanMessages = null;
            for (Cliente clienteListOldCliente : clienteListOld) {
                if (!clienteListNew.contains(clienteListOldCliente)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Cliente " + clienteListOldCliente + " since its direccion field is not nullable.");
                }
            }
            for (Sucursal sucursalListOldSucursal : sucursalListOld) {
                if (!sucursalListNew.contains(sucursalListOldSucursal)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Sucursal " + sucursalListOldSucursal + " since its direccion field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Cliente> attachedClienteListNew = new ArrayList<Cliente>();
            for (Cliente clienteListNewClienteToAttach : clienteListNew) {
                clienteListNewClienteToAttach = em.getReference(clienteListNewClienteToAttach.getClass(), clienteListNewClienteToAttach.getIdCliente());
                attachedClienteListNew.add(clienteListNewClienteToAttach);
            }
            clienteListNew = attachedClienteListNew;
            direccion.setClienteList(clienteListNew);
            List<Sucursal> attachedSucursalListNew = new ArrayList<Sucursal>();
            for (Sucursal sucursalListNewSucursalToAttach : sucursalListNew) {
                sucursalListNewSucursalToAttach = em.getReference(sucursalListNewSucursalToAttach.getClass(), sucursalListNewSucursalToAttach.getIdSucursal());
                attachedSucursalListNew.add(sucursalListNewSucursalToAttach);
            }
            sucursalListNew = attachedSucursalListNew;
            direccion.setSucursalList(sucursalListNew);
            direccion = em.merge(direccion);
            for (Cliente clienteListNewCliente : clienteListNew) {
                if (!clienteListOld.contains(clienteListNewCliente)) {
                    Direccion oldDireccionOfClienteListNewCliente = clienteListNewCliente.getDireccion();
                    clienteListNewCliente.setDireccion(direccion);
                    clienteListNewCliente = em.merge(clienteListNewCliente);
                    if (oldDireccionOfClienteListNewCliente != null && !oldDireccionOfClienteListNewCliente.equals(direccion)) {
                        oldDireccionOfClienteListNewCliente.getClienteList().remove(clienteListNewCliente);
                        oldDireccionOfClienteListNewCliente = em.merge(oldDireccionOfClienteListNewCliente);
                    }
                }
            }
            for (Sucursal sucursalListNewSucursal : sucursalListNew) {
                if (!sucursalListOld.contains(sucursalListNewSucursal)) {
                    Direccion oldDireccionOfSucursalListNewSucursal = sucursalListNewSucursal.getDireccion();
                    sucursalListNewSucursal.setDireccion(direccion);
                    sucursalListNewSucursal = em.merge(sucursalListNewSucursal);
                    if (oldDireccionOfSucursalListNewSucursal != null && !oldDireccionOfSucursalListNewSucursal.equals(direccion)) {
                        oldDireccionOfSucursalListNewSucursal.getSucursalList().remove(sucursalListNewSucursal);
                        oldDireccionOfSucursalListNewSucursal = em.merge(oldDireccionOfSucursalListNewSucursal);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = direccion.getIdDireccion();
                if (findDireccion(id) == null) {
                    throw new NonexistentEntityException("The direccion with id " + id + " no longer exists.");
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
            Direccion direccion;
            try {
                direccion = em.getReference(Direccion.class, id);
                direccion.getIdDireccion();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The direccion with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Cliente> clienteListOrphanCheck = direccion.getClienteList();
            for (Cliente clienteListOrphanCheckCliente : clienteListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Direccion (" + direccion + ") cannot be destroyed since the Cliente " + clienteListOrphanCheckCliente + " in its clienteList field has a non-nullable direccion field.");
            }
            List<Sucursal> sucursalListOrphanCheck = direccion.getSucursalList();
            for (Sucursal sucursalListOrphanCheckSucursal : sucursalListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Direccion (" + direccion + ") cannot be destroyed since the Sucursal " + sucursalListOrphanCheckSucursal + " in its sucursalList field has a non-nullable direccion field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(direccion);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Direccion> findDireccionEntities() {
        return findDireccionEntities(true, -1, -1);
    }

    public List<Direccion> findDireccionEntities(int maxResults, int firstResult) {
        return findDireccionEntities(false, maxResults, firstResult);
    }

    private List<Direccion> findDireccionEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Direccion.class));
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

    public Direccion findDireccion(Integer id) {
        EntityManager em = getEntityManager();
        Direccion d;
        try {
            d = em.find(Direccion.class, id);
            if (d != null)
                em.refresh(d);            
            return d;
        } finally {
            em.close();
        }
    }

    public int getDireccionCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Direccion> rt = cq.from(Direccion.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
