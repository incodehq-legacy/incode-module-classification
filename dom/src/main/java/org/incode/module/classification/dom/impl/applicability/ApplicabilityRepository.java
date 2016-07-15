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
package org.incode.module.classification.dom.impl.applicability;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.incode.module.classification.dom.impl.classification.Classification;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = Applicability.class
)
public class ApplicabilityRepository {

    //region > findByParentAndLocalName (programmatic)
    @Programmatic
    public List<Applicability> findByDomainTypeAndAtPaths( final Class<?> domainType, final List<String> atPaths) {
        return repositoryService.allMatches(
                new QueryDefault<>(Applicability.class,
                        "findByDomainTypeAndAtPaths",
                        "domainType", domainType.getName(),
                        "atPaths", atPaths));
    }
    //endregion

    //region > findByClassification (programmatic)
    @Programmatic
    public List<Applicability> findByClassification(final Classification classification) {
        return repositoryService.allMatches(
                new QueryDefault<>(Applicability.class,
                        "findByClassification",
                        "classification", classification));
    }
    //endregion

    //region > injected
    @Inject
    RepositoryService repositoryService;
    //endregion

}