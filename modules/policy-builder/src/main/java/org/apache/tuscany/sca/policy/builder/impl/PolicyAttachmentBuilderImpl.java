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

package org.apache.tuscany.sca.policy.builder.impl;

import static javax.xml.XMLConstants.DEFAULT_NS_PREFIX;
import static javax.xml.XMLConstants.XMLNS_ATTRIBUTE;
import static javax.xml.XMLConstants.XMLNS_ATTRIBUTE_NS_URI;

import java.io.StringWriter;
import java.util.StringTokenizer;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;

import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilder;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderException;
import org.apache.tuscany.sca.common.xml.dom.DOMHelper;
import org.apache.tuscany.sca.common.xml.stax.StAXHelper;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.definitions.Definitions;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.policy.PolicySet;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A builder that attaches policy sets to the domain composite using the xpath defined by
 * the attachTo attribute. It first creates a DOM model for the composite so that the xpath
 * expression can be evaluated. For the nodes selected by the xpath, add the policySets attribute
 * to the subject element. Then reload the patched DOM into a Composite model again.  
 *
 * @version $Rev$ $Date$
 */
public class PolicyAttachmentBuilderImpl implements CompositeBuilder {
    private StAXHelper staxHelper;
    private DOMHelper domHelper;
    private StAXArtifactProcessor<Composite> processor;

    public PolicyAttachmentBuilderImpl(ExtensionPointRegistry registry) {
        domHelper = DOMHelper.getInstance(registry);
        staxHelper = StAXHelper.getInstance(registry);
        StAXArtifactProcessorExtensionPoint processors =
            registry.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        processor = processors.getProcessor(Composite.class);
    }

    public String getID() {
        return "org.apache.tuscany.sca.policy.builder.PolicyAttachmentBuilder";
    }

    public void build(Composite composite, Definitions definitions, Monitor monitor) throws CompositeBuilderException {
        try {
            Composite patched = applyXPath(composite, definitions, monitor);
            System.out.println(patched);
        } catch (Exception e) {
            throw new CompositeBuilderException(e);
        }
    }

    /**
     * Apply the attachTo XPath against the composite model
     * @param composite The orginal composite
     * @param definitions SCA definitions that contain the policy sets
     * @param monitor The monitor
     * @return A reloaded composite
     * @throws Exception
     */
    private Composite applyXPath(Composite composite, Definitions definitions, Monitor monitor) throws Exception {
        // First write the composite into a DOM document so that we can apply the xpath
        StringWriter sw = new StringWriter();
        XMLStreamWriter writer = staxHelper.createXMLStreamWriter(sw);
        // Write the composite into a DOM document
        processor.write(composite, writer);
        writer.close();

        Document document = domHelper.load(sw.toString());

        boolean changed = false;
        for (PolicySet ps : definitions.getPolicySets()) {
            XPathExpression exp = ps.getAttachToXPathExpression();
            if (exp != null) {
                NodeList nodes = (NodeList)exp.evaluate(document, XPathConstants.NODESET);
                for (int i = 0; i < nodes.getLength(); i++) {
                    Node node = nodes.item(i);
                    // The node can be a component, service, reference or binding
                    if (attach(node, ps) && !changed) {
                        changed = true;
                    }
                }
            }
        }

        if (changed) {
            XMLStreamReader reader = staxHelper.createXMLStreamReader(document);
            reader.nextTag();
            Composite patchedComposite = (Composite)processor.read(reader);
            return patchedComposite;
        } else {
            return composite;
        }
    }

    /**
     * Attach the policySet to the given DOM node 
     * @param node The DOM node (should be an element)
     * @param policySet The policy set to be attached
     * @return true if the element is changed, false if the element already contains the same policy set
     * and no change is made
     */
    private boolean attach(Node node, PolicySet policySet) {
        Element element = (Element)node;
        Document document = element.getOwnerDocument();

        QName qname = policySet.getName();
        String prefix = DOMHelper.getPrefix(element, qname.getNamespaceURI());
        if (prefix == null) {
            // Find the a non-conflicting prefix
            int i = 0;
            while (true) {
                prefix = "ns" + i;
                String ns = DOMHelper.getNamespaceURI(element, prefix);
                if (ns == null) {
                    break;
                }
            }
            // Declare the namespace
            Attr nsAttr = document.createAttributeNS(XMLNS_ATTRIBUTE_NS_URI, XMLNS_ATTRIBUTE + ":" + prefix);
            nsAttr.setValue(qname.getNamespaceURI());
            element.setAttributeNodeNS(nsAttr);
        }
        // Form the value as a qualified name
        String qvalue = null;
        if (DEFAULT_NS_PREFIX.equals(prefix)) {
            qvalue = qname.getLocalPart();
        } else {
            qvalue = prefix + ":" + qname.getLocalPart();
        }

        // Check if the attribute exists
        Attr attr = element.getAttributeNode("policySets");
        if (attr == null) {
            // Create the policySets attr
            attr = document.createAttributeNS(null, "policySets");
            attr.setValue(qvalue);
            element.setAttributeNodeNS(attr);
            return true;
        } else {
            // Append to the existing value
            boolean duplicate = false;
            String value = attr.getValue();
            StringTokenizer tokenizer = new StringTokenizer(value);
            while (tokenizer.hasMoreTokens()) {
                String ps = tokenizer.nextToken();
                int index = ps.indexOf(':');
                String ns = null;
                String localName = null;
                if (index == -1) {
                    ns = DOMHelper.getNamespaceURI(element, DEFAULT_NS_PREFIX);
                    localName = ps;
                } else {
                    ns = DOMHelper.getNamespaceURI(element, ps.substring(0, index));
                    localName = ps.substring(index + 1);
                }
                QName psName = new QName(ns, localName);
                if (qname.equals(psName)) {
                    duplicate = true;
                    break;
                }
            }
            if (!duplicate) {
                // REVIEW: [rfeng] How to comply to POL40012?
                value = value + " " + qvalue;
                attr.setValue(value.trim());
                return true;
            }
            return false;
        }
    }

}
