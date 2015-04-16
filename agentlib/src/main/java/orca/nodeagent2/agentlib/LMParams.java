package orca.nodeagent2.agentlib;

public class LMParams {
	ReservationId id;
	Properties props;

	public LMParams() {
		
	}
	
	public void setId(ReservationId i) {
		id = i;
	}
	
	public void setProps(Properties p) {
		props = p;
	}
	
	public LMParams(ReservationId i, Properties p) {
		id = i;
		props = p;
	}

	public Properties getProperties() {
		return props;
	}

	public ReservationId getId() {
		return id;
	}
}
