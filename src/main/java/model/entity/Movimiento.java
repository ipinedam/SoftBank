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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Ignacio Pineda Martín
 */
@Entity
@Table(name = "movimiento")
@NamedQueries({
    @NamedQuery(name = "Movimiento.findAll", query = "SELECT m FROM Movimiento m"),
    @NamedQuery(name = "Movimiento.findByIdMovimiento", query = "SELECT m FROM Movimiento m WHERE m.idMovimiento = :idMovimiento"),
    @NamedQuery(name = "Movimiento.findByFecMovimiento", query = "SELECT m FROM Movimiento m WHERE m.fecMovimiento = :fecMovimiento"),
    @NamedQuery(name = "Movimiento.findByConcepto", query = "SELECT m FROM Movimiento m WHERE m.concepto = :concepto"),
    @NamedQuery(name = "Movimiento.findByImpMovimiento", query = "SELECT m FROM Movimiento m WHERE m.impMovimiento = :impMovimiento")})
public class Movimiento implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID_MOVIMIENTO")
    private Integer idMovimiento;
    @Basic(optional = false)
    @Column(name = "FEC_MOVIMIENTO")
    @Temporal(TemporalType.DATE)
    private Date fecMovimiento;
    @Basic(optional = false)
    @Column(name = "CONCEPTO")
    private String concepto;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @Column(name = "IMP_MOVIMIENTO")
    private BigDecimal impMovimiento;
    @JoinColumn(name = "ID_PRODUCTO_BANCARIO", referencedColumnName = "ID_PRODUCTO_BANCARIO")
    @ManyToOne(optional = false)
    private ProductoBancario productoBancario;

    public Movimiento() {
    }

    public Movimiento(Integer idMovimiento) {
        this.idMovimiento = idMovimiento;
    }

    public Movimiento(Integer idMovimiento, Date fecMovimiento, String concepto, BigDecimal impMovimiento) {
        this.idMovimiento = idMovimiento;
        this.fecMovimiento = fecMovimiento;
        this.concepto = concepto;
        this.impMovimiento = impMovimiento;
    }

    public Integer getIdMovimiento() {
        return idMovimiento;
    }

    public void setIdMovimiento(Integer idMovimiento) {
        this.idMovimiento = idMovimiento;
    }

    public Date getFecMovimiento() {
        return fecMovimiento;
    }

    public void setFecMovimiento(Date fecMovimiento) {
        this.fecMovimiento = fecMovimiento;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public BigDecimal getImpMovimiento() {
        return impMovimiento;
    }

    public void setImpMovimiento(BigDecimal impMovimiento) {
        this.impMovimiento = impMovimiento;
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
        hash += (idMovimiento != null ? idMovimiento.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Movimiento)) {
            return false;
        }
        Movimiento other = (Movimiento) object;
        if ((this.idMovimiento == null && other.idMovimiento != null) || (this.idMovimiento != null && !this.idMovimiento.equals(other.idMovimiento))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.entity.Movimiento[ idMovimiento=" + idMovimiento + " ]";
    }
    
}
