package orca.nodeagent2.agent.config;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import orca.nodeagent2.agent.config.xsd.AgentConfigType;
import orca.nodeagent2.agent.config.xsd.PluginType;
import orca.nodeagent2.agent.config.xsd.PluginsType;
import orca.nodeagent2.agent.config.xsd.UnitChoice;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ConfigParser extends ParserHelper {
	Log l;
	String from;
	boolean initialized = false;

	AgentConfigType root = null;
	Map<String, PluginType> plugins = new HashMap<String, PluginType>();

	protected static final String PKG_LIST = "orca.nodeagent2.agent.config.xsd";
	protected static final String[] SCHEMA_LIST = { "AgentConfig.xsd" };
	protected enum timeUnit{WEEK, DAY, HOUR, MINUTE, SECOND};

	public ConfigParser(String fname) throws Exception {
		l = LogFactory.getLog("config");
		l.debug("Initializing " + this.getClass().getCanonicalName() + " with " + fname);
		File f = null;
		Scanner s = null;
		try {
			f = new File(fname);
			s = new Scanner(f);
			String text = s.useDelimiter("\\A").next();

			root = (AgentConfigType)validateXSDAndParse(text, PKG_LIST, AgentConfigType.class, SCHEMA_LIST, true, l);

			PluginsType pts = root.getPlugins();
			List<PluginType> cPlugins = pts.getPlugin();

			for(PluginType pt: cPlugins) {
				l.info("Detected plugin " + pt.getName() + " with jar " + pt.getJar());
				plugins.put(pt.getName(), pt);
			}

			initialized = true;

		} catch(Exception e) {
			throw new Exception(e);
		} finally {
			if (s != null)
				s.close();
		}
	}

	public boolean isInitialized() {
		return initialized;
	}

	/**
	 * Get the password in configuration file
	 * @return
	 */
	public String getPassword() {
		return root.getPassword();
	}

	public Integer getDuration(String name) {
		if (!plugins.containsKey(name))
			return null;
		return plugins.get(name).getSchedulePeriod().getLength();
	}
	
	public UnitChoice getDurationUnit(String name) {
		if (!plugins.containsKey(name))
			return null;
		return plugins.get(name).getSchedulePeriod().getUnit();
	}
	
	/**
	 * Return an unmodifiable copy
	 * @return
	 */
	public Map<String, PluginType> getPlugins() {
		return Collections.unmodifiableMap(plugins);
	}

	public static void main(String argv[]) {
		try {
			ConfigParser cp = new ConfigParser("/Users/ibaldin/workspace-nodeagent2/node-agent2/agent/config/src/main/resources/orca/nodeagent2/agent/config/xsd/test-config.xml");
			System.out.println(cp.getPassword());
			
			for(Map.Entry<String, PluginType> te: cp.getPlugins().entrySet()) {
				System.out.println("Plugin " + te.getValue().getName() + ": " + 
						cp.getDuration(te.getValue().getName()) + " " + 
						cp.getDurationUnit(te.getValue().getName()));
			}
		} catch (Exception e) {
			System.err.println(e);
			e.printStackTrace();
		}
		// list inside jar
		//		try {
		//			// Load the directory as a resource
		//			URL dir_url = ClassLoader.getSystemResource("orca/agent/config/xsd");
		//			// Turn the resource into a File object
		//			File dir = new File(dir_url.toURI());
		//			// List the directory
		//			String[] files = dir.list();
		//			for(String f: files) {
		//				System.out.println("\t" + f);
		//			}
		//		} catch (Exception e) {
		//			System.err.println(e);
		//			e.printStackTrace();
		//		}
	}

}
