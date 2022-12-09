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
@Table(name = "aval")
@NamedQueries({
    @NamedQuery(name = "Aval.findAll", query = "SELECT a FROM Aval a"),
    @NamedQuery(name = "Aval.findByIdAval", query = "SELECT a FROM Aval a WHERE a.idAval = :idAval"),
    @NamedQuery(name = "Aval.findByNumeroAval", query = "SELECT a FROM Aval a WHERE a.numeroAval = :numeroAval"),
    @NamedQuery(name = "Aval.findByFecVencimiento", query = "SELECT a FROM Aval a WHERE a.fecVencimiento = :fecVencimiento"),
    @NamedQuery(name = "Aval.findByImpAvalado", query = "SELECT a FROM Aval a WHERE a.impAvalado = :impAvalado")})
public class Aval implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID_AVAL")
    private Integer idAval;
    @Basic(optional = false)
    @Column(name = "NUMERO_AVAL")
    private long numeroAval;
    @Basic(optional = false)
    @Column(name = "FEC_VENCIMIENTO")
    @Temporal(TemporalType.DATE)
    private Date fecVencimiento;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @Column(name = "IMP_AVALADO")
    private BigDecimal impAvalado;
    @JoinColumn(name = "ID_PRODUCTO_BANCARIO", referencedColumnName = "ID_PRODUCTO_BANCARIO")
    @OneToOne(optional = false)
    private ProductoBancario productoBancario;

    public Aval() {
    }

    public Aval(Integer idAval) {
        this.idAval = idAval;
    }

    public Aval(Integer idAval, long numeroAval, Date fecVencimiento, BigDecimal impAvalado) {
        this.idAval = idAval;
        this.numeroAval = numeroAval;
        this.fecVencimiento = fecVencimiento;
        this.impAvalado = impAvalado;
    }

    public Integer getIdAval() {
        return idAval;
    }

    public void setIdAval(Integer idAval) {
        this.idAval = idAval;
    }

    public long getNumeroAval() {
        return numeroAval;
    }

    public void setNumeroAval(long numeroAval) {
        this.numeroAval = numeroAval;
    }

    public Date getFecVencimiento() {
        return fecVencimiento;
    }

    public void setFecVencimiento(Date fecVencimiento) {
        this.fecVencimiento = fecVencimiento;
    }

    public BigDecimal getImpAvalado() {
        return impAvalado;
    }

    public void setImpAvalado(BigDecimal impAvalado) {
        this.impAvalado = impAvalado;
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
        hash += (idAval != null ? idAval.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Aval)) {
            return false;
        }
        Aval other = (Aval) object;
        if ((this.idAval == null && other.idAval != null) || (this.idAval != null && !this.idAval.equals(other.idAval))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.entity.Aval[ idAval=" + idAval + " ]";
    }
    
}
