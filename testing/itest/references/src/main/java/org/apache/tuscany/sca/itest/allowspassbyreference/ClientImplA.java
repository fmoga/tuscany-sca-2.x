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

package org.apache.tuscany.sca.itest.allowspassbyreference;

import junit.framework.Assert;

import org.oasisopen.sca.annotation.ComponentName;
import org.oasisopen.sca.annotation.Reference;
import org.oasisopen.sca.annotation.Scope;
import org.oasisopen.sca.annotation.Service;

/**
 * 
 */
@Service(AServiceClient.class)
@Scope("COMPOSITE")
public class ClientImplA implements AServiceClient {
    @ComponentName
    private String componentName;

    @Reference
    private AService service;

    @Override
    public int create(String state) {
        MutableObject req = new MutableObject(state);
        MutableObject res = service.create(req);
        if ("ClientA1Component".equals(componentName)) {
            Assert.assertTrue(req.getId() == -1);
            Assert.assertNotSame(req, res);
        } else if ("ClientA2Component".equals(componentName)) {
            Assert.assertTrue(req.getId() == -1);
            Assert.assertNotSame(req, res);
        }
        return res.getId();
    }

    @Override
    public String read(int id) {
        MutableObject req = new MutableObject(id);
        MutableObject res = service.read(req);
        if ("ClientA1Component".equals(componentName)) {
            Assert.assertTrue(req.getState() == null);
            Assert.assertNotSame(req, res);
        } else if ("ClientA2Component".equals(componentName)) {
            Assert.assertTrue(req.getState() == null);
        }
        return res.getState();
    }

    @Override
    public String update(int id, String newState) {
        MutableObject req = new MutableObject(id, newState);
        MutableObject res = service.update(req);
        if ("ClientA1Component".equals(componentName)) {
            Assert.assertTrue(req.getState() == newState);
        } else if ("ClientA2Component".equals(componentName)) {
            Assert.assertTrue(req.getState() == newState);
        }
        return res.getState();
    }

    @Override
    public boolean delete(int id) {
        MutableObject req = new MutableObject(id);
        MutableObject res = service.delete(req);
        return res != null;
    }

}
