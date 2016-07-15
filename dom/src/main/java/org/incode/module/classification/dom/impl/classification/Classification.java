package org.incode.module.classification.dom.impl.classification;

import java.util.Objects;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.VersionStrategy;

import com.google.common.collect.Sets;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Collection;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.services.repository.RepositoryService;
import org.apache.isis.applib.services.title.TitleService;
import org.apache.isis.applib.util.ObjectContracts;

import org.incode.module.classification.dom.ClassificationModule;
import org.incode.module.classification.dom.impl.applicability.Applicability;
import org.incode.module.classification.dom.impl.classifiablelink.ClassifiableLinkRepository;

import lombok.Getter;
import lombok.Setter;

/**
 * An event that has or is scheduled to occur at some point in time, pertaining
 * to an "classifiable" domain object.
 */
@javax.jdo.annotations.PersistenceCapable(
        schema = "incodeClassification",
        table = "Classification",
        identityType = IdentityType.DATASTORE)
@javax.jdo.annotations.DatastoreIdentity(
        strategy = IdGeneratorStrategy.NATIVE,
        column = "id")
@javax.jdo.annotations.Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByFullyQualifiedName", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incode.module.classification.dom.impl.classification.Classification "
                        + "WHERE fullyQualifiedName == :fullyQualifiedName"),
        @javax.jdo.annotations.Query(
                name = "findByParent", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incode.module.classification.dom.impl.classification.Classification "
                        + "WHERE parent == :parent"),
        @javax.jdo.annotations.Query(
                name = "findByParentAndLocalName", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incode.module.classification.dom.impl.classification.Classification "
                        + "WHERE parent == :parent "
                        + "&&    localName == :localName "),
})
@javax.jdo.annotations.Uniques({
        @javax.jdo.annotations.Unique(
                name="Classification_fullyQualifiedName_UNQ",
                members = { "fullyQualifiedName" }),
        @javax.jdo.annotations.Unique(
                name="Classification_parent_localName_UNQ",
                members = { "parent", "localName" })
})
@DomainObject(
        editing = Editing.DISABLED
)
public class Classification implements Comparable<Classification> {

    //region > constructor
    /**
     * Create a top-level classification.
     */
    public Classification(final String name) {
        this(null, name, false);
    }

    private Classification(final Classification parent, final String name, final boolean selectable) {
        setParent(parent);
        setLocalName(name);
        setSelectable(selectable);

        deriveFullyQualifiedName();
    }

    private void deriveFullyQualifiedName() {
        StringBuilder buf = new StringBuilder();
        prependName(this, buf);
        setFullyQualifiedName(buf.toString());
    }

    private static void prependName(Classification classification, final StringBuilder buf) {
        while(classification != null) {
            prependLocalNameOf(classification, buf);
            classification = classification.getParent();
        }
    }

    private static void prependLocalNameOf(final Classification classification, final StringBuilder buf) {
        if(buf.length() > 0) {
            buf.insert(0, "/");
        }
        buf.insert(0, classification.getLocalName());
    }
    //endregion

    //region > event classes
    public static abstract class PropertyDomainEvent<S,T> extends ClassificationModule.PropertyDomainEvent<S, T> { }
    public static abstract class CollectionDomainEvent<S,T> extends ClassificationModule.CollectionDomainEvent<S, T> { }
    public static abstract class ActionDomainEvent<S> extends ClassificationModule.ActionDomainEvent<S> { }
    //endregion

    public static class FullyQualifiedNameDomainEvent extends PropertyDomainEvent<Classification,String> { }
    @Title
    @Getter @Setter
    @javax.jdo.annotations.Column(allowsNull = "false", length = ClassificationModule.JdoColumnLength.FULLY_QUALIFIED_NAME)
    @Property(domainEvent = FullyQualifiedNameDomainEvent.class)
    private String fullyQualifiedName;


    public static class ParentDomainEvent extends PropertyDomainEvent<Classification,Classification> { }
    @Column(allowsNull = "true")
    @Property(domainEvent = ParentDomainEvent.class)
    @Getter @Setter
    private Classification parent;


    public static class LocalNameDomainEvent extends PropertyDomainEvent<Classification,String> { }
    @Getter @Setter
    @javax.jdo.annotations.Column(allowsNull = "false", length = ClassificationModule.JdoColumnLength.LOCAL_NAME)
    @Property(domainEvent = LocalNameDomainEvent.class)
    private String localName;


    public static class SelectableDomainEvent extends PropertyDomainEvent<Classification,Boolean> { }
    @Getter @Setter
    @javax.jdo.annotations.Column(allowsNull = "false")
    @Property(domainEvent = SelectableDomainEvent.class)
    private boolean selectable;

    @Persistent(mappedBy = "parent", dependentElement = "false")
    @Collection()
    @Getter @Setter
    private SortedSet<Classification> children = new TreeSet<>();

    @Action()
    @ActionLayout(
            cssClassFa = "fa-plus"
    )
    @MemberOrder(name = "children", sequence = "1")
    public Classification newChild(@ParameterLayout(named="Name") final String localName) {
        Classification classification = new Classification(this, localName, true);
        repositoryService.persistAndFlush(classification);
        return classification;
    }

    public String validate0NewChild(final String localName) {
        final Optional<Classification> any =
                getChildren().stream().filter(x -> Objects.equals(x.getLocalName(), localName)).findAny();
        return any.isPresent() ? "There is already a child classification with the name of '" + localName + "'": null;
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT_ARE_YOU_SURE)
    @ActionLayout(
            cssClassFa = "fa-minus"
    )
    @MemberOrder(name = "children", sequence = "2")
    public Classification remove(final Classification classification) {
        removeCascade(classification);
        return this;
    }

    private void removeCascade(final Classification classification) {
        SortedSet<Classification> children = classification.getChildren();
        for (Classification child : children) {
            removeCascade(child);
        }
        repositoryService.remove(classification);
    }

    public java.util.Collection<Classification> choices0Remove() {
        return getChildren();
    }
    

    @Persistent(mappedBy = "classification", dependentElement = "true")
    @Collection()
    @Getter @Setter
    private SortedSet<Applicability> applicabilities = new TreeSet<>();


    @Action()
    @ActionLayout(
            cssClassFa = "fa-plus"
    )
    @MemberOrder(name = "applicabilities", sequence = "1")
    public Classification applicableTo(final String atPath, final Class<?> domainType) {
        String domainTypeName = domainType.getName();
        Applicability applicability = new Applicability(this, atPath, domainTypeName);
        repositoryService.persistAndFlush(applicability);
        return this;
    }
    public String validateApplicableTo(final String atPath, final Class<?> domainType) {
        String domainTypeName = domainType.getName();
        final Optional<Applicability> any =
                getApplicabilities().stream()
                        .filter(x -> Objects.equals(x.getAtPath(), atPath) &&
                                     Objects.equals(x.getDomainType(), domainTypeName))
                        .findAny();
        return any.isPresent() ? "Already applicable for '" + atPath + "' and '" + domainTypeName + "'": null;
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT_ARE_YOU_SURE)
    @ActionLayout(
            cssClassFa = "fa-minus"
    )
    @MemberOrder(name = "applicabilities", sequence = "2")
    public Classification noLongerApplicable(final Applicability applicability) {
        repositoryService.remove(applicability);
        return this;
    }

    public SortedSet<Applicability> choices0NoLongerApplicable() {
        return getApplicabilities();
    }



    @javax.jdo.annotations.NotPersistent
    @Collection(notPersisted = true)
    public SortedSet<Applicability> getInheritedApplicabilities() {
        SortedSet<Applicability> applicabilities = Sets.newTreeSet();
        appendApplicabilities(this, applicabilities);
        return applicabilities;
    }

    private void appendApplicabilities(
            Classification classification,
            final SortedSet<Applicability> applicabilities) {
        while(classification != null) {
            appendApplicabilitiesTo(classification, applicabilities);
            classification = classification.getParent();
        }
    }

    private void appendApplicabilitiesTo(
            final Classification classification,
            final SortedSet<Applicability> applicabilities) {
        applicabilities.addAll(classification.getApplicabilities());
    }

    //region > toString, compareTo

    @Override
    public String toString() {
        return ObjectContracts.toString(this, "fullyQualifiedName");
    }

    @Override
    public int compareTo(final Classification other) {
        return ObjectContracts.compare(this, other, "fullyQualifiedName");
    }

    //endregion

    //region > injected services

    @Inject
    ClassifiableLinkRepository classifiableLinkRepository;
    @Inject
    TitleService titleService;
    @Inject
    RepositoryService repositoryService;

    //endregion


}
