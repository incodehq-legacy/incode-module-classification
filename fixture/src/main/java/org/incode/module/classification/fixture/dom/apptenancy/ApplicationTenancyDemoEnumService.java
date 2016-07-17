package org.incode.module.classification.fixture.dom.apptenancy;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.incode.module.classification.dom.spi.ApplicationTenancyService;
import org.incode.module.classification.fixture.dom.demo.DemoObject;

@DomainService(
    nature = NatureOfService.DOMAIN
)
public class ApplicationTenancyDemoEnumService implements ApplicationTenancyService {

    @Override
    public String atPathFor(final Object domainObjectToClassify) {
        if(domainObjectToClassify instanceof DemoObject) {
            DemoObject domainObject = (DemoObject) domainObjectToClassify;
            return domainObject.getAtPath();
        }
        return null;
    }
}
