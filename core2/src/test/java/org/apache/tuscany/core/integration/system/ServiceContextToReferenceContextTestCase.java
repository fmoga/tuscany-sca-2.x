package org.apache.tuscany.core.integration.system;

import junit.framework.TestCase;
import org.apache.tuscany.core.context.WorkContextImpl;
import org.apache.tuscany.core.context.scope.ModuleScopeContext;
import org.apache.tuscany.core.mock.component.Target;
import org.apache.tuscany.core.mock.component.TargetImpl;
import org.apache.tuscany.core.mock.context.MockReferenceContext;
import org.apache.tuscany.core.system.context.SystemCompositeContext;
import org.apache.tuscany.core.system.context.SystemCompositeContextImpl;
import org.apache.tuscany.core.system.context.SystemServiceContext;
import org.apache.tuscany.core.system.context.SystemServiceContextImpl;
import org.apache.tuscany.spi.context.ReferenceContext;
import org.apache.tuscany.spi.context.WorkContext;

/**
 * @version $$Rev$$ $$Date$$
 */
public class ServiceContextToReferenceContextTestCase extends TestCase {

    public void testWireResolution() throws NoSuchMethodException {
        WorkContext ctx = new WorkContextImpl();
        ModuleScopeContext scope = new ModuleScopeContext(ctx);
        SystemCompositeContext context = new SystemCompositeContextImpl();
        scope.start();
        ReferenceContext<Target> referenceContext = new MockReferenceContext<Target>("reference", Target.class, new TargetImpl());
        context.registerContext(referenceContext);
        SystemServiceContext<Target> serviceContext = new SystemServiceContextImpl<Target>("service", Target.class, "reference", context);
        context.registerContext(serviceContext);
        context.start();
        SystemServiceContext serviceContext2 = (SystemServiceContext) context.getContext("service");
        assertSame(serviceContext, serviceContext2);
        Target target = (Target) serviceContext2.getService();
        assertNotNull(target);
    }
}
