package model.entity;

import java.util.Date;
import javax.annotation.processing.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import model.entity.Aval;
import model.entity.Catalogo;
import model.entity.Cliente;
import model.entity.CuentaCorriente;
import model.entity.Movimiento;
import model.entity.Prestamo;
import model.entity.Sucursal;
import model.entity.TarjetaCredito;

@Generated(value="org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProcessor", date="2022-12-06T16:04:36", comments="EclipseLink-2.7.10.v20211216-rNA")
@StaticMetamodel(ProductoBancario.class)
public class ProductoBancario_ { 

    public static volatile ListAttribute<ProductoBancario, Cliente> clienteList;
    public static volatile SingularAttribute<ProductoBancario, TarjetaCredito> tarjetaCredito;
    public static volatile SingularAttribute<ProductoBancario, Prestamo> prestamo;
    public static volatile SingularAttribute<ProductoBancario, Sucursal> sucursal;
    public static volatile ListAttribute<ProductoBancario, Movimiento> movimientoList;
    public static volatile SingularAttribute<ProductoBancario, Date> fecLiquidacion;
    public static volatile SingularAttribute<ProductoBancario, Catalogo> catalogo;
    public static volatile SingularAttribute<ProductoBancario, CuentaCorriente> cuentaCorriente;
    public static volatile SingularAttribute<ProductoBancario, Date> fecCancelacion;
    public static volatile SingularAttribute<ProductoBancario, Date> fecApertura;
    public static volatile SingularAttribute<ProductoBancario, Aval> aval;
    public static volatile SingularAttribute<ProductoBancario, Integer> idProductoBancario;

}