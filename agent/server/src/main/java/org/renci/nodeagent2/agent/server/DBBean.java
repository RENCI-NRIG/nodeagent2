package orca.nodeagent2.agent.server;

import java.util.List;

import orca.nodeagent2.agent.server.persistence.ScheduleEntry;

public class DBBean {

	List<ScheduleEntry> list;
	
	public DBBean(List<ScheduleEntry> e) {
		list = e;
	}
	
	public List<ScheduleEntry> getList() {
		return list;
	}
}
