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

import org.incode.module.classification.fixture.dom.classifiable.ClassifiableDomainObject;
import org.incode.module.classification.fixture.dom.classifiable.ClassifiableDomainObjectMenu;
import org.incode.module.classification.fixture.scripts.scenarios.ClassifiableDemoObjectsFixture;
import org.incode.module.classification.integtests.ClassificationModuleIntegTest;

public class ClassifiableDomainObjectMenuTest extends ClassificationModuleIntegTest {

    @Inject
    ClassifiableDomainObjectMenu classifiableDomainObjectMenu;

    @Before
    public void setUpData() throws Exception {
        fixtureScripts.runFixtureScript(new ClassifiableDemoObjectsFixture(), null);
    }

    @Test
    public void listAll() throws Exception {

        final List<ClassifiableDomainObject> all = wrap(classifiableDomainObjectMenu).listAll();
        Assertions.assertThat(all.size()).isEqualTo(3);
        
        ClassifiableDomainObject classifiableDomainObject = wrap(all.get(0));
        Assertions.assertThat(classifiableDomainObject.getName()).isEqualTo("Foo");
    }
    
    @Test
    public void create() throws Exception {

        wrap(classifiableDomainObjectMenu).create("Faz", "/");
        
        final List<ClassifiableDomainObject> all = wrap(classifiableDomainObjectMenu).listAll();
        Assertions.assertThat(all.size()).isEqualTo(4);
    }

}