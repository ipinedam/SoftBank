package controller;

//<editor-fold defaultstate="collapsed" desc=" Librerías importadas... ">
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
import model.entity.Empleado;
import model.entity.Usuario;
import model.entity.ProductoBancario;
//</editor-fold>

/**
 *
 * @author Ignacio Pineda Martín
 */
public class ClienteJpaController implements Serializable {

    private EntityManagerFactory emf = null;
    
    public ClienteJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }    

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Cliente cliente) {
        if (cliente.getProductoBancarioList() == null) {
            cliente.setProductoBancarioList(new ArrayList<ProductoBancario>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Direccion direccion = cliente.getDireccion();
            if (direccion != null) {
                direccion = em.getReference(direccion.getClass(), direccion.getIdDireccion());
                cliente.setDireccion(direccion);
            }
            Empleado empleado = cliente.getEmpleado();
            if (empleado != null) {
                empleado = em.getReference(empleado.getClass(), empleado.getIdEmpleado());
                cliente.setEmpleado(empleado);
            }
            Usuario usuario = cliente.getUsuario();
            if (usuario != null) {
                usuario = em.getReference(usuario.getClass(), usuario.getIdUsuario());
                cliente.setUsuario(usuario);
            }
            List<ProductoBancario> attachedProductoBancarioList = new ArrayList<ProductoBancario>();
            for (ProductoBancario productoBancarioListProductoBancarioToAttach : cliente.getProductoBancarioList()) {
                productoBancarioListProductoBancarioToAttach = em.getReference(productoBancarioListProductoBancarioToAttach.getClass(), productoBancarioListProductoBancarioToAttach.getIdProductoBancario());
                attachedProductoBancarioList.add(productoBancarioListProductoBancarioToAttach);
            }
            cliente.setProductoBancarioList(attachedProductoBancarioList);
            em.persist(cliente);
            if (direccion != null) {
                direccion.getClienteList().add(cliente);
                direccion = em.merge(direccion);
            }
            if (empleado != null) {
                empleado.getClienteList().add(cliente);
                empleado = em.merge(empleado);
            }
            if (usuario != null) {
                Cliente oldClienteOfUsuario = usuario.getCliente();
                if (oldClienteOfUsuario != null) {
                    oldClienteOfUsuario.setUsuario(null);
                    oldClienteOfUsuario = em.merge(oldClienteOfUsuario);
                }
                usuario.setCliente(cliente);
                usuario = em.merge(usuario);
            }
            for (ProductoBancario productoBancarioListProductoBancario : cliente.getProductoBancarioList()) {
                productoBancarioListProductoBancario.getClienteList().add(cliente);
                productoBancarioListProductoBancario = em.merge(productoBancarioListProductoBancario);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Cliente cliente) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Cliente persistentCliente = em.find(Cliente.class, cliente.getIdCliente());
            Direccion direccionOld = persistentCliente.getDireccion();
            Direccion direccionNew = cliente.getDireccion();
            Empleado empleadoOld = persistentCliente.getEmpleado();
            Empleado empleadoNew = cliente.getEmpleado();
            Usuario usuarioOld = persistentCliente.getUsuario();
            Usuario usuarioNew = cliente.getUsuario();
            List<ProductoBancario> productoBancarioListOld = persistentCliente.getProductoBancarioList();
            List<ProductoBancario> productoBancarioListNew = cliente.getProductoBancarioList();
            if (direccionNew != null) {
//                direccionNew = em.getReference(direccionNew.getClass(), direccionNew.getIdDireccion());
                cliente.setDireccion(direccionNew);
            }
            if (empleadoNew != null) {
//                empleadoNew = em.getReference(empleadoNew.getClass(), empleadoNew.getIdEmpleado());
                cliente.setEmpleado(empleadoNew);
            }
            if (usuarioNew != null) {
//                usuarioNew = em.getReference(usuarioNew.getClass(), usuarioNew.getIdUsuario());
                cliente.setUsuario(usuarioNew);
            }
            List<ProductoBancario> attachedProductoBancarioListNew = new ArrayList<ProductoBancario>();
            for (ProductoBancario productoBancarioListNewProductoBancarioToAttach : productoBancarioListNew) {
                productoBancarioListNewProductoBancarioToAttach = em.getReference(productoBancarioListNewProductoBancarioToAttach.getClass(), productoBancarioListNewProductoBancarioToAttach.getIdProductoBancario());
                attachedProductoBancarioListNew.add(productoBancarioListNewProductoBancarioToAttach);
            }
            productoBancarioListNew = attachedProductoBancarioListNew;
            cliente.setProductoBancarioList(productoBancarioListNew);
            cliente = em.merge(cliente);
            if (direccionOld != null && !direccionOld.equals(direccionNew)) {
                direccionOld.getClienteList().remove(cliente);
                direccionOld = em.merge(direccionOld);
            }
            if (direccionNew != null && !direccionNew.equals(direccionOld)) {
                direccionNew.getClienteList().add(cliente);
                direccionNew = em.merge(direccionNew);
            }
            if (empleadoOld != null && !empleadoOld.equals(empleadoNew)) {
                empleadoOld.getClienteList().remove(cliente);
                empleadoOld = em.merge(empleadoOld);
            }
            if (empleadoNew != null && !empleadoNew.equals(empleadoOld)) {
                empleadoNew.getClienteList().add(cliente);
                empleadoNew = em.merge(empleadoNew);
            }
            if (usuarioOld != null && !usuarioOld.equals(usuarioNew)) {
                usuarioOld.setCliente(null);
                usuarioOld = em.merge(usuarioOld);
            }
            if (usuarioNew != null && !usuarioNew.equals(usuarioOld)) {
//                Cliente oldClienteOfUsuario = usuarioNew.getCliente();
//                if (oldClienteOfUsuario != null) {
//                    oldClienteOfUsuario.setUsuario(null);
//                    oldClienteOfUsuario = em.merge(oldClienteOfUsuario);
//                }
                usuarioNew.setCliente(cliente);
                usuarioNew = em.merge(usuarioNew);
            }
            for (ProductoBancario productoBancarioListOldProductoBancario : productoBancarioListOld) {
                if (!productoBancarioListNew.contains(productoBancarioListOldProductoBancario)) {
                    productoBancarioListOldProductoBancario.getClienteList().remove(cliente);
                    productoBancarioListOldProductoBancario = em.merge(productoBancarioListOldProductoBancario);
                }
            }
            for (ProductoBancario productoBancarioListNewProductoBancario : productoBancarioListNew) {
                if (!productoBancarioListOld.contains(productoBancarioListNewProductoBancario)) {
                    productoBancarioListNewProductoBancario.getClienteList().add(cliente);
                    productoBancarioListNewProductoBancario = em.merge(productoBancarioListNewProductoBancario);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = cliente.getIdCliente();
                if (findCliente(id) == null) {
                    throw new NonexistentEntityException("The cliente with id " + id + " no longer exists.");
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
            Cliente cliente;
            try {
                cliente = em.getReference(Cliente.class, id);
                cliente.getIdCliente();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The cliente with id " + id + " no longer exists.", enfe);
            }
            Direccion direccion = cliente.getDireccion();
            if (direccion != null) {
                direccion.getClienteList().remove(cliente);
                direccion = em.merge(direccion);
                // Eliminamos la dirección si no está asignada a ningún objeto.
                if (direccion.getClienteList().isEmpty() && direccion.getSucursalList().isEmpty())
                    em.remove(direccion);                
            }
            Empleado empleado = cliente.getEmpleado();
            if (empleado != null) {
                empleado.getClienteList().remove(cliente);
                empleado = em.merge(empleado);
            }
            Usuario usuario = cliente.getUsuario();
            if (usuario != null) {
                usuario.setCliente(null);
                // Eliminamos el usuario asociado.
                em.remove(usuario);
            }
            List<ProductoBancario> productoBancarioList = cliente.getProductoBancarioList();
            for (ProductoBancario productoBancarioListProductoBancario : productoBancarioList) {
                productoBancarioListProductoBancario.getClienteList().remove(cliente);
                productoBancarioListProductoBancario = em.merge(productoBancarioListProductoBancario);
            }
            em.remove(cliente);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Cliente> findClienteEntities() {
        return findClienteEntities(true, -1, -1);
    }

    public List<Cliente> findClienteEntities(int maxResults, int firstResult) {
        return findClienteEntities(false, maxResults, firstResult);
    }

    private List<Cliente> findClienteEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Cliente.class));
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

    public Cliente findCliente(Integer id) {
        EntityManager em = getEntityManager();
        Cliente c;
        try {
            c = em.find(Cliente.class, id);
            if (c != null)
                em.refresh(c);
            return c;
        } finally {
            em.close();
        }
    }

    public int getClienteCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Cliente> rt = cq.from(Cliente.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
