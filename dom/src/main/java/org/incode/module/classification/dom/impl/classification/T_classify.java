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

import com.google.common.collect.Sets;
import org.apache.isis.applib.annotation.*;
import org.apache.isis.applib.services.registry.ServiceRegistry;
import org.incode.module.classification.dom.ClassificationModule;
import org.incode.module.classification.dom.impl.applicability.Applicability;
import org.incode.module.classification.dom.impl.applicability.ApplicabilityRepository;
import org.incode.module.classification.dom.impl.category.Category;
import org.incode.module.classification.dom.impl.category.CategoryRepository;
import org.incode.module.classification.dom.impl.category.taxonomy.Taxonomy;
import org.incode.module.classification.dom.spi.ApplicationTenancyService;

import javax.inject.Inject;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.stream.Collectors;

public abstract class T_classify<T> {

    //region > constructor
    private final T classified;
    public T_classify(final T classified) {
        this.classified = classified;
    }

    public T getClassified() {
        return classified;
    }

    //endregion

    //region > $$
    public static class DomainEvent extends ClassificationModule.ActionDomainEvent<T_classify> { }

    @Action(
            domainEvent = DomainEvent.class,
            semantics = SemanticsOf.NON_IDEMPOTENT
    )
    @ActionLayout(
            cssClassFa = "fa-plus",
            contributed = Contributed.AS_ACTION
    )
    @MemberOrder(name = "classifications", sequence = "1")
    public T $$(
            final Taxonomy taxonomy,
            final Category category) {
        classificationRepository.create(taxonomy, category, classified);
        return classified;
    }

    public Collection<Taxonomy> choices0$$() {
        SortedSet<Applicability> applicableToClassHierarchy = Sets.newTreeSet();

        // pull together all the 'Applicability's for this domain type and all its supertypes.
        String atPath = getAtPath();
        appendDirectApplicabilities(atPath, classified.getClass(), applicableToClassHierarchy);


        // the obtain the corresponding 'Taxonomy's of each of these
        Set<Taxonomy> taxonomies = Sets.newTreeSet();
        taxonomies.addAll(
            applicableToClassHierarchy.stream()
                    .map(Applicability::getTaxonomy)
                    .distinct()
                    .collect(Collectors.toSet())
            );

        // remove any taxonomies already selected
        T_classifications t_classifications = new T_classifications(classified) {};
        serviceRegistry.injectServicesInto(t_classifications);
        final List<Classification> classifications = t_classifications.$$();
        final Set<Taxonomy> existing = classifications.stream().map(Classification::getTaxonomy).collect(Collectors.toSet());
        taxonomies.removeAll(existing);

        return taxonomies;
    }


    String getAtPath() {
        return applicationTenancyRepositories.stream()
                .map(x -> x.atPathFor(classified))
                .filter(x -> x != null)
                .findFirst()
                .orElse(null);
    }

    private void appendDirectApplicabilities(
            final String atPath,
            Class<?> domainType,
            final SortedSet<Applicability> applicabilities) {
        while(domainType != null) {
            appendDirectApplicatiesFor(atPath, domainType, applicabilities);
            domainType = domainType.getSuperclass();
        }
    }

    private void appendDirectApplicatiesFor(
            final String atPath,
            final Class<?> domainType,
            final SortedSet<Applicability> applicabilities) {
        // this query handles fact that atPath is hierarchy.
        List<Applicability> applicabilitiesForDomainType =
                applicabilityRepository.findByDomainTypeAndUnderAtPath(domainType, atPath);
        applicabilities.addAll(applicabilitiesForDomainType);
    }


    public Collection<Category> choices1$$(final Taxonomy taxonomy) {
        return categoryRepository.findByTaxonomy(taxonomy);
    }

    //endregion

    //region  > (injected)
    @Inject
    ApplicabilityRepository applicabilityRepository;
    @Inject
    ClassificationRepository classificationRepository;
    @Inject
    CategoryRepository categoryRepository;
    @Inject
    ServiceRegistry serviceRegistry;
    @Inject
    List<ApplicationTenancyService> applicationTenancyRepositories;
    //endregion

}
