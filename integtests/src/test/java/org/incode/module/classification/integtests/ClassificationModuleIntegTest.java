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
package org.incode.module.classification.integtests;

import java.util.List;

import javax.inject.Inject;

import com.google.common.base.Throwables;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

import org.apache.isis.applib.fixturescripts.FixtureScripts;
import org.apache.isis.core.integtestsupport.IntegrationTestAbstract;
import org.apache.isis.core.integtestsupport.IsisSystemForTest;
import org.apache.isis.core.integtestsupport.scenarios.ScenarioExecutionForIntegration;
import org.apache.isis.objectstore.jdo.datanucleus.IsisConfigurationForJdoIntegTests;

import org.isisaddons.module.fakedata.FakeDataModule;
import org.isisaddons.module.fakedata.dom.FakeDataService;

import org.incode.module.classification.app.ClassificationModuleAppManifest;
import org.incode.module.classification.dom.impl.classifiablelink.Object_classify;
import org.incode.module.classification.dom.impl.classifiablelink.Object_classificationLinks;
import org.incode.module.classification.dom.impl.classifiablelink.Object_unclassify;

public abstract class ClassificationModuleIntegTest extends IntegrationTestAbstract {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Inject
    protected FixtureScripts fixtureScripts;

    @Inject
    protected FakeDataService fakeData;


    protected Object_classify mixinClassify(final Object classifiable) {
        return mixin(Object_classify.class, classifiable);
    }
    protected Object_unclassify mixinUnclassify(final Object classifiable) {
        return mixin(Object_unclassify.class, classifiable);
    }

    protected Object_classificationLinks classificationLinksOf(final Object classifiable) {
        return mixin(Object_classificationLinks.class, classifiable);
    }

    protected static <T> List<T> asList(final Iterable<T> iterable) {
        return Lists.newArrayList(iterable);
    }

    protected static TypeSafeMatcher<Throwable> of(final Class<?> type) {
        return new TypeSafeMatcher<Throwable>() {
            @Override
            protected boolean matchesSafely(final Throwable throwable) {
                final List<Throwable> causalChain = Throwables.getCausalChain(throwable);
                return !FluentIterable.from(causalChain).filter(type).isEmpty();
            }

            @Override public void describeTo(final Description description) {
                description.appendText("Caused by " + type.getName());
            }
        };
    }


    @BeforeClass
    public static void initClass() {
        org.apache.log4j.PropertyConfigurator.configure("logging.properties");

        IsisSystemForTest isft = IsisSystemForTest.getElseNull();
        if(isft == null) {
            isft = new IsisSystemForTest.Builder()
                    .withLoggingAt(org.apache.log4j.Level.INFO)
                    .with(new ClassificationModuleAppManifest()
                            .withModules(ClassificationModuleIntegTest.class, FakeDataModule.class))
                    .with(new IsisConfigurationForJdoIntegTests())
                    .build()
                    .setUpSystem();
            IsisSystemForTest.set(isft);
        }

        // instantiating will install onto ThreadLocal
        new ScenarioExecutionForIntegration();
    }
}
