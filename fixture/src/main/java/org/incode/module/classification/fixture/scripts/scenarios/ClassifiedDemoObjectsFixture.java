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
import org.incode.module.classification.dom.impl.category.Category;
import org.incode.module.classification.dom.impl.category.CategoryRepository;
import org.incode.module.classification.dom.impl.category.taxonomy.Taxonomy;
import org.incode.module.classification.dom.impl.classification.T_classify;
import org.incode.module.classification.fixture.dom.classification.ClassificationForDemoObject;
import org.incode.module.classification.fixture.dom.demo.DemoObject;
import org.incode.module.classification.fixture.dom.demo.DemoObjectMenu;
import org.incode.module.classification.fixture.scripts.teardown.ClassificationDemoAppTearDownFixture;

public class ClassifiedDemoObjectsFixture extends DiscoverableFixtureScript {

    //region > injected services
    @javax.inject.Inject
    DemoObjectMenu demoObjectMenu;
    @javax.inject.Inject
    CategoryRepository categoryRepository;
    //endregion

    //region > constructor
    public ClassifiedDemoObjectsFixture() {
        withDiscoverability(Discoverability.DISCOVERABLE);
    }
    //endregion

    //region > mixins
    T_classify classify(final Object classifiable) {
        return mixin(ClassificationForDemoObject._classify.class, classifiable);
    }
    //endregion

    @Override
    protected void execute(final ExecutionContext executionContext) {

        // prereqs
        executionContext.executeChild(this, new ClassificationDemoAppTearDownFixture());

        Taxonomy colourTaxonomy = categoryRepository.createTaxonomy("Colour");
        Category colourOfRed = colourTaxonomy.addChild("Red");
        Category colourOfGreen = colourTaxonomy.addChild("Green");
        Category colourOfBlue = colourTaxonomy.addChild("Blue");

        Taxonomy sizeTaxonomy = categoryRepository.createTaxonomy("Size");
        Category large = sizeTaxonomy.addChild("Large");
        Category largeLarge = large.addChild("Large");
        Category largeLarger = large.addChild("Larger");
        Category largeLargest = large.addChild("Largest");
        Category medium = sizeTaxonomy.addChild("Medium");
        Category small = sizeTaxonomy.addChild("Small");
        Category smallSmall = small.addChild("Small");
        Category smallSmaller = small.addChild("Smaller");
        Category smallSmallest = small.addChild("Smallest");

        colourTaxonomy.applicableTo("/ITA", DemoObject.class);

        // TODO: need to make applicability transitive for all sub-tenancies
        sizeTaxonomy.applicableTo("/", DemoObject.class);
        sizeTaxonomy.applicableTo("/ITA", DemoObject.class);

        final DemoObject foo = create("Foo", "/ITA", executionContext);
        wrap(classify(foo)).$$(colourTaxonomy, colourOfRed);
        wrap(classify(foo)).$$(sizeTaxonomy, medium);

        final DemoObject bar = create("Bar", "/", executionContext);
        wrap(classify(bar)).$$(sizeTaxonomy, smallSmaller);

        final DemoObject baz = create("Baz", "/", executionContext);
    }


    // //////////////////////////////////////

    private DemoObject create(
            final String name,
            final String atPath,
            final ExecutionContext executionContext) {
        return executionContext.addResult(this, wrap(demoObjectMenu).create(name, atPath));
    }

}
