package model.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
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
@Table(name = "cliente")
@NamedQueries({
    @NamedQuery(name = "Cliente.findAll", query = "SELECT c FROM Cliente c"),
    @NamedQuery(name = "Cliente.findByIdCliente", query = "SELECT c FROM Cliente c WHERE c.idCliente = :idCliente"),
    @NamedQuery(name = "Cliente.findByTipoIdentificacion", query = "SELECT c FROM Cliente c WHERE c.tipoIdentificacion = :tipoIdentificacion"),
    @NamedQuery(name = "Cliente.findByClaveIdentificacion", query = "SELECT c FROM Cliente c WHERE c.claveIdentificacion = :claveIdentificacion"),
    @NamedQuery(name = "Cliente.findByNombreCliente", query = "SELECT c FROM Cliente c WHERE c.nombreCliente = :nombreCliente"),
    @NamedQuery(name = "Cliente.findByApellidosCliente", query = "SELECT c FROM Cliente c WHERE c.apellidosCliente = :apellidosCliente"),
    @NamedQuery(name = "Cliente.findByFecNacimiento", query = "SELECT c FROM Cliente c WHERE c.fecNacimiento = :fecNacimiento"),
    @NamedQuery(name = "Cliente.findByNacionalidad", query = "SELECT c FROM Cliente c WHERE c.nacionalidad = :nacionalidad")})
public class Cliente implements Serializable {
  
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID_CLIENTE")
    private Integer idCliente;
    @Basic(optional = false)
    @Column(name = "TIPO_IDENTIFICACION")
    private String tipoIdentificacion;
    @Basic(optional = false)
    @Column(name = "CLAVE_IDENTIFICACION")
    private String claveIdentificacion;
    @Basic(optional = false)
    @Column(name = "NOMBRE_CLIENTE")
    private String nombreCliente;
    @Column(name = "APELLIDOS_CLIENTE")
    private String apellidosCliente;
    @Basic(optional = false)
    @Column(name = "FEC_NACIMIENTO")
    @Temporal(TemporalType.DATE)
    private Date fecNacimiento;
    @Basic(optional = false)
    @Column(name = "NACIONALIDAD")
    private String nacionalidad;
    @JoinTable(name = "cliente_producto_bancario", joinColumns = {
        @JoinColumn(name = "ID_CLIENTE", referencedColumnName = "ID_CLIENTE")}, inverseJoinColumns = {
        @JoinColumn(name = "ID_PRODUCTO_BANCARIO", referencedColumnName = "ID_PRODUCTO_BANCARIO")})
    @ManyToMany
    private List<ProductoBancario> productoBancarioList;
    @JoinColumn(name = "ID_DIRECCION", referencedColumnName = "ID_DIRECCION")
    @ManyToOne(optional = false)
    private Direccion direccion;
    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_EMPLEADO", referencedColumnName = "ID_EMPLEADO")
    private Empleado empleado;
    @OneToOne(optional = false)
    @JoinColumn(name = "ID_USUARIO", referencedColumnName = "ID_USUARIO")    
    private Usuario usuario;

    public Cliente() {
    }

    public Cliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public Cliente(Integer idCliente, String tipoIdentificacion, String claveIdentificacion, String nombreCliente, Date fecNacimiento, String nacionalidad) {
        this.idCliente = idCliente;
        this.tipoIdentificacion = tipoIdentificacion;
        this.claveIdentificacion = claveIdentificacion;
        this.nombreCliente = nombreCliente;
        this.fecNacimiento = fecNacimiento;
        this.nacionalidad = nacionalidad;
    }

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public String getTipoIdentificacion() {       
        return tipoIdentificacion;
    }

    public void setTipoIdentificacion(String tipoIdentificacion) {
        this.tipoIdentificacion = tipoIdentificacion;
    }

    public String getClaveIdentificacion() {
        return claveIdentificacion;
    }

    public void setClaveIdentificacion(String claveIdentificacion) {
        this.claveIdentificacion = claveIdentificacion;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getApellidosCliente() {
        // Convertimos "null" a cadena vacía.
        if (apellidosCliente == null)
            apellidosCliente = "";
        return apellidosCliente;
    }

    public void setApellidosCliente(String apellidosCliente) {
        this.apellidosCliente = apellidosCliente;
    }

    public Date getFecNacimiento() {
        return fecNacimiento;
    }

    public void setFecNacimiento(Date fecNacimiento) {
        this.fecNacimiento = fecNacimiento;
    }

    public String getNacionalidad() {
        return nacionalidad;
    }

    public void setNacionalidad(String nacionalidad) {
        this.nacionalidad = nacionalidad;
    }

    public List<ProductoBancario> getProductoBancarioList() {
        return productoBancarioList;
    }

    public void setProductoBancarioList(List<ProductoBancario> productoBancarioList) {
        this.productoBancarioList = productoBancarioList;
    }

    public Direccion getDireccion() {
        return direccion;
    }

    public void setDireccion(Direccion direccion) {
        this.direccion = direccion;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idCliente != null ? idCliente.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Cliente)) {
            return false;
        }
        Cliente other = (Cliente) object;
        if ((this.idCliente == null && other.idCliente != null) || (this.idCliente != null && !this.idCliente.equals(other.idCliente))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Cliente[idCliente=" + idCliente
                + ", tipoIdentificacion=" + tipoIdentificacion
                + ", claveIdentificacion=" + claveIdentificacion
                + ", nombreCliente=" + nombreCliente
                + ", apellidosCliente=" + apellidosCliente
                + ", fecNacimiento=" + fecNacimiento
                + ", nacionalidad=" + nacionalidad
                + ", direccion=" + direccion
                + ", empleado=" + empleado
                + ", usuario=" + usuario + ']';
    }
    
}
