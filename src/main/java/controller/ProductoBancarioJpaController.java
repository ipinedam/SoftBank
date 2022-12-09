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
import model.entity.Catalogo;
import model.entity.Cliente;
import model.entity.CuentaCorriente;
import model.entity.Movimiento;
import model.entity.Prestamo;
import model.entity.ProductoBancario;
import model.entity.Sucursal;
import model.entity.TarjetaCredito;
//</editor-fold>

/**
 *
 * @author Ignacio Pineda Martín
 */
public class ProductoBancarioJpaController implements Serializable {

    private EntityManagerFactory emf = null;    
    
    public ProductoBancarioJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(ProductoBancario productoBancario) {
        if (productoBancario.getClienteList() == null) {
            productoBancario.setClienteList(new ArrayList<Cliente>());
        }
        if (productoBancario.getMovimientoList() == null) {
            productoBancario.setMovimientoList(new ArrayList<Movimiento>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Prestamo prestamo = productoBancario.getPrestamo();
            if (prestamo != null) {
                prestamo = em.getReference(prestamo.getClass(), prestamo.getIdPrestamo());
                productoBancario.setPrestamo(prestamo);
            }
            CuentaCorriente cuentaCorriente = productoBancario.getCuentaCorriente();
            if (cuentaCorriente != null) {
                cuentaCorriente = em.getReference(cuentaCorriente.getClass(), cuentaCorriente.getIdCuentaCorriente());
                productoBancario.setCuentaCorriente(cuentaCorriente);
            }
            TarjetaCredito tarjetaCredito = productoBancario.getTarjetaCredito();
            if (tarjetaCredito != null) {
                tarjetaCredito = em.getReference(tarjetaCredito.getClass(), tarjetaCredito.getIdTarjetaCredito());
                productoBancario.setTarjetaCredito(tarjetaCredito);
            }
            Aval aval = productoBancario.getAval();
            if (aval != null) {
                aval = em.getReference(aval.getClass(), aval.getIdAval());
                productoBancario.setAval(aval);
            }
            Catalogo catalogo = productoBancario.getCatalogo();
            if (catalogo != null) {
                catalogo = em.getReference(catalogo.getClass(), catalogo.getIdCatalogo());
                productoBancario.setCatalogo(catalogo);
            }
            Sucursal sucursal = productoBancario.getSucursal();
            if (sucursal != null) {
                sucursal = em.getReference(sucursal.getClass(), sucursal.getIdSucursal());
                productoBancario.setSucursal(sucursal);
            }
            List<Cliente> attachedClienteList = new ArrayList<Cliente>();
            for (Cliente clienteListClienteToAttach : productoBancario.getClienteList()) {
                clienteListClienteToAttach = em.getReference(clienteListClienteToAttach.getClass(), clienteListClienteToAttach.getIdCliente());
                attachedClienteList.add(clienteListClienteToAttach);
            }
            productoBancario.setClienteList(attachedClienteList);
            List<Movimiento> attachedMovimientoList = new ArrayList<Movimiento>();
            for (Movimiento movimientoListMovimientoToAttach : productoBancario.getMovimientoList()) {
                movimientoListMovimientoToAttach = em.getReference(movimientoListMovimientoToAttach.getClass(), movimientoListMovimientoToAttach.getIdMovimiento());
                attachedMovimientoList.add(movimientoListMovimientoToAttach);
            }
            productoBancario.setMovimientoList(attachedMovimientoList);
            em.persist(productoBancario);
            if (prestamo != null) {
                ProductoBancario oldProductoBancarioOfPrestamo = prestamo.getProductoBancario();
                if (oldProductoBancarioOfPrestamo != null) {
                    oldProductoBancarioOfPrestamo.setPrestamo(null);
                    oldProductoBancarioOfPrestamo = em.merge(oldProductoBancarioOfPrestamo);
                }
                prestamo.setProductoBancario(productoBancario);
                prestamo = em.merge(prestamo);
            }
            if (cuentaCorriente != null) {
                ProductoBancario oldProductoBancarioOfCuentaCorriente = cuentaCorriente.getProductoBancario();
                if (oldProductoBancarioOfCuentaCorriente != null) {
                    oldProductoBancarioOfCuentaCorriente.setCuentaCorriente(null);
                    oldProductoBancarioOfCuentaCorriente = em.merge(oldProductoBancarioOfCuentaCorriente);
                }
                cuentaCorriente.setProductoBancario(productoBancario);
                cuentaCorriente = em.merge(cuentaCorriente);
            }
            if (tarjetaCredito != null) {
                ProductoBancario oldProductoBancarioOfTarjetaCredito = tarjetaCredito.getProductoBancario();
                if (oldProductoBancarioOfTarjetaCredito != null) {
                    oldProductoBancarioOfTarjetaCredito.setTarjetaCredito(null);
                    oldProductoBancarioOfTarjetaCredito = em.merge(oldProductoBancarioOfTarjetaCredito);
                }
                tarjetaCredito.setProductoBancario(productoBancario);
                tarjetaCredito = em.merge(tarjetaCredito);
            }
            if (aval != null) {
                ProductoBancario oldProductoBancarioOfAval = aval.getProductoBancario();
                if (oldProductoBancarioOfAval != null) {
                    oldProductoBancarioOfAval.setAval(null);
                    oldProductoBancarioOfAval = em.merge(oldProductoBancarioOfAval);
                }
                aval.setProductoBancario(productoBancario);
                aval = em.merge(aval);
            }
            if (catalogo != null) {
                catalogo.getProductoBancarioList().add(productoBancario);
                catalogo = em.merge(catalogo);
            }
            if (sucursal != null) {
                sucursal.getProductoBancarioList().add(productoBancario);
                sucursal = em.merge(sucursal);
            }
            for (Cliente clienteListCliente : productoBancario.getClienteList()) {
                clienteListCliente.getProductoBancarioList().add(productoBancario);
                clienteListCliente = em.merge(clienteListCliente);
            }
            for (Movimiento movimientoListMovimiento : productoBancario.getMovimientoList()) {
                ProductoBancario oldProductoBancarioOfMovimientoListMovimiento = movimientoListMovimiento.getProductoBancario();
                movimientoListMovimiento.setProductoBancario(productoBancario);
                movimientoListMovimiento = em.merge(movimientoListMovimiento);
                if (oldProductoBancarioOfMovimientoListMovimiento != null) {
                    oldProductoBancarioOfMovimientoListMovimiento.getMovimientoList().remove(movimientoListMovimiento);
                    oldProductoBancarioOfMovimientoListMovimiento = em.merge(oldProductoBancarioOfMovimientoListMovimiento);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(ProductoBancario productoBancario) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            ProductoBancario persistentProductoBancario = em.find(ProductoBancario.class, productoBancario.getIdProductoBancario());
            Prestamo prestamoOld = persistentProductoBancario.getPrestamo();
            Prestamo prestamoNew = productoBancario.getPrestamo();
            CuentaCorriente cuentaCorrienteOld = persistentProductoBancario.getCuentaCorriente();
            CuentaCorriente cuentaCorrienteNew = productoBancario.getCuentaCorriente();
            TarjetaCredito tarjetaCreditoOld = persistentProductoBancario.getTarjetaCredito();
            TarjetaCredito tarjetaCreditoNew = productoBancario.getTarjetaCredito();
            Aval avalOld = persistentProductoBancario.getAval();
            Aval avalNew = productoBancario.getAval();
            Catalogo catalogoOld = persistentProductoBancario.getCatalogo();
            Catalogo catalogoNew = productoBancario.getCatalogo();
            Sucursal sucursalOld = persistentProductoBancario.getSucursal();
            Sucursal sucursalNew = productoBancario.getSucursal();
            List<Cliente> clienteListOld = persistentProductoBancario.getClienteList();
            List<Cliente> clienteListNew = productoBancario.getClienteList();
            List<Movimiento> movimientoListOld = persistentProductoBancario.getMovimientoList();
            List<Movimiento> movimientoListNew = productoBancario.getMovimientoList();
            List<String> illegalOrphanMessages = null;
            if (prestamoOld != null && !prestamoOld.equals(prestamoNew)) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("You must retain Prestamo " + prestamoOld + " since its productoBancario field is not nullable.");
            }
            if (cuentaCorrienteOld != null && !cuentaCorrienteOld.equals(cuentaCorrienteNew)) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("You must retain CuentaCorriente " + cuentaCorrienteOld + " since its productoBancario field is not nullable.");
            }
            if (tarjetaCreditoOld != null && !tarjetaCreditoOld.equals(tarjetaCreditoNew)) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("You must retain TarjetaCredito " + tarjetaCreditoOld + " since its productoBancario field is not nullable.");
            }
            if (avalOld != null && !avalOld.equals(avalNew)) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("You must retain Aval " + avalOld + " since its productoBancario field is not nullable.");
            }
            for (Movimiento movimientoListOldMovimiento : movimientoListOld) {
                if (!movimientoListNew.contains(movimientoListOldMovimiento)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Movimiento " + movimientoListOldMovimiento + " since its productoBancario field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (prestamoNew != null) {
                prestamoNew = em.getReference(prestamoNew.getClass(), prestamoNew.getIdPrestamo());
                productoBancario.setPrestamo(prestamoNew);
            }
            if (cuentaCorrienteNew != null) {
                cuentaCorrienteNew = em.getReference(cuentaCorrienteNew.getClass(), cuentaCorrienteNew.getIdCuentaCorriente());
                productoBancario.setCuentaCorriente(cuentaCorrienteNew);
            }
            if (tarjetaCreditoNew != null) {
                tarjetaCreditoNew = em.getReference(tarjetaCreditoNew.getClass(), tarjetaCreditoNew.getIdTarjetaCredito());
                productoBancario.setTarjetaCredito(tarjetaCreditoNew);
            }
            if (avalNew != null) {
                avalNew = em.getReference(avalNew.getClass(), avalNew.getIdAval());
                productoBancario.setAval(avalNew);
            }
            if (catalogoNew != null) {
                catalogoNew = em.getReference(catalogoNew.getClass(), catalogoNew.getIdCatalogo());
                productoBancario.setCatalogo(catalogoNew);
            }
            if (sucursalNew != null) {
                sucursalNew = em.getReference(sucursalNew.getClass(), sucursalNew.getIdSucursal());
                productoBancario.setSucursal(sucursalNew);
            }
            List<Cliente> attachedClienteListNew = new ArrayList<Cliente>();
            for (Cliente clienteListNewClienteToAttach : clienteListNew) {
                clienteListNewClienteToAttach = em.getReference(clienteListNewClienteToAttach.getClass(), clienteListNewClienteToAttach.getIdCliente());
                attachedClienteListNew.add(clienteListNewClienteToAttach);
            }
            clienteListNew = attachedClienteListNew;
            productoBancario.setClienteList(clienteListNew);
            List<Movimiento> attachedMovimientoListNew = new ArrayList<Movimiento>();
            for (Movimiento movimientoListNewMovimientoToAttach : movimientoListNew) {
                movimientoListNewMovimientoToAttach = em.getReference(movimientoListNewMovimientoToAttach.getClass(), movimientoListNewMovimientoToAttach.getIdMovimiento());
                attachedMovimientoListNew.add(movimientoListNewMovimientoToAttach);
            }
            movimientoListNew = attachedMovimientoListNew;
            productoBancario.setMovimientoList(movimientoListNew);
            productoBancario = em.merge(productoBancario);
            if (prestamoNew != null && !prestamoNew.equals(prestamoOld)) {
                ProductoBancario oldProductoBancarioOfPrestamo = prestamoNew.getProductoBancario();
                if (oldProductoBancarioOfPrestamo != null) {
                    oldProductoBancarioOfPrestamo.setPrestamo(null);
                    oldProductoBancarioOfPrestamo = em.merge(oldProductoBancarioOfPrestamo);
                }
                prestamoNew.setProductoBancario(productoBancario);
                prestamoNew = em.merge(prestamoNew);
            }
            if (cuentaCorrienteNew != null && !cuentaCorrienteNew.equals(cuentaCorrienteOld)) {
                ProductoBancario oldProductoBancarioOfCuentaCorriente = cuentaCorrienteNew.getProductoBancario();
                if (oldProductoBancarioOfCuentaCorriente != null) {
                    oldProductoBancarioOfCuentaCorriente.setCuentaCorriente(null);
                    oldProductoBancarioOfCuentaCorriente = em.merge(oldProductoBancarioOfCuentaCorriente);
                }
                cuentaCorrienteNew.setProductoBancario(productoBancario);
                cuentaCorrienteNew = em.merge(cuentaCorrienteNew);
            }
            if (tarjetaCreditoNew != null && !tarjetaCreditoNew.equals(tarjetaCreditoOld)) {
                ProductoBancario oldProductoBancarioOfTarjetaCredito = tarjetaCreditoNew.getProductoBancario();
                if (oldProductoBancarioOfTarjetaCredito != null) {
                    oldProductoBancarioOfTarjetaCredito.setTarjetaCredito(null);
                    oldProductoBancarioOfTarjetaCredito = em.merge(oldProductoBancarioOfTarjetaCredito);
                }
                tarjetaCreditoNew.setProductoBancario(productoBancario);
                tarjetaCreditoNew = em.merge(tarjetaCreditoNew);
            }
            if (avalNew != null && !avalNew.equals(avalOld)) {
                ProductoBancario oldProductoBancarioOfAval = avalNew.getProductoBancario();
                if (oldProductoBancarioOfAval != null) {
                    oldProductoBancarioOfAval.setAval(null);
                    oldProductoBancarioOfAval = em.merge(oldProductoBancarioOfAval);
                }
                avalNew.setProductoBancario(productoBancario);
                avalNew = em.merge(avalNew);
            }
            if (catalogoOld != null && !catalogoOld.equals(catalogoNew)) {
                catalogoOld.getProductoBancarioList().remove(productoBancario);
                catalogoOld = em.merge(catalogoOld);
            }
            if (catalogoNew != null && !catalogoNew.equals(catalogoOld)) {
                catalogoNew.getProductoBancarioList().add(productoBancario);
                catalogoNew = em.merge(catalogoNew);
            }
            if (sucursalOld != null && !sucursalOld.equals(sucursalNew)) {
                sucursalOld.getProductoBancarioList().remove(productoBancario);
                sucursalOld = em.merge(sucursalOld);
            }
            if (sucursalNew != null && !sucursalNew.equals(sucursalOld)) {
                sucursalNew.getProductoBancarioList().add(productoBancario);
                sucursalNew = em.merge(sucursalNew);
            }
            for (Cliente clienteListOldCliente : clienteListOld) {
                if (!clienteListNew.contains(clienteListOldCliente)) {
                    clienteListOldCliente.getProductoBancarioList().remove(productoBancario);
                    clienteListOldCliente = em.merge(clienteListOldCliente);
                }
            }
            for (Cliente clienteListNewCliente : clienteListNew) {
                if (!clienteListOld.contains(clienteListNewCliente)) {
                    clienteListNewCliente.getProductoBancarioList().add(productoBancario);
                    clienteListNewCliente = em.merge(clienteListNewCliente);
                }
            }
            for (Movimiento movimientoListNewMovimiento : movimientoListNew) {
                if (!movimientoListOld.contains(movimientoListNewMovimiento)) {
                    ProductoBancario oldProductoBancarioOfMovimientoListNewMovimiento = movimientoListNewMovimiento.getProductoBancario();
                    movimientoListNewMovimiento.setProductoBancario(productoBancario);
                    movimientoListNewMovimiento = em.merge(movimientoListNewMovimiento);
                    if (oldProductoBancarioOfMovimientoListNewMovimiento != null && !oldProductoBancarioOfMovimientoListNewMovimiento.equals(productoBancario)) {
                        oldProductoBancarioOfMovimientoListNewMovimiento.getMovimientoList().remove(movimientoListNewMovimiento);
                        oldProductoBancarioOfMovimientoListNewMovimiento = em.merge(oldProductoBancarioOfMovimientoListNewMovimiento);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = productoBancario.getIdProductoBancario();
                if (findProductoBancario(id) == null) {
                    throw new NonexistentEntityException("The productoBancario with id " + id + " no longer exists.");
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
            ProductoBancario productoBancario;
            try {
                productoBancario = em.getReference(ProductoBancario.class, id);
                productoBancario.getIdProductoBancario();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The productoBancario with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Prestamo prestamoOrphanCheck = productoBancario.getPrestamo();
            if (prestamoOrphanCheck != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This ProductoBancario (" + productoBancario + ") cannot be destroyed since the Prestamo " + prestamoOrphanCheck + " in its prestamo field has a non-nullable productoBancario field.");
            }
            CuentaCorriente cuentaCorrienteOrphanCheck = productoBancario.getCuentaCorriente();
            if (cuentaCorrienteOrphanCheck != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This ProductoBancario (" + productoBancario + ") cannot be destroyed since the CuentaCorriente " + cuentaCorrienteOrphanCheck + " in its cuentaCorriente field has a non-nullable productoBancario field.");
            }
            TarjetaCredito tarjetaCreditoOrphanCheck = productoBancario.getTarjetaCredito();
            if (tarjetaCreditoOrphanCheck != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This ProductoBancario (" + productoBancario + ") cannot be destroyed since the TarjetaCredito " + tarjetaCreditoOrphanCheck + " in its tarjetaCredito field has a non-nullable productoBancario field.");
            }
            Aval avalOrphanCheck = productoBancario.getAval();
            if (avalOrphanCheck != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This ProductoBancario (" + productoBancario + ") cannot be destroyed since the Aval " + avalOrphanCheck + " in its aval field has a non-nullable productoBancario field.");
            }
            List<Movimiento> movimientoListOrphanCheck = productoBancario.getMovimientoList();
            for (Movimiento movimientoListOrphanCheckMovimiento : movimientoListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This ProductoBancario (" + productoBancario + ") cannot be destroyed since the Movimiento " + movimientoListOrphanCheckMovimiento + " in its movimientoList field has a non-nullable productoBancario field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Catalogo catalogo = productoBancario.getCatalogo();
            if (catalogo != null) {
                catalogo.getProductoBancarioList().remove(productoBancario);
                catalogo = em.merge(catalogo);
            }
            Sucursal sucursal = productoBancario.getSucursal();
            if (sucursal != null) {
                sucursal.getProductoBancarioList().remove(productoBancario);
                sucursal = em.merge(sucursal);
            }
            List<Cliente> clienteList = productoBancario.getClienteList();
            for (Cliente clienteListCliente : clienteList) {
                clienteListCliente.getProductoBancarioList().remove(productoBancario);
                clienteListCliente = em.merge(clienteListCliente);
            }
            em.remove(productoBancario);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<ProductoBancario> findProductoBancarioEntities() {
        return findProductoBancarioEntities(true, -1, -1);
    }

    public List<ProductoBancario> findProductoBancarioEntities(int maxResults, int firstResult) {
        return findProductoBancarioEntities(false, maxResults, firstResult);
    }

    private List<ProductoBancario> findProductoBancarioEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(ProductoBancario.class));
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

    public ProductoBancario findProductoBancario(Integer id) {
        EntityManager em = getEntityManager();
        ProductoBancario pb;
        try {
            pb = em.find(ProductoBancario.class, id);
            if (pb != null) 
                em.refresh(pb);
            return pb;
        } finally {
            em.close();
        }
    }

    public int getProductoBancarioCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<ProductoBancario> rt = cq.from(ProductoBancario.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
