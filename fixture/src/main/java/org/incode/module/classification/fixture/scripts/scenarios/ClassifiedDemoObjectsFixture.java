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

import com.google.common.collect.Lists;
import org.apache.isis.applib.fixturescripts.DiscoverableFixtureScript;
import org.incode.module.classification.dom.impl.category.Category;
import org.incode.module.classification.dom.impl.category.CategoryRepository;
import org.incode.module.classification.dom.impl.category.taxonomy.Taxonomy;
import org.incode.module.classification.dom.impl.classification.T_classify;
import org.incode.module.classification.fixture.app.classification.demo.ClassificationForDemoObject;
import org.incode.module.classification.fixture.dom.demo.first.DemoObject;
import org.incode.module.classification.fixture.dom.demo.first.DemoObjectMenu;
import org.incode.module.classification.fixture.dom.demo.other.OtherObject;
import org.incode.module.classification.fixture.dom.demo.other.OtherObjectMenu;
import org.incode.module.classification.fixture.scripts.teardown.ClassificationDemoAppTearDownFixture;

import java.util.List;

public class ClassifiedDemoObjectsFixture extends DiscoverableFixtureScript {


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

    //region > demoObjects (output)
    private List<DemoObject> demoObjects = Lists.newArrayList();
    public List<DemoObject> getDemoObjects() {
        return demoObjects;
    }
    //endregion

    //region > otherObjects (output)
    private List<OtherObject> otherObjects = Lists.newArrayList();
    public List<OtherObject> getOtherObjects() {
        return otherObjects;
    }
    //endregion

    @Override
    protected void execute(final ExecutionContext executionContext) {

        // prereqs
        executionContext.executeChild(this, new ClassificationDemoAppTearDownFixture());

        // italian taxonomy applicable only to Italian DemoObject and OtherObject
        Taxonomy italianColours = categoryRepository.createTaxonomy("Italian Colours");
        italianColours.setReference("ITACOL");
        Category italianRed = italianColours.addChild("Red", "RED", null);
        Category italianGreen = italianColours.addChild("Green", "GREEN", null);
        Category italianWhite = italianColours.addChild("White", "WHITE", null);

        wrap(italianColours).applicable("/ITA", DemoObject.class.getName());
        wrap(italianColours).applicable("/ITA", OtherObject.class.getName());

        // french taxonomy applicable only to French DemoObject (and not to OtherObject even if with FRA app tenancy)
        Taxonomy frenchColours = categoryRepository.createTaxonomy("French Colours");
        Category frenchRed = frenchColours.addChild("Red", "FRRED", null);
        Category frenchWhite = frenchColours.addChild("White", "FRWHITE", null);
        Category frenchBlue = frenchColours.addChild("Blue", "FRBLUE", null);

        wrap(frenchColours).applicable("/FRA", DemoObject.class.getName());

        // global taxonomy applicable only to DemoObject (any app tenancy)
        Taxonomy globalSizes = categoryRepository.createTaxonomy("Sizes");
        globalSizes.setReference("SIZES");
        Category large = globalSizes.addChild("Large", "LGE", 1);
        Category medium = globalSizes.addChild("Medium", "M", 2);
        Category small = globalSizes.addChild("Small", "SML", 3);

        Category largeLargest = large.addChild("Largest", "XXL", 1);
        Category largeLarger = large.addChild("Larger", "XL", 2);
        Category largeLarge = large.addChild("Large", "L", 3);

        Category smallSmall = small.addChild("Small", "S", 1);
        Category smallSmaller = small.addChild("Smaller", "XS", 2);
        Category smallSmallest = small.addChild("Smallest", "XXS", 3);

        wrap(globalSizes).applicable("/", DemoObject.class.getName());


        // create a sample set of DemoObject and OtherObject, for various app tenancies

        final DemoObject demoFooInItaly = createDemo("Demo foo (in Italy)", "/ITA", executionContext);
        final DemoObject demoBarInFrance = createDemo("Demo bar (in France)", "/FRA", executionContext);
        final DemoObject demoBaz = createDemo("Demo baz (Global)", "/", executionContext);
        final DemoObject demoBip = createDemo("Demo bip (in Milan)", "/ITA/I-MIL", executionContext);
        final DemoObject demoBop = createDemo("Demo bop (in Paris)", "/FRA/F-PAR", executionContext);

        final OtherObject otherFooInItaly = createOther("Other foo (in Italy)", "/ITA", executionContext);

        final OtherObject otherBarInFrance = createOther("Other bar (in France)", "/FRA", executionContext);
        final OtherObject otherBaz = createOther("Other baz (Global)", "/", executionContext);
        final OtherObject otherBip = createOther("Other bip (in Milan)", "/ITA/I-MIL", executionContext);
        final OtherObject otherBop = createOther("Other bop (in Paris)", "/FRA/F-PAR", executionContext);


        // classify DemoObject
        wrap(classify(demoFooInItaly)).$$(italianColours, italianRed);
        wrap(classify(demoFooInItaly)).$$(globalSizes, medium);
        wrap(classify(demoBarInFrance)).$$(globalSizes, smallSmaller);


        // leave OtherObject unclassified

    }

    private DemoObject createDemo(
            final String name,
            final String atPath,
            final ExecutionContext executionContext) {
        final DemoObject demoObject = wrap(demoObjectMenu).create(name, atPath);
        demoObjects.add(demoObject);
        return executionContext.addResult(this, demoObject);
    }

    private OtherObject createOther(
            final String name,
            final String atPath,
            final ExecutionContext executionContext) {
        final OtherObject otherObject = wrap(otherObjectMenu).create(name, atPath);
        otherObjects.add(otherObject);
        return executionContext.addResult(this, otherObject);
    }

    //region > injected services
    @javax.inject.Inject
    DemoObjectMenu demoObjectMenu;
    @javax.inject.Inject
    OtherObjectMenu otherObjectMenu;
    @javax.inject.Inject
    CategoryRepository categoryRepository;
    //endregion


}
