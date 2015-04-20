package orca.nodeagent2.agent.server.persistence;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;

import orca.nodeagent2.agentlib.Properties;

@Entity
public class ScheduleEntry {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	long id;
	
	private int status = 0;
	private String errorMsg = null;
	private String name;
	private String reservationId;
	private Date deadline;
	/**
	 * Properties passed in on join
	 */
	@Lob
	private Properties inProps;
	/**
	 * Properties generated by join
	 */
	@Lob
	private Properties joinProps;
	
	protected ScheduleEntry() {}
	
	public ScheduleEntry(String n, String r, Date when, Properties ip, Properties jp, int st, String em) {
		name = n;
		deadline = when;
		reservationId = r;
		inProps = ip;
		joinProps = jp;
		status = st;
		errorMsg = em;
	}
	
	public Date getDeadline() {
		return deadline;
	}
	
	public Properties getInProperties() {
		return inProps;
	}
	
	public Properties getJoinProperties() {
		return joinProps;
	}
	
	public String getReservationId() {
		return reservationId;
	}
	
	public String getName() {
		return name;
	}
	
	public int getStatus() {
		return status;
	}
	
	public void setStatus(int st) {
		status = st;
	}
	
	public String getErrorMsg() {
		return errorMsg;
	}
	
	public void setErrorMsg(String em) {
		errorMsg = em;
	}
	
	@Override
    public String toString() {
        return String.format(
                "ScheduleEntry[id=%s, Name='%s', deadline='%s', inProperties=%s, joinProperties=%s]",
                reservationId, name, deadline.toString(), inProps, joinProps);
	}
}
