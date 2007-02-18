/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
package org.apache.tuscany.runtime.standalone.host;

import java.net.URI;
import java.net.URL;
import java.util.Map;
import java.util.Collection;
import java.lang.reflect.InvocationTargetException;

import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.RegistrationException;
import org.apache.tuscany.spi.component.TargetResolutionException;
import org.apache.tuscany.spi.component.TargetInvokerCreationException;
import org.apache.tuscany.spi.deployer.Deployer;
import org.apache.tuscany.spi.implementation.java.JavaMappedService;
import org.apache.tuscany.spi.implementation.java.PojoComponentType;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.CompositeComponentType;
import org.apache.tuscany.spi.model.CompositeImplementation;
import org.apache.tuscany.spi.model.Implementation;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.wire.TargetInvoker;

import org.apache.tuscany.core.runtime.AbstractRuntime;
import org.apache.tuscany.host.runtime.InitializationException;
import org.apache.tuscany.runtime.standalone.StandaloneRuntime;
import org.apache.tuscany.runtime.standalone.StandaloneRuntimeInfo;
import org.apache.tuscany.runtime.standalone.host.implementation.launched.Launched;
import org.osoa.sca.ComponentContext;

/**
 * @version $Rev$ $Date$
 */
public class StandaloneRuntimeImpl extends AbstractRuntime<StandaloneRuntimeInfo> implements StandaloneRuntime {

    public StandaloneRuntimeImpl() {
        super(StandaloneRuntimeInfo.class);
    }

    /**
     * Deploys the specified application SCDL and runs the lauched component within the deployed composite.
     * 
     * @param applicationScdl Application SCDL that implements the composite.
     * @param applicationClassLoader Classloader used to deploy the composite.
     * @param args Arguments to be passed to the lauched component.
     * @deprecated This is a hack for deployment and should be removed.
     */
    public Object deployAndRun(URL applicationScdl, ClassLoader applicationClassLoader, String[] args) throws Exception {
        
        URI compositeUri = new URI("/test/composite/");
        
        CompositeImplementation impl = new CompositeImplementation();
        impl.setScdlLocation(applicationScdl);
        impl.setClassLoader(applicationClassLoader);

        ComponentDefinition<CompositeImplementation> definition =
            new ComponentDefinition<CompositeImplementation>(compositeUri, impl);
        Collection<Component> components =  getDeployer().deploy(null, definition);
        for (Component component : components) {
            component.start();
        }

        return run(impl, args, compositeUri);
    }

    private Object run(CompositeImplementation impl, String[] args, URI compositeUri) throws Exception {
        CompositeComponentType<?,?,?> componentType = impl.getComponentType();
        Map<String, ComponentDefinition<? extends Implementation<?>>> components = componentType.getComponents();
        for (Map.Entry<String, ComponentDefinition<? extends Implementation<?>>> entry : components.entrySet()) {
            String name = entry.getKey();
            ComponentDefinition<? extends Implementation<?>> launchedDefinition = entry.getValue();
            Implementation implementation = launchedDefinition.getImplementation();
            if(implementation.getClass().isAssignableFrom(Launched.class)) {
                return run(compositeUri.resolve(name), implementation, args);
            }
        }
        return null;
    }

    private Object run(URI componentUri, Implementation implementation, String[] args) throws TargetInvokerCreationException, InvocationTargetException {
        Launched launched = (Launched) implementation;
        PojoComponentType launchedType = launched.getComponentType();
        Map services = launchedType.getServices();
        JavaMappedService testService = (JavaMappedService) services.get("main");
        Operation<?> operation = testService.getServiceContract().getOperations().get("main");
        Component component = getComponentManager().getComponent(componentUri);
        TargetInvoker targetInvoker = component.createTargetInvoker("main", operation);
        return targetInvoker.invokeTarget(new Object[]{args}, TargetInvoker.NONE);
    }

    protected Deployer getDeployer() {
        try {
            URI uri = URI.create("sca://root.system/main/deployer");
            AtomicComponent component = (AtomicComponent) getComponentManager().getComponent(uri);
            return (Deployer) component.getTargetInstance();
        } catch (TargetResolutionException e) {
            throw new AssertionError(e);
        }
    }
}
