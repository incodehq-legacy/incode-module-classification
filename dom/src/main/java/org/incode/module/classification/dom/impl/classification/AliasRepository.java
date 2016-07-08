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

import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.incode.module.classification.dom.api.classifiable.AliasType;
import org.incode.module.classification.dom.api.classifiable.Classifiable;
import org.incode.module.classification.dom.impl.classificationlink.ClassifiableLink;
import org.incode.module.classification.dom.impl.classificationlink.AliasableLinkRepository;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = Alias.class
)
public class AliasRepository {

    //region > findByAliasable (programmatic)
    @Programmatic
    public List<Alias> findByAliasable(final Classifiable classifiable) {
        final List<ClassifiableLink> links = aliasableLinkRepository.findByAliasable(classifiable);
        return toAliases(links);
    }

    //endregion

    //region > findByAliasableAndApplicationTenancy (programmatic)
    @Programmatic
    public List<Alias> findByAliasableAndAtPath(
            final Classifiable classifiable,
            final String atPath) {
        final List<ClassifiableLink> links = aliasableLinkRepository.findByAliasableAndAtPath(classifiable, atPath);
        return toAliases(links);
    }
    //endregion

    //region > findByAliasableAndAliasType (programmatic)
    @Programmatic
    public List<Alias> findByAliasableAndAliasType(
            final Classifiable classifiable,
            final AliasType aliasType) {
        final List<ClassifiableLink> links = aliasableLinkRepository.findByAliasableAndAliasType(classifiable, aliasType);
        return toAliases(links);
    }
    //endregion

    //region > findByAliasableAndAtPathAndAliasType (programmatic)
    @Programmatic
    public Alias findByAliasableAndAtPathAndAliasType(
            final Classifiable classifiable,
            final String atPath,
            final AliasType aliasType) {
        final ClassifiableLink link = aliasableLinkRepository
                .findByAliasableAndAtPathAndAliasType(classifiable, atPath, aliasType);
        return ClassifiableLink.Functions.alias().apply(link);
    }
    //endregion

    //region > add (programmatic)
    @Programmatic
    public Alias add(
            final Classifiable classifiable,
            final String atPath,
            final AliasType aliasType,
            final String aliasReference) {
        final Alias alias = container.newTransientInstance(Alias.class);

        alias.setAtPath(atPath);
        alias.setAliasTypeId(aliasType.getId());
        alias.setReference(aliasReference);

        // must be set after atPath and aliasTypeId, because these are sync'ed from the alias onto the ClassifiableLink.
        alias.setAliasable(classifiable);

        container.persistIfNotAlready(alias);

        return alias;
    }
    //endregion

    //region > remove (programmatic)
    @Programmatic
    public void remove(Alias alias) {
        final ClassifiableLink link = aliasableLinkRepository.findByAlias(alias);
        container.removeIfNotAlready(link);
        container.flush();
        container.removeIfNotAlready(alias);
        container.flush();
    }
    //endregion

    static List<Alias> toAliases(final List<ClassifiableLink> links) {
        return Lists.newArrayList(
                Iterables.transform(links, ClassifiableLink.Functions.alias()));
    }

    //region > injected
    @Inject
    AliasableLinkRepository aliasableLinkRepository;
    @Inject
    DomainObjectContainer container;
    //endregion

}
