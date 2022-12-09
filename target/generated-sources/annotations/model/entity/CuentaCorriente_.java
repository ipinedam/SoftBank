package model.entity;

import java.math.BigDecimal;
import javax.annotation.processing.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import model.entity.Prestamo;
import model.entity.ProductoBancario;
import model.entity.TarjetaCredito;

@Generated(value="org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProcessor", date="2022-12-06T16:04:36", comments="EclipseLink-2.7.10.v20211216-rNA")
@StaticMetamodel(CuentaCorriente.class)
public class CuentaCorriente_ { 

    public static volatile ListAttribute<CuentaCorriente, Prestamo> prestamoList;
    public static volatile SingularAttribute<CuentaCorriente, String> iban;
    public static volatile SingularAttribute<CuentaCorriente, BigDecimal> tipoInteres;
    public static volatile SingularAttribute<CuentaCorriente, Integer> idCuentaCorriente;
    public static volatile SingularAttribute<CuentaCorriente, Integer> numeroCuenta;
    public static volatile SingularAttribute<CuentaCorriente, BigDecimal> impSaldoActual;
    public static volatile ListAttribute<CuentaCorriente, TarjetaCredito> tarjetaCreditoList;
    public static volatile SingularAttribute<CuentaCorriente, ProductoBancario> productoBancario;

}