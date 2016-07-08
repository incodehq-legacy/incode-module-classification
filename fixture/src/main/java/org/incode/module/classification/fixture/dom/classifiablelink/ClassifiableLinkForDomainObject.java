/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
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
package org.incode.module.classification.fixture.dom.classifiablelink;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.InheritanceStrategy;

import com.google.common.eventbus.Subscribe;

import org.apache.isis.applib.AbstractSubscriber;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.incode.module.classification.dom.impl.classifiablelink.ClassifiableLink;
import org.incode.module.classification.fixture.dom.classifiable.ClassifiableDomainObject;

@javax.jdo.annotations.PersistenceCapable(
        identityType= IdentityType.DATASTORE,
        schema="classificationdemo")
@javax.jdo.annotations.Inheritance(
        strategy = InheritanceStrategy.NEW_TABLE)
@DomainObject(
        objectType = "classificationdemo.ClassifiableLinkForDomainObject"
)
public class ClassifiableLinkForDomainObject extends ClassifiableLink {

    //region > instantiationSubscriber, setPolymorphicReference
    @DomainService(nature = NatureOfService.DOMAIN)
    @DomainServiceLayout(menuOrder = "1")
    public static class InstantiationSubscriber extends AbstractSubscriber {
        @Programmatic
        @Subscribe
        public void on(final InstantiateEvent ev) {
            if(ev.getPolymorphicReference() instanceof ClassifiableDomainObject) {
                ev.setSubtype(ClassifiableLinkForDomainObject.class);
            }
        }
    }

    @Override
    public void setPolymorphicReference(final Object polymorphicReference) {
        super.setPolymorphicReference(polymorphicReference);
        setDomainObject((ClassifiableDomainObject) polymorphicReference);
    }
    //endregion

    //region > domainObject (property)
    private ClassifiableDomainObject domainObject;

    @Column(
            allowsNull = "false",
            name = "domainObjectId"
    )
    public ClassifiableDomainObject getDomainObject() {
        return domainObject;
    }

    public void setDomainObject(final ClassifiableDomainObject domainObject) {
        this.domainObject = domainObject;
    }
    //endregion
}
