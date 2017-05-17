package org.renci.nodeagent2.agent.config;

import org.apache.commons.logging.Log;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

/***
 * Custom resolver for schema files contained in different packages.
 * Used for XML/XSD validation
 * @author ibaldin
 *
 */
public class SchemaFileResolver implements LSResourceResolver {
        private DOMImplementationLS dls;
        private ClassLoader cll;
        private String pkgPath;
        private Log l;

        /***
         * Needs a class loader of the class to which the resource belongs
         * @param cll
         * @throws IllegalAccessException
         * @throws ClassNotFoundException
         * @throws InstantiationException
         */
        SchemaFileResolver(ClassLoader cll, String pkgPath, Log log) throws IllegalAccessException, ClassNotFoundException, InstantiationException {
                this.cll = cll;
                this.pkgPath = pkgPath;
                this.l = log;
                DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
                dls = (DOMImplementationLS)registry.getDOMImplementation("LS");
        }

        @Override
        public LSInput resolveResource(String type, String ns, String pubId,
                        String sysId, String baseURI) {
                l.debug("Trying to resolve " + 
                                type + ", " + ns + ", " + pubId + ", " + sysId + ", " + baseURI + " in package " + pkgPath);
                // from http://www.java.net/forum/topic/java-web-services-and-xml/jaxp/
                // schemafactory-problem-compiling-multiple-schemas-same-na-0

                LSInput lsi = dls.createLSInput();
                lsi.setByteStream(cll.getResourceAsStream(pkgPath.replace('.', '/') + "/" + sysId));
                lsi.setSystemId(sysId);
                return lsi;
        }
}