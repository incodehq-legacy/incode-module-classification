package org.incode.module.classification.fixture.dom.apptenancy;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.incode.module.classification.dom.spi.aliastype.ApplicationTenancyRepository;
import org.incode.module.classification.fixture.dom.classifiable.ClassifiableDomainObject;

@DomainService(
    nature = NatureOfService.DOMAIN
)
public class ApplicationTenancyDemoEnumRepository implements ApplicationTenancyRepository {

    @Override
    public Collection<String> atPathsFor(final Object classifiable) {
        if(classifiable instanceof ClassifiableDomainObject) {
            ClassifiableDomainObject domainObject = (ClassifiableDomainObject) classifiable;
            return Collections.singletonList(domainObject.getAtPath());
        }
        return Collections.emptyList();
    }
}
