package model.entity;

import java.math.BigDecimal;
import javax.annotation.processing.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import model.entity.Cliente;
import model.entity.Sucursal;

@Generated(value="org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProcessor", date="2022-12-06T16:04:36", comments="EclipseLink-2.7.10.v20211216-rNA")
@StaticMetamodel(Direccion.class)
public class Direccion_ { 

    public static volatile ListAttribute<Direccion, Cliente> clienteList;
    public static volatile ListAttribute<Direccion, Sucursal> sucursalList;
    public static volatile SingularAttribute<Direccion, Integer> idDireccion;
    public static volatile SingularAttribute<Direccion, String> nombreVia;
    public static volatile SingularAttribute<Direccion, BigDecimal> numero;
    public static volatile SingularAttribute<Direccion, String> tipoVia;
    public static volatile SingularAttribute<Direccion, String> poblacion;
    public static volatile SingularAttribute<Direccion, String> provinciaEstado;
    public static volatile SingularAttribute<Direccion, Integer> codPostal;
    public static volatile SingularAttribute<Direccion, String> pais;

}