package model.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 *
 * @author Ignacio Pineda Martín
 */
@Entity
@Table(name = "cuenta_corriente")
@NamedQueries({
    @NamedQuery(name = "CuentaCorriente.findAll", query = "SELECT c FROM CuentaCorriente c"),
    @NamedQuery(name = "CuentaCorriente.findByIdCuentaCorriente", query = "SELECT c FROM CuentaCorriente c WHERE c.idCuentaCorriente = :idCuentaCorriente"),
    @NamedQuery(name = "CuentaCorriente.findByNumeroCuenta", query = "SELECT c FROM CuentaCorriente c WHERE c.numeroCuenta = :numeroCuenta"),
    @NamedQuery(name = "CuentaCorriente.findByIban", query = "SELECT c FROM CuentaCorriente c WHERE c.iban = :iban"),
    @NamedQuery(name = "CuentaCorriente.findByTipoInteres", query = "SELECT c FROM CuentaCorriente c WHERE c.tipoInteres = :tipoInteres"),
    @NamedQuery(name = "CuentaCorriente.findByImpSaldoActual", query = "SELECT c FROM CuentaCorriente c WHERE c.impSaldoActual = :impSaldoActual")})
public class CuentaCorriente implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID_CUENTA_CORRIENTE")
    private Integer idCuentaCorriente;
    @Basic(optional = false)
    @Column(name = "NUMERO_CUENTA")
    private int numeroCuenta;
    @Basic(optional = false)
    @Column(name = "IBAN")
    private String iban;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @Column(name = "TIPO_INTERES")
    private BigDecimal tipoInteres;
    @Basic(optional = false)
    @Column(name = "IMP_SALDO_ACTUAL")
    private BigDecimal impSaldoActual;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "cuentaCorrientePago")
    private List<Prestamo> prestamoList;
    @JoinColumn(name = "ID_PRODUCTO_BANCARIO", referencedColumnName = "ID_PRODUCTO_BANCARIO")
    @OneToOne(optional = false)
    private ProductoBancario productoBancario;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "cuentaCorrientePago")
    private List<TarjetaCredito> tarjetaCreditoList;

    public CuentaCorriente() {
    }

    public CuentaCorriente(Integer idCuentaCorriente) {
        this.idCuentaCorriente = idCuentaCorriente;
    }

    public CuentaCorriente(Integer idCuentaCorriente, int numeroCuenta, String iban, BigDecimal tipoInteres, BigDecimal impSaldoActual) {
        this.idCuentaCorriente = idCuentaCorriente;
        this.numeroCuenta = numeroCuenta;
        this.iban = iban;
        this.tipoInteres = tipoInteres;
        this.impSaldoActual = impSaldoActual;
    }

    public Integer getIdCuentaCorriente() {
        return idCuentaCorriente;
    }

    public void setIdCuentaCorriente(Integer idCuentaCorriente) {
        this.idCuentaCorriente = idCuentaCorriente;
    }

    public int getNumeroCuenta() {
        return numeroCuenta;
    }

    public void setNumeroCuenta(int numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public BigDecimal getTipoInteres() {
        return tipoInteres;
    }

    public void setTipoInteres(BigDecimal tipoInteres) {
        this.tipoInteres = tipoInteres;
    }

    public BigDecimal getImpSaldoActual() {
        return impSaldoActual;
    }

    public void setImpSaldoActual(BigDecimal impSaldoActual) {
        this.impSaldoActual = impSaldoActual;
    }

    public List<Prestamo> getPrestamoList() {
        return prestamoList;
    }

    public void setPrestamoList(List<Prestamo> prestamoList) {
        this.prestamoList = prestamoList;
    }

    public ProductoBancario getProductoBancario() {
        return productoBancario;
    }

    public void setProductoBancario(ProductoBancario productoBancario) {
        this.productoBancario = productoBancario;
    }

    public List<TarjetaCredito> getTarjetaCreditoList() {
        return tarjetaCreditoList;
    }

    public void setTarjetaCreditoList(List<TarjetaCredito> tarjetaCreditoList) {
        this.tarjetaCreditoList = tarjetaCreditoList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idCuentaCorriente != null ? idCuentaCorriente.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CuentaCorriente)) {
            return false;
        }
        CuentaCorriente other = (CuentaCorriente) object;
        if ((this.idCuentaCorriente == null && other.idCuentaCorriente != null) || (this.idCuentaCorriente != null && !this.idCuentaCorriente.equals(other.idCuentaCorriente))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.entity.CuentaCorriente[ idCuentaCorriente=" + idCuentaCorriente + " ]";
    }
    
}
