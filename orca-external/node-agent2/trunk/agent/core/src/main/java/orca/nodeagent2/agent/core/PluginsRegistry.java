package orca.nodeagent2.agent.core;

import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Semaphore;

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

	public static class UniqueId {
		String name;
		ReservationId rid;
		
		public UniqueId(String n, ReservationId r) {
			name = n; rid = r;
		}
		
		public int compare(UniqueId o) {
			if (name.compareTo(o.name) != 0)
				return name.compareTo(o.name);
			return rid.getId().compareTo(o.rid.getId());
		}
		
		@Override
		public boolean equals(Object o) {
			if (!(o instanceof UniqueId))
				throw new ClassCastException("Unable to compare object of type " + o.getClass().getName() + " to UniqueId");
			return compare((UniqueId)o) == 0;
		}
	}
	
	public static class UniqueIdComparator implements Comparator<UniqueId> {

		public int compare(UniqueId o1, UniqueId o2) {
			return o1.compare(o2);
		}
	}
	
	// lookup semaphores here to make sure operations on the same reservations aren't overlapped
	protected TreeMap<UniqueId, Semaphore> semaphores = new TreeMap<UniqueId, Semaphore>(new UniqueIdComparator());
	
	private static PluginsRegistry instance = new PluginsRegistry();
	
	public static PluginsRegistry getInstance() {
		return instance;
	}
	
	private PluginsRegistry() {
		
	}
	
	/**
	 * Retrieve or create a semaphore for a given endpoint and reservation id
	 * @param name
	 * @param rid
	 * @return
	 */
	private Semaphore getSemaphore(String name, ReservationId rid) {
		UniqueId uid = new UniqueId(name, rid);
		
		if (semaphores.containsKey(uid))
			return semaphores.get(uid);
		
		Semaphore sem = new Semaphore(1, true);
		
		semaphores.put(uid, sem);
		
		return sem;
	}
	
	private void acquire(String name, ReservationId rid) throws InterruptedException {
		l.debug("Acquiring semaphore on " + name + "/" + rid);
		getSemaphore(name, rid).acquire();
	}
	
	private void release(String name, ReservationId rid) {
		l.debug("Releasing semaphore on " + name + "/" + rid);
		getSemaphore(name, rid).release();
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
	
	public PluginReturn join(String pluginName, Date until, Properties callerProperties) throws Exception, PluginException {
		if (!plugins.containsKey(pluginName))
			throw new Exception("Error in join call to plugin " + pluginName + ": plugin not found");
		return plugins.get(pluginName).join(until, callerProperties);
	}
	
	public PluginReturn leave(String pluginName, ReservationId resId, Properties callerProperties, Properties schedProperties) throws Exception, PluginException {
		if (!plugins.containsKey(pluginName))
			throw new Exception("Error in leave call to plugin " + pluginName + ": plugin not found");
		try {
			acquire(pluginName, resId);
			return plugins.get(pluginName).leave(resId, callerProperties, schedProperties);
		} catch (Exception e) {
			throw e;
		} finally {
			release(pluginName, resId);
		}
	}
	
	public PluginReturn modify(String pluginName, ReservationId resId, Properties callerProperties, Properties schedProperties) throws Exception, PluginException {
		if (!plugins.containsKey(pluginName))
			throw new Exception("Error in modify call to plugin " + pluginName + ": plugin not found");
		
		try {
			acquire(pluginName, resId);
			return plugins.get(pluginName).modify(resId, callerProperties, schedProperties);
		} catch (Exception e) {
			throw e;
		} finally {
			release(pluginName, resId);
		}
	}
	
	public PluginReturn renew(String pluginName, ReservationId resId, Date until, Properties joinProperties, Properties schedProperties) throws Exception, PluginException {
		if (!plugins.containsKey(pluginName))
			throw new Exception("Error in renew call to plugin " + pluginName + ": plugin not found");
		try {
			acquire(pluginName, resId);
			return plugins.get(pluginName).renew(resId, until, joinProperties, schedProperties);
		} catch (Exception e) {
			throw e;
		} finally {
			release(pluginName, resId);
		}
	}
	
	public PluginReturn status(String pluginName, ReservationId resId, Properties schedProperties) throws Exception, PluginException {
		if (!plugins.containsKey(pluginName))
			throw new Exception("Error in status call to plugin " + pluginName + ": plugin not found");
		try {
			acquire(pluginName, resId);
			return plugins.get(pluginName).status(resId, schedProperties);
		}catch (Exception e) {
			throw e;
		} finally {
			release(pluginName, resId);
		}
	}
	
	public String getDescription(String pluginName) throws Exception {
		if (!pluginConfigs.containsKey(pluginName))
			throw new Exception("Cannot get description for plugin " + pluginName + ": plugin not found");
		return pluginConfigs.get(pluginName).getDescription();
	}
}
