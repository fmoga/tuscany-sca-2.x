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

package org.apache.tuscany.idl.wsdl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.wsdl.Fault;
import javax.wsdl.Input;
import javax.wsdl.Message;
import javax.wsdl.Operation;
import javax.wsdl.Output;
import javax.wsdl.Part;
import javax.xml.namespace.QName;

import org.apache.tuscany.spi.idl.ElementInfo;
import org.apache.tuscany.spi.idl.InvalidServiceContractException;
import org.apache.tuscany.spi.idl.TypeInfo;
import org.apache.tuscany.spi.idl.WrapperInfo;
import org.apache.tuscany.spi.model.DataType;

import org.apache.ws.commons.schema.XmlSchemaComplexType;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.apache.ws.commons.schema.XmlSchemaObject;
import org.apache.ws.commons.schema.XmlSchemaObjectCollection;
import org.apache.ws.commons.schema.XmlSchemaParticle;
import org.apache.ws.commons.schema.XmlSchemaSequence;
import org.apache.ws.commons.schema.XmlSchemaSimpleType;
import org.apache.ws.commons.schema.XmlSchemaType;

/**
 * Metadata for a WSDL operation
 */
public class WSDLOperation {
    private static final String OPERATION_KEY = org.apache.tuscany.spi.model.Operation.class.getName();

    protected XMLSchemaRegistry schemaRegistry;

    protected Operation operation;

    private String dataBinding;

    protected org.apache.tuscany.spi.model.Operation<QName> operationModel;

    protected DataType<List<DataType<QName>>> inputType;

    protected DataType<QName> outputType;

    protected List<DataType<QName>> faultTypes;

    /**
     * @param operation      The WSDL4J operation
     * @param dataBinding    The default databinding
     * @param schemaRegistry The XML Schema registry
     */
    public WSDLOperation(Operation operation, String dataBinding, XMLSchemaRegistry schemaRegistry) {
        super();
        this.operation = operation;
        this.dataBinding = dataBinding;
        this.schemaRegistry = schemaRegistry;
        this.wrapper = new Wrapper();
    }

    private Wrapper wrapper;

    private Boolean wrapperStyle;

    /**
     * Test if the operation qualifies wrapper style as defined by the JAX-WS 2.0 spec
     *
     * @return true if the operation qualifies wrapper style, otherwise false
     */
    public boolean isWrapperStyle() throws InvalidWSDLException {
        if (wrapperStyle == null) {
            wrapperStyle =
                Boolean.valueOf(wrapper.getInputChildElements() != null
                    && (operation.getOutput() == null || wrapper.getOutputChildElements() != null));
        }
        return wrapperStyle.booleanValue();
    }

    public Wrapper getWrapper() throws InvalidWSDLException {
        if (!isWrapperStyle()) {
            throw new IllegalStateException("The operation is not wrapper style.");
        } else {
            return wrapper;
        }
    }

    /**
     * @return
     * @throws InvalidServiceContractException
     *
     */
    public DataType<List<DataType<QName>>> getInputType() throws InvalidServiceContractException {
        if (inputType == null) {
            Input input = operation.getInput();
            Message message = (input == null) ? null : input.getMessage();
            inputType = getMessageType(message);
            inputType.setDataBinding("idl:input");
        }
        return inputType;
    }

    /**
     * @return
     * @throws NotSupportedWSDLException
     */
    public DataType<QName> getOutputType() throws InvalidServiceContractException {
        if (outputType == null) {
            Output output = operation.getOutput();
            Message outputMsg = (output == null) ? null : output.getMessage();

            List outputParts = (outputMsg == null) ? null : outputMsg.getOrderedParts(null);
            if (outputParts != null && outputParts.size() > 0) {
                if (outputParts.size() > 1) {
                    // We don't support output with multiple parts
                    throw new NotSupportedWSDLException("Multi-part output is not supported");
                }
                Part part = (Part) outputParts.get(0);
                outputType = new WSDLPart(part).getDataType();
                // outputType.setMetadata(WSDLOperation.class.getName(), this);
            }
        }
        return outputType;
    }

    /**
     * @return
     * @throws NotSupportedWSDLException
     */
    public List<DataType<QName>> getFaultTypes() throws InvalidServiceContractException {
        if (faultTypes == null) {
            Collection faults = operation.getFaults().values();
            faultTypes = new ArrayList<DataType<QName>>();
            for (Object f : faults) {
                Fault fault = (Fault) f;
                Message faultMsg = fault.getMessage();
                List faultParts = faultMsg.getOrderedParts(null);
                if (faultParts.size() != 1) {
                    throw new NotSupportedWSDLException("The fault message MUST have a single part");
                }
                Part part = (Part) faultParts.get(0);
                WSDLPart wsdlPart = new WSDLPart(part);
                faultTypes.add(wsdlPart.getDataType());
            }
        }
        return faultTypes;
    }

    private DataType<List<DataType<QName>>> getMessageType(Message message) throws InvalidServiceContractException {
        List<DataType<QName>> partTypes = new ArrayList<DataType<QName>>();
        if (message != null) {
            Collection parts = message.getOrderedParts(null);
            for (Object p : parts) {
                WSDLPart part = new WSDLPart((Part) p);
                DataType<QName> partType = part.getDataType();
                partTypes.add(partType);
            }
        }
        return new DataType<List<DataType<QName>>>(dataBinding, Object[].class, partTypes);
    }

    /**
     * @return
     * @throws NotSupportedWSDLException
     */
    public org.apache.tuscany.spi.model.Operation<QName> getOperation() throws InvalidServiceContractException {
        if (operationModel == null) {
            boolean oneway = (operation.getOutput() == null);
            operationModel = new org.apache.tuscany.spi.model.Operation<QName>(operation.getName(),
                getInputType(),
                getOutputType(),
                getFaultTypes(),
                oneway,
                dataBinding,
                org.apache.tuscany.spi.model.Operation.NO_CONVERSATION);
            operationModel.setWrapperStyle(isWrapperStyle());
            // operationModel.setMetaData(WSDLOperation.class.getName(), this);
            if (isWrapperStyle()) {
                operationModel.setWrapper(getWrapper().getWrapperInfo());
                // Register the operation with the types
                for (DataType<?> d : wrapper.getUnwrappedInputType().getLogical()) {
                    d.setMetadata(OPERATION_KEY, operationModel);
                }
                if (wrapper.getUnwrappedOutputType() != null) {
                    wrapper.getUnwrappedOutputType().setMetadata(OPERATION_KEY, operationModel);
                }
            }
        }
        inputType.setMetadata(OPERATION_KEY, operationModel);
        if (outputType != null) {
            outputType.setMetadata(OPERATION_KEY, operationModel);
        }
        return operationModel;
    }

    /**
     * Metadata for a WSDL part
     */
    public class WSDLPart {
        private Part part;

        private XmlSchemaElement element;

        private DataType<QName> dataType;

        public WSDLPart(Part part) throws InvalidWSDLException {
            this.part = part;
            QName elementName = part.getElementName();
            if (elementName != null) {
                element = schemaRegistry.getElement(elementName);
                if (element == null) {
                    throw new InvalidWSDLException("Element cannot be resolved: " + elementName);
                }
            } else {
                // Create an faked XSD element to host the metadata
                element = new XmlSchemaElement();
                element.setName(part.getName());
                element.setQName(new QName(null, part.getName()));
                QName typeName = part.getTypeName();
                if (typeName != null) {
                    XmlSchemaType type = schemaRegistry.getType(typeName);
                    if (type == null) {
                        throw new InvalidWSDLException("Type cannot be resolved: " + typeName);
                    }
                    element.setSchemaType(type);
                    element.setSchemaTypeName(type.getQName());
                }
            }
            dataType = new DataType<QName>(dataBinding, Object.class, element.getQName());
            // dataType.setMetadata(WSDLPart.class.getName(), this);
            dataType.setMetadata(ElementInfo.class.getName(), getElementInfo(element));
        }

        /**
         * @return the element
         */
        public XmlSchemaElement getElement() {
            return element;
        }

        /**
         * @return the part
         */
        public Part getPart() {
            return part;
        }

        /**
         * @return the dataType
         */
        public DataType<QName> getDataType() {
            return dataType;
        }
    }

    /**
     * The "Wrapper Style" WSDL operation is defined by The Java API for XML-Based Web Services (JAX-WS) 2.0
     * specification, section 2.3.1.2 Wrapper Style.
     * <p/>
     * A WSDL operation qualifies for wrapper style mapping only if the following criteria are met: <ul> <li>(i) The
     * operationís input and output messages (if present) each contain only a single part <li>(ii) The input message
     * part refers to a global element declaration whose localname is equal to the operation name <li>(iii) The output
     * message part refers to a global element declaration <li>(iv) The elements referred to by the input and output
     * message parts (henceforth referred to as wrapper elements) are both complex types defined using the xsd:sequence
     * compositor <li>(v) The wrapper elements only contain child elements, they must not contain other structures such
     * as wildcards (element or attribute), xsd:choice, substitution groups (element references are not permitted) or
     * attributes; furthermore, they must not be nillable. </ul>
     */
    public class Wrapper {
        private XmlSchemaElement inputWrapperElement;

        private XmlSchemaElement outputWrapperElement;

        private List<XmlSchemaElement> inputElements;

        private List<XmlSchemaElement> outputElements;

        private DataType<List<DataType<QName>>> unwrappedInputType;

        private DataType<QName> unwrappedOutputType;

        private transient WrapperInfo wrapperInfo;

        private List<XmlSchemaElement> getChildElements(XmlSchemaElement element) throws InvalidWSDLException {
            if (element == null) {
                return null;
            }
            XmlSchemaType type = element.getSchemaType();
            if (type == null) {
                InvalidWSDLException ex =
                    new InvalidWSDLException("The XML schema element doesn't have a type");
                ex.addContextName("element: " + element.getQName());
                throw ex;
            }
            if (!(type instanceof XmlSchemaComplexType)) {
                // Has to be a complexType
                return null;
            }
            XmlSchemaComplexType complexType = (XmlSchemaComplexType) type;
            if (complexType.getAttributes().getCount() != 0 || complexType.getAnyAttribute() != null) {
                // No attributes
                return null;
            }
            XmlSchemaParticle particle = complexType.getParticle();
            if (particle == null) {
                // No particle
                return Collections.emptyList();
            }
            if (!(particle instanceof XmlSchemaSequence)) {
                return null;
            }
            XmlSchemaSequence sequence = (XmlSchemaSequence) complexType.getParticle();
            XmlSchemaObjectCollection items = sequence.getItems();
            List<XmlSchemaElement> childElements = new ArrayList<XmlSchemaElement>();
            for (int i = 0; i < items.getCount(); i++) {
                XmlSchemaObject schemaObject = items.getItem(i);
                if (!(schemaObject instanceof XmlSchemaElement)) {
                    return null;
                }
                XmlSchemaElement childElement = (XmlSchemaElement) schemaObject;
                if (childElement.getName() == null || childElement.getRefName() != null || childElement.isNillable()) {
                    return null;
                }
                // TODO: Do we support maxOccurs >1 ?
                if (childElement.getMaxOccurs() > 1) {
                    return null;
                }
                childElements.add(childElement);
            }
            return childElements;
        }

        /**
         * Return a list of child XSD elements under the wrapped request element
         *
         * @return a list of child XSD elements or null if if the request element is not wrapped
         */
        public List<XmlSchemaElement> getInputChildElements() throws InvalidWSDLException {
            if (inputElements != null) {
                return inputElements;
            }
            Input input = operation.getInput();
            if (input != null) {
                Message inputMsg = input.getMessage();
                Collection parts = inputMsg.getParts().values();
                if (parts.size() != 1) {
                    return null;
                }
                Part part = (Part) parts.iterator().next();
                QName elementName = part.getElementName();
                if (elementName == null) {
                    return null;
                }
                if (!operation.getName().equals(elementName.getLocalPart())) {
                    return null;
                }
                inputWrapperElement = schemaRegistry.getElement(elementName);
                if (inputWrapperElement == null) {
                    InvalidWSDLException ex =
                        new InvalidWSDLException("The element is not declared in a XML schema");
                    ex.addContextName("element: " + elementName);
                    throw ex;
                }
                inputElements = getChildElements(inputWrapperElement);
                return inputElements;
            } else {
                return null;
            }
        }

        /**
         * Return a list of child XSD elements under the wrapped response element
         *
         * @return a list of child XSD elements or null if if the response element is not wrapped
         */
        public List<XmlSchemaElement> getOutputChildElements() throws InvalidWSDLException {
            if (outputElements != null) {
                return outputElements;
            }
            Output output = operation.getOutput();
            if (output != null) {
                Message outputMsg = output.getMessage();
                Collection parts = outputMsg.getParts().values();
                if (parts.size() != 1) {
                    return null;
                }
                Part part = (Part) parts.iterator().next();
                QName elementName = part.getElementName();
                if (elementName == null) {
                    InvalidWSDLException ex =
                        new InvalidWSDLException("The element is not declared in the XML schema");
                    ex.addContextName("element: " + elementName);
                    throw ex;
                }
                outputWrapperElement = schemaRegistry.getElement(elementName);
                if (outputWrapperElement == null) {
                    return null;
                }
                outputElements = getChildElements(outputWrapperElement);
                // FIXME: Do we support multiple child elements for the response?
                return outputElements;
            } else {
                return null;
            }
        }

        /**
         * @return the inputWrapperElement
         */
        public XmlSchemaElement getInputWrapperElement() {
            return inputWrapperElement;
        }

        /**
         * @return the outputWrapperElement
         */
        public XmlSchemaElement getOutputWrapperElement() {
            return outputWrapperElement;
        }

        public DataType<List<DataType<QName>>> getUnwrappedInputType() throws InvalidWSDLException {
            if (unwrappedInputType == null) {
                List<DataType<QName>> childTypes = new ArrayList<DataType<QName>>();
                for (XmlSchemaElement element : getInputChildElements()) {
                    DataType<QName> type = new DataType<QName>(dataBinding, Object.class, element.getQName());
                    type.setMetadata(ElementInfo.class.getName(), getElementInfo(element));
                    childTypes.add(type);
                }
                unwrappedInputType =
                    new DataType<List<DataType<QName>>>("idl:unwrapped.input", Object[].class, childTypes);
            }
            return unwrappedInputType;
        }

        public DataType<QName> getUnwrappedOutputType() throws InvalidServiceContractException {
            if (unwrappedOutputType == null) {
                List<XmlSchemaElement> elements = getOutputChildElements();
                if (elements != null && elements.size() > 0) {
                    if (elements.size() > 1) {
                        // We don't support output with multiple parts
                        throw new NotSupportedWSDLException("Multi-part output is not supported");
                    }
                    XmlSchemaElement element = elements.get(0);
                    unwrappedOutputType = new DataType<QName>(dataBinding, Object.class, element.getQName());
                    unwrappedOutputType.setMetadata(ElementInfo.class.getName(), getElementInfo(element));
                }
            }
            return unwrappedOutputType;
        }

        public WrapperInfo getWrapperInfo() throws InvalidServiceContractException {
            if (wrapperInfo == null) {
                ElementInfo in = getElementInfo(getInputWrapperElement());
                ElementInfo out = getElementInfo(getOutputWrapperElement());
                List<ElementInfo> inChildren = new ArrayList<ElementInfo>();
                for (XmlSchemaElement e : getInputChildElements()) {
                    inChildren.add(getElementInfo(e));
                }
                List<ElementInfo> outChildren = new ArrayList<ElementInfo>();
                if (out != null) {
                    for (XmlSchemaElement e : getOutputChildElements()) {
                        outChildren.add(getElementInfo(e));
                    }
                }
                wrapperInfo =
                    new WrapperInfo(in, out, inChildren, outChildren, getUnwrappedInputType(),
                        getUnwrappedOutputType());
            }
            return wrapperInfo;
        }
    }

    private static ElementInfo getElementInfo(XmlSchemaElement element) {
        if (element == null) {
            return null;
        }
        return new ElementInfo(element.getQName(), getTypeInfo(element.getSchemaType()));
    }

    private static TypeInfo getTypeInfo(XmlSchemaType type) {
        if (type == null) {
            return null;
        }
        XmlSchemaType baseType = (XmlSchemaType) type.getBaseSchemaType();
        QName name = type.getQName();
        boolean simple = (type instanceof XmlSchemaSimpleType);
        if (baseType == null) {
            return new TypeInfo(name, simple, null);
        } else {
            return new TypeInfo(name, simple, getTypeInfo(baseType));
        }
    }

}
