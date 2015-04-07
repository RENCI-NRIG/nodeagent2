package orca.agent.config;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import orca.agent.config.xsd.AgentConfigType;
import orca.agent.config.xsd.PluginType;
import orca.agent.config.xsd.PluginsType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ConfigParser extends ParserHelper {
	Log l;
	String from;

	AgentConfigType root = null;
	List<PluginType> plugins = null;
	
	protected static final String PKG_LIST = "orca.agent.config.xsd";
	protected static final String[] SCHEMA_LIST = { "AgentConfig.xsd" };

	public ConfigParser(String fname) {
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
			
		} catch(Exception e) {
			l.error("Unable to parse configuration file: " + e);
		} finally {
			if (s != null)
				s.close();
		}
	}

	/**
	 * Get the password in configuration file
	 * @return
	 */
	public String getPassword() {
		return root.getPassword();
	}
	
	/**
	 * Return an unmodifiable copy
	 * @return
	 */
	public List<PluginType> getPlugins() {
		return Collections.unmodifiableList(plugins);
	}
	
	public static void main(String argv[]) {
		ConfigParser cp = new ConfigParser("/Users/ibaldin/workspace-nodeagent2/node-agent2/agent/config/src/main/resources/orca/agent/config/xsd/test-config.xml");
		System.out.println(cp.getPassword());
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
