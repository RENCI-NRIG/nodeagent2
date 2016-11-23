package orca.nodeagent2.agentlib;

/**
 * simple bean to store the plugin reservation id
 * the format is left to the plugins, they can also subclass it
 * @author ibaldin
 *
 */
public class ReservationId {
	String id;

	
	public ReservationId(String i) {
		if (i == null)
			id = "NIL";
		id = i;
	}
	
	public String getId() {
		return id;
	}
	
	public String toString() {
		return id;
	}
}
