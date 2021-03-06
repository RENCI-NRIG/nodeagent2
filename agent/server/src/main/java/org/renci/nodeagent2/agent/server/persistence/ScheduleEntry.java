package org.renci.nodeagent2.agent.server.persistence;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;

import org.renci.nodeagent2.agentlib.Properties;

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
	@Column(length = 64000)
	private Properties joinProps;
	/**
	 * Properties generated by join
	 */
	@Lob
	@Column(length = 64000)
	private Properties schedProps;
	
	protected ScheduleEntry() {}
	
	/**
	 * @param n name 
	 * @param r reservation
	 * @param when deadline
	 * @param jp join properties
	 * @param sp schedule properties
	 * @param st status 
	 * @param em error message
	 */
	public ScheduleEntry(String n, String r, Date when, Properties jp, Properties sp, int st, String em) {
		name = n;
		deadline = when;
		reservationId = r;
		joinProps = jp;
		schedProps = sp;
		status = st;
		errorMsg = em;
	}
	
	public Date getDeadline() {
		return deadline;
	}
	
	public Properties getJoinProperties() {
		return joinProps;
	}
	
	public Properties getSchedProperties() {
		return schedProps;
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
                reservationId, name, deadline.toString(), joinProps, schedProps);
	}
}
