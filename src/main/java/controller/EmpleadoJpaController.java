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
import model.entity.Empleado;
import model.entity.Sucursal;
import model.entity.Usuario;
//</editor-fold>

/**
 *
 * @author Ignacio Pineda Martín
 */
public class EmpleadoJpaController implements Serializable {

    public EmpleadoJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Empleado empleado) throws IllegalOrphanException {
        if (empleado.getClienteList() == null) {
            empleado.setClienteList(new ArrayList<Cliente>());
        }
        List<String> illegalOrphanMessages = null;
        Usuario usuarioOrphanCheck = empleado.getUsuario();
        if (usuarioOrphanCheck != null) {
            Empleado oldEmpleadoOfUsuario = usuarioOrphanCheck.getEmpleado();
            if (oldEmpleadoOfUsuario != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("The Usuario " + usuarioOrphanCheck + " already has an item of type Empleado whose usuario column cannot be null. Please make another selection for the usuario field.");
            }
        }
        if (illegalOrphanMessages != null) {
            throw new IllegalOrphanException(illegalOrphanMessages);
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Sucursal sucursal = empleado.getSucursal();
            if (sucursal != null) {
                sucursal = em.getReference(sucursal.getClass(), sucursal.getIdSucursal());
                empleado.setSucursal(sucursal);
            }
            Usuario usuario = empleado.getUsuario();
            if (usuario != null) {
                usuario = em.getReference(usuario.getClass(), usuario.getIdUsuario());
                empleado.setUsuario(usuario);
            }
            List<Cliente> attachedClienteList = new ArrayList<Cliente>();
            for (Cliente clienteListClienteToAttach : empleado.getClienteList()) {
                clienteListClienteToAttach = em.getReference(clienteListClienteToAttach.getClass(), clienteListClienteToAttach.getIdCliente());
                attachedClienteList.add(clienteListClienteToAttach);
            }
            empleado.setClienteList(attachedClienteList);
            em.persist(empleado);
            if (sucursal != null) {
                sucursal.getEmpleadoList().add(empleado);
                sucursal = em.merge(sucursal);
            }
            if (usuario != null) {
                usuario.setEmpleado(empleado);
                usuario = em.merge(usuario);
            }
            for (Cliente clienteListCliente : empleado.getClienteList()) {
                Empleado oldEmpleadoOfClienteListCliente = clienteListCliente.getEmpleado();
                clienteListCliente.setEmpleado(empleado);
                clienteListCliente = em.merge(clienteListCliente);
                if (oldEmpleadoOfClienteListCliente != null) {
                    oldEmpleadoOfClienteListCliente.getClienteList().remove(clienteListCliente);
                    oldEmpleadoOfClienteListCliente = em.merge(oldEmpleadoOfClienteListCliente);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Empleado empleado) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Empleado persistentEmpleado = em.find(Empleado.class, empleado.getIdEmpleado());
            Sucursal sucursalOld = persistentEmpleado.getSucursal();
            Sucursal sucursalNew = empleado.getSucursal();
            Usuario usuarioOld = persistentEmpleado.getUsuario();
            Usuario usuarioNew = empleado.getUsuario();
            List<Cliente> clienteListOld = persistentEmpleado.getClienteList();
            List<Cliente> clienteListNew = empleado.getClienteList();
            List<String> illegalOrphanMessages = null;
//            if (usuarioNew != null && !usuarioNew.equals(usuarioOld)) {
//                Empleado oldEmpleadoOfUsuario = usuarioNew.getEmpleado();
//                if (oldEmpleadoOfUsuario != null) {
//                    if (illegalOrphanMessages == null) {
//                        illegalOrphanMessages = new ArrayList<String>();
//                    }
//                    illegalOrphanMessages.add("The Usuario " + usuarioNew + " already has an item of type Empleado whose usuario column cannot be null. Please make another selection for the usuario field.");
//                }
//            }
            for (Cliente clienteListOldCliente : clienteListOld) {
                if (!clienteListNew.contains(clienteListOldCliente)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Cliente " + clienteListOldCliente + " since its empleado field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (sucursalNew != null) {
//                sucursalNew = em.getReference(sucursalNew.getClass(), sucursalNew.getIdSucursal());
                empleado.setSucursal(sucursalNew);
            }
            if (usuarioNew != null) {
//                usuarioNew = em.getReference(usuarioNew.getClass(), usuarioNew.getIdUsuario());
                empleado.setUsuario(usuarioNew);
            }
            List<Cliente> attachedClienteListNew = new ArrayList<Cliente>();
            for (Cliente clienteListNewClienteToAttach : clienteListNew) {
                clienteListNewClienteToAttach = em.getReference(clienteListNewClienteToAttach.getClass(), clienteListNewClienteToAttach.getIdCliente());
                attachedClienteListNew.add(clienteListNewClienteToAttach);
            }
            clienteListNew = attachedClienteListNew;
            empleado.setClienteList(clienteListNew);
            empleado = em.merge(empleado);
            if (sucursalOld != null && !sucursalOld.equals(sucursalNew)) {
                sucursalOld.getEmpleadoList().remove(empleado);
                sucursalOld = em.merge(sucursalOld);
            }
            if (sucursalNew != null && !sucursalNew.equals(sucursalOld)) {
                sucursalNew.getEmpleadoList().add(empleado);
                sucursalNew = em.merge(sucursalNew);
            }
            if (usuarioOld != null && !usuarioOld.equals(usuarioNew)) {
                usuarioOld.setEmpleado(null);
                usuarioOld = em.merge(usuarioOld);
            }
            if (usuarioNew != null && !usuarioNew.equals(usuarioOld)) {
                usuarioNew.setEmpleado(empleado);
                usuarioNew = em.merge(usuarioNew);
            }
            for (Cliente clienteListNewCliente : clienteListNew) {
                if (!clienteListOld.contains(clienteListNewCliente)) {
                    Empleado oldEmpleadoOfClienteListNewCliente = clienteListNewCliente.getEmpleado();
                    clienteListNewCliente.setEmpleado(empleado);
                    clienteListNewCliente = em.merge(clienteListNewCliente);
                    if (oldEmpleadoOfClienteListNewCliente != null && !oldEmpleadoOfClienteListNewCliente.equals(empleado)) {
                        oldEmpleadoOfClienteListNewCliente.getClienteList().remove(clienteListNewCliente);
                        oldEmpleadoOfClienteListNewCliente = em.merge(oldEmpleadoOfClienteListNewCliente);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = empleado.getIdEmpleado();
                if (findEmpleado(id) == null) {
                    throw new NonexistentEntityException("The empleado with id " + id + " no longer exists.");
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
            Empleado empleado;
            try {
                empleado = em.getReference(Empleado.class, id);
                empleado.getIdEmpleado();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The empleado with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Cliente> clienteListOrphanCheck = empleado.getClienteList();
            for (Cliente clienteListOrphanCheckCliente : clienteListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Empleado (" + empleado + ") cannot be destroyed since the Cliente " + clienteListOrphanCheckCliente + " in its clienteList field has a non-nullable empleado field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Sucursal sucursal = empleado.getSucursal();
            if (sucursal != null) {
                sucursal.getEmpleadoList().remove(empleado);
                sucursal = em.merge(sucursal);
            }
            Usuario usuario = empleado.getUsuario();
            if (usuario != null) {
                usuario.setEmpleado(null);
                // Eliminamos el usuario asociado.
                em.remove(usuario);
            }
            em.remove(empleado);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Empleado> findEmpleadoEntities() {
        return findEmpleadoEntities(true, -1, -1);
    }

    public List<Empleado> findEmpleadoEntities(int maxResults, int firstResult) {
        return findEmpleadoEntities(false, maxResults, firstResult);
    }

    private List<Empleado> findEmpleadoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Empleado.class));
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

    public Empleado findEmpleado(Integer id) {
        EntityManager em = getEntityManager();
        Empleado e;
        try {
            e = em.find(Empleado.class, id);
            if (e != null)
                em.refresh(e);            
            return e;
        } finally {
            em.close();
        }
    }

    public int getEmpleadoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Empleado> rt = cq.from(Empleado.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
