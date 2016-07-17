package org.incode.module.classification.dom.impl.applicability;

import javax.inject.Inject;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;

import com.google.common.base.Function;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.services.title.TitleService;
import org.apache.isis.applib.util.ObjectContracts;
import org.apache.isis.applib.util.TitleBuffer;

import org.incode.module.classification.dom.ClassificationModule;
import org.incode.module.classification.dom.impl.classification.Classification;
import org.incode.module.classification.dom.impl.classification.Taxonomy;

import lombok.Getter;
import lombok.Setter;

/**
 * Indicates whether a domain object('s type) is applicable to a particular {@link Classification}, with respect to
 * the application tenancy of that domain object.
 */
@javax.jdo.annotations.PersistenceCapable(
        schema = "incodeClassification",
        table = "Applicability",
        identityType = IdentityType.DATASTORE)
@javax.jdo.annotations.DatastoreIdentity(
        strategy = IdGeneratorStrategy.NATIVE,
        column = "id")
@javax.jdo.annotations.Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByDomainTypeAndAtPaths", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incode.module.classification.dom.impl.applicability.Applicability "
                        + "WHERE domainType == :domainType "
                        + "&&    :atPaths.contains(atPath)"),
        @javax.jdo.annotations.Query(
                name = "findByTaxonomy", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incode.module.classification.dom.impl.applicability.Applicability "
                        + "WHERE taxonomy == :taxonomy "),
})
@javax.jdo.annotations.Indices({
        @javax.jdo.annotations.Index(
                name = "Applicability_domainType_atPath_taxonomy_UNQ",
                members = { "domainType", "atPath", "taxonomy" },
                unique = "true"
        ),
})
@javax.jdo.annotations.Uniques({
        @javax.jdo.annotations.Unique(
                name="Applicability_taxonomy_atPath_domainType_UNQ",
                members = { "taxonomy", "atPath", "domainType" }),

})
@DomainObject(
        editing = Editing.DISABLED
)
public class Applicability implements Comparable<Applicability> {

    public Applicability(final Taxonomy taxonomy, final String atPath, final Class<?> domainType) {
        this(taxonomy, atPath, domainType.getName());
    }

    public Applicability(final Taxonomy taxonomy, final String atPath, final String domainTypeName) {
        setAtPath(atPath);
        setTaxonomy(taxonomy);
        setDomainType(domainTypeName);
    }

    //region > event classes
    public static abstract class PropertyDomainEvent<S,T> extends ClassificationModule.PropertyDomainEvent<S, T> { }
    public static abstract class CollectionDomainEvent<S,T> extends ClassificationModule.CollectionDomainEvent<S, T> { }
    public static abstract class ActionDomainEvent<S> extends ClassificationModule.ActionDomainEvent<S> { }
    //endregion

    //region > title
    public String title() {
        final TitleBuffer buf = new TitleBuffer();
        buf.append(simpleNameOf(getDomainType()));
        buf.append(" [");
        buf.append(getAtPath());
        buf.append("]: ");
        buf.append(titleService.titleOf(getTaxonomy()));
        return buf.toString();
    }

    private static String simpleNameOf(final String domainType) {
        int lastDot = domainType.lastIndexOf(".");
        return domainType.substring(lastDot+1);
    }
    //endregion


    public static class ClassificationDomainEvent extends PropertyDomainEvent<Applicability,String> { }
    @Getter @Setter
    @javax.jdo.annotations.Column(allowsNull = "false", name = "taxonomyId")
    @Property(
            domainEvent = ClassificationDomainEvent.class,
            editing = Editing.DISABLED
    )
    private Taxonomy taxonomy;


    public static class AtPathDomainEvent extends Applicability.PropertyDomainEvent<Applicability,String> { }
    @Getter @Setter
    @javax.jdo.annotations.Column(allowsNull = "false", length = ClassificationModule.JdoColumnLength.AT_PATH)
    @Property(
            domainEvent = AtPathDomainEvent.class,
            editing = Editing.DISABLED
    )
    @PropertyLayout(
            named = "Application tenancy"
    )
    private String atPath;


    public static class DomainTypeDomainEvent extends PropertyDomainEvent<Applicability,String> { }
    @Getter @Setter
    @javax.jdo.annotations.Column(allowsNull = "false", length = ClassificationModule.JdoColumnLength.DOMAIN_TYPE)
    @Property(
            domainEvent = DomainTypeDomainEvent.class
    )
    private String domainType;


    //region > Functions

    public final static class Functions {
        private Functions() {}
        public final static Function<Applicability, Classification> GET_TAXONOMY = input -> input.getTaxonomy();
        public final static Function<Applicability, String> GET_AT_PATH = input -> input.getAtPath();
        public final static Function<Applicability, String> GET_DOMAIN_TYPE = input -> input.getDomainType();
    }
    //endregion

    //region > toString, compareTo

    @Override
    public String toString() {
        return ObjectContracts.toString(this, "taxonomy", "atPath", "domainType");
    }

    @Override
    public int compareTo(final Applicability other) {
        return ObjectContracts.compare(this, other, "taxonomy", "atPath", "domainType");
    }

    //endregion

    @Inject
    TitleService titleService;

}
