package org.apache.tuscany.container.js.builder;

import java.util.Collection;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.tuscany.common.resource.impl.ResourceLoaderImpl;
import org.apache.tuscany.container.js.assembly.mock.HelloWorldService;
import org.apache.tuscany.container.js.config.JavaScriptComponentRuntimeConfiguration;
import org.apache.tuscany.container.js.mock.MockAssemblyFactory;
import org.apache.tuscany.core.builder.RuntimeConfiguration;
import org.apache.tuscany.core.context.EventContext;
import org.apache.tuscany.core.context.InstanceContext;
import org.apache.tuscany.core.context.QualifiedName;
import org.apache.tuscany.core.context.impl.EventContextImpl;
import org.apache.tuscany.core.context.scope.ModuleScopeContext;
import org.apache.tuscany.core.invocation.jdk.JDKProxyFactoryFactory;
import org.apache.tuscany.core.invocation.spi.ProxyFactory;
import org.apache.tuscany.model.assembly.Scope;
import org.apache.tuscany.model.assembly.SimpleComponent;
import org.apache.tuscany.model.assembly.impl.AssemblyFactoryImpl;
import org.apache.tuscany.model.assembly.impl.AssemblyModelContextImpl;
import org.apache.tuscany.model.scdl.loader.impl.SCDLAssemblyModelLoaderImpl;

public class JSComponentContextBuilderTestCase extends TestCase {

    public void testBasicInvocation() throws Exception {
        JavaScriptComponentContextBuilder jsBuilder = new JavaScriptComponentContextBuilder();
        jsBuilder.setProxyFactoryFactory(new JDKProxyFactoryFactory());
        JavaScriptTargetWireBuilder jsWireBuilder = new JavaScriptTargetWireBuilder();
        SimpleComponent component = MockAssemblyFactory.createComponent("foo",
                "org/apache/tuscany/container/js/assembly/mock/HelloWorldImpl.js", HelloWorldService.class, Scope.MODULE);
        component.initialize(new AssemblyModelContextImpl(new AssemblyFactoryImpl(), new SCDLAssemblyModelLoaderImpl(null), new ResourceLoaderImpl(Thread.currentThread().getContextClassLoader())));
        jsBuilder.build(component);
        ModuleScopeContext context = new ModuleScopeContext(new EventContextImpl());
        RuntimeConfiguration<InstanceContext> config = (RuntimeConfiguration) component.getComponentImplementation()
                .getRuntimeConfiguration();
        context.registerConfiguration(config);
        context.start();
        context.onEvent(EventContext.MODULE_START, null);
        for (ProxyFactory proxyFactory : (Collection<ProxyFactory>) config.getTargetProxyFactories().values()) {
            jsWireBuilder.completeTargetChain(proxyFactory, JavaScriptComponentRuntimeConfiguration.class, context);
            proxyFactory.initialize();
        }
        InstanceContext ctx = config.createInstanceContext();
        HelloWorldService hello = (HelloWorldService) ctx.getInstance(new QualifiedName("foo/HelloWorldService"));
        Assert.assertNotNull(hello);
        Assert.assertEquals("Hello foo", hello.hello("foo"));
    }
}
