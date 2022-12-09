package model.entity;

import java.math.BigDecimal;
import java.util.Date;
import javax.annotation.processing.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import model.entity.CuentaCorriente;
import model.entity.ProductoBancario;

@Generated(value="org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProcessor", date="2022-12-06T16:04:36", comments="EclipseLink-2.7.10.v20211216-rNA")
@StaticMetamodel(Prestamo.class)
public class Prestamo_ { 

    public static volatile SingularAttribute<Prestamo, Date> fecVencimiento;
    public static volatile SingularAttribute<Prestamo, String> tipoGarantia;
    public static volatile SingularAttribute<Prestamo, BigDecimal> tipoInteres;
    public static volatile SingularAttribute<Prestamo, Integer> idPrestamo;
    public static volatile SingularAttribute<Prestamo, CuentaCorriente> cuentaCorrientePago;
    public static volatile SingularAttribute<Prestamo, BigDecimal> impConcedido;
    public static volatile SingularAttribute<Prestamo, BigDecimal> impSaldoPendiente;
    public static volatile SingularAttribute<Prestamo, Long> numeroPrestamo;
    public static volatile SingularAttribute<Prestamo, ProductoBancario> productoBancario;

}