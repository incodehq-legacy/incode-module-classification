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
import org.incode.module.classification.integtests.ClassificationModuleIntegTest;
import org.junit.Before;
import org.junit.Ignore;

import javax.inject.Inject;

public class Category_name_IntegTest extends ClassificationModuleIntegTest {

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
        // fixtureScripts.runFixtureScript(new ClassificationDemoAppTearDownFixture(), null);
        // fixtureScripts.runFixtureScript(new ClassifiedDemoObjectsFixture(), null);
    }


    @Ignore
    public void happy_case() {

        // eg given "Sizes/Medium", can rename to "Middle".  The fully qualified name should be updated

    }

    @Ignore
    public void fully_qualified_name_of_children_also_updated() {

        // eg given "Sizes/Large", can rename to "LRG".  The fully qualified name should be updated of "Sizes/LRG", and also that of the three children

    }

    @Ignore
    public void cannot_rename_to_a_name_already_in_use() {

        // eg given "French Colours/Red", cannot rename to "French Colours/White"

    }



}