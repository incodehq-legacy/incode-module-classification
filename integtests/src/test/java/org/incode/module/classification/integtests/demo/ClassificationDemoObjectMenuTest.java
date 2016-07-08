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
package org.incode.module.classification.integtests.demo;

import java.util.List;

import javax.inject.Inject;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import org.incode.module.classification.fixture.dom.classificationdemoobject.ClassificationDemoObject;
import org.incode.module.classification.fixture.dom.classificationdemoobject.AliasDemoObjectMenu;
import org.incode.module.classification.fixture.scripts.scenarios.AliasDemoObjectsFixture;
import org.incode.module.classification.integtests.ClassificationModuleIntegTest;

public class ClassificationDemoObjectMenuTest extends ClassificationModuleIntegTest {

    @Inject
    AliasDemoObjectMenu aliasDemoObjectMenu;

    @Before
    public void setUpData() throws Exception {
        fixtureScripts.runFixtureScript(new AliasDemoObjectsFixture(), null);
    }

    @Test
    public void listAll() throws Exception {

        final List<ClassificationDemoObject> all = wrap(aliasDemoObjectMenu).listAll();
        Assertions.assertThat(all.size()).isEqualTo(3);
        
        ClassificationDemoObject classificationDemoObject = wrap(all.get(0));
        Assertions.assertThat(classificationDemoObject.getName()).isEqualTo("Foo");
    }
    
    @Test
    public void create() throws Exception {

        wrap(aliasDemoObjectMenu).create("Faz");
        
        final List<ClassificationDemoObject> all = wrap(aliasDemoObjectMenu).listAll();
        Assertions.assertThat(all.size()).isEqualTo(4);
    }

}