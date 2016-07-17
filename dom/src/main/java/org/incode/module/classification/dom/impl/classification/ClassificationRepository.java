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
package org.incode.module.classification.dom.impl.classification;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.bookmark.Bookmark;
import org.apache.isis.applib.services.bookmark.BookmarkService;
import org.apache.isis.applib.services.registry.ServiceRegistry;
import org.apache.isis.applib.services.repository.RepositoryService;
import org.incode.module.classification.dom.impl.category.Category;
import org.incode.module.classification.dom.impl.category.taxonomy.Taxonomy;

import javax.inject.Inject;
import java.util.List;


@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = Classification.class
)
public class ClassificationRepository {

    //region > findByClassified (programmatic)
    @Programmatic
    public List<Classification> findByClassified(
            final Object classifiable) {
        final Bookmark bookmark = bookmarkService.bookmarkFor(classifiable);
        final String classifiedStr = bookmark.toString();
        return repositoryService.allMatches(
                new QueryDefault<>(Classification.class,
                        "findByClassified",
                        "classifiedStr", classifiedStr));
    }
    //endregion

    //region > findByTaxonomy (programmatic)
    @Programmatic
    public List<Classification> findByTaxonomy(
            final Taxonomy taxonomy) {
        return repositoryService.allMatches(
                new QueryDefault<>(Classification.class,
                        "findByTaxonomy",
                        "taxonomy", taxonomy));
    }
    //endregion

    //region > create (programmatic)

    @Programmatic
    public Classification create(
            final Taxonomy taxonomy,
            final Category category,
            final Object classified) {
        final Class<? extends Classification> subtype = subtypeClassFor(taxonomy, classified);

        final Classification classification = repositoryService.instantiate(subtype);

        classification.setCategory(category);
        classification.setTaxonomy(taxonomy);

        final Bookmark bookmark = bookmarkService.bookmarkFor(classified);
        classification.setClassified(classified);
        classification.setClassifiedStr(bookmark.toString());

        repositoryService.persist(classification);

        return classification;
    }

    private Class<? extends Classification> subtypeClassFor(Taxonomy taxonomy, final Object classified) {
        Class<?> domainClass = classified.getClass();
        for (SubtypeProvider subtypeProvider : subtypeProviders) {
            Class<? extends Classification> subtype = subtypeProvider.subtypeFor(domainClass, taxonomy);
            if(subtype != null) {
                return subtype;
            }
        }
        throw new IllegalStateException(String.format(
                "No subtype of Classification was found for '%s' and taxonomy '%s'; implement the ClassificationRepository.SubtypeProvider SPI",
                domainClass.getName(), taxonomy.getName()));
    }

    //endregion

    //region > remove (programmatic)

    @Programmatic
    public void remove(Classification classification) {
        repositoryService.remove(classification);
    }

    //endregion

    //region > SubtypeProvider SPI

    /**
     * SPI to be implemented (as a {@link DomainService}) for any domain object to which {@link Classification}s can be
     * attached.
     */
    public interface SubtypeProvider {
        /**
         * @return the subtype of {@link Classification} to use to hold the (type-safe) classification of the domain object with respect to the provided {@link Taxonomy}.
         */
        @Programmatic
        Class<? extends Classification> subtypeFor(Class<?> domainObject, Taxonomy taxonomy);
    }
    /**
     * Convenience adapter to help implement the {@link SubtypeProvider} SPI; ignores the {@link Taxonomy} passed into
     * {@link #subtypeClassFor(Taxonomy, Object)}, simply returns the class pair passed into constructor.
     */
    public abstract static class SubtypeProviderAbstract implements SubtypeProvider {
        private final Class<?> classifiedDomainType;
        private final Class<? extends Classification> classifiedSubtype;

        protected SubtypeProviderAbstract(final Class<?> classifiedDomainType, final Class<? extends Classification> classifiedSubtype) {
            this.classifiedDomainType = classifiedDomainType;
            this.classifiedSubtype = classifiedSubtype;
        }

        @Override
        public Class<? extends Classification> subtypeFor(final Class<?> domainType, Taxonomy taxonomy) {
            return domainType.isAssignableFrom(classifiedDomainType) ? classifiedSubtype : null;
        }
    }

    //endregion

    //region > injected services

    @javax.inject.Inject
    ServiceRegistry serviceRegistry;

    @javax.inject.Inject
    RepositoryService repositoryService;

    @javax.inject.Inject
    BookmarkService bookmarkService;

    @Inject
    List<SubtypeProvider> subtypeProviders;
    //endregion

}
