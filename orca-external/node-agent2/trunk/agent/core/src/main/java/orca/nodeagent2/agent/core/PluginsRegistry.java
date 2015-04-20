package orca.nodeagent2.agent.core;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import orca.nodeagent2.agent.config.Config;
import orca.nodeagent2.agent.config.xsd.PluginType;
import orca.nodeagent2.agent.config.xsd.PropertiesType;
import orca.nodeagent2.agent.config.xsd.PropertyType;
import orca.nodeagent2.agentlib.PluginException;
import orca.nodeagent2.agentlib.PluginReturn;
import orca.nodeagent2.agentlib.Properties;
import orca.nodeagent2.agentlib.ReservationId;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Maintains runtime info for all plugins
 * @author ibaldin
 *
 */
public class PluginsRegistry {
	protected Map<String, PluginJarHandler> plugins = new HashMap<String, PluginJarHandler>();
	protected Map<String, PluginType> pluginConfigs = new HashMap<String, PluginType>();
	
	protected Log l = LogFactory.getLog("pluginsRegistry");
	
	private static PluginsRegistry instance = new PluginsRegistry();
	
	public static PluginsRegistry getInstance() {
		return instance;
	}
	
	private PluginsRegistry() {
		
	}
	
	/**
	 * Initialize plugins registry based on information in configuration (add all plugins)
	 * @throws Exception
	 */
	public void initialize() throws Exception {
		// initialize plugins
		for(PluginType pt: Config.getInstance().getPluginsAsList()) {
			l.info("Initializing plugin " + pt.getName() + " with period " + 
		Config.getInstance().getSchedulePeriod(pt.getName()) + " " + 
		Config.getInstance().getSchedulePeriodUnit(pt.getName()).value());
			PluginsRegistry.getInstance().addPlugin(pt);
		}
	}
	
	/**
	 * Add a new plugin to registry
	 * @param pt
	 * @throws Exception
	 */
	public void addPlugin(PluginType pt) throws Exception {
		PluginJarHandler pjh = new PluginJarHandler(pt.getJar(), pt.getMainClass());
		// collect config properties
		PropertiesType pts = pt.getProperties();
		Properties pluginConfigProps = new Properties();
		for(PropertyType prop: pts.getProperty()) {
			pluginConfigProps.put(prop.getName(), prop.getValue());
		}
		pjh.initialize(pt.getConfig(), pluginConfigProps);
		plugins.put(pt.getName(), pjh);
		pluginConfigs.put(pt.getName(), pt);
	}
	
	public PluginReturn join(String pluginName, Date until, Properties inProperties) throws Exception, PluginException {
		if (!plugins.containsKey(pluginName))
			throw new Exception("Error in join call to plugin " + pluginName + ": plugin not found");
		
		return plugins.get(pluginName).join(until, inProperties);
	}
	
	public PluginReturn leave(String pluginName, ReservationId resId, Properties inProperties) throws Exception, PluginException {
		if (!plugins.containsKey(pluginName))
			throw new Exception("Error in leave call to plugin " + pluginName + ": plugin not found");
		
		return plugins.get(pluginName).leave(resId, inProperties);
	}
	
	public PluginReturn modify(String pluginName, ReservationId resId, Properties inProperties) throws Exception, PluginException {
		if (!plugins.containsKey(pluginName))
			throw new Exception("Error in modify call to plugin " + pluginName + ": plugin not found");
		
		return plugins.get(pluginName).modify(resId, inProperties);
	}
	
	public PluginReturn renew(String pluginName, ReservationId resId, Date until, Properties inProperties, Properties joinProperties) throws Exception, PluginException {
		if (!plugins.containsKey(pluginName))
			throw new Exception("Error in renew call to plugin " + pluginName + ": plugin not found");
		
		return plugins.get(pluginName).renew(resId, until, inProperties, joinProperties);
	}
	
	public PluginReturn status(String pluginName, ReservationId resId) throws Exception, PluginException {
		if (!plugins.containsKey(pluginName))
			throw new Exception("Error in status call to plugin " + pluginName + ": plugin not found");
		
		return plugins.get(pluginName).status(resId);
	}
	
	public String getDescription(String pluginName) throws Exception {
		if (!pluginConfigs.containsKey(pluginName))
			throw new Exception("Cannot get description for plugin " + pluginName + ": plugin not found");
		return pluginConfigs.get(pluginName).getDescription();
	}
}
