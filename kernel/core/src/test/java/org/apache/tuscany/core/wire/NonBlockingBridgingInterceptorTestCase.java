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
package org.apache.tuscany.core.wire;

import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.services.work.WorkScheduler;
import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.MessageImpl;

import junit.framework.TestCase;
import static org.apache.tuscany.core.component.scope.ConversationalScopeContainer.CONVERSATIONAL_IDENTIFIER;
import org.easymock.EasyMock;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.getCurrentArguments;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import org.easymock.IAnswer;

/**
 * @version $Rev$ $Date$
 */
public class NonBlockingBridgingInterceptorTestCase extends TestCase {

    public void testInvoke() throws Exception {
        WorkScheduler scheduler = createMock(WorkScheduler.class);
        scheduler.scheduleWork(isA(Runnable.class));
        expectLastCall().andStubAnswer(new IAnswer<Object>() {
            public Object answer() throws Throwable {
                Runnable runnable = (Runnable) getCurrentArguments()[0];
                runnable.run();
                return null;
            }
        });
        replay(scheduler);
        WorkContext context = createMock(WorkContext.class);
        String convID = "convID";
        EasyMock.expect(context.getIdentifier(CONVERSATIONAL_IDENTIFIER)).andReturn(convID);
        context.setCurrentMessageId(null);
        context.setCurrentCorrelationId(null);
        context.setIdentifier(CONVERSATIONAL_IDENTIFIER, convID);
        EasyMock.replay(context);
        Message msg = new MessageImpl();
        Interceptor next = EasyMock.createMock(Interceptor.class);
        EasyMock.expect(next.invoke(EasyMock.eq(msg))).andReturn(msg);
        EasyMock.replay(next);
        Interceptor interceptor = new NonBlockingBridgingInterceptor(scheduler, context, next);
        interceptor.invoke(msg);
        verify(context);
        verify(next);
    }

}
