package model.entity;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author Ignacio Pineda Martín
 */
@Entity
@Table(name = "catalogo")
@NamedQueries({
    @NamedQuery(name = "Catalogo.findAll", query = "SELECT c FROM Catalogo c"),
    @NamedQuery(name = "Catalogo.findByIdCatalogo", query = "SELECT c FROM Catalogo c WHERE c.idCatalogo = :idCatalogo"),
    @NamedQuery(name = "Catalogo.findByCodProducto", query = "SELECT c FROM Catalogo c WHERE c.codProducto = :codProducto"),
    @NamedQuery(name = "Catalogo.findByNombreProducto", query = "SELECT c FROM Catalogo c WHERE c.nombreProducto = :nombreProducto")})
public class Catalogo implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID_CATALOGO")
    private Integer idCatalogo;
    @Basic(optional = false)
    @Column(name = "COD_PRODUCTO")
    private String codProducto;
    @Basic(optional = false)
    @Column(name = "NOMBRE_PRODUCTO")
    private String nombreProducto;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "catalogo")
    private List<ProductoBancario> productoBancarioList;

    public Catalogo() {
    }

    public Catalogo(Integer idCatalogo) {
        this.idCatalogo = idCatalogo;
    }

    public Catalogo(Integer idCatalogo, String codProducto, String nombreProducto) {
        this.idCatalogo = idCatalogo;
        this.codProducto = codProducto;
        this.nombreProducto = nombreProducto;
    }

    public Integer getIdCatalogo() {
        return idCatalogo;
    }

    public void setIdCatalogo(Integer idCatalogo) {
        this.idCatalogo = idCatalogo;
    }

    public String getCodProducto() {
        return codProducto;
    }

    public void setCodProducto(String codProducto) {
        this.codProducto = codProducto;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public List<ProductoBancario> getProductoBancarioList() {
        return productoBancarioList;
    }

    public void setProductoBancarioList(List<ProductoBancario> productoBancarioList) {
        this.productoBancarioList = productoBancarioList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idCatalogo != null ? idCatalogo.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Catalogo)) {
            return false;
        }
        Catalogo other = (Catalogo) object;
        if ((this.idCatalogo == null && other.idCatalogo != null) || (this.idCatalogo != null && !this.idCatalogo.equals(other.idCatalogo))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Catalogo[idCatalogo=" + idCatalogo 
                + ", codProducto=" + codProducto 
                + ", nombreProducto=" + nombreProducto + ']';
    }

}
