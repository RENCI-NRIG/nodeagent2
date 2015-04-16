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
	
	private String name;
	private Date deadline;
	@Lob
	private Properties props;
	
	protected ScheduleEntry() {}
	
	public ScheduleEntry(String n, Date when, Properties p) {
		name = n;
		deadline = when;
		props = p;
	}
	
	public Date getDeadline() {
		return deadline;
	}
	
	public Properties getProperties() {
		return props;
	}
	
	@Override
    public String toString() {
        return String.format(
                "ScheduleEntry[id=%d, Name='%s', deadline='%s', properties=%s]",
                id, name, deadline.toString(), props);
	}
}
