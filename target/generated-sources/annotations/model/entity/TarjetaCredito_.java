package model.entity;

import java.math.BigDecimal;
import javax.annotation.processing.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import model.entity.CuentaCorriente;
import model.entity.ProductoBancario;

@Generated(value="org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProcessor", date="2022-12-06T16:04:36", comments="EclipseLink-2.7.10.v20211216-rNA")
@StaticMetamodel(TarjetaCredito.class)
public class TarjetaCredito_ { 

    public static volatile SingularAttribute<TarjetaCredito, BigDecimal> tipoInteres;
    public static volatile SingularAttribute<TarjetaCredito, CuentaCorriente> cuentaCorrientePago;
    public static volatile SingularAttribute<TarjetaCredito, BigDecimal> impLimiteTarjeta;
    public static volatile SingularAttribute<TarjetaCredito, Long> pan;
    public static volatile SingularAttribute<TarjetaCredito, String> formaPago;
    public static volatile SingularAttribute<TarjetaCredito, Integer> idTarjetaCredito;
    public static volatile SingularAttribute<TarjetaCredito, Long> numeroTarjeta;
    public static volatile SingularAttribute<TarjetaCredito, BigDecimal> impSaldoPendiente;
    public static volatile SingularAttribute<TarjetaCredito, ProductoBancario> productoBancario;

}