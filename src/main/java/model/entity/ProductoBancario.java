package model.entity;

//<editor-fold defaultstate="collapsed" desc=" Librerías importadas... ">
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
//</editor-fold>

/**
 *
 * @author Ignacio Pineda Martín
 */
@Entity
@Table(name = "producto_bancario")
@NamedQueries({
    @NamedQuery(name = "ProductoBancario.findAll", query = "SELECT p FROM ProductoBancario p"),
    @NamedQuery(name = "ProductoBancario.findByIdProductoBancario", query = "SELECT p FROM ProductoBancario p WHERE p.idProductoBancario = :idProductoBancario"),
    @NamedQuery(name = "ProductoBancario.findByFecApertura", query = "SELECT p FROM ProductoBancario p WHERE p.fecApertura = :fecApertura"),
    @NamedQuery(name = "ProductoBancario.findByFecCancelacion", query = "SELECT p FROM ProductoBancario p WHERE p.fecCancelacion = :fecCancelacion")})
public class ProductoBancario implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID_PRODUCTO_BANCARIO")
    private Integer idProductoBancario;
    @Basic(optional = false)
    @Column(name = "FEC_APERTURA")
    @Temporal(TemporalType.DATE)
    private Date fecApertura;
    @Column(name = "FEC_CANCELACION")
    @Temporal(TemporalType.DATE)
    private Date fecCancelacion;
    @Column(name = "FEC_LIQUIDACION")
    @Temporal(TemporalType.DATE)
    private Date fecLiquidacion;
    @ManyToMany(mappedBy = "productoBancarioList")
    private List<Cliente> clienteList;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "productoBancario")
    private Prestamo prestamo;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "productoBancario")
    private CuentaCorriente cuentaCorriente;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "productoBancario")
    private List<Movimiento> movimientoList;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "productoBancario")
    private TarjetaCredito tarjetaCredito;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "productoBancario")
    private Aval aval;
    @JoinColumn(name = "ID_CATALOGO", referencedColumnName = "ID_CATALOGO")
    @ManyToOne(optional = false)
    private Catalogo catalogo;
    @JoinColumn(name = "ID_SUCURSAL", referencedColumnName = "ID_SUCURSAL")
    @ManyToOne(optional = false)
    private Sucursal sucursal;

    public ProductoBancario() {
    }

    public ProductoBancario(Integer idProductoBancario) {
        this.idProductoBancario = idProductoBancario;
    }

    public ProductoBancario(Integer idProductoBancario, Date fecApertura) {
        this.idProductoBancario = idProductoBancario;
        this.fecApertura = fecApertura;
    }

    public Integer getIdProductoBancario() {
        return idProductoBancario;
    }

    public void setIdProductoBancario(Integer idProductoBancario) {
        this.idProductoBancario = idProductoBancario;
    }

    public Date getFecApertura() {
        return fecApertura;
    }

    public void setFecApertura(Date fecApertura) {
        this.fecApertura = fecApertura;
    }

    public Date getFecCancelacion() {
        return fecCancelacion;
    }

    public void setFecCancelacion(Date fecCancelacion) {
        this.fecCancelacion = fecCancelacion;
    }

    public Date getFecLiquidacion() {
        return fecLiquidacion;
    }

    public void setFecLiquidacion(Date fecLiquidacion) {
        this.fecLiquidacion = fecLiquidacion;
    }

    public List<Cliente> getClienteList() {
        return clienteList;
    }

    public void setClienteList(List<Cliente> clienteList) {
        this.clienteList = clienteList;
    }

    public Prestamo getPrestamo() {
        return prestamo;
    }

    public void setPrestamo(Prestamo prestamo) {
        this.prestamo = prestamo;
    }

    public CuentaCorriente getCuentaCorriente() {
        return cuentaCorriente;
    }

    public void setCuentaCorriente(CuentaCorriente cuentaCorriente) {
        this.cuentaCorriente = cuentaCorriente;
    }

    public List<Movimiento> getMovimientoList() {
        return movimientoList;
    }

    public void setMovimientoList(List<Movimiento> movimientoList) {
        this.movimientoList = movimientoList;
    }

    public TarjetaCredito getTarjetaCredito() {
        return tarjetaCredito;
    }

    public void setTarjetaCredito(TarjetaCredito tarjetaCredito) {
        this.tarjetaCredito = tarjetaCredito;
    }

    public Aval getAval() {
        return aval;
    }

    public void setAval(Aval aval) {
        this.aval = aval;
    }

    public Catalogo getCatalogo() {
        return catalogo;
    }

    public void setCatalogo(Catalogo catalogo) {
        this.catalogo = catalogo;
    }

    public Sucursal getSucursal() {
        return sucursal;
    }

    public void setSucursal(Sucursal sucursal) {
        this.sucursal = sucursal;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idProductoBancario != null ? idProductoBancario.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ProductoBancario)) {
            return false;
        }
        ProductoBancario other = (ProductoBancario) object;
        if ((this.idProductoBancario == null && other.idProductoBancario != null) || (this.idProductoBancario != null && !this.idProductoBancario.equals(other.idProductoBancario))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        String mensaje = "ProductoBancario[idProductoBancario=" + idProductoBancario 
                + ", fecApertura=" + fecApertura 
                + ", fecCancelacion=" + fecCancelacion 
                + ", fecLiquidacion=" + fecLiquidacion 
                + ", catalogo=" + catalogo 
                + ", sucursal=" + sucursal;

        if (cuentaCorriente != null) {
            mensaje = mensaje + ", " + cuentaCorriente;
        }
        if (tarjetaCredito != null) {
            mensaje = mensaje + ", " + tarjetaCredito;
        }
        if (prestamo != null) {
            mensaje = mensaje + ", " + prestamo;
        }        
        if (aval != null) {
            mensaje = mensaje + ", " + aval;
        }

        for (Cliente cliente : clienteList) {
            mensaje = mensaje + "\n" + cliente;
        }
        mensaje = mensaje + ']';
        return mensaje;
    }
   
}
