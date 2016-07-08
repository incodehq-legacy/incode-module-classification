/*
 *
 *  Copyright 2015 incode.org
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.incode.module.classification.dom.impl.classifiablelink;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.incode.module.classification.dom.api.classifiable.Classifiable;

@Mixin
public class Object_unclassify {

    //region  > (injected)
    @Inject
    ClassifiableLinkRepository classifiableLinkRepository;
    //endregion

    //region > constructor
    private final Object classifiable;
    public Object_unclassify(final Object classifiable) {
        this.classifiable = classifiable;
    }

    public Object getClassifiable() {
        return classifiable;
    }
    //endregion

    public static class DomainEvent extends Classifiable.ActionDomainEvent<Object_unclassify> { } { }

    @Action(
            domainEvent = DomainEvent.class,
            semantics = SemanticsOf.IDEMPOTENT
    )
    @ActionLayout(
            cssClassFa = "fa-minus"
    )
    @MemberOrder(name = "classificationLinks", sequence = "2")
    public Object $$(final ClassifiableLink classifiableLink) {
        classifiableLinkRepository.delete(classifiableLink);
        return this.classifiable;
    }

    public String disable$$() {
        return choices0$$().isEmpty()? "No classifications to delete": null;
    }

    public List<ClassifiableLink> choices0$$() {
        return classifiableLinkRepository.findByClassifiableAndDate(this.classifiable, null);
    }

    public boolean hide$$() {
        return !classifiableLinkRepository.supports(classifiable);
    }

}
