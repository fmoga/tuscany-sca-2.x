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
package org.apache.tuscany.sca.core.spring.implementation.java.impl;

import org.apache.tuscany.sca.interfacedef.InvalidInterfaceException;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceContract;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;
import org.apache.tuscany.sca.interfacedef.java.impl.JavaInterfaceIntrospectorImpl;
import org.apache.tuscany.sca.interfacedef.java.introspect.JavaInterfaceIntrospectorExtensionPoint;

/**
 * An alternate implementation of the SCA Java assembly model factory that creates SCA
 * Java assembly model objects backed by Spring bean definitions.
 *
 *  @version $Rev$ $Date$
 */
public class BeanJavaInterfaceFactory implements JavaInterfaceFactory {
	
    private JavaInterfaceIntrospectorImpl introspector;
    
        public BeanJavaInterfaceFactory(JavaInterfaceIntrospectorExtensionPoint visitors) {
            introspector = new JavaInterfaceIntrospectorImpl(this, visitors);
        }

	public JavaInterface createJavaInterface() {
		return new BeanJavaInterfaceImpl();
	}
        
        public JavaInterface createJavaInterface(Class<?> interfaceClass) throws InvalidInterfaceException {
            return introspector.introspect(interfaceClass);
        }
        
        public JavaInterfaceContract createJavaInterfaceContract() {
            return new BeanJavaInterfaceContractImpl();
        }

}
