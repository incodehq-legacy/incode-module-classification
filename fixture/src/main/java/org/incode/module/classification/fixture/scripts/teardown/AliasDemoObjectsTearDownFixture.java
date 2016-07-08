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
package org.incode.module.classification.fixture.scripts.teardown;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.jdosupport.IsisJdoSupport;

public class AliasDemoObjectsTearDownFixture extends FixtureScript {

    @Override
    protected void execute(final ExecutionContext executionContext) {
        isisJdoSupport.executeUpdate("delete from \"classificationdemo\".\"ClassifiableLinkForDemoObject\"");
        isisJdoSupport.executeUpdate("delete from \"classificationdemo\".\"ClassificationDemoObject\"");

        isisJdoSupport.executeUpdate("delete from \"incodeClassification\".\"ClassifiableLink\"");
        isisJdoSupport.executeUpdate("delete from \"incodeClassification\".\"Alias\"");
    }


    @javax.inject.Inject
    private IsisJdoSupport isisJdoSupport;

}
