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

import java.util.List;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.bookmark.Bookmark;
import org.apache.isis.applib.services.bookmark.BookmarkService;
import org.apache.isis.applib.services.registry.ServiceRegistry;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.isisaddons.module.poly.dom.PolymorphicAssociationLink;

import org.incode.module.classification.dom.impl.classification.Classification;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = ClassifiableLink.class
)
public class ClassifiableLinkRepository {

    //region > init
    PolymorphicAssociationLink.Factory<Classification,Object,ClassifiableLink,ClassifiableLink.InstantiateEvent> linkFactory;

    @PostConstruct
    public void init() {
        linkFactory = serviceRegistry.injectServicesInto(
                new PolymorphicAssociationLink.Factory<>(
                        this,
                        Classification.class,
                        Object.class,
                        ClassifiableLink.class,
                        ClassifiableLink.InstantiateEvent.class
                ));

    }
    //endregion


    //region > findByClassifiableAndDate (programmatic)
    @Programmatic
    public List<ClassifiableLink> findByClassifiableAndDate(
            final Object classifiable,
            @Nullable
            final LocalDate date) {
        Bookmark bookmark = bookmarkService.bookmarkFor(classifiable);
        final String classifiableObjectType = bookmark.getObjectType();
        final String classifiableIdentifier = bookmark.getIdentifier();
        if (date != null) {
            return repositoryService.allMatches(
                    new QueryDefault<>(ClassifiableLink.class,
                            "findByClassifiableAndBetween",
                            "classifiableObjectType", classifiableObjectType,
                            "classifiableIdentifier", classifiableIdentifier,
                            "date", date));
        } else {
            return repositoryService.allMatches(
                    new QueryDefault<>(ClassifiableLink.class,
                            "findByClassifiable",
                            "classifiableObjectType", classifiableObjectType,
                            "classifiableIdentifier", classifiableIdentifier));
        }
    }
    //endregion

    //region > create, delete

    @Programmatic
    public boolean supports(final Object classifiable) {
        return linkFactory.supportsLink(classifiable);
    }

    @Programmatic
    public void create(
            final Classification classification,
            final Object classifiable,
            final LocalDate startDate,
            final LocalDate endDate) {
        ClassifiableLink link = linkFactory.createLink(classification, classifiable);
        link.setStartDate(startDate);
        link.setEndDate(endDate);
    }

    @Programmatic
    public void delete(final ClassifiableLink classifiableLink) {
        repositoryService.remove(classifiableLink);
    }
    //endregion

    //region > injected services

    @javax.inject.Inject
    ServiceRegistry serviceRegistry;

    @javax.inject.Inject
    RepositoryService repositoryService;

    @javax.inject.Inject
    BookmarkService bookmarkService;


    //endregion

}
