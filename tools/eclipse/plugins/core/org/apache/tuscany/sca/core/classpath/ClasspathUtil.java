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

package org.apache.tuscany.sca.core.classpath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;

/**
 * Utility functions handling the runtime classpath.
 *
 * @version $Rev: $ $Date: $
 */
public class ClasspathUtil {

    private static final String TUSCANY_RUNTIME_LIBRARIES = "org.apache.tuscany.sca.core.runtimeLibraries";

    /**
     * Return the installed runtime classpath entries.
     * 
     * @return
     * @throws CoreException
     */
    public static String installedRuntimeClasspath() throws CoreException {
        
        List<IClasspathEntry> classpathEntries = new ArrayList<IClasspathEntry>(); 
        for (IExtension extension: Platform.getExtensionRegistry().getExtensionPoint(TUSCANY_RUNTIME_LIBRARIES).getExtensions()) {
            for (IConfigurationElement configuration: extension.getConfigurationElements()) {
                IClasspathContainer container = (IClasspathContainer)configuration.createExecutableExtension("class");
                classpathEntries.addAll(Arrays.asList(container.getClasspathEntries()));
            }
        }
        
        String separator = System.getProperty("path.separator");
        StringBuffer classpath = new StringBuffer();
        for (int i = 0, n = classpathEntries.size(); i < n; i++) {
            IClasspathEntry entry = classpathEntries.get(i);
            if (i >0) {
                classpath.append(separator);
            }
            classpath.append(entry.getPath().toFile().toURI().getPath());
        }
        
        return classpath.toString();
    }

}
