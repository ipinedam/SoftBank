package model.entity;

import javax.annotation.processing.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import model.entity.Direccion;
import model.entity.Empleado;
import model.entity.ProductoBancario;

@Generated(value="org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProcessor", date="2022-12-06T16:04:36", comments="EclipseLink-2.7.10.v20211216-rNA")
@StaticMetamodel(Sucursal.class)
public class Sucursal_ { 

    public static volatile SingularAttribute<Sucursal, Integer> idSucursal;
    public static volatile ListAttribute<Sucursal, ProductoBancario> productoBancarioList;
    public static volatile ListAttribute<Sucursal, Empleado> empleadoList;
    public static volatile SingularAttribute<Sucursal, String> nombreSucursal;
    public static volatile SingularAttribute<Sucursal, Direccion> direccion;
    public static volatile SingularAttribute<Sucursal, Integer> codSucursal;

}