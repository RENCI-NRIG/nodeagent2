package orca.nodeagent2.agentlib;


/**
 * Each plugin call returns this bean
 * 
 * @author ibaldin
 *
 */
public class PluginReturn {
	final int status;
	final Properties properties;
	final ReservationId resId;
	final String errorMsg;
	
	/**
	 * Error-free return
	 * @param r - reservation id
	 * @param p - properties
	 */
	public PluginReturn(ReservationId r, Properties p) {
		resId = r;
		properties = p;
		errorMsg = null;
		status = 0;
	}
	
	/**
	 * Return with error (see PluginErrorCodes)
	 * @param s - status
	 * @param err - error
	 * @param r - reservation id
	 * @param p - properties
	 */
	public PluginReturn(int s, String err, ReservationId r, Properties p) {
		resId = r;
		status = s;
		errorMsg = err;
		properties = p;
	}
	
	public int getStatus() {
		return status;
	}
	
	public Properties getProperties() {
		return properties;
	}
	
	public ReservationId getResId() {
		return resId;
	}
	
	public String getErrorMsg() {
		return errorMsg;
	}
	
	@Override
	public String toString() {
		return "Reservation: " + resId + " status [" + status + "], error message " + errorMsg + " with properties " + properties;
	}
}
