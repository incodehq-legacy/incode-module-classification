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
package org.incode.module.classification.integtests.category.taxonomy;

import org.incode.module.classification.dom.impl.applicability.ApplicabilityRepository;
import org.incode.module.classification.dom.impl.category.CategoryRepository;
import org.incode.module.classification.dom.impl.classification.ClassificationRepository;
import org.incode.module.classification.dom.spi.ApplicationTenancyService;
import org.incode.module.classification.fixture.dom.demo.first.DemoObjectMenu;
import org.incode.module.classification.integtests.ClassificationModuleIntegTest;
import org.junit.Before;
import org.junit.Ignore;

import javax.inject.Inject;

public class Taxonomy_applicable_IntegTest extends ClassificationModuleIntegTest {

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

        // eg set up for "/ITA", search for app tenancy "/"

    }

    @Ignore
    public void can_add_applicability_for_different_domain_types_with_same_atPath() {

        // eg given an applicability for "/ITA" and 'DemoObject', can also add an applicability for "/ITA" and 'OtherObject'

    }

    @Ignore
    public void can_add_applicability_for_same_domain_types_with_different_atPath() {

        // eg given an applicability for "/ITA" and 'DemoObject', can also add an applicability for "/ITA/MIL" and 'DemoObject'

    }

    @Ignore
    public void cannot_add_applicability_if_already_has_applicability_for_given_domainType_and_atPath() {

        // eg set up for "/ITA" and 'DemoObject', cannot add again

    }


}