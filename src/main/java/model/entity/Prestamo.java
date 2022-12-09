package model.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Ignacio Pineda Martín
 */
@Entity
@Table(name = "prestamo")
@NamedQueries({
    @NamedQuery(name = "Prestamo.findAll", query = "SELECT p FROM Prestamo p"),
    @NamedQuery(name = "Prestamo.findByIdPrestamo", query = "SELECT p FROM Prestamo p WHERE p.idPrestamo = :idPrestamo"),
    @NamedQuery(name = "Prestamo.findByNumeroPrestamo", query = "SELECT p FROM Prestamo p WHERE p.numeroPrestamo = :numeroPrestamo"),
    @NamedQuery(name = "Prestamo.findByTipoGarantia", query = "SELECT p FROM Prestamo p WHERE p.tipoGarantia = :tipoGarantia"),
    @NamedQuery(name = "Prestamo.findByTipoInteres", query = "SELECT p FROM Prestamo p WHERE p.tipoInteres = :tipoInteres"),
    @NamedQuery(name = "Prestamo.findByFecVencimiento", query = "SELECT p FROM Prestamo p WHERE p.fecVencimiento = :fecVencimiento"),
    @NamedQuery(name = "Prestamo.findByImpConcedido", query = "SELECT p FROM Prestamo p WHERE p.impConcedido = :impConcedido"),
    @NamedQuery(name = "Prestamo.findByImpSaldoPendiente", query = "SELECT p FROM Prestamo p WHERE p.impSaldoPendiente = :impSaldoPendiente")})
public class Prestamo implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID_PRESTAMO")
    private Integer idPrestamo;
    @Basic(optional = false)
    @Column(name = "NUMERO_PRESTAMO")
    private long numeroPrestamo;
    @Basic(optional = false)
    @Column(name = "TIPO_GARANTIA")
    private String tipoGarantia;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @Column(name = "TIPO_INTERES")
    private BigDecimal tipoInteres;
    @Basic(optional = false)
    @Column(name = "FEC_VENCIMIENTO")
    @Temporal(TemporalType.DATE)
    private Date fecVencimiento;
    @Basic(optional = false)
    @Column(name = "IMP_CONCEDIDO")
    private BigDecimal impConcedido;
    @Basic(optional = false)
    @Column(name = "IMP_SALDO_PENDIENTE")
    private BigDecimal impSaldoPendiente;
    @JoinColumn(name = "ID_CUENTA_CORRIENTE_PAGO", referencedColumnName = "ID_CUENTA_CORRIENTE")
    @ManyToOne(optional = false)
    private CuentaCorriente cuentaCorrientePago;
    @JoinColumn(name = "ID_PRODUCTO_BANCARIO", referencedColumnName = "ID_PRODUCTO_BANCARIO")
    @OneToOne(optional = false)
    private ProductoBancario productoBancario;

    public Prestamo() {
    }

    public Prestamo(Integer idPrestamo) {
        this.idPrestamo = idPrestamo;
    }

    public Prestamo(Integer idPrestamo, long numeroPrestamo, String tipoGarantia, BigDecimal tipoInteres, Date fecVencimiento, BigDecimal impConcedido, BigDecimal impSaldoPendiente) {
        this.idPrestamo = idPrestamo;
        this.numeroPrestamo = numeroPrestamo;
        this.tipoGarantia = tipoGarantia;
        this.tipoInteres = tipoInteres;
        this.fecVencimiento = fecVencimiento;
        this.impConcedido = impConcedido;
        this.impSaldoPendiente = impSaldoPendiente;
    }

    public Integer getIdPrestamo() {
        return idPrestamo;
    }

    public void setIdPrestamo(Integer idPrestamo) {
        this.idPrestamo = idPrestamo;
    }

    public long getNumeroPrestamo() {
        return numeroPrestamo;
    }

    public void setNumeroPrestamo(long numeroPrestamo) {
        this.numeroPrestamo = numeroPrestamo;
    }

    public String getTipoGarantia() {
        return tipoGarantia;
    }

    public void setTipoGarantia(String tipoGarantia) {
        this.tipoGarantia = tipoGarantia;
    }

    public BigDecimal getTipoInteres() {
        return tipoInteres;
    }

    public void setTipoInteres(BigDecimal tipoInteres) {
        this.tipoInteres = tipoInteres;
    }

    public Date getFecVencimiento() {
        return fecVencimiento;
    }

    public void setFecVencimiento(Date fecVencimiento) {
        this.fecVencimiento = fecVencimiento;
    }

    public BigDecimal getImpConcedido() {
        return impConcedido;
    }

    public void setImpConcedido(BigDecimal impConcedido) {
        this.impConcedido = impConcedido;
    }

    public BigDecimal getImpSaldoPendiente() {
        return impSaldoPendiente;
    }

    public void setImpSaldoPendiente(BigDecimal impSaldoPendiente) {
        this.impSaldoPendiente = impSaldoPendiente;
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
        hash += (idPrestamo != null ? idPrestamo.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Prestamo)) {
            return false;
        }
        Prestamo other = (Prestamo) object;
        if ((this.idPrestamo == null && other.idPrestamo != null) || (this.idPrestamo != null && !this.idPrestamo.equals(other.idPrestamo))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.entity.Prestamo[ idPrestamo=" + idPrestamo + " ]";
    }
    
}
