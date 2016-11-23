package orca.nodeagent2.agent.server;

import java.util.List;

import orca.nodeagent2.agent.config.xsd.PluginType;

public class StatusBean {
	
	List<PluginType> plugins;
	String name;
	
	public StatusBean(String n, List<PluginType> l) {
		name = n;
		plugins = l;
	}
	
	public String getName() {
		return name;
	}
	
	public List<PluginType> getPlugins() {
		return plugins;
	}
	
	public void setPlugins(List<PluginType> l) {
		plugins = l;
	}
}
