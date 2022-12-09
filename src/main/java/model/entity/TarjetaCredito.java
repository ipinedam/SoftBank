package model.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 *
 * @author Ignacio Pineda Martín
 */
@Entity
@Table(name = "tarjeta_credito")
@NamedQueries({
    @NamedQuery(name = "TarjetaCredito.findAll", query = "SELECT t FROM TarjetaCredito t"),
    @NamedQuery(name = "TarjetaCredito.findByIdTarjetaCredito", query = "SELECT t FROM TarjetaCredito t WHERE t.idTarjetaCredito = :idTarjetaCredito"),
    @NamedQuery(name = "TarjetaCredito.findByNumeroTarjeta", query = "SELECT t FROM TarjetaCredito t WHERE t.numeroTarjeta = :numeroTarjeta"),
    @NamedQuery(name = "TarjetaCredito.findByTipoInteres", query = "SELECT t FROM TarjetaCredito t WHERE t.tipoInteres = :tipoInteres"),
    @NamedQuery(name = "TarjetaCredito.findByImpLimiteTarjeta", query = "SELECT t FROM TarjetaCredito t WHERE t.impLimiteTarjeta = :impLimiteTarjeta"),
    @NamedQuery(name = "TarjetaCredito.findByImpSaldoPendiente", query = "SELECT t FROM TarjetaCredito t WHERE t.impSaldoPendiente = :impSaldoPendiente"),
    @NamedQuery(name = "TarjetaCredito.findByFormaPago", query = "SELECT t FROM TarjetaCredito t WHERE t.formaPago = :formaPago")})
public class TarjetaCredito implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID_TARJETA_CREDITO")
    private Integer idTarjetaCredito;
    @Basic(optional = false)
    @Column(name = "NUMERO_TARJETA")
    private long numeroTarjeta;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @Column(name = "PAN")
    private long pan;    
    @Basic(optional = false)
    @Column(name = "TIPO_INTERES")
    private BigDecimal tipoInteres;
    @Basic(optional = false)
    @Column(name = "IMP_LIMITE_TARJETA")
    private BigDecimal impLimiteTarjeta;
    @Basic(optional = false)
    @Column(name = "IMP_SALDO_PENDIENTE")
    private BigDecimal impSaldoPendiente;
    @Basic(optional = false)
    @Column(name = "FORMA_PAGO")
    private String formaPago;
    @JoinColumn(name = "ID_CUENTA_CORRIENTE_PAGO", referencedColumnName = "ID_CUENTA_CORRIENTE")
    @ManyToOne(optional = false)
    private CuentaCorriente cuentaCorrientePago;
    @JoinColumn(name = "ID_PRODUCTO_BANCARIO", referencedColumnName = "ID_PRODUCTO_BANCARIO")
    @OneToOne(optional = false)
    private ProductoBancario productoBancario;

    public TarjetaCredito() {
    }

    public TarjetaCredito(Integer idTarjetaCredito) {
        this.idTarjetaCredito = idTarjetaCredito;
    }

    public TarjetaCredito(Integer idTarjetaCredito, long numeroTarjeta, BigDecimal tipoInteres, BigDecimal impLimiteTarjeta, BigDecimal impSaldoPendiente, String formaPago) {
        this.idTarjetaCredito = idTarjetaCredito;
        this.numeroTarjeta = numeroTarjeta;
        this.tipoInteres = tipoInteres;
        this.impLimiteTarjeta = impLimiteTarjeta;
        this.impSaldoPendiente = impSaldoPendiente;
        this.formaPago = formaPago;
    }

    public Integer getIdTarjetaCredito() {
        return idTarjetaCredito;
    }

    public void setIdTarjetaCredito(Integer idTarjetaCredito) {
        this.idTarjetaCredito = idTarjetaCredito;
    }

    public long getNumeroTarjeta() {
        return numeroTarjeta;
    }

    public void setNumeroTarjeta(long numeroTarjeta) {
        this.numeroTarjeta = numeroTarjeta;
    }

    public long getPan() {
        return pan;
    }

    public void setPan(long pan) {
        this.pan = pan;
    }    
    
    public BigDecimal getTipoInteres() {
        return tipoInteres;
    }

    public void setTipoInteres(BigDecimal tipoInteres) {
        this.tipoInteres = tipoInteres;
    }

    public BigDecimal getImpLimiteTarjeta() {
        return impLimiteTarjeta;
    }

    public void setImpLimiteTarjeta(BigDecimal impLimiteTarjeta) {
        this.impLimiteTarjeta = impLimiteTarjeta;
    }

    public BigDecimal getImpSaldoPendiente() {
        return impSaldoPendiente;
    }

    public void setImpSaldoPendiente(BigDecimal impSaldoPendiente) {
        this.impSaldoPendiente = impSaldoPendiente;
    }

    public String getFormaPago() {
        return formaPago;
    }

    public void setFormaPago(String formaPago) {
        this.formaPago = formaPago;
    }

    public CuentaCorriente getCuentaCorrientePago() {
        return cuentaCorrientePago;
    }

    public void setCuentaCorrientePago(CuentaCorriente cuentaCorrientePago) {
        this.cuentaCorrientePago = cuentaCorrientePago;
    }

    public ProductoBancario getProductoBancario() {
        return productoBancario;
    }

    public void setProductoBancario(ProductoBancario productoBancario) {
        this.productoBancario = productoBancario;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idTarjetaCredito != null ? idTarjetaCredito.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TarjetaCredito)) {
            return false;
        }
        TarjetaCredito other = (TarjetaCredito) object;
        if ((this.idTarjetaCredito == null && other.idTarjetaCredito != null) || (this.idTarjetaCredito != null && !this.idTarjetaCredito.equals(other.idTarjetaCredito))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.entity.TarjetaCredito[ idTarjetaCredito=" + idTarjetaCredito + " ]";
    }

}
