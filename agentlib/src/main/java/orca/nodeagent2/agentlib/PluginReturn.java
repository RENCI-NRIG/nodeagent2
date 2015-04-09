package orca.nodeagent2.agentlib;

import java.util.Map;

/**
 * Each plugin call returns this bean
 * 
 * @author ibaldin
 *
 */
public class PluginReturn {
	int status = 0;
	Map<String, Object> properties;
	ReservationId resId;
	String error = null;
	
	public PluginReturn(ReservationId r, Map<String, Object> p) {
		resId = r;
		properties = p;
	}
	
	public PluginReturn(int s, String err, ReservationId r, Map<String, Object> p) {
		resId = r;
		status = s;
		error = err;
		properties = p;
	}
	
	public int getStatus() {
		return status;
	}
	
	public Map<String, Object> getProperties() {
		return properties;
	}
	
	public ReservationId getResId() {
		return resId;
	}
}
