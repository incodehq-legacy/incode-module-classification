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
package org.incode.module.classification.dom.impl.classification;

import org.apache.isis.applib.annotation.*;
import org.apache.isis.applib.services.i18n.TranslatableString;
import org.incode.module.classification.dom.ClassificationModule;

import javax.inject.Inject;
import java.util.List;

public abstract class T_unclassify<T> {

    //region > constructor
    private final T classified;
    public T_unclassify(final T classified) {
        this.classified = classified;
    }

    public T getClassified() {
        return classified;
    }
    //endregion

    //region > $$

    public static class DomainEvent extends ClassificationModule.ActionDomainEvent<T_unclassify> { } { }

    @Action(
            domainEvent = DomainEvent.class,
            semantics = SemanticsOf.IDEMPOTENT
    )
    @ActionLayout(
            cssClassFa = "fa-minus",
            contributed = Contributed.AS_ACTION
    )
    @MemberOrder(name = "classifications", sequence = "2")
    public Object $$(final Classification classification) {
        classificationRepository.remove(classification);
        return this.classified;
    }

    public TranslatableString disable$$() {
        return choices0$$().isEmpty()? TranslatableString.tr("No classifications to delete"): null;
    }

    public List<Classification> choices0$$() {
        return classificationRepository.findByClassified(this.classified);
    }

    //endregion

    //region  > (injected)
    @Inject
    ClassificationRepository classificationRepository;
    //endregion
}
