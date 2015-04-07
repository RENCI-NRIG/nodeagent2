package orca.agent.config;

import java.util.List;

import orca.agent.config.xsd.PluginType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Config {

	Log l;
	ConfigParser cp;
	static String fname = null;
	
	public static Config instance = null;
	
	private Config() throws Exception {
		l = LogFactory.getLog("config");		
		if (fname == null) {
			throw new Exception("Configuration filename not specified");
		}
		cp = new ConfigParser(fname);
	}
	
	public static Config getInstance() throws Exception {
		if (instance == null)
			throw new Exception("Config not initialized");
		return instance;
	}
	
	public static void initialize(String c) throws Exception {
		fname = c;
		instance = new Config();
	}
	
	public String getPassword() {
		return cp.getPassword();
	}
	
	public List<PluginType> getPlugins() {
		return cp.getPlugins();
	}
}
