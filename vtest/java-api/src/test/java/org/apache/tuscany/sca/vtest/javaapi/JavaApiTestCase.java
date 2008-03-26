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

package org.apache.tuscany.sca.vtest.javaapi;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 */
public class JavaApiTestCase {
    
    @BeforeClass
    public static void init() throws Exception {
             
        try {
            System.out.println("Setting up");
                        
        } catch(Exception ex){
            ex.printStackTrace();
        }  
        
   }

    @AfterClass
    public static void destroy() throws Exception {

        System.out.println("Clean up");

    }
    
    @Test
    public void testStartWithNoNodeContributions() throws Exception { 

                  Assert.assertTrue(true);
    }
    
    
}
