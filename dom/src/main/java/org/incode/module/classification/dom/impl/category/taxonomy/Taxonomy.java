package org.incode.module.classification.dom.impl.category.taxonomy;

import lombok.Getter;
import lombok.Setter;
import org.apache.isis.applib.annotation.*;
import org.apache.isis.applib.services.i18n.TranslatableString;
import org.incode.module.classification.dom.impl.applicability.Applicability;
import org.incode.module.classification.dom.impl.category.Category;

import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.Persistent;
import java.util.Objects;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.SUPERCLASS_TABLE)
@DomainObject(
        editing = Editing.DISABLED
)
public class Taxonomy extends Category {

    //region > constructor
    public Taxonomy(final String name) {
        super(null, null, name);
    }
    //endregion

    //region > applicabilties (collection)
    public static class ApplicabilitiesDomainEvent extends CollectionDomainEvent<Applicability> { }
    @Persistent(mappedBy = "taxonomy", dependentElement = "true")
    @Collection(
            domainEvent = ApplicabilitiesDomainEvent.class,
            editing = Editing.DISABLED
    )
    @Getter @Setter
    private SortedSet<Applicability> applicabilities = new TreeSet<>();

    //endregion

    //region > applicableTo (action)
    public static class ApplicableToDomainEvent extends ActionDomainEvent { }
    @Action(
            domainEvent = ApplicableToDomainEvent.class
    )
    @ActionLayout(
            cssClassFa = "fa-plus"
    )
    @MemberOrder(name = "applicabilities", sequence = "1")
    public Category applicableTo(final String atPath, final Class<?> domainType) {
        String domainTypeName = domainType.getName();
        Applicability applicability = new Applicability(this, atPath, domainTypeName);
        repositoryService.persistAndFlush(applicability);
        return this;
    }
    public TranslatableString validateApplicableTo(final String atPath, final Class<?> domainType) {
        String domainTypeName = domainType.getName();
        final Optional<Applicability> any =
                getApplicabilities().stream()
                        .filter(x -> Objects.equals(x.getAtPath(), atPath) &&
                                Objects.equals(x.getDomainType(), domainTypeName))
                        .findAny();
        return any.isPresent()
                ? TranslatableString.tr(
                        "Already applicable for '{atPath}' and '{domainTypeName}'",
                        "atPath", atPath,
                        "domainTypeName", domainTypeName)
                : null;
    }

    //endregion

    //region > noLongerApplicableTo (action)
    public static class NoLongerApplicableToDomainEvent extends ActionDomainEvent { }
    @Action(
            domainEvent = NoLongerApplicableToDomainEvent.class,
            semantics = SemanticsOf.IDEMPOTENT_ARE_YOU_SURE
    )
    @ActionLayout(
            cssClassFa = "fa-minus"
    )
    @MemberOrder(name = "applicabilities", sequence = "2")
    public Category noLongerApplicable(final Applicability applicability) {
        repositoryService.remove(applicability);
        return this;
    }

    public SortedSet<Applicability> choices0NoLongerApplicable() {
        return getApplicabilities();
    }
    //endregion

}
