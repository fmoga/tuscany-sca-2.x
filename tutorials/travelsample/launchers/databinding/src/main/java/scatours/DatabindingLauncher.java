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

package scatours;

import static scatours.launcher.LauncherUtil.locate;

import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;

public class DatabindingLauncher {
    public static void main(String[] args) throws Exception {
        Node node1 = NodeFactory.getInstance().createNode(
                                                                   locate("payment-java"), 
                                                                   locate("databinding-client"));

        Node node2 = NodeFactory.getInstance().createNode(locate("creditcard-payment-sdo"));

        node1.start();
        node2.start();

        Runnable runner = ((Node)node1).getService(Runnable.class, "TestClient/Runnable");
        runner.run();

        node1.stop();
        node2.stop();
    }
}
