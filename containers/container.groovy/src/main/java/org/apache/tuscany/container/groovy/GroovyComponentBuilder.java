package org.apache.tuscany.container.groovy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.tuscany.model.Component;
import org.apache.tuscany.model.Scope;
import org.apache.tuscany.model.Service;
import org.apache.tuscany.spi.builder.BuilderConfigException;
import org.apache.tuscany.spi.context.ComponentContext;
import org.apache.tuscany.spi.context.CompositeContext;
import org.apache.tuscany.spi.extension.ComponentBuilderExtension;

/**
 * @version $$Rev$$ $$Date$$
 */
public class GroovyComponentBuilder extends ComponentBuilderExtension<GroovyImplementation> {

    protected Class<GroovyImplementation> getImplementationType() {
        return GroovyImplementation.class;
    }

    public ComponentContext build(CompositeContext parent, Component<GroovyImplementation> component) throws BuilderConfigException {
        List<Class<?>> services = new ArrayList<Class<?>>();
        Collection<Service> collection = component.getImplementation().getComponentType().getServices().values();
        for (Service service : collection) {
            services.add(service.getServiceContract().getInterface());
        }
        String script = component.getImplementation().getScript();
        String name = component.getName();
        Scope scope = component.getImplementation().getComponentType().getLifecycleScope();
        return new GroovyAtomicContext(name, script, services, scope, parent);
    }
}
