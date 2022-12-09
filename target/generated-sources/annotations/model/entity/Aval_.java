package model.entity;

import java.math.BigDecimal;
import java.util.Date;
import javax.annotation.processing.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import model.entity.ProductoBancario;

@Generated(value="org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProcessor", date="2022-12-06T16:04:36", comments="EclipseLink-2.7.10.v20211216-rNA")
@StaticMetamodel(Aval.class)
public class Aval_ { 

    public static volatile SingularAttribute<Aval, BigDecimal> impAvalado;
    public static volatile SingularAttribute<Aval, Long> numeroAval;
    public static volatile SingularAttribute<Aval, Date> fecVencimiento;
    public static volatile SingularAttribute<Aval, Integer> idAval;
    public static volatile SingularAttribute<Aval, ProductoBancario> productoBancario;

}