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

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.repository.RepositoryService;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = Classification.class
)
public class ClassificationRepository {

    //region > findByFullyQualifiedName (programmatic)
    @Programmatic
    public Classification findByFullyQualifiedName(final String fullyQualifiedName) {
        return repositoryService.uniqueMatch(
                new QueryDefault<>(Classification.class,
                        "findByFullyQualifiedName",
                        "fullyQualifiedName", fullyQualifiedName));
    }
    //endregion

    //region > findByParentAndLocalName (programmatic)
    @Programmatic
    public Classification findByParentAndLocalName(final Classification parent, final String localName) {
        return repositoryService.uniqueMatch(
                new QueryDefault<>(Classification.class,
                        "findByParentAndLocalName",
                        "parent", parent,
                        "localName", localName));
    }
    //endregion

    //region > findByParent (programmatic)
    @Programmatic
    public List<Classification> findByParent(final Classification parent) {
        return repositoryService.allMatches(
                new QueryDefault<>(Classification.class,
                        "findByParent",
                        "parent", parent));
    }
    //endregion

    @Programmatic
    public Taxonomy createTaxonomy(final String name) {
        final Taxonomy taxonomy = new Taxonomy(name);
        repositoryService.persistAndFlush(taxonomy);
        return taxonomy;
    }


    //region > injected
    @Inject
    RepositoryService repositoryService;
    //endregion

}
