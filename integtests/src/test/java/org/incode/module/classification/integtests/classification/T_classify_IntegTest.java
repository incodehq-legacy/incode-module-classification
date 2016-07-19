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
package org.incode.module.classification.integtests.classification;

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

public class T_classify_IntegTest extends ClassificationModuleIntegTest {

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
    public void when_applicability_and_no_classification() {

        // given "Demo bip (in Milan)",

        // can classify as both italian colour and as a size

    }

    @Ignore
    public void cannot_classify_when_applicability_but_classifications_already_defined() {

        // given "Demo foo (in Italy)", which already has classifications for italian colours and size

        // cannot classify

    }

    @Ignore
    public void cannot_classify_when_no_applicability_for_domain_type() {

        // given "Other baz (Global)", has no available applicabilities for "Size" (which only relates to 'Demo'

        // cannot classify

    }

    @Ignore
    public void cannot_classify_when_no_applicability_for_atPath() {

        // given "Other bar (in France)", has no available applicabilities for "Italian Colour" (which only relates to '/ITA' atPath)

        // cannot classify

    }

}