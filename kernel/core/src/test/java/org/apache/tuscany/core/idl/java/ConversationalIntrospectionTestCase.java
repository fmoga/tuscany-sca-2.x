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
package org.apache.tuscany.core.idl.java;

import org.osoa.sca.annotations.Conversation;
import org.osoa.sca.annotations.EndConversation;
import org.osoa.sca.annotations.Scope;

import org.apache.tuscany.spi.idl.InvalidConversationalContractException;
import org.apache.tuscany.spi.idl.java.JavaServiceContract;
import static org.apache.tuscany.spi.model.InteractionScope.CONVERSATIONAL;
import static org.apache.tuscany.spi.model.InteractionScope.NONCONVERSATIONAL;
import org.apache.tuscany.spi.model.Operation;
import static org.apache.tuscany.spi.model.ServiceContract.UNDEFINED;

import junit.framework.TestCase;

/**
 * @version $Rev$ $Date$
 */
public class ConversationalIntrospectionTestCase extends TestCase {
    private JavaInterfaceProcessorRegistryImpl registry = new JavaInterfaceProcessorRegistryImpl();

    public void testServiceContractConversationalInformationIntrospection() throws Exception {
        JavaServiceContract contract = registry.introspect(Foo.class);
        assertEquals(UNDEFINED, contract.getMaxAge());
        assertEquals(UNDEFINED, contract.getMaxIdleTime());
        assertEquals(CONVERSATIONAL, contract.getInteractionScope());
        int seq = contract.getOperations().get("operation").getConversationSequence();
        assertEquals(Operation.CONVERSATION_CONTINUE, seq);
        seq = contract.getOperations().get("endOperation").getConversationSequence();
        assertEquals(Operation.CONVERSATION_END, seq);
    }

    public void testServiceContractConversationAnnotation() throws Exception {
        JavaServiceContract contract = registry.introspect(FooConversation.class);
        assertEquals(100000, contract.getMaxAge());
    }

    public void testBadServiceContract() throws Exception {
        try {
            registry.introspect(BadFoo.class);
            fail();
        } catch (InvalidConversationalContractException e) {
            //expected
        }
    }

    public void testBadConversationAnnotation() throws Exception {
        try {
            registry.introspect(BadFooConversation.class);
            fail();
        } catch (InvalidConversationalContractException e) {
            //expected
        }
    }

    public void testNonConversationalInformationIntrospection() throws Exception {
        JavaServiceContract contract = registry.introspect(NonConversationalFoo.class);
        assertEquals(UNDEFINED, contract.getMaxAge());
        assertEquals(UNDEFINED, contract.getMaxIdleTime());
        assertEquals(NONCONVERSATIONAL, contract.getInteractionScope());
        int seq = contract.getOperations().get("operation").getConversationSequence();
        assertEquals(Operation.NO_CONVERSATION, seq);
    }

    @Scope("CONVERSATION")
    private interface Foo {
        void operation();

        @EndConversation
        void endOperation();
    }

    @Scope("CONVERSATION")
    @Conversation(maxAge = "100")
    private interface FooConversation {
    }


    private interface BadFoo {
        void operation();

        @EndConversation
        void endOperation();
    }

    @Conversation(maxAge = "100")
    private interface BadFooConversation {
    }

    private interface NonConversationalFoo {
        void operation();
    }

}
