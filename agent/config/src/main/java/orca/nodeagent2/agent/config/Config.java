package orca.nodeagent2.agent.config;

import java.util.List;

import orca.nodeagent2.agent.config.xsd.PluginType;
import orca.nodeagent2.agent.config.xsd.UnitChoice;

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
		
		if (!cp.isInitialized()) 
			throw new Exception("Config parser was not initialized");
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
	
	public int getDuration(PluginType t) {
		return cp.getDuration(t) ;
	}
	
	public UnitChoice getDurationUnit(PluginType t) {
		return cp.getDurationUnit(t);
	}

}
