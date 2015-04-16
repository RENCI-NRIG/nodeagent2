package orca.nodeagent2.agent.server.persistence;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import orca.nodeagent2.agentlib.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * A thread safe interface to schedule persistence
 * @author ibaldin
 *
 */
@Repository
public class SchedulePersistence {

	@Autowired
	IScheduleRepository repository;
	
	Log l = LogFactory.getLog("schedulePersistence");
	
	/**
	 * Save the renew deadline for a plugin with properties
	 * @param name
	 * @param deadline
	 * @param props
	 */
	public synchronized void saveDeadline(String name, Date deadline, Properties props) {
		l.debug("Saving plugin " + name + " deadline " + deadline);
		repository.save(new ScheduleEntry(name, deadline, props));
	}
	
	/**
	 * Find a deadline for a plugin
	 * @param name
	 * @return
	 * @throws PersistenceException
	 */
	public synchronized Date findDeadline(String name) throws PersistenceException {
		l.debug("Searching for deadline for plugin " + name);
		List<ScheduleEntry> matches = repository.findByName(name);
		if (matches.size() == 0) {
			l.warn("No matches found for plugin " + name);
			throw new PersistenceException("No matches found for plugin " + name);
		}
		if (matches.size() > 1)
			l.warn("Schedule repository returned more than one result for " + name);
		return matches.get(0).getDeadline();

	}
	
	/**
	 * Removes the currently stored deadline for a plugin (if present)
	 * @param name
	 * @throws PersistenceException
	 */
	public synchronized void removeDeadline(String name) throws PersistenceException {
		l.debug("Removing deadline(s) for plugin " + name);
		List<ScheduleEntry> matches = repository.findByName(name);
		for(ScheduleEntry e: matches) {
			l.debug("Removing entry " + e);
			repository.delete(e);
		}
	}
	
	public synchronized List<ScheduleEntry> getAll() throws PersistenceException {
		l.debug("Getting a list from DB");
		Iterable<ScheduleEntry> e = repository.findAll();
		Iterator<ScheduleEntry> ei = e.iterator();
		List<ScheduleEntry> entries = new ArrayList<ScheduleEntry>();
		while (ei.hasNext()) {
			entries.add(ei.next());
		}
		return entries;
	}
}
