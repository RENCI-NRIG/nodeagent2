package orca.agent.config;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import orca.agent.config.xsd.AgentConfigType;
import orca.agent.config.xsd.PluginType;
import orca.agent.config.xsd.PluginsType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigParser extends ParserHelper {
	Logger l;
	String from;

	AgentConfigType root = null;
	List<PluginType> plugins = null;
	
	protected static final String PKG_LIST = "orca.agent.config.xsd";
	protected static final String[] SCHEMA_LIST = { "AgentConfig.xsd" };

	public ConfigParser(Logger log, String fname) {
		l = log;
		l.debug("Initializing " + this.getClass().getCanonicalName());
		File f = null;
		Scanner s = null;
		try {
			f = new File(fname);
			s = new Scanner(f);
			String text = s.useDelimiter("\\A").next();
			
			root = (AgentConfigType)validateXSDAndParse(text, PKG_LIST, AgentConfigType.class, SCHEMA_LIST, true, log);
			
			PluginsType pts = root.getPlugins();
			plugins = pts.getPlugin();
			
		} catch(Exception e) {
			
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
	 * Return a copy
	 * @return
	 */
	public List<PluginType> getPlugins() {
		return new ArrayList<PluginType>(plugins);
	}
	
	public static void main(String argv[]) {
		try {
			org.apache.log4j.BasicConfigurator.configure();
			Logger log = LoggerFactory.getLogger(ConfigParser.class);
			ClassLoader classloader = Thread.currentThread().getContextClassLoader();
			InputStream is = classloader.getResourceAsStream("orca/agent/config/xsd/test-config.xml");
			Scanner s = new Scanner(is);
			String text = s.useDelimiter("\\A").next();
			s.close();
			is.close();

			AgentConfigType agent = (AgentConfigType)validateXSDAndParse(text, PKG_LIST, AgentConfigType.class, SCHEMA_LIST, true, log);

			System.out.println("Password: " + agent.getPassword());
			PluginsType pts = agent.getPlugins();

			List<PluginType> lugins = pts.getPlugin();

			for (PluginType p: lugins) {
				System.out.println(p.getName() + ": " + p.getJar());
			}
		} catch (Exception e) {
			System.err.println(e);
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
