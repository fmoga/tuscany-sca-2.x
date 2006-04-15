/**
 *
 *  Copyright 2005 BEA Systems Inc.
 *  Copyright 2005 International Business Machines Corporation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.core.system.config;

import org.apache.tuscany.core.builder.ContextCreationException;
import org.apache.tuscany.core.builder.ContextFactory;
import org.apache.tuscany.core.builder.ObjectFactory;
import org.apache.tuscany.core.context.CompositeContext;
import org.apache.tuscany.core.context.Context;
import org.apache.tuscany.core.injection.SingletonObjectFactory;
import org.apache.tuscany.core.invocation.spi.ProxyFactory;
import org.apache.tuscany.core.system.context.SystemAtomicContext;
import org.apache.tuscany.model.assembly.Scope;

import java.util.List;
import java.util.Map;

/**
 * A ContextFactory that contains the configuration needed to convert a simple
 * Java Object into a component. The object is assumed to be fully initialized and
 * will always be added with MODULE scope.
 *
 * @version $Rev$ $Date$
 */
public class SystemObjectContextFactory implements ContextFactory {
    private final String name;
    private final ObjectFactory<?> objectFactory;

    /**
     * Construct a ContextFactory for the supplied Java Object.
     *
     * @param name the name to be assigned to the resulting component
     * @param instance the Java Object that provides the implementation
     */
    public SystemObjectContextFactory(String name, Object instance) {
        this.name = name;
        objectFactory = new SingletonObjectFactory<Object>(instance);
    }

    public Context createContext() throws ContextCreationException {
        return new SystemAtomicContext(name, objectFactory, false, null, null, false);
    }

    public Scope getScope() {
        return Scope.MODULE;
    }

    public String getName() {
        return name;
    }

    public void addTargetProxyFactory(String serviceName, ProxyFactory factory) {
        throw new UnsupportedOperationException();
    }

    public ProxyFactory getTargetProxyFactory(String serviceName) {
        throw new UnsupportedOperationException();
    }

    public Map getTargetProxyFactories() {
        throw new UnsupportedOperationException();
    }

    public void addSourceProxyFactory(String referenceName, ProxyFactory factory) {
        throw new UnsupportedOperationException();
    }

    public ProxyFactory getSourceProxyFactory(String referenceName) {
        throw new UnsupportedOperationException();
    }

    public List<ProxyFactory> getSourceProxyFactories() {
        throw new UnsupportedOperationException();
    }

    public void prepare(CompositeContext parent) {
    }
}
