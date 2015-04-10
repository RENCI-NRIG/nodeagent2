package orca.nodeagent2.agent.config;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import orca.nodeagent2.agent.config.xsd.AgentConfigType;
import orca.nodeagent2.agent.config.xsd.PluginType;
import orca.nodeagent2.agent.config.xsd.PluginsType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ConfigParser extends ParserHelper {
	Log l;
	String from;
	boolean initialized = false;

	AgentConfigType root = null;
	List<PluginType> plugins = null;

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
			plugins = pts.getPlugin();

			for(PluginType pt: plugins) {
				l.info("Detected plugin " + pt.getName() + " with jar " + pt.getJar());
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

	
	public int getDuration(PluginType t) {
		return t.getSchedulePeriod().getLength();
	}
	
	public timeUnit getDurationUnit(PluginType t) {
		if (t.getSchedulePeriod().getWeek() != null)
			return timeUnit.WEEK;
		
		if (t.getSchedulePeriod().getDay() != null)
			return timeUnit.DAY;
		
		if (t.getSchedulePeriod().getHour() != null) 
			return timeUnit.HOUR;
			
		if (t.getSchedulePeriod().getMinute() != null)
			return timeUnit.MINUTE;
		
		if (t.getSchedulePeriod().getSecond() != null)
			return timeUnit.SECOND;
		
		return timeUnit.SECOND;
	}
	
	/**
	 * Return an unmodifiable copy
	 * @return
	 */
	public List<PluginType> getPlugins() {
		return Collections.unmodifiableList(plugins);
	}

	public static void main(String argv[]) {
		try {
			ConfigParser cp = new ConfigParser("/Users/ibaldin/workspace-nodeagent2/node-agent2/agent/config/src/main/resources/orca/nodeagent2/agent/config/xsd/test-config.xml");
			System.out.println(cp.getPassword());
			
			for(PluginType t: cp.getPlugins()) {
				System.out.println("Plugin " + t.getName() + ": " + cp.getDuration(t) + " " + cp.getDurationUnit(t));
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
