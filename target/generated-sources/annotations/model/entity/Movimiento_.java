package model.entity;

import java.math.BigDecimal;
import java.util.Date;
import javax.annotation.processing.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import model.entity.ProductoBancario;

@Generated(value="org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProcessor", date="2022-12-06T16:04:36", comments="EclipseLink-2.7.10.v20211216-rNA")
@StaticMetamodel(Movimiento.class)
public class Movimiento_ { 

    public static volatile SingularAttribute<Movimiento, BigDecimal> impMovimiento;
    public static volatile SingularAttribute<Movimiento, Integer> idMovimiento;
    public static volatile SingularAttribute<Movimiento, Date> fecMovimiento;
    public static volatile SingularAttribute<Movimiento, String> concepto;
    public static volatile SingularAttribute<Movimiento, ProductoBancario> productoBancario;

}