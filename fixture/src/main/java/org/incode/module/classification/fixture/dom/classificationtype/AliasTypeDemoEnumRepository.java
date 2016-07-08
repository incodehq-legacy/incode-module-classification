package org.incode.module.classification.fixture.dom.classificationtype;

import java.util.Arrays;
import java.util.Collection;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.incode.module.classification.dom.api.classifiable.AliasType;
import org.incode.module.classification.dom.api.classifiable.Classifiable;
import org.incode.module.classification.dom.spi.aliastype.AliasTypeRepository;

@DomainService(
    nature = NatureOfService.DOMAIN
)
public class AliasTypeDemoEnumRepository implements AliasTypeRepository {

    @Override
    public Collection<AliasType> aliasTypesFor(
            final Classifiable classifiable, final String atPath) {
        final AliasTypeDemoEnum[] values = AliasTypeDemoEnum.values();
        return Lists.newArrayList(
                FluentIterable.from(Arrays.asList(values))
                .transform(x -> new AliasTypeViewModel(x))
        );
    }
}
