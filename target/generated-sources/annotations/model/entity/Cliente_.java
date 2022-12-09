package model.entity;

import java.util.Date;
import javax.annotation.processing.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import model.entity.Direccion;
import model.entity.Empleado;
import model.entity.ProductoBancario;
import model.entity.Usuario;

@Generated(value="org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProcessor", date="2022-12-06T16:04:36", comments="EclipseLink-2.7.10.v20211216-rNA")
@StaticMetamodel(Cliente.class)
public class Cliente_ { 

    public static volatile SingularAttribute<Cliente, String> claveIdentificacion;
    public static volatile SingularAttribute<Cliente, Integer> idCliente;
    public static volatile SingularAttribute<Cliente, String> nombreCliente;
    public static volatile SingularAttribute<Cliente, String> apellidosCliente;
    public static volatile SingularAttribute<Cliente, String> tipoIdentificacion;
    public static volatile ListAttribute<Cliente, ProductoBancario> productoBancarioList;
    public static volatile SingularAttribute<Cliente, Empleado> empleado;
    public static volatile SingularAttribute<Cliente, Direccion> direccion;
    public static volatile SingularAttribute<Cliente, Date> fecNacimiento;
    public static volatile SingularAttribute<Cliente, Usuario> usuario;
    public static volatile SingularAttribute<Cliente, String> nacionalidad;

}