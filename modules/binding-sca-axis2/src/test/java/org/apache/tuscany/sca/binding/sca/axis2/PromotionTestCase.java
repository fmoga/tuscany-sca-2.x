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
package org.apache.tuscany.sca.binding.sca.axis2;

import junit.framework.Assert;

import org.apache.tuscany.sca.binding.sca.axis2.helloworld.HelloWorldClient;
import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class PromotionTestCase {
    
    public static Node nodeC;
    public static Node nodeD;

    @BeforeClass
    public static void init() throws Exception {
        System.out.println("Setting up nodes");

        try {
            // create and start domains
            NodeFactory nodeFactory = NodeFactory.newInstance();
            ClassLoader cl = AsynchTestCase.class.getClassLoader();
            nodeC = nodeFactory.createNode("HelloWorld.composite", new Contribution("http://calculator", cl.getResource("nodeC").toString()));
            nodeD = nodeFactory.createNode("HelloWorld.composite", new Contribution("http://calculator", cl.getResource("nodeD").toString()));

            nodeC.start();
            nodeD.start();

        } catch (Exception ex) {
            System.err.println("Exception when creating domain " + ex.getMessage());
            ex.printStackTrace(System.err);
            throw ex;
        }     
    }

    @AfterClass
    public static void destroy() throws Exception {
        nodeC.stop();
        nodeC.destroy();
        nodeD.stop();
        nodeD.destroy();
    }     
    
    @Test
    public void testHelloWorldPromotion() throws Exception {  
        HelloWorldClient helloWorldClientA;
        helloWorldClientA = nodeC.getService(HelloWorldClient.class, "AHelloWorldClientRemotePromotion");
        Assert.assertEquals(helloWorldClientA.getGreetings("fred"), "Hello fred");

    }      
  
}
