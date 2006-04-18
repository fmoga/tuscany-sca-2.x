package org.apache.tuscany.tools.java2wsdl.generate;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Element;

/**
 * This is the default Java2WSDL Generator class that extends
 * Java2WSDLGeneratorImpl which inturn implements a facade to the Axis2
 * implementation for Java2WSDL conversion. This extension of
 * Java2WSDLGeneratorImpl deals with fixing the errors in the Java2WSDL model
 * that is generated by Axis2 implementation. This class can also be used to
 * introduce any extensions to the generated WSDL just in case.
 */

/**
 * @author administrator
 *
 */
/**
 * @author administrator
 * 
 */
public class Java2WSDLDefaultGenerator extends Java2WSDLGeneratorImpl implements
		WSDLGenListener {
	/**
	 * map of namespace URIs used in the schema type definitions fragment of the
	 * WSDL. Each URI is mapped to a a corresponding prefix. The namespaces in
	 * this map are declared in wsdl:definitions element. This map is used to
	 * provide valid and consistent namespaces to the data types used in the
	 * message part elements.
	 */
	protected static final Map<String, String> schemaNamespace2GlobalPrefix = new HashMap<String, String>();

	private static int prefixCount = 1;

	private static final String SCHEMA_NAMESPACE_PRFIX = "impl_";

	/**
	 * client can get an instance of this class only via the generator factory
	 */
	Java2WSDLDefaultGenerator() {
		addWSDLGenListener(this);
	}

	/*
	 * @see org.apache.tuscany.tools.java2wsdl.generate.WSDLGenListener#WSDLGenPhaseCompleted(org.apache.tuscany.tools.java2wsdl.generate.WSDLGenEvent)
	 */
	public void WSDLGenPhaseCompleted(WSDLGenEvent event) {
		// if it is the end of the WSDL Model Creation Phase
		if (event.getGenerationPhase() == WSDLGenListener.WSDL_MODEL_CREATION) {
			// fix the generated model
			fixModel();
		}
	}

	/**
	 * fix method that remeoves redundant and invalid namespaces and attributes
	 * in the xml schema fragment within the wsdl:types element of the generated
	 * WSDL
	 * 
	 */
	private void removeSchemaNamespacesFix() {
		String[] attrsToRemove = { "xmlns:ns1", "xmlns:ns2", "xmlns:ns0",
				"ns1:elementFormDefault", "ns0:attributeFormDefault",
				"ns2:targetNamespace" };

		WSDLModel model = getWSDLModel();
		Element schemaElement = model.getSchemaElement();

		String targetNamespace = schemaElement
				.getAttribute("ns2:targetNamespace");
		if (targetNamespace != null && targetNamespace.length() > 0) {
			schemaElement.setAttribute("targetNamespace", targetNamespace);
		}

		for (int count = 0; count < attrsToRemove.length; ++count) {
			schemaElement.removeAttribute(attrsToRemove[count]);
		}

		schemaElement.setAttribute("elementFormDefault", "qualified");
		schemaElement.setAttribute("attributeFormDefault", "qualified");

	}

	/**
	 * fix to add schema target namespace to global namespace definitions
	 */
	private void addSchemaTargetNameSpace2DefnFix() {
		WSDLModel wsdlModel = getWSDLModel();
		Element schemaElement = wsdlModel.getSchemaElement();

		// create a new prefix for the schema target namespace URI and store
		// into map
		schemaNamespace2GlobalPrefix.put(schemaElement
				.getAttribute("targetNamespace"), SCHEMA_NAMESPACE_PRFIX
				+ prefixCount++);

		// add the schema target namespace URI and prefix as a namespace
		// declaration
		// into the wsdl:definitions element (to enable the valid use of prefixs
		// in the
		// type attribute of message part elements of the wsdl

		wsdlModel.getDefnNamespaceMap().put(
				schemaNamespace2GlobalPrefix.get(schemaElement
						.getAttribute("targetNamespace")),
				schemaElement.getAttribute("targetNamespace"));
	}

	/**
	 * apply a series of fixes on the generated wsdl model
	 */
	private void fixModel() {
		// fix to remeove redundant and invalid namespaces and attributes in the
		// xml schema
		// fragment within the wsdl:types element of the generated WSDL
		removeSchemaNamespacesFix();
		// fix to add schema namespaces to the global namespaces definitions
		addSchemaTargetNameSpace2DefnFix();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.tuscany.tools.java2wsdl.generate.WSDLGenListener#WSDLGenPhaseStarted(org.apache.tuscany.tools.java2wsdl.generate.WSDLGenEvent)
	 */
	public void WSDLGenPhaseStarted(WSDLGenEvent event) {

	}

	/**
	 * @return
	 */
	public static Map<String, String> getSchemaNamespace2GlobalPrefix() {
		return schemaNamespace2GlobalPrefix;
	}

	/**
	 * @param schemaNamespace2GlobalPrefix
	 */
	public static void setSchemaNamespace2GlobalPrefix(
			Map<String, String> schemaNamespace2GlobalPrefix) {
		schemaNamespace2GlobalPrefix = schemaNamespace2GlobalPrefix;
	}

}
