package orca.nodeagent2.agentlib;

import java.util.Date;


/**
 * This is the interface all plugins must conform to. The dynamic loader does its best to check
 * the compliance of loaded plugins to the interface. Access to classloader is provided to reduce
 * the need to link new classes and enable reusing classes available in the core.
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
	 * @parm config - config file name
	 * @param inProperties - config properties
	 * @param cl - main class loader
	 * @throws PluginException
	 */
	public void initialize(String config, Properties inProperties, ClassLoader cl) throws PluginException;
	
	/**
	 * Provision a new resource with properties specified in the map.
	 * @param until - until when, configured in the plugin 
	 * @param inPropeties
	 * @return properties, status and reservation id 
	 */
	public PluginReturn join(Date until, Properties inPropeties) throws PluginException;
	
	/**
	 * Close the reservation. The core provides storage for properties for recovery
	 * @param resId
	 * @return
	 */
	public PluginReturn leave(ReservationId resId, Properties inProperties) throws PluginException;
	
	/**
	 * Modify the reservation
	 * @param resId
	 * @param inProperties
	 * @return
	 */
	public PluginReturn modify(ReservationId resId, Properties inProperties) throws PluginException;
	
	/**
	 * Renew the reservation
	 * @param resId
	 * @param until - the date
	 * @param inProperties - properties passed in originally
	 * @param joinProperties - properties returned by join operation
	 * @return
	 */
	public PluginReturn renew(ReservationId resId, Date until, Properties inProperties, Properties joinProperties) throws PluginException;

	/**
	 * Return a status message for this plugin - free format
	 * @return
	 */
	public String status() throws PluginException;
}
