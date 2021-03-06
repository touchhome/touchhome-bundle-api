package org.touchhome.bundle.api.service.scan;

import lombok.RequiredArgsConstructor;
import org.touchhome.bundle.api.EntityContext;

import java.util.List;
import java.util.stream.Collectors;

/**
 * BaseItemsDiscovery successor that creates list of DevicesScanner based on declared beans
 */
@RequiredArgsConstructor
public abstract class BaseBeansItemsDiscovery extends BaseItemsDiscovery {

    private final Class<? extends ItemDiscoverySupport> declaredBeanClass;

    @Override
    protected List<DevicesScanner> getScanners(EntityContext entityContext) {
        return entityContext.getBeansOfType(declaredBeanClass).stream()
                .map(bean -> new DevicesScanner(bean.getName(), bean::scan))
                .collect(Collectors.toList());
    }

    @Override
    protected String getBatchName() {
        return declaredBeanClass.getSimpleName();
    }
}
