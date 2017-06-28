package org.renci.nodeagent2.agent.server;

import java.util.Arrays;

import org.renci.nodeagent2.agent.config.Config;
import org.renci.nodeagent2.agent.config.xsd.PluginType;
import org.renci.nodeagent2.agent.server.persistence.SchedulePersistence;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatusController {
	Log l;
	
	final SchedulePersistence sp;
	
	// remember that Spring beans are singletons (in Spring sense) by default
	@Autowired
	public StatusController(SchedulePersistence sp) {
		this.sp = sp;
		l = LogFactory.getLog(this.getClass().getName());
	}
	
	/**
	 * List configured plugins (or specific plugin by name)
	 * @param name
	 * @return
	 */
	@RequestMapping(value="/plugins/{pName}", method=RequestMethod.GET)
	@ResponseBody
	public StatusBean plugins(@PathVariable(value="pName") String name) {
		try {
			l.info("Status request for " + name);
			if ((name == null) || "all".equalsIgnoreCase(name)) 
				return new StatusBean(name, Config.getInstance().getPluginsAsList());
			else {
				PluginType pt = Config.getInstance().getPlugin(name);
				if (pt != null)
					return new StatusBean(name, Arrays.asList(Config.getInstance().getPlugin(name) ));
				return null;
			}
		} catch (Exception e) {
			l.error("Unable to populate status bean: " + e);
			throw new InternalError("Unable to populate status bean: " + e);
		}
	}
	
	@RequestMapping(value="/plugins", method=RequestMethod.GET)
	@ResponseBody
	public StatusBean plugins() {
		try {
			l.info("Status request for all plugins");
			return new StatusBean(null, Config.getInstance().getPluginsAsList());
		} catch (Exception e) {
			l.error("Unable to populate status bean: " + e);
			throw new InternalError("Unable to populate status bean: " + e);
		}
	}
	
	/**
	 * List the schedule in the database
	 * @return
	 */
	@RequestMapping(value="/schedule", method=RequestMethod.GET)
	@ResponseBody
	public DBBean schedule() {
		try {
			l.info("DB request ");
			return new DBBean(sp.getAll());
		} catch (Exception e) {
			l.error("Unable to populate db bean: " + e);
			throw new InternalError("Unable to populate db bean: " + e);
		}
	}
	
	/**
	 * List the schedule for a plugin in the database
	 * @return
	 */
	@RequestMapping(value="/schedule/{pName}", method=RequestMethod.GET)
	@ResponseBody
	public DBBean schedule(@PathVariable(value="pName") String name) {
		try {
			l.info("DB schedule request for " + name);
			return new DBBean(sp.findEntries(name));
		} catch (Exception e) {
			l.error("Unable to populate db bean: " + e);
			throw new InternalError("Unable to populate db bean: " + e);
		}
	}
	
	@RequestMapping(value="/schedule/{pName}/{resId:.+}", method=RequestMethod.GET)
	@ResponseBody
	public DBBean schedule(@PathVariable(value="pName") String name, @PathVariable(value="resId") String resId) {
		try {
			l.info("DB schedule request for " + name);
			return new DBBean(Arrays.asList(sp.findEntry(name, resId)));
		} catch (Exception e) {
			l.error("Unable to populate db bean: " + e);
			throw new InternalError("Unable to populate db bean: " + e);
		}
	}
	
	@RequestMapping(value="/expired", method=RequestMethod.GET)
	@ResponseBody
	public DBBean expired() {
		try {
			l.info("DB expired request");
			return new DBBean(sp.findExpiredEntries());
		} catch (Exception e) {
			l.error("Unable to populate db bean: " + e);
			throw new InternalError("Unable to populate db bean: " + e);
		}
	}
}
