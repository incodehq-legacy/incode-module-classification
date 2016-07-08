package org.incode.module.classification.fixture.dom.apptenancy;

import java.util.Arrays;
import java.util.Collection;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.incode.module.classification.dom.api.classifiable.Classifiable;
import org.incode.module.classification.dom.spi.aliastype.ApplicationTenancyRepository;

@DomainService(
    nature = NatureOfService.DOMAIN
)
public class ApplicationTenancyDemoEnumRepository implements ApplicationTenancyRepository {

    @Override
    public Collection<String> atPathsFor(final Classifiable classifiable) {
        return Lists.newArrayList(
                FluentIterable
                .from(Arrays.asList(ApplicationTenancyDemoEnum.values()))
                .transform(ApplicationTenancyDemoEnum::getPath)
                .toList()
        );
    }
}
