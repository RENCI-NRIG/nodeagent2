package orca.nodeagent2.agent.server;

import java.util.Calendar;

import orca.nodeagent2.agent.config.Config;
import orca.nodeagent2.agent.core.PluginsRegistry;
import orca.nodeagent2.agent.server.error.DuplicateObjectError;
import orca.nodeagent2.agent.server.error.ObjectNotFoundError;
import orca.nodeagent2.agent.server.persistence.ScheduleEntry;
import orca.nodeagent2.agent.server.persistence.SchedulePersistence;
import orca.nodeagent2.agentlib.PluginErrorCodes;
import orca.nodeagent2.agentlib.PluginException;
import orca.nodeagent2.agentlib.PluginReturn;
import orca.nodeagent2.agentlib.Properties;
import orca.nodeagent2.agentlib.ReservationId;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PluginController {
	Log l;

	final SchedulePersistence sp;

	// remember that Spring beans are singletons (in Spring sense) by default
	@Autowired
	public PluginController(SchedulePersistence sp) {
		this.sp = sp;
		l = LogFactory.getLog("pluginController");
	}

	/**
	 * Join operation. ResponseBody forces the return object to be exactly PluginReturn (not wrapped with a model)
	 * RequestBody lets us bypass naming the parameters - simpler to call in curl and simple JSON
	 * @param name
	 * @param props
	 * @return
	 */
	@RequestMapping(value="/join/{pName}", method=RequestMethod.POST, consumes = { "application/json" })
	@ResponseBody
	public PluginReturn join(@PathVariable("pName") String name, @RequestBody Properties props) {
		try {
			Calendar future = Calendar.getInstance();
			future.add(Config.getInstance().getSchedulePeriodCalendarUnit(name), Config.getInstance().getSchedulePeriod(name));
			l.info("JOIN call to " + name + ", setting deadline to " + future.getTime());
			l.debug("  with properties " + props);
			
			PluginReturn pr = PluginsRegistry.getInstance().join(name, future.getTime(), props);
			
			if (pr == null) 
				throw new InternalError("Plugin " + name + " returned null on join");
			
			if (pr.getStatus() == PluginErrorCodes.OK.code) {
				// insert the renew event into the database with initial and returned properties 
				// the deadline is set to the execution deadline (schedule period - tick advance)
				ScheduleEntry se = sp.findEntry(name, pr.getResId().getId());
				
				if (se != null)
					throw new DuplicateObjectError("Plugin " + name + " reservation with id " + pr.getResId() + " already exists!");

				l.info("Updating the database for " + name + " new reservation " + pr.getResId());
				sp.saveRenewDeadline(name, future.getTime(), pr.getResId(), props, pr.getProperties(), 0, null);
			} else {
				l.error("JOIN returned an error " + pr.getStatus() + " " + pr.getErrorMsg());
				throw new InternalError("JOIN returned an error " + pr.getStatus() + " " + pr.getErrorMsg());
			}
			
			return pr;
		} catch (PluginException pe) {
			l.error("PluginError invoking join on " + name + ": " + pe);
			pe.printStackTrace();
			return new PluginReturn(PluginErrorCodes.EXCEPTION.code, "join error: " + pe.getMessage(), null, null);
		} catch (InternalError ie) {
			throw ie;
		} catch (DuplicateObjectError doe) {
			throw doe;
		} catch (Exception e) {
			l.error("Error invoking join on " + name + ": " + e);
			throw new InternalError("Error invoking join on " + name + ": " + e);
		}	
	}

	@RequestMapping(value="/leave/{pName}/{resId:.+}", method=RequestMethod.POST, consumes = { "application/json" })
	@ResponseBody 
	public PluginReturn leave(@PathVariable(value="pName") String name, @PathVariable(value="resId") String resId, @RequestBody Properties props) {
		ReservationId rid = new ReservationId(resId);
		try {
			l.info("LEAVE call to " + name + " for " + rid);
			l.debug("  with properties " + props);
			
			// get the lock
			PluginsRegistry.getInstance().acquire(name, rid);
			
			l.info("Removing from the database");
			ScheduleEntry se = sp.removeEntry(name, rid);
			
			if (se == null)
				throw new ObjectNotFoundError("No schedule entries found in the NA2 database for " + name + " reservation " + resId);
			
			PluginReturn pr = PluginsRegistry.getInstance().leave(name, rid, props, se.getSchedProperties());
			
			if (pr == null) 
				throw new InternalError("Plugin returned null");
			
			l.info("LEAVE call to " + name + " for " + rid + " returned " + pr.getStatus() + " " + pr.getErrorMsg());
			return pr;
		} catch (PluginException pe) {
			l.error("PluginError invoking leave on " + name + ": " + pe);
			return new PluginReturn(PluginErrorCodes.EXCEPTION.code, "leave error: " + pe.getMessage(), rid, null);
		} catch (InternalError ie) {
			throw ie;
		} catch (DuplicateObjectError doe) {
			throw doe;
		} catch (Exception e) {
			l.error("Error invoking leave on " + name + ": " + e);
			throw new InternalError("Error invoking leave on " + name + ": " + e);
		} finally {
			PluginsRegistry.getInstance().release(name, rid);
		}
	}

	@RequestMapping(value="/modify/{pName}/{resId:.+}", method=RequestMethod.POST, consumes = { "application/json" })
	@ResponseBody
	public PluginReturn modify(@PathVariable(value="pName") String name, @PathVariable(value="resId") String resId, @RequestBody Properties props) {
		ReservationId rid = new ReservationId(resId);
		try {
			l.info("MODIFY call to " + name + " for " + rid);
			l.debug("  with properties " + props);
			// get the lock
			PluginsRegistry.getInstance().acquire(name, rid);
			
			ScheduleEntry se = sp.findEntry(name, resId);
			
			if (se == null)
				throw new ObjectNotFoundError("No schedule entries found in the NA2 database for " + name + " reservation " + resId);
			
			PluginReturn pr = PluginsRegistry.getInstance().modify(name, rid, props, se.getSchedProperties());
			
			if (pr == null) 
				throw new InternalError("Plugin returned null");
			
			l.info("MODIFY call to " + name + " for " + rid + " returned " + pr.getStatus() + " " + pr.getErrorMsg());
			return pr;
		} catch (PluginException pe) {
			l.error("PluginError invoking modify on " + name + ": " + pe);
			return new PluginReturn(PluginErrorCodes.EXCEPTION.code, "modify error: " + pe.getMessage(), rid, null);
		} catch (InternalError ie) {
			throw ie;
		} catch (DuplicateObjectError doe) {
			throw doe;
		} catch (Exception e) {
			l.error("Error invoking modify on " + name + ": " + e);
			throw new InternalError("Error invoking modify on " + name + ": " + e);
		} finally {
			PluginsRegistry.getInstance().release(name, rid);
		}
	}

	@RequestMapping(value="/status/{pName}/{resId:.+}", method=RequestMethod.GET)
	public PluginReturn status(@PathVariable(value="pName") String name, @PathVariable(value="resId") String resId) {
		ReservationId rid = new ReservationId(resId);
		try {
			l.info("STATUS call to " + name + " reservation " + resId);
			ScheduleEntry se = sp.findEntry(name, resId);
			
			if (se == null)
				throw new ObjectNotFoundError("No schedule entries found in the NA2 database for " + name + " reservation " + resId);
			
			return PluginsRegistry.getInstance().status(name, rid,
					(se != null ? se.getSchedProperties() : null));
		} catch (PluginException pe) {
			l.error("PluginError getting status for " + name + ": " + pe.getMessage());
			return new PluginReturn(PluginErrorCodes.EXCEPTION.code, "status error: error getting status for " + name + ": " + pe.getMessage(), 
					rid, null);
		} catch (InternalError ie) {
			throw ie;
		} catch (DuplicateObjectError doe) {
			throw doe;
		} catch (Exception e) {
			l.error("Error getting status for " + name + ": " + e.getMessage());
			throw new InternalError("Error getting status for " + name + ": " + e.getMessage());
		}
	}

	@RequestMapping(value="/description/{pName}", method=RequestMethod.GET)
	public String description(@PathVariable(value="pName") String name) {
		try {
			l.info("DESCRIPTION call to " + name);
			String desc = PluginsRegistry.getInstance().getDescription(name);
			if (desc == null)
				return "No description provided for " + name;
			else
				return desc;
		} catch (PluginException pe) {
			l.error("PluginError getting description for " + name + ": " + pe.getMessage());
			return "PluginError getting description for " + name + ": " + pe.getMessage();
		} catch (InternalError ie) {
			throw ie;
		} catch (DuplicateObjectError doe) {
			throw doe;
		} catch (Exception e) {
			l.error("Error getting description for " + name + ": " + e.getMessage());
			throw new InternalError("Error getting description for " + name + ": " + e.getMessage());
		}
	}

}
