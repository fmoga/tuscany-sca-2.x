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
package test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import junit.framework.Assert;

import org.apache.tuscany.sca.http.jetty.JettyServer;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class BindingTestCase {

    private static Node node;

    @Test
    public void testService() throws MalformedURLException, IOException {
        URL url = new URL("http://localhost:8085/HelloWorldComponent/HelloWorldService/sayHello?name=petra&callback=foo");
        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
        String response = br.readLine();
        Assert.assertEquals("foo(\"Hello petra\");", response);

    }

    @Test
    public void testTwoArgs() throws MalformedURLException, IOException {
        URL url = new URL("http://localhost:8085/HelloWorldComponent/HelloWorldService/sayHello2?first=petra&last=arnold&callback=foo");
        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
        String response = br.readLine();
        Assert.assertEquals("foo(\"Hello petra arnold\");", response);

    }

    @BeforeClass
    public static void init() throws Exception {
        JettyServer.portDefault = 8085;
        node = NodeFactory.newInstance().createNode("helloworld.composite").start();
    }
    
    @AfterClass
    public static void destroy() throws Exception {
        if (node != null) {
            node.stop();
        }
    }

}
