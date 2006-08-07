/**
 *
 * Copyright 2006 The Apache Software Foundation or its licensors, as applicable.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this 
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under 
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY 
 * KIND, either express or implied. See the License for the specific language governing 
 * permissions and limitations under the License.
 */
package org.apache.tuscany.binding.celtix;

import org.apache.tuscany.spi.builder.BuilderException;

/**
 * Thrown when an error is encountered creating a {@link CeltixInvoker}
 *
 * @version $Rev$ $Date$
 */
public class InvokerCreationException extends BuilderException {
    public InvokerCreationException() {
    }

    public InvokerCreationException(String message) {
        super(message);
    }

    public InvokerCreationException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvokerCreationException(Throwable cause) {
        super(cause);
    }
}
