/*
 *  Copyright 2016 Dan Haywood
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
package org.incode.module.classification.integtests.category;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.apache.isis.applib.services.wrapper.InvalidException;

import org.incode.module.classification.dom.impl.applicability.ApplicabilityRepository;
import org.incode.module.classification.dom.impl.category.Category;
import org.incode.module.classification.dom.impl.category.CategoryRepository;
import org.incode.module.classification.dom.impl.classification.ClassificationRepository;
import org.incode.module.classification.dom.spi.ApplicationTenancyService;
import org.incode.module.classification.fixture.dom.demo.first.DemoObjectMenu;
import org.incode.module.classification.fixture.scripts.scenarios.ClassifiedDemoObjectsFixture;
import org.incode.module.classification.integtests.ClassificationModuleIntegTest;

import static org.assertj.core.api.Assertions.assertThat;

public class Category_reference_IntegTest extends ClassificationModuleIntegTest {

    @Inject
    ClassificationRepository classificationRepository;
    @Inject
    CategoryRepository categoryRepository;
    @Inject
    ApplicabilityRepository applicabilityRepository;

    @Inject
    DemoObjectMenu demoObjectMenu;
    @Inject
    ApplicationTenancyService applicationTenancyService;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUpData() throws Exception {
        fixtureScripts.runFixtureScript(new ClassifiedDemoObjectsFixture(), null);
    }

    @Test
    public void happy_case() {
        // TODO: Should the reference be modified through setter, or should a modifyReference method be implemented?
        // eg given "Sizes/Small/Smallest" ("XXS"), can rename reference to "V.SMALL".

        // given
        Category smallest = categoryRepository.findByReference("XXS");

        // when
        smallest.setReference("V.SMALL");

        // then
        assertThat(smallest.getReference()).isEqualTo("V.SMALL");
    }

    @Ignore
    public void cannot_rename_to_a_reference_already_in_use() {
        //TODO: Same as Category_name_IntegTest.cannot_rename_to_a_name_already_in_use():
        //TODO: validateReference does not seem to actually validate a change of reference

        // eg given "Sizes/Small/Smallest" ("XXS"), cannot rename reference to "XS".
        // given
        Category smallest = categoryRepository.findByReference("XXS");

        // then
        expectedException.expect(InvalidException.class);
        expectedException.expectMessage("A category with reference 'XS' already exists (under this parent)");

        // when
        smallest.setReference("XS");
    }

    @Test
    public void can_clear() {
        // eg given "Sizes/Small/Smallest" ("XXS"), can set reference to null
        // given
        Category smallest = categoryRepository.findByReference("XXS");

        // when
        smallest.setReference(null);

        // then
        assertThat(smallest.getReference()).isNull();
    }

}