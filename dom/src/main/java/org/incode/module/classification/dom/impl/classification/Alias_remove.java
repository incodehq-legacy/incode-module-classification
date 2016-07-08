package org.incode.module.classification.dom.impl.classification;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.incode.module.classification.dom.api.classifiable.Classifiable;

@Mixin
public class Alias_remove {

    //region > injected services
    @Inject
    AliasRepository aliasRepository;
    //endregion

    //region > constructor
    private final Alias alias;
    public Alias_remove(final Alias alias) {
        this.alias = alias;
    }
    @Programmatic
    public Alias getAlias() {
        return alias;
    }
    //endregion


    public static class DomainEvent extends Alias.ActionDomainEvent<Alias_remove> { }
    @Action(
            domainEvent = DomainEvent.class,
            semantics = SemanticsOf.IDEMPOTENT_ARE_YOU_SURE
    )
    public Classifiable $$() {
        final Classifiable classifiable = this.alias.getClassifiable();
        aliasRepository.remove(this.alias);
        return classifiable;
    }

}
