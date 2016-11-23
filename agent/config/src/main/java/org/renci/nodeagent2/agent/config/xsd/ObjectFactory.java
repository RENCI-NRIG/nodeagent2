//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.11.23 at 10:46:01 AM EST 
//


package org.renci.nodeagent2.agent.config.xsd;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.renci.nodeagent2.agent.config.xsd package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _AgentConfig_QNAME = new QName("http://geni-orca.renci.org/xml/node-agent2/AgentConfig", "agentConfig");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.renci.nodeagent2.agent.config.xsd
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link AgentConfigType }
     * 
     */
    public AgentConfigType createAgentConfigType() {
        return new AgentConfigType();
    }

    /**
     * Create an instance of {@link PropertyType }
     * 
     */
    public PropertyType createPropertyType() {
        return new PropertyType();
    }

    /**
     * Create an instance of {@link PropertiesType }
     * 
     */
    public PropertiesType createPropertiesType() {
        return new PropertiesType();
    }

    /**
     * Create an instance of {@link PluginType }
     * 
     */
    public PluginType createPluginType() {
        return new PluginType();
    }

    /**
     * Create an instance of {@link PluginsType }
     * 
     */
    public PluginsType createPluginsType() {
        return new PluginsType();
    }

    /**
     * Create an instance of {@link TimeUnit }
     * 
     */
    public TimeUnit createTimeUnit() {
        return new TimeUnit();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AgentConfigType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://geni-orca.renci.org/xml/node-agent2/AgentConfig", name = "agentConfig")
    public JAXBElement<AgentConfigType> createAgentConfig(AgentConfigType value) {
        return new JAXBElement<AgentConfigType>(_AgentConfig_QNAME, AgentConfigType.class, null, value);
    }

}
