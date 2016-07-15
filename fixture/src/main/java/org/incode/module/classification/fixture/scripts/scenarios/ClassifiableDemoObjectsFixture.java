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
package org.incode.module.classification.fixture.scripts.scenarios;

import org.apache.isis.applib.fixturescripts.DiscoverableFixtureScript;

import org.incode.module.classification.dom.impl.classifiablelink.Object_classify;
import org.incode.module.classification.dom.impl.classification.Classification;
import org.incode.module.classification.dom.impl.classification.ClassificationRepository;
import org.incode.module.classification.fixture.dom.classifiable.ClassifiableDemoObject;
import org.incode.module.classification.fixture.dom.classifiable.ClassifiableDomainObjectMenu;
import org.incode.module.classification.fixture.scripts.teardown.ClassificationDemoAppTearDownFixture;

public class ClassifiableDemoObjectsFixture extends DiscoverableFixtureScript {

    //region > injected services
    @javax.inject.Inject
    ClassifiableDomainObjectMenu classifiableDomainObjectMenu;
    @javax.inject.Inject
    ClassificationRepository classificationRepository;
    //endregion

    //region > constructor
    public ClassifiableDemoObjectsFixture() {
        withDiscoverability(Discoverability.DISCOVERABLE);
    }
    //endregion

    //region > mixins
    Object_classify classify(final Object classifiable) {
        return mixin(Object_classify.class, classifiable);
    }
    //endregion

    @Override
    protected void execute(final ExecutionContext executionContext) {

        // prereqs
        executionContext.executeChild(this, new ClassificationDemoAppTearDownFixture());

        Classification colour = classificationRepository.createTopLevel("Colour");
        Classification colourOfRed = colour.newChild("Red");
        Classification colourOfGreen = colour.newChild("Green");
        Classification colourOfBlue = colour.newChild("Blue");

        Classification size = classificationRepository.createTopLevel("Size");
        Classification sizeOfBig = size.newChild("Big");
        Classification sizeOfMedium = size.newChild("Medium");
        Classification sizeOfLittle = size.newChild("Little");

        colour.applicableTo("/ITA", ClassifiableDemoObject.class);

        // TODO: need to make applicability transitive for all sub-tenancies
        size.applicableTo("/", ClassifiableDemoObject.class);
        size.applicableTo("/ITA", ClassifiableDemoObject.class);

        final ClassifiableDemoObject foo = create("Foo", "/ITA", executionContext);
        wrap(classify(foo)).$$(colourOfRed, null, null);
        wrap(classify(foo)).$$(sizeOfMedium, null, null);

        final ClassifiableDemoObject bar = create("Bar", "/", executionContext);
        wrap(classify(bar)).$$(sizeOfLittle, null, null);

        final ClassifiableDemoObject baz = create("Baz", "/", executionContext);
    }


    // //////////////////////////////////////

    private ClassifiableDemoObject create(
            final String name,
            final String atPath,
            final ExecutionContext executionContext) {
        return executionContext.addResult(this, wrap(classifiableDomainObjectMenu).create(name, atPath));
    }

}
