package model.entity;

import javax.annotation.processing.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import model.entity.ProductoBancario;

@Generated(value="org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProcessor", date="2022-12-06T16:04:36", comments="EclipseLink-2.7.10.v20211216-rNA")
@StaticMetamodel(Catalogo.class)
public class Catalogo_ { 

    public static volatile SingularAttribute<Catalogo, Integer> idCatalogo;
    public static volatile SingularAttribute<Catalogo, String> codProducto;
    public static volatile ListAttribute<Catalogo, ProductoBancario> productoBancarioList;
    public static volatile SingularAttribute<Catalogo, String> nombreProducto;

}