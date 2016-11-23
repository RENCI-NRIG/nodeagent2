package orca.nodeagent2.agent.server.persistence;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import javax.persistence.PersistenceException;

import orca.nodeagent2.agent.config.Config;
import orca.nodeagent2.agent.server.renew.RenewExpiringThread;
import orca.nodeagent2.agentlib.Properties;
import orca.nodeagent2.agentlib.ReservationId;

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

	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	private ScheduledFuture<?> tickHandle = null;

	@Autowired
	IScheduleRepository repository;

	Log l = LogFactory.getLog("schedulePersistence");

	public SchedulePersistence() {
		// start periodic thread
		try {
			l.info("Starting periodic tick thread every " + Config.getInstance().getTickLength() + " " + Config.getInstance().getTickTimeUnit());
			tickHandle = scheduler.scheduleAtFixedRate(new RenewExpiringThread(this), 1, 
					Config.getInstance().getTickLength(), 
					Config.getInstance().getTickTimeUnit());
		} catch (Exception e) {
			l.error("Unable to start periodic expiry check thread! : " + e);
		}
	}

	/**
	 * Save the renew deadline for a plugin with properties. Subtract the advance tick
	 * prior to saving
	 * @param name
	 * @param what the deadline is set to 
	 * @param deadline
	 * @param props
	 */
	public synchronized void saveRenewDeadline(String name, Date future, ReservationId r, 
			Properties inProps, Properties joinProps, 
			int status, String errorMsg) throws Exception {

		// see if this plugin has a schedule period
		if (Config.getInstance().getSchedulePeriod(name) <= 0) {
			l.info("Plugin " + name + " has schedule period of 0, skipping saving deadline for " + r);
			return;
		}

		// get now time
		Calendar cal = Calendar.getInstance();
		cal.setTime(future);
		// subtract the number of ticks
		cal.add(Config.getInstance().getTickCalendarUnit(), -1 * Config.getInstance().getTickLength() * Config.getInstance().getAdvanceTicks(name));
		
		// subtract one additional second to avoid schedule sliding forward every time
		// config won't allow a tick of one sec.
		cal.add(Calendar.SECOND, -1);
		
		l.info("Saving  renew deadline for reservation " + r + " plugin " + name + " at " + cal.getTime());
		l.debug("inProperties: " + inProps);
		l.debug("joinProperties: " + joinProps);

		repository.save(new ScheduleEntry(name, r.getId(), cal.getTime(), inProps, joinProps, status, errorMsg));
	}

	/**
	 * Find all expired entries in db
	 * @return
	 */
	public synchronized List<ScheduleEntry> findExpiredEntries() {
		l.info("Searching for expired entries in db");
		return repository.findByDeadlineBefore(new Date());
	}

	/**
	 * Return all entries for a particular plugin
	 * @param name
	 * @return
	 * @throws PersistenceException
	 */
	public synchronized List<ScheduleEntry> findEntries(String name) throws PersistenceException {
		l.info("Searching for entries for plugin " + name);
		List<ScheduleEntry> matches = repository.findByName(name);
		return matches;
	}

	public synchronized ScheduleEntry findEntry(String name, String resId) {
		l.info("Searching for entries for " + name + " and reservation " + resId);
		List<ScheduleEntry> matches = repository.findByNameAndReservationId(name, resId);
		if (matches.size() > 0)
			return matches.get(0);
		return null;
	}

	public synchronized ScheduleEntry removeEntry(String name, ReservationId resId) {
		List<ScheduleEntry> matches = repository.findByNameAndReservationId(name, resId.getId());
		if (matches.size() > 0) {
			removeEntries(matches);
			return matches.get(0);
		}
		return null;
	}

	/**
	 * Removes the currently stored deadlines for all reservations for a  plugin (if present)
	 * @param name
	 * @throws PersistenceException
	 */
	public synchronized void removeEntries(String name) throws PersistenceException {
		l.info("Removing deadline(s) for plugin " + name);
		List<ScheduleEntry> matches = repository.findByName(name);
		for(ScheduleEntry e: matches) {
			l.debug("Removing entry " + e);
			repository.delete(e);
		}
	}

	/**
	 * Remove specified entries
	 * @param ee
	 */
	public synchronized void removeEntries(List<ScheduleEntry> ee) {
		for(ScheduleEntry e: ee) {
			l.debug("Removing entry " + e);
			repository.delete(e);
		}
	}

	public synchronized List<ScheduleEntry> getAll() throws PersistenceException {
		l.info("Getting a list from DB");
		Iterable<ScheduleEntry> e = repository.findAll();
		Iterator<ScheduleEntry> ei = e.iterator();
		List<ScheduleEntry> entries = new ArrayList<ScheduleEntry>();
		while (ei.hasNext()) {
			entries.add(ei.next());
		}
		return entries;
	}

}
