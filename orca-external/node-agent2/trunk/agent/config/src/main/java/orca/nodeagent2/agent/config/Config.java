package orca.nodeagent2.agent.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
	
	public Map<String, PluginType> getPlugins() {
		return cp.getPlugins();
	}
	
	public List<PluginType> getPluginsAsList() {
		List<PluginType> ret = new ArrayList<PluginType>();
		ret.addAll(cp.getPlugins().values());
		
		return ret;
	}
	
	public int getDuration(String name) {
		return cp.getDuration(name) ;
	}
	
	public UnitChoice getDurationUnit(String name) {
		return cp.getDurationUnit(name);
	}

	public PluginType getPlugin(String name) {
		return cp.getPlugins().get(name);
	}
	
}
