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

package org.apache.tuscany.sca.host.rmi;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.ModuleActivator;

/**
 * Default implementation of an RMI host extension point.
 *
 * @version $Rev$ $Date$
 */
public class DefaultRMIHostExtensionPoint implements RMIHostExtensionPoint, ModuleActivator {

    private List<RMIHost> rmiHosts = new ArrayList<RMIHost>();

    public DefaultRMIHostExtensionPoint() {
        addRMIHost(new DefaultRMIHost());
    }

    public void addRMIHost(RMIHost rmiHost) {
        rmiHosts.add(rmiHost);
    }

    public void removeRMIHost(RMIHost rmiHost) {
        rmiHosts.remove(rmiHost);
    }

    public List<RMIHost> getRMIHosts() {
        return rmiHosts;
    }

    public void start(ExtensionPointRegistry registry) {
    }

    public void stop(ExtensionPointRegistry registry) {
        for (RMIHost host : rmiHosts) {
            host.destroy();
        }
    }
}
