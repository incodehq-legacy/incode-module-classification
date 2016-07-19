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

import org.incode.module.classification.dom.impl.applicability.ApplicabilityRepository;
import org.incode.module.classification.dom.impl.category.CategoryRepository;
import org.incode.module.classification.dom.impl.classification.ClassificationRepository;
import org.incode.module.classification.dom.spi.ApplicationTenancyService;
import org.incode.module.classification.fixture.dom.demo.first.DemoObjectMenu;
import org.incode.module.classification.fixture.scripts.scenarios.ClassifiedDemoObjectsFixture;
import org.incode.module.classification.integtests.ClassificationModuleIntegTest;
import org.junit.Before;
import org.junit.Ignore;

import javax.inject.Inject;

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


    @Ignore
    public void happy_case() {

        // eg given "Sizes/Large", can remove "Largest" child

    }

    @Ignore
    public void happy_case_cascading() {

        // eg given "Sizes", can remove "Large" child.  All the children of "Large" should also be removed.

    }

    @Ignore
    public void cannot_remove_if_has_classification() {

        // eg given "Sizes", cannot remove "Medium" child (used by 'Demo foo (in Italy)').

        // nb: currently this is handled by a referential integrity constraint, and this doesn't get rendered particularly well (a bug I think in Isis).  So as a workaround, probably need to add a validateXxx to check explicitly.

    }

    @Ignore
    public void cannot_remove_if_child_has_classification() {

        // eg given "Sizes", cannot remove "Small" child (because "Small/Smaller" used by 'Demo bar (in France)').

        // nb: currently this is handled by a referential integrity constraint, and this doesn't get rendered particularly well (a bug I think in Isis).  So as a workaround, probably need to add a validateXxx to check explicitly.

    }

}