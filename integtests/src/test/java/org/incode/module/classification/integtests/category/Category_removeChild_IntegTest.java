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
import org.junit.Test;

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

public class Category_removeChild_IntegTest extends ClassificationModuleIntegTest {

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

    @Before
    public void setUpData() throws Exception {
        fixtureScripts.runFixtureScript(new ClassifiedDemoObjectsFixture(), null);
    }

    @Ignore("EST-771")
    public void happy_case() {
        // TODO: removeChild does not seem to actually remove the child from the parent

        // eg given "Sizes/Large", can remove "Largest" child
        // given
        Category large = categoryRepository.findByReference("LGE");
        Category largest = categoryRepository.findByReference("XXL");
        assertThat(large.getChildren()).contains(largest);

        // when
        wrap(large).removeChild(largest);

        // then
        assertThat(large.getChildren()).doesNotContain(largest);
    }

    @Ignore("EST-771")
    public void happy_case_cascading() {
        // TODO: same issue

        // eg given "Sizes", can remove "Large" child.  All the children of "Large" should also be removed.
        // given
        Category sizes = categoryRepository.findByReference("SIZES");
        Category large = categoryRepository.findByReference("LGE");
        assertThat(sizes.getChildren()).contains(large);
        assertThat(large.getChildren()).isNotEmpty();

        // when
        wrap(sizes).removeChild(large);

        // then
        assertThat(sizes.getChildren()).doesNotContain(large);
        assertThat(large.getChildren()).isEmpty();
    }

    @Test
    public void cannot_remove_if_has_classification() {
        // given
        Category sizes = categoryRepository.findByReference("SIZES");
        Category medium = categoryRepository.findByReference("M");

        // then
        expectedException.expect(InvalidException.class);
        expectedException.expectMessage("Child 'Sizes/Medium' is classified by 'DemoObject{name=Demo foo (in Italy)}' and cannot be removed");

        // when
        wrap(sizes).removeChild(medium);
    }

    @Test
    public void cannot_remove_if_child_has_classification() {
        // given
        Category sizes = categoryRepository.findByReference("SIZES");
        Category small = categoryRepository.findByReference("SML");

        // then
        expectedException.expect(InvalidException.class);
        expectedException.expectMessage("Child 'Sizes/Small/Smaller' is classified by 'DemoObject{name=Demo bar (in France)}' and cannot be removed");

        // when
        wrap(sizes).removeChild(small);
    }

}