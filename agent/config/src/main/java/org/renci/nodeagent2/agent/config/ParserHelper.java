package org.renci.nodeagent2.agent.config;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.StringReader;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.logging.Log;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class ParserHelper {
	
	/**
	 * @param from - string containing XML to validate
	 * @param startPkg - colon-separated list of pacakage paths. First one must be the root package.
	 * @param startClass - root class containing RSpec. Typically RSpecContents
	 * @param schemas - list of initial schema files (root and extensions)
	 * @param l
	 * @return unmarshalled JAXB object
	 * @throws Exception
	 */
	protected static Object validateXSDAndParse(String from, String startPkg, Class<?> startClass, String[] schemas, boolean strict, Log l)
			throws Exception {

		// stronger alternative validation
		if (strict)
			validateXSD(from, startPkg, startClass, schemas, l);

		Object ret = null;

		try {
			JAXBContext jbcontext = JAXBContext.newInstance(startPkg);

			Unmarshaller um = jbcontext.createUnmarshaller();

			SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

			// use custom resolver to help locate schema files
			String rootPkg = startPkg.split(":")[0];
			sf.setResourceResolver(new SchemaFileResolver(startClass.getClassLoader(),
					rootPkg, l));
			// load relevant schemas
			Source [] schemaSources = new Source[schemas.length];
			int i=0;
			for (String schemaName: schemas) {
				//
				// see http://forum.springsource.org/showthread.php?t=26464 to see how to substitute
				// which SchemaFactory is used (xerces or SUNs). Java 5's SchemaFactory is faulty.
				// Also requires maven dependency update (to include xerces)
				//
				schemaSources[i++] = new StreamSource(
						startClass.getClassLoader().getResourceAsStream(
								rootPkg.replace('.', File.separatorChar) + File.separator + schemaName));
			}                       
			Schema sch = sf.newSchema(schemaSources);
			um.setSchema(sch);

			// rspec element is not marked as root element, so have to do this see
			// http://jaxb.java.net/guide/_XmlRootElement_and_unmarshalling.html
			JAXBElement<?> root = um.unmarshal(new StreamSource(
					new StringReader(from)), startClass);

			ret = root.getValue();
		} catch (Exception e) {
			throw new Exception ("ERROR: unable to parse document: " + e.toString());
		} finally {
			//l.info("Resetting classloader");
			//Thread.currentThread().setContextClassLoader( savedClassLoader );
		}
		return ret;
	}

	public static void validateXSD(String from, String startPkg, Class<?> startClass, String[] schemas, Log l) {
		// parse an XML document into a DOM tree

		for(int i = 0; i < schemas.length; i++)
			l.debug("Using schema " + schemas[i]);

		DocumentBuilderFactory parserFactory = DocumentBuilderFactory.newInstance();
		parserFactory.setNamespaceAware(true);

		try {
			DocumentBuilder parser = parserFactory.newDocumentBuilder();
			Document document = parser.parse(new ByteArrayInputStream(from.getBytes("UTF-8")));

			// create a SchemaFactory capable of understanding WXS schemas
			SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

			// load a WXS schema, represented by a Schema instance
			int i=0;
			String rootPkg = startPkg.split(":")[0];
			Source [] schemaSources = new Source[schemas.length];
			for (String schemaName: schemas) {
				//
				// see http://forum.springsource.org/showthread.php?t=26464 to see how to substitute
				// which SchemaFactory is used (xerces or SUNs). Java 5's SchemaFactory is faulty.
				// Also requires maven dependency update (to include xerces)
				//
				schemaSources[i++] = new StreamSource(
						startClass.getClassLoader().getResourceAsStream(
								rootPkg.replace('.', File.separatorChar) + File.separator + schemaName));
			}                       

			Schema schema = factory.newSchema(schemaSources);

			// create a Validator instance, which can be used to validate an instance document
			Validator validator = schema.newValidator();

			// validate the DOM tree
			validator.validate(new DOMSource(document));
		} catch (SAXException e) {
			// instance document is invalid!
			l.debug("Validation exception here: " + e);
		} catch (Exception e) {
			l.debug("Exception there: " + e);
		}
	}
}
