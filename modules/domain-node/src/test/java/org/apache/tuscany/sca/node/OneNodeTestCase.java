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

package org.apache.tuscany.sca.node;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import itest.nodes.Helloworld;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.oasisopen.sca.NoSuchDomainException;
import org.oasisopen.sca.client.SCAClient;

/**
 * This shows how to test the Calculator service component.
 */
public class OneNodeTestCase{

    private static DomainNode domain;
    private static String serviceContributionUri;
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {

        domain = new DomainNode();
        serviceContributionUri = domain.addContribution("target/test-classes/itest-nodes-helloworld-service-2.0-SNAPSHOT.jar");
        domain.addContribution("target/test-classes/itest-nodes-helloworld-client-2.0-SNAPSHOT.jar");
    }

    @Test
    public void testCalculator() throws Exception {
        Helloworld service = SCAClient.getService(Helloworld.class, "defaultDomain/HelloworldService");
        assertNotNull(service);
        assertEquals("Hello Petra", service.sayHello("Petra"));

        Helloworld client = SCAClient.getService(Helloworld.class, "defaultDomain/HelloworldClient");
        assertNotNull(client);
        assertEquals("Hi Hello Petra", client.sayHello("Petra"));

        // FIXME: this should give a service not found as the service contribution has been removed 
        // domain.removeContribution(serviceContributionUri);
        // assertEquals("Hi Hello Petra", client.sayHello("Petra"));
        
        domain.stop();
        try {
            SCAClient.getService(Helloworld.class, "defaultDomain/HelloworldClient");
            fail();
        } catch (NoSuchDomainException e) {
            // expected
        }
    }
    

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        if (domain != null && domain.isStarted()) {
            domain.stop();
        }
    }
}