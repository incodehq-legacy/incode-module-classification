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

import org.incode.module.classification.dom.api.classifiable.Classifiable;
import org.incode.module.classification.dom.impl.classification.Aliasable_addAlias;
import org.incode.module.classification.fixture.dom.classificationdemoobject.ClassificationDemoObject;
import org.incode.module.classification.fixture.dom.classificationdemoobject.AliasDemoObjectMenu;
import org.incode.module.classification.fixture.dom.classificationtype.AliasTypeDemoEnum;
import org.incode.module.classification.fixture.scripts.teardown.AliasDemoObjectsTearDownFixture;

public class AliasDemoObjectsFixture extends DiscoverableFixtureScript {

    //region > injected services
    @javax.inject.Inject
    AliasDemoObjectMenu aliasDemoObjectMenu;
    //endregion

    //region > constructor
    public AliasDemoObjectsFixture() {
        withDiscoverability(Discoverability.DISCOVERABLE);
    }
    //endregion

    //region > mixins
    Aliasable_addAlias mixinAddAlias(final Classifiable classifiable) {
        return container.mixin(Aliasable_addAlias.class, classifiable);
    }
    //endregion

    @Override
    protected void execute(final ExecutionContext executionContext) {

        // prereqs
        executionContext.executeChild(this, new AliasDemoObjectsTearDownFixture());

        final ClassificationDemoObject foo = create("Foo", executionContext);
        wrap(mixinAddAlias(foo)).$$("/uk", AliasTypeDemoEnum.GENERAL_LEDGER, "12345");
        wrap(mixinAddAlias(foo)).$$("/uk", AliasTypeDemoEnum.DOCUMENT_MANAGEMENT, "http://docserver.mycompany/url/12345");
        wrap(mixinAddAlias(foo)).$$("/uk", AliasTypeDemoEnum.PERSONNEL_SYSTEM, "12345");

        final ClassificationDemoObject bar = create("Bar", executionContext);
        wrap(mixinAddAlias(bar)).$$("/uk", AliasTypeDemoEnum.GENERAL_LEDGER, "98765");

        final ClassificationDemoObject baz = create("Baz", executionContext);
        wrap(mixinAddAlias(bar)).$$("/nl", AliasTypeDemoEnum.GENERAL_LEDGER, "12345");
        wrap(mixinAddAlias(foo)).$$("/nl", AliasTypeDemoEnum.DOCUMENT_MANAGEMENT, "http://docserver.mycompany/url/12345");
    }


    // //////////////////////////////////////

    private ClassificationDemoObject create(
            final String name,
            final ExecutionContext executionContext) {

        return executionContext.addResult(this, wrap(aliasDemoObjectMenu).create(name));
    }


}
