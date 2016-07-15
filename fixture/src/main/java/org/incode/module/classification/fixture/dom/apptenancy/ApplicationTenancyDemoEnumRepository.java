package org.incode.module.classification.fixture.dom.apptenancy;

import java.util.Collection;
import java.util.Collections;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.incode.module.classification.dom.spi.ApplicationTenancyRepository;
import org.incode.module.classification.fixture.dom.classifiable.ClassifiableDemoObject;

@DomainService(
    nature = NatureOfService.DOMAIN
)
public class ApplicationTenancyDemoEnumRepository implements ApplicationTenancyRepository {

    @Override
    public Collection<String> atPathsFor(final Object classifiable) {
        if(classifiable instanceof ClassifiableDemoObject) {
            ClassifiableDemoObject domainObject = (ClassifiableDemoObject) classifiable;
            return Collections.singletonList(domainObject.getAtPath());
        }
        return Collections.emptyList();
    }
}
