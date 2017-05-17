package org.renci.nodeagent2.agentlib;

import java.util.Date;


/**
 * This is the interface all plugins must conform to. The dynamic loader does its best to check
 * the compliance of loaded plugins to the interface. NA2 uses child first/parent last classloader
 * so any jar dependencies are loaded from the plugin classpath first.
 * 
 * Plugins are expected to be largely stateless, leaving it to NA core to save state for recovery
 * purposes.
 * @author ibaldin
 *
 */
public interface Plugin {
	
	/**
	 * Initialize the behavior of the plugin based on a combination of config file and configuration properties
	 * (either can be null)
	 * @param config - config file name
	 * @param configProperties - properties from config file
	 * @throws PluginException
	 */
	public void initialize(String config, Properties configProperties) throws PluginException;
	
	/**
	 * Provision a new resource with properties specified in the map.
	 * @param until - until when, configured in the plugin 
	 * @param callerPropeties - provided by the caller (ORCA)
	 * @return properties, status and reservation id 
	 */
	public PluginReturn join(Date until, Properties callerPropeties) throws PluginException;
	
	/**
	 * Close the reservation. The core provides storage for properties for recovery
	 * @param resId
	 * @param callerProperties - provided by the caller (ORCA)
	 * @param callerProperties - provided from the schedule (otherwise null)
	 * @return properties, status and reservation id
	 */
	public PluginReturn leave(ReservationId resId, Properties callerProperties, Properties schedProperties) throws PluginException;
	
	/**
	 * Modify the reservation
	 * @param resId
	 * @param callerProperties - provided by the caller
	 * @param schedProperties  - provided from the schedule (otherwise null)
	 * @return properties, status and reservation id
	 */
	public PluginReturn modify(ReservationId resId, Properties callerProperties, Properties schedProperties) throws PluginException;
	
	/**
	 * Renew the reservation. Properties are provided from the schedule.
	 * @param resId
	 * @param until - the date
	 * @param inProperties - properties passed in by the caller to join 
	 * @param joinProperties - properties returned by the schedule (or null)
	 * @return properties, status and reservation id
	 */
	public PluginReturn renew(ReservationId resId, Date until, Properties joinProperties, 
			Properties schedProperties) throws PluginException;

	/**
	 * Return a status for the last operation on the plugin's reservation. Primarily for checking 
	 * on renew. Properties are provided from the schedule, otherwise null is supplied.
	 * @param resId
	 * @param schedProperties - properties returned by the schedule (or null)
	 * @return properties, status and reservation id
	 */
	public PluginReturn status(ReservationId resId, Properties schedProperties) throws PluginException;
}
