/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
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

import javax.inject.Inject;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.InheritanceStrategy;

import com.google.common.base.Function;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.i18n.TranslatableString;

import org.isisaddons.module.poly.dom.PolymorphicAssociationLink;

import org.incode.module.classification.dom.ClassificationModule;
import org.incode.module.classification.dom.impl.classification.Classification;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        identityType=IdentityType.DATASTORE,
        schema = "incodeClassification",
        table = "ClassifiableLink"
)
@javax.jdo.annotations.DatastoreIdentity(strategy = IdGeneratorStrategy.IDENTITY, column = "id")
@javax.jdo.annotations.Inheritance(
        strategy = InheritanceStrategy.NEW_TABLE)
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByClassification", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incode.module.classification.dom.impl.classifiablelink.ClassifiableLink "
                        + "WHERE classification == :classification "
                        + "ORDER BY classifiableObjectType, classifiableIdentifier, startDate, endDate"),
        @javax.jdo.annotations.Query(
                name = "findByClassificationAndBetween", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incode.module.classification.dom.impl.classifiablelink.ClassifiableLink "
                        + "WHERE classification == :classification "
                        + "&&    (startDate IS NULL || startDate <= :date) "
                        + "&&    (endDate IS NULL   || endDate > :date) "
                        + "ORDER BY classifiableObjectType, classifiableIdentifier, startDate, endDate"),
        @javax.jdo.annotations.Query(
                name = "findByClassifiable", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incode.module.classification.dom.impl.classifiablelink.ClassifiableLink "
                        + "WHERE classifiableObjectType == :classifiableObjectType "
                        + "&&    classifiableIdentifier == :classifiableIdentifier "
                        + "ORDER BY classifiableObjectType, classifiableIdentifier, startDate, endDate"),
        @javax.jdo.annotations.Query(
                name = "findByClassifiableAndBetween", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incode.module.classification.dom.impl.classifiablelink.ClassifiableLink "
                        + "WHERE classifiableObjectType == :classifiableObjectType "
                        + "&&    classifiableIdentifier == :classifiableIdentifier "
                        + "&&    (startDate IS NULL || startDate <= :date) "
                        + "&&    (endDate IS NULL   || endDate > :date) "
                        + "ORDER BY classifiableObjectType, classifiableIdentifier, startDate, endDate"),
})
@javax.jdo.annotations.Indices({
        @javax.jdo.annotations.Index(
                name = "ClassificationLink_classifiable_classification_IDX",
                members = { "classifiableObjectType", "classifiableIdentifier", "classification" }
        ),
        @javax.jdo.annotations.Index(
                name = "ClassificationLink_classification_classifiable_IDX",
                members = { "classification", "classifiableObjectType", "classifiableIdentifier" }
        )
})
@javax.jdo.annotations.Uniques({
})
@DomainObject(
        objectType = "incodeClassification.ClassifiableLink"
)
public abstract class ClassifiableLink
        extends PolymorphicAssociationLink<Classification, Object, ClassifiableLink> {

    //region > event classes
    public static abstract class PropertyDomainEvent<T> extends ClassificationModule.PropertyDomainEvent<ClassifiableLink, T> { }
    public static abstract class CollectionDomainEvent<T> extends ClassificationModule.CollectionDomainEvent<ClassifiableLink, T> { }
    public static abstract class ActionDomainEvent extends ClassificationModule.ActionDomainEvent<ClassifiableLink> { }
    //endregion

    //region > instantiateEvent (poly pattern)
    public static class InstantiateEvent
            extends PolymorphicAssociationLink.InstantiateEvent<Classification, Object, ClassifiableLink> {

        public InstantiateEvent(final Object source, final Classification subject, final Object classifiable) {
            super(ClassifiableLink.class, source, subject, classifiable);
        }
    }
    //endregion

    //region > constructor
    public ClassifiableLink() {
        super("{polymorphicReference} has {subject}");
    }
    //endregion

    //region > title, icon etc
    @Override
    public TranslatableString title() {
        if(getStartDate() != null) {
            if(getEndDate() != null) {
                return TranslatableString.tr(
                        "{polymorphicReference} has {subject} ({startDate} to {endDate})",
                        "polymorphicReference", titleService.titleOf(getPolymorphicReference()),
                        "subject", titleService.titleOf(getSubject()),
                        "startDate", getStartDate(),
                        "endDate", getEndDate());
            } else {
                return TranslatableString.tr(
                        "{polymorphicReference} has {subject} ({startDate} to date)",
                        "polymorphicReference", titleService.titleOf(getPolymorphicReference()),
                        "subject", titleService.titleOf(getSubject()),
                        "startDate", getStartDate());
            }
        } else {
            if(getEndDate() != null) {
                return TranslatableString.tr(
                        "{polymorphicReference} has {subject} (until {endDate})",
                        "polymorphicReference", titleService.titleOf(getPolymorphicReference()),
                        "subject", titleService.titleOf(getSubject()),
                        "endDate", getEndDate());
            } else {
                return TranslatableString.tr(
                        "{polymorphicReference} has {subject}",
                        "polymorphicReference", titleService.titleOf(getPolymorphicReference()),
                        "subject", titleService.titleOf(getSubject()));
            }
        }
    }

    public String cssClass() {
        return between(getStartDate(), getEndDate(), clockService.now())? "current": null;
    }

    private static boolean between(LocalDate startDate, LocalDate endDate, final LocalDate currDate) {
        startDate = startDate != null? startDate : currDate.minusDays(1); // ensure before  if null
        endDate = endDate != null? endDate : currDate.plusDays(1); // ensure after if null
        return (startDate.isBefore(currDate) || startDate.isEqual(currDate)) && endDate.isAfter(currDate);
    }
    //endregion

    //region > SubjectPolymorphicReferenceLink API

    /**
     * The subject of the pattern, which (perhaps confusingly in this instance) is actually the
     * {@link #getClassification() event}.
     */
    @Override
    @Programmatic
    public Classification getSubject() {
        return this.getClassification();
    }

    @Override
    @Programmatic
    public void setSubject(final Classification subject) {
        this.setClassification(subject);
    }

    @Override
    @Programmatic
    public String getPolymorphicObjectType() {
        return getClassifiableObjectType();
    }

    @Override
    @Programmatic
    public void setPolymorphicObjectType(final String polymorphicObjectType) {
        setClassifiableObjectType(polymorphicObjectType);
    }

    @Override
    @Programmatic
    public String getPolymorphicIdentifier() {
        return getClassifiableIdentifier();
    }

    @Override
    @Programmatic
    public void setPolymorphicIdentifier(final String polymorphicIdentifier) {
        setClassifiableIdentifier(polymorphicIdentifier);
    }
    //endregion


    public static class ClassificationDomainEvent extends PropertyDomainEvent<Classification> { }
    @Getter @Setter
    @javax.jdo.annotations.Column(allowsNull = "false", name = "aliasRef")
    @Property(
            domainEvent = ClassificationDomainEvent.class,
            editing = Editing.DISABLED
    )
    private Classification classification;

    public static class ClassifiableObjectTypeDomainEvent extends PropertyDomainEvent<String> { }
    @Getter @Setter
    @javax.jdo.annotations.Column(allowsNull = "false", length = 255)
    @Property(
            domainEvent = ClassifiableObjectTypeDomainEvent.class,
            editing = Editing.DISABLED
    )
    private String classifiableObjectType;

    public static class ClassifiableIdentifierDomainEvent extends PropertyDomainEvent<String> {
    }
    @Getter @Setter
    @javax.jdo.annotations.Column(allowsNull = "false", length= ClassificationModule.JdoColumnLength.IDENTIFIER)
    @Property(
            domainEvent = ClassifiableIdentifierDomainEvent.class,
            editing = Editing.DISABLED
    )
    private String classifiableIdentifier;

    public static class StartDateDomainEvent extends PropertyDomainEvent<LocalDate> { }
    @Getter @Setter
    @javax.jdo.annotations.Column(allowsNull = "true")
    @Property(
            domainEvent = StartDateDomainEvent.class,
            editing = Editing.DISABLED
    )
    private LocalDate startDate;

    public static class EndDateDomainEvent extends PropertyDomainEvent<LocalDate> { }
    @Getter @Setter
    @javax.jdo.annotations.Column(allowsNull = "true")
    @Property(
            domainEvent = EndDateDomainEvent.class,
            editing = Editing.DISABLED
    )
    private LocalDate endDate;


    //region > classifiable (derived property)
    /**
     * Simply returns the {@link #getPolymorphicReference()}.
     */
    @Programmatic
    public Object getClassifiable() {
        return getPolymorphicReference();
    }
    //endregion

    //region > Functions
    public static class Functions {
        public static Function<ClassifiableLink, Classification> classification() {
            return classification(Classification.class);
        }
        public static <T extends Classification> Function<ClassifiableLink, T> classification(Class<T> cls) {
            return input -> input != null
                                ? (T) input.getClassification()
                                : null;
        }
        public static Function<ClassifiableLink, Object> classifiable() {
            return classifiable(Object.class);
        }
        public static <T> Function<ClassifiableLink, T> classifiable(final Class<T> cls) {
            return input -> input != null
                                ? (T)input.getClassifiable()
                                : null;
        }
    }
    //endregion

    @Inject
    ClockService clockService;

}
