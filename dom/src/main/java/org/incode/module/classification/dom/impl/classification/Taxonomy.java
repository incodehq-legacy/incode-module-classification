package org.incode.module.classification.dom.impl.classification;

import java.util.Objects;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.Persistent;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Collection;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.incode.module.classification.dom.impl.applicability.Applicability;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        schema = "incodeClassification",
        table = "Classification",
        identityType = IdentityType.DATASTORE)
@javax.jdo.annotations.Inheritance(
        strategy = InheritanceStrategy.SUPERCLASS_TABLE)
@DomainObject(editing = Editing.DISABLED )
public class Taxonomy extends Classification {

    //region > constructor
    /**
     * Create a top-level taxonomy.
     */
    public Taxonomy(final String name) {
        super(null, name);
    }

    //endregion

    //region > applicabilties
    @Persistent(mappedBy = "taxonomy", dependentElement = "true")
    @Collection()
    @Getter @Setter
    private SortedSet<Applicability> applicabilities = new TreeSet<>();

    //endregion

    //region > applicableTo

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

    //endregion

    //region > noLongerApplicableTo

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

    //endregion



}
