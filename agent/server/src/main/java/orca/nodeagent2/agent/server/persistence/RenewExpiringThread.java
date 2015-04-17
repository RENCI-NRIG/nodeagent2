package orca.nodeagent2.agent.server.persistence;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import orca.nodeagent2.agent.config.Config;
import orca.nodeagent2.agent.core.PluginsRegistry;
import orca.nodeagent2.agentlib.PluginException;
import orca.nodeagent2.agentlib.PluginReturn;
import orca.nodeagent2.agentlib.ReservationId;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RenewExpiringThread implements Runnable {

	private final Log l = LogFactory.getLog("renewExpiringThread");

	private final SchedulePersistence sp;
	
	public RenewExpiringThread(SchedulePersistence sp) {
		this.sp = sp;
		Thread.currentThread().setName("renewExpiringThread");
	}
	
	@Override
	public void run() {
		List<ScheduleEntry> expired = sp.findExpiredEntries();
		l.info("There are " + expired.size() + " expired entries");
		for(ScheduleEntry e: expired) {
			l.info("Renewing" + e.getName() + " reservation " + e.getReservationId());
			try {
			
				// compute new expiry time as now + plugin period
				Calendar future = Calendar.getInstance();
				future.add(Config.getInstance().getSchedulePeriodCalendarUnit(e.getName()), 
						Config.getInstance().getSchedulePeriod(e.getName()));
				l.info("RENEW call to " + e.getName() + " reservation " + e.getReservationId() + ", setting new deadline to " + future.getTime());	
				
				// invoke renew
				PluginReturn pr = PluginsRegistry.getInstance().renew(e.getName(), 
						new ReservationId(e.getReservationId()), future.getTime(), 
						e.getInProperties(), e.getJoinProperties());
				
				// update the database with a new schedule entry
				l.info("Updating the database for " + e.getName() + " existing reservation " + e.getReservationId());
				sp.saveRenewDeadline(e.getName(), future.getTime(), pr.getResId(), e.getInProperties(), e.getJoinProperties());
				
				// remove old entry
				l.debug("Removing old entry from the database");
				sp.removeEntries(Arrays.asList(e));
			} catch (PluginException pe) {
				l.error("Unable to renew plugin " + e.getName() + " reservation " + e.getReservationId());
			} catch (Exception ee) {
				l.error("Unable to renew plugin " + e.getName() + " reservation " + e.getReservationId());
			}
		}
	}
}
