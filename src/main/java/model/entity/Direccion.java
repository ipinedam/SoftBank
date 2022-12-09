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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author Ignacio Pineda Martín
 */
@Entity
@Table(name = "direccion")
@NamedQueries({
    @NamedQuery(name = "Direccion.findAll", query = "SELECT d FROM Direccion d"),
    @NamedQuery(name = "Direccion.findByIdDireccion", query = "SELECT d FROM Direccion d WHERE d.idDireccion = :idDireccion"),
    @NamedQuery(name = "Direccion.findByTipoVia", query = "SELECT d FROM Direccion d WHERE d.tipoVia = :tipoVia"),
    @NamedQuery(name = "Direccion.findByNombreVia", query = "SELECT d FROM Direccion d WHERE d.nombreVia = :nombreVia"),
    @NamedQuery(name = "Direccion.findByNumero", query = "SELECT d FROM Direccion d WHERE d.numero = :numero"),
    @NamedQuery(name = "Direccion.findByPoblacion", query = "SELECT d FROM Direccion d WHERE d.poblacion = :poblacion"),
    @NamedQuery(name = "Direccion.findByProvinciaEstado", query = "SELECT d FROM Direccion d WHERE d.provinciaEstado = :provinciaEstado"),
    @NamedQuery(name = "Direccion.findByCodPostal", query = "SELECT d FROM Direccion d WHERE d.codPostal = :codPostal"),
    @NamedQuery(name = "Direccion.findByPais", query = "SELECT d FROM Direccion d WHERE d.pais = :pais")})
public class Direccion implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID_DIRECCION")
    private Integer idDireccion;
    @Basic(optional = false)
    @Column(name = "TIPO_VIA")
    private String tipoVia;
    @Basic(optional = false)
    @Column(name = "NOMBRE_VIA")
    private String nombreVia;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @Column(name = "NUMERO")
    private BigDecimal numero;
    @Basic(optional = false)
    @Column(name = "POBLACION")
    private String poblacion;
    @Basic(optional = false)
    @Column(name = "PROVINCIA_ESTADO")
    private String provinciaEstado;
    @Basic(optional = false)
    @Column(name = "COD_POSTAL")
    private int codPostal;
    @Basic(optional = false)
    @Column(name = "PAIS")
    private String pais;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "direccion")
    private List<Cliente> clienteList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "direccion")
    private List<Sucursal> sucursalList;

    public Direccion() {
    }

    public Direccion(Integer idDireccion) {
        this.idDireccion = idDireccion;
    }

    public Direccion(Integer idDireccion, String tipoVia, String nombreVia, BigDecimal numero, String poblacion, String provinciaEstado, int codPostal, String pais) {
        this.idDireccion = idDireccion;
        this.tipoVia = tipoVia;
        this.nombreVia = nombreVia;
        this.numero = numero;
        this.poblacion = poblacion;
        this.provinciaEstado = provinciaEstado;
        this.codPostal = codPostal;
        this.pais = pais;
    }

    public Integer getIdDireccion() {
        return idDireccion;
    }

    public void setIdDireccion(Integer idDireccion) {
        this.idDireccion = idDireccion;
    }

    public String getTipoVia() {
        return tipoVia;
    }

    public void setTipoVia(String tipoVia) {
        this.tipoVia = tipoVia;
    }

    public String getNombreVia() {
        return nombreVia;
    }

    public void setNombreVia(String nombreVia) {
        this.nombreVia = nombreVia;
    }

    public BigDecimal getNumero() {
        return numero;
    }

    public void setNumero(BigDecimal numero) {
        this.numero = numero;
    }

    public String getPoblacion() {
        return poblacion;
    }

    public void setPoblacion(String poblacion) {
        this.poblacion = poblacion;
    }

    public String getProvinciaEstado() {
        return provinciaEstado;
    }

    public void setProvinciaEstado(String provinciaEstado) {
        this.provinciaEstado = provinciaEstado;
    }

    public int getCodPostal() {
        return codPostal;
    }

    public void setCodPostal(int codPostal) {
        this.codPostal = codPostal;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public List<Cliente> getClienteList() {
        return clienteList;
    }

    public void setClienteList(List<Cliente> clienteList) {
        this.clienteList = clienteList;
    }

    public List<Sucursal> getSucursalList() {
        return sucursalList;
    }

    public void setSucursalList(List<Sucursal> sucursalList) {
        this.sucursalList = sucursalList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idDireccion != null ? idDireccion.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Direccion)) {
            return false;
        }
        Direccion other = (Direccion) object;
        if ((this.idDireccion == null && other.idDireccion != null)
                || (this.idDireccion != null && !this.idDireccion.equals(other.idDireccion))
                || (this.idDireccion != null && other.idDireccion != null && !this.toString().equals(other.toString()))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Direccion[" + "idDireccion=" + idDireccion + ", "
                + "tipoVia=" + tipoVia + ", nombreVia=" + nombreVia
                + ", numero=" + numero.stripTrailingZeros().toPlainString()
                + ", poblacion=" + poblacion
                + ", provinciaEstado=" + provinciaEstado
                + ", codPostal=" + codPostal + ", pais=" + pais + ']';
    }

}
