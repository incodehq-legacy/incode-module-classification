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
package org.incode.module.classification.dom.impl.category;

import java.util.List;
import java.util.SortedSet;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.incode.module.classification.dom.impl.category.taxonomy.Taxonomy;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = Category.class
)
public class CategoryRepository {

    //region > findByTaxonomy (programmatic)
    @Programmatic
    public List<Category> findByTaxonomy(final Taxonomy taxonomy) {
        return repositoryService.allMatches(
                new QueryDefault<>(Category.class,
                        "findByTaxonomy",
                        "taxonomy", taxonomy));
    }
    //endregion

    //region > findByParent (programmatic)
    @Programmatic
    public List<Category> findByParent(final Category parent) {
        return repositoryService.allMatches(
                new QueryDefault<>(Category.class,
                        "findByParent",
                        "parent", parent));
    }
    //endregion

    //region > findByName (programmatic)
    @Programmatic
    public Category findByName(final String name) {
        return repositoryService.uniqueMatch(
                new QueryDefault<>(Category.class,
                        "findByName",
                        "name", name));
    }
    //endregion

    //region > findByParentCascade (programmatic)
    @Programmatic
    public List<Category> findByParentCascade(Category category) {
        final List<Category> allCategories = Lists.newArrayList();
        append(category, allCategories);
        return allCategories;
    }

    void append(final Category parent, final List<Category> all) {
        final List<Category> children = findByParent(parent);
        all.addAll(children);
        for (Category category : children) {
            append(category, all);
        }
    }

    //endregion

    //region > findByParentAndName (programmatic)
    @Programmatic
    public Category findByParentAndName(final Category parent, final String name) {
        return repositoryService.uniqueMatch(
                new QueryDefault<>(Category.class,
                        "findByParentAndName",
                        "parent", parent,
                        "name", name));
    }
    //endregion

    //region > findByParentAndReference (programmatic)
    @Programmatic
    public Category findByParentAndReference(final Category parent, final String reference) {
        return repositoryService.uniqueMatch(
                new QueryDefault<>(Category.class,
                        "findByParentAndReference",
                        "parent", parent,
                        "reference", reference));
    }
    //endregion

    //region > createTaxonomy (programmatic)
    @Programmatic
    public Taxonomy createTaxonomy(final String name) {
        final Taxonomy taxonomy = new Taxonomy(name);
        repositoryService.persistAndFlush(taxonomy);
        taxonomy.setTaxonomy(taxonomy);
        return taxonomy;
    }
    //endregion

    //region > createChild (programmatic)
    @Programmatic
    public Category createChild(Category parent, String name, String reference, Integer ordinal) {
        final Category category = new Category(parent, name, reference, ordinal);
        repositoryService.persistAndFlush(category);
        return category;

    }
    //endregion

    //region > removeCascade (programmatic)
    @Programmatic
    public void removeCascade(final Category category) {
        SortedSet<Category> children = category.getChildren();
        for (final Category child : children) {
            removeCascade(child);
        }
        repositoryService.remove(category);
    }
    //endregion

    //region > injected
    @Inject
    RepositoryService repositoryService;

    //endregion

}
