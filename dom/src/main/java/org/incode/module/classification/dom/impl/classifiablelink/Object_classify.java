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

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.inject.Inject;

import com.google.common.collect.Sets;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.incode.module.classification.dom.ClassificationModule;
import org.incode.module.classification.dom.impl.applicability.Applicability;
import org.incode.module.classification.dom.impl.applicability.ApplicabilityRepository;
import org.incode.module.classification.dom.impl.classification.Classification;
import org.incode.module.classification.dom.spi.ApplicationTenancyRepository;

@Mixin
public class Object_classify {

    //region  > (injected)
    @Inject
    ApplicabilityRepository applicabilityRepository;
    @Inject
    ClassifiableLinkRepository classifiableLinkRepository;
    @Inject
    List<ApplicationTenancyRepository> applicationTenancyRepositories;
    //endregion

    //region > constructor
    private final Object classifiable;
    public Object_classify(final Object classifiable) {
        this.classifiable = classifiable;
    }

    public Object getClassifiable() {
        return classifiable;
    }

    //endregion

    //region > $$


    public static class DomainEvent extends ClassificationModule.ActionDomainEvent<Object_classify> { }

    @Action(
            domainEvent = DomainEvent.class,
            semantics = SemanticsOf.NON_IDEMPOTENT
    )
    @ActionLayout(
            cssClassFa = "fa-plus"
    )
    @MemberOrder(name = "classificationLinks", sequence = "1")
    public Object $$(
            @ParameterLayout(named = "Classification")
            final Classification classification,
            @Nullable
            @ParameterLayout(named = "Start date")
            final LocalDate startDate,
            @Nullable
            @ParameterLayout(named = "End date")
            final LocalDate endDate) {
        classifiableLinkRepository.create(classification, classifiable, startDate, endDate);
        return this.classifiable;
    }

    public Collection<Classification> choices0$$() {
        SortedSet<Applicability> applicableToClassHierarchy = Sets.newTreeSet();

        // first, pull together all the applicabilities for this domain type and all its supertypes.
        Class<?> domainType = classifiable.getClass();
        appendDirectApplicabilities(domainType, applicableToClassHierarchy);

        // next, obtain the corresponding Classifications of each of these
        Set<Classification> applicableClassificationsToClassHierarchy =
                applicableToClassHierarchy.stream()
                .map(Applicability::getClassification)
                .distinct()
                .collect(Collectors.toSet());

        // then, obtain all the Classifications available (ie all the children)
        SortedSet<Classification> classifications = Sets.newTreeSet();
        for (Classification classification : applicableClassificationsToClassHierarchy) {
            appendChildClassifications(classification, classifications);
        }

        List<Classification> currentClassifications = classifiableLinkRepository
                .findByClassifiableAndDate(classifiable, null)
                .stream()
                .map(ClassifiableLink::getClassification)
                .collect(Collectors.toList());

        // finally, only return those that are 'selectable' and not already a classification
        return classifications.stream()
                .filter(Classification::isSelectable)
                .filter(x -> !currentClassifications.contains(x))
                .collect(Collectors.toList());
    }

    private void appendChildClassifications(
            final Classification classification,
            final SortedSet<Classification> classifications) {
        classifications.add(classification);
        SortedSet<Classification> children = classification.getChildren();
        for (Classification child : children) {
            appendChildClassifications(child, classifications);
        }
    }

    private void appendDirectApplicabilities(
            Class<?> domainType,
            final SortedSet<Applicability> applicabilities) {
        while(domainType != null) {
            appendDirectApplicabilitiesFor(domainType, applicabilities);
            // TODO: support interfaces too?
            domainType = domainType.getSuperclass();
        }
    }

    private void appendDirectApplicabilitiesFor(
            final Class<?> domainType,
            final SortedSet<Applicability> applicabilities) {

        // TODO: this doesn't yet handle sub-tenancies (need to enumerate all sub-tenancies in fixture)
        List<String> atPaths = getAtPaths();
        List<Applicability> applicabilitiesForDomainType =
                applicabilityRepository.findByDomainTypeAndAtPaths(domainType, atPaths);
        applicabilities.addAll(applicabilitiesForDomainType);
    }

    public boolean hide$$() {
        return !classifiableLinkRepository.supports(classifiable);
    }

    /**
     * All atPaths for the {@link #getClassifiable() classifiable} that this mixin object mixes-in to.
     * @return
     */
    List<String> getAtPaths() {
        return applicationTenancyRepositories.stream()
                .map(repo -> repo.atPathsFor(classifiable))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }


    //endregion


}
