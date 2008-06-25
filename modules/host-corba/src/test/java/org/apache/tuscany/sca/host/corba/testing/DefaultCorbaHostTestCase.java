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

package org.apache.tuscany.sca.host.corba.testing;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

import java.io.IOException;

import org.apache.tuscany.sca.host.corba.CorbaHost;
import org.apache.tuscany.sca.host.corba.CorbaHostException;
import org.apache.tuscany.sca.host.corba.DefaultCorbaHost;
import org.apache.tuscany.sca.host.corba.testing.general.TestInterface;
import org.apache.tuscany.sca.host.corba.testing.general.TestInterfaceHelper;
import org.apache.tuscany.sca.host.corba.testing.servants.TestInterfaceServant;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.omg.CORBA.ORB;

/**
 * General tests
 */
public class DefaultCorbaHostTestCase {

    private static final String LOCALHOST = "localhost";
    private static final int DEFAULT_PORT = 11100; //1050;
    private static final long INTERVAL = 500;

    private static Process tn;
    private static CorbaHost host;

    /**
     * Spawn tnameserv under given port
     * 
     * @param port
     * @return
     * @throws IOException
     */
    private static Process spawnTnameserv(int port) throws IOException {
        Process process = Runtime.getRuntime().exec("tnameserv -ORBInitialPort " + port);
        for (int i = 0; i < 3; i++) {
            try {
                // Thread.sleep(SPAWN_TIME);
                String[] args = {"-ORBInitialHost", LOCALHOST, "-ORBInitialPort", "" + port};
                ORB orb = ORB.init(args, null);
                orb.resolve_initial_references("NameService");
                break;
            } catch (Throwable e) {
                try {
                    Thread.sleep(INTERVAL);
                } catch (InterruptedException e1) {
                    // Ignore
                }
            }
        }
        return process;
    }

    /**
     * Kill previously spawned tnameserv
     * 
     * @param p
     */
    private static void killProcess(Process p) {
        if (p != null) {
            p.destroy();
        }
    }

    @BeforeClass
    public static void start() {
        try {
            tn = spawnTnameserv(DEFAULT_PORT);
            host = new DefaultCorbaHost();
        } catch (Throwable e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @AfterClass
    public static void stop() {
        killProcess(tn);
    }

    /**
     * Tests registering, getting and unregistering CORBA object
     */
    @Test
    public void test_registerServant() {
        try {
            TestInterface servant = new TestInterfaceServant();
            host.registerServant("Test", LOCALHOST, DEFAULT_PORT, servant);

            TestInterface ref = TestInterfaceHelper.narrow(host.getReference("Test", null, DEFAULT_PORT));
            assertEquals(2, ref.getInt(2));

            host.unregisterServant("Test", LOCALHOST, DEFAULT_PORT);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    /**
     * Tests situation when name is already registered
     */
    @Test
    public void test_nameAlreadyRegistered() {
        try {
            TestInterface servant = new TestInterfaceServant();
            host.registerServant("Test", LOCALHOST, DEFAULT_PORT, servant);
            host.registerServant("Test", LOCALHOST, DEFAULT_PORT, servant);
            fail();
        } catch (CorbaHostException e) {
            assertTrue(e.getMessage().equals(CorbaHostException.BINDING_IN_USE));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    /**
     * Tests getting non existing reference
     */
    @Test
    public void test_getNonExistingObject() {
        try {
            host.getReference("NonExistingReference", LOCALHOST, DEFAULT_PORT);
            fail();
        } catch (CorbaHostException e) {
            assertTrue(e.getMessage().equals(CorbaHostException.NO_SUCH_OBJECT));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    /**
     * Tests unregistering non existing reference
     */
    @Test
    public void test_unregisterNonExistentObject() {
        try {
            host.unregisterServant("NonExistingReference2", LOCALHOST, DEFAULT_PORT);
            fail();
        } catch (CorbaHostException e) {
            assertTrue(e.getMessage().equals(CorbaHostException.NO_SUCH_OBJECT));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    /**
     * Tests registering under invalid host
     */
    @Test
    public void test_invalidHost() {
        try {
            TestInterface servant = new TestInterfaceServant();
            host.registerServant("Test", "nosuchhost", DEFAULT_PORT, servant);
            fail();
        } catch (CorbaHostException e) {
            // Expected
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    /**
     * Tests registering under invalid port
     */
    @Test
    public void test_invalidPort() {
        try {
            TestInterface servant = new TestInterfaceServant();
            host.registerServant("Test", LOCALHOST, 9991, servant);
            fail();
        } catch (CorbaHostException e) {
            // Expected
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    /**
     * Tests registering under invalid name
     */
    @Test
    @Ignore("SUN JDK 6 is happy with all kind of names")
    public void test_invalidBindingName() {
        try {
            TestInterface servant = new TestInterfaceServant();
            host.registerServant("---", LOCALHOST, DEFAULT_PORT, servant);
            fail();
        } catch (CorbaHostException e) {
            assertTrue(e.getMessage().equals(CorbaHostException.WRONG_NAME));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }
}
