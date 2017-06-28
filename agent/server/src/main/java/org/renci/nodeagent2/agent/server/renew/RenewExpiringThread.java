package org.renci.nodeagent2.agent.server.renew;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.renci.nodeagent2.agent.config.Config;
import org.renci.nodeagent2.agent.core.PluginsRegistry;
import org.renci.nodeagent2.agent.server.persistence.ScheduleEntry;
import org.renci.nodeagent2.agent.server.persistence.SchedulePersistence;
import org.renci.nodeagent2.agentlib.PluginErrorCodes;
import org.renci.nodeagent2.agentlib.PluginException;
import org.renci.nodeagent2.agentlib.PluginReturn;
import org.renci.nodeagent2.agentlib.ReservationId;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RenewExpiringThread implements Runnable {

	private final Log l = LogFactory.getLog(this.getClass().getName());

	private final SchedulePersistence sp;
	
	private static final Set<String> inRenewal = new HashSet<String>();

	private static final int MAX_RENEW_SERVICE_THREADS = 10;
	static ExecutorService renewThreadFactory = Executors.newFixedThreadPool(MAX_RENEW_SERVICE_THREADS);

	public RenewExpiringThread(SchedulePersistence sp) {
		this.sp = sp;
	}

	/**
	 * Check if reservation id is in renewal
	 * @param id
	 * @return
	 */
	private static boolean isInRenewal(String id) {
		if (id == null)
			return false;
		
		synchronized(inRenewal) {
			if (inRenewal.contains(id))
				return true;
			return false;
		}
	}
	
	private static void startRenewal(String id) {
		if (id == null)
			return;
		synchronized(inRenewal) {
			inRenewal.add(id);
		}
	}
	
	private static void finishRenewal(String id) {
		if (id == null)
			return;
		synchronized(inRenewal) {
			inRenewal.remove(id);
		}
	}
	
	@Override
	public void run() {
		Thread.currentThread().setName("renewPeriodicThread");
		List<ScheduleEntry> expired = sp.findExpiredEntries();
		l.info("There are " + expired.size() + " expired entries");
		for(final ScheduleEntry e: expired) {
			if (isInRenewal(e.getReservationId())) {
				l.info("Reservation " + e.getReservationId() + " of " + e.getName() + " is currently being renewed, ignoring");
				continue;
			}
			startRenewal(e.getReservationId());
			l.info("Spawning a thread to renew " + e.getName() + " reservation " + e.getReservationId());
			renewThreadFactory.submit(new Thread() {
				public void run() {
					ReservationId resId = new ReservationId(e.getReservationId());
					try {
						Thread.currentThread().setName("renew" + e.getName() + "/" + e.getReservationId() + "Thread");
						
						// acquire the lock for this reservation
						PluginsRegistry.getInstance().acquire(e.getName(), resId);
						
						// compute new expiry time as now + plugin period
						Calendar future = Calendar.getInstance();
						future.add(Config.getInstance().getSchedulePeriodCalendarUnit(e.getName()), 
								Config.getInstance().getSchedulePeriod(e.getName()));
						l.info("RENEW call to " + e.getName() + " reservation " + e.getReservationId() + ", setting new deadline to " + future.getTime());	

						// invoke renew
						PluginReturn pr = PluginsRegistry.getInstance().renew(e.getName(), 
								resId, future.getTime(), 
								e.getJoinProperties(), e.getSchedProperties());

						if (pr.getStatus() == PluginErrorCodes.OK.code) {
							// merge original join properties with properties returned by renew
							e.getSchedProperties().putAll(pr.getProperties());

							// update the database with a new schedule entry
							l.info("Updating the database for " + e.getName() + " existing reservation " + e.getReservationId());
							sp.saveRenewDeadline(e.getName(), future.getTime(), resId, 
									e.getJoinProperties(), e.getSchedProperties(), 0, null);
						} else 
							l.error("RENEW call to " + e.getName() + " reservation " + e.getReservationId() + 
									" returned non-zero status: " + 
									pr.getStatus() + " " + pr.getErrorMsg());
						
					} catch (PluginException pe) {
						l.error("Unable to renew plugin " + e.getName() + " reservation " + e.getReservationId() + " due to " + pe.getMessage());
					} catch (Exception ee) {
						l.error("Unable to renew plugin " + e.getName() + " reservation " + e.getReservationId() + " due to " + ee.getMessage());
					} finally {
						finishRenewal(e.getReservationId());
						
						// remove old entry
						l.debug("Removing old entry " + e + " from the database");
						sp.removeEntries(Arrays.asList(e));
						
						// release the lock
						PluginsRegistry.getInstance().release(e.getName(), resId);
					}
				}
			});
		}
	}
}
