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
package org.incode.module.classification.dom.impl.classificationlink;

import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.bookmark.Bookmark;
import org.apache.isis.applib.services.bookmark.BookmarkService;

import org.isisaddons.module.poly.dom.PolymorphicAssociationLink;

import org.incode.module.classification.dom.api.classifiable.AliasType;
import org.incode.module.classification.dom.api.classifiable.Classifiable;
import org.incode.module.classification.dom.impl.classification.Alias;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = ClassifiableLink.class
)
public class AliasableLinkRepository {

    //region > init
    PolymorphicAssociationLink.Factory<Alias,Classifiable,ClassifiableLink,ClassifiableLink.InstantiateEvent> linkFactory;

    @PostConstruct
    public void init() {
        linkFactory = container.injectServicesInto(
                new PolymorphicAssociationLink.Factory<>(
                        this,
                        Alias.class,
                        Classifiable.class,
                        ClassifiableLink.class,
                        ClassifiableLink.InstantiateEvent.class
                ));

    }
    //endregion

    //region > findByAlias (programmatic)
    @Programmatic
    public ClassifiableLink findByAlias(final Alias alias) {
        return container.firstMatch(
                new QueryDefault<>(ClassifiableLink.class,
                        "findByAlias",
                        "alias", alias));
    }
    //endregion


    //region > findByAliasable (programmatic)
    @Programmatic
    public List<ClassifiableLink> findByAliasable(final Classifiable classifiable) {
        if(classifiable == null) {
            return null;
        }
        final Bookmark bookmark = bookmarkService.bookmarkFor(classifiable);
        if(bookmark == null) {
            return null;
        }
        return container.allMatches(
                new QueryDefault<>(ClassifiableLink.class,
                        "findByAliasable",
                        "aliasableObjectType", bookmark.getObjectType(),
                        "aliasableIdentifier", bookmark.getIdentifier()));
    }
    //endregion

    //region > findByAliasableAndAtPath
    @Programmatic
    public List<ClassifiableLink> findByAliasableAndAtPath(
            final Classifiable classifiable,
            final String atPath) {
        if(classifiable == null) {
            return null;
        }
        if(atPath == null) {
            return null;
        }
        final Bookmark bookmark = bookmarkService.bookmarkFor(classifiable);
        if(bookmark == null) {
            return null;
        }
        return container.allMatches(
                new QueryDefault<>(ClassifiableLink.class,
                        "findByAliasAndAtPath",
                        "aliasableObjectType", bookmark.getObjectType(),
                        "aliasableIdentifier", bookmark.getIdentifier(),
                        "atPath", atPath));
    }
    //endregion

    //region > findByAliasableAndAliasType
    public List<ClassifiableLink> findByAliasableAndAliasType(
            final Classifiable classifiable,
            final AliasType aliasType) {
        if(classifiable == null) {
            return null;
        }
        if(aliasType == null) {
            return null;
        }
        final Bookmark bookmark = bookmarkService.bookmarkFor(classifiable);
        if(bookmark == null) {
            return null;
        }
        return container.allMatches(
                new QueryDefault<>(ClassifiableLink.class,
                        "findByAliasAndAtPath",
                        "aliasableObjectType", bookmark.getObjectType(),
                        "aliasableIdentifier", bookmark.getIdentifier(),
                        "aliasTypeId", aliasType.getId()));
    }
    //endregion

    //region > findByAliasableAndAtPathAndAliasType
    public ClassifiableLink findByAliasableAndAtPathAndAliasType(
            final Classifiable classifiable,
            final String atPath,
            final AliasType aliasType) {
        if(classifiable == null) {
            return null;
        }
        if(atPath == null) {
            return null;
        }
        if(aliasType == null) {
            return null;
        }
        final Bookmark bookmark = bookmarkService.bookmarkFor(classifiable);
        if(bookmark == null) {
            return null;
        }
        return container.firstMatch(
                new QueryDefault<>(ClassifiableLink.class,
                        "findByAliasAndAtPath",
                        "aliasableObjectType", bookmark.getObjectType(),
                        "aliasableIdentifier", bookmark.getIdentifier(),
                        "atPath", atPath,
                        "aliasTypeId", aliasType.getId()));
    }
    //endregion

    //region > createLink (programmatic)
    @Programmatic
    public ClassifiableLink createLink(final Alias alias, final Classifiable classifiable) {
        final ClassifiableLink link = linkFactory.createLink(alias, classifiable);

        sync(alias, link);

        return link;
    }

    //endregion

    //region > updateLink
    @Programmatic
    public void updateLink(final Alias alias) {
        final ClassifiableLink link = findByAlias(alias);
        sync(alias, link);
    }
    //endregion

    //region > helpers (sync)

    /**
     * copy over details from the {@link Alias#} to the {@link ClassifiableLink} (derived propoerties to support querying).
     */
    void sync(final Alias alias, final ClassifiableLink link) {
        if(link == null) {
            return;
        }
        link.setAliasTypeId(alias.getAliasTypeId());
        link.setAtPath(alias.getAtPath());
    }
    //endregion

    //region > injected services

    @javax.inject.Inject
    DomainObjectContainer container;

    @javax.inject.Inject
    BookmarkService bookmarkService;

    //endregion

}
