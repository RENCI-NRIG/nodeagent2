package orca.nodeagent2.agentlib;


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
	 * Provision a new resource with properties specified in the map. 
	 * @param inPropeties
	 * @param cl
	 * @return properties, status and reservation id 
	 */
	public PluginReturn join(Properties inPropeties, ClassLoader cl) throws PluginException;
	
	/**
	 * Close the reservation. The core provides storage for properties for recovery
	 * @param resId
	 * @param cl
	 * @return
	 */
	public PluginReturn leave(ReservationId resId, Properties inProperties, ClassLoader cl) throws PluginException;
	
	/**
	 * Modify the reservation
	 * @param resId
	 * @param inProperties
	 * @param cl
	 * @return
	 */
	public PluginReturn modify(ReservationId resId, Properties inProperties, ClassLoader cl) throws PluginException;
	
	/**
	 * Renew the reservation
	 * @param resId
	 * @param inProperties
	 * @param cl
	 * @return
	 */
	public PluginReturn renew(ReservationId resId, Properties inProperties, ClassLoader cl) throws PluginException;

	/**
	 * Return a status message for this plugin - free format
	 * @param cl
	 * @return
	 */
	public String status(ClassLoader cl) throws PluginException;
}
