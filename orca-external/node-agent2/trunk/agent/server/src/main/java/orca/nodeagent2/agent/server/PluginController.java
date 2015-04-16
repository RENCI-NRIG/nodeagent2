package orca.nodeagent2.agent.server;

import orca.nodeagent2.agent.core.PluginsRegistry;
import orca.nodeagent2.agent.server.persistence.SchedulePersistence;
import orca.nodeagent2.agentlib.LMParams;
import orca.nodeagent2.agentlib.PluginErrorCodes;
import orca.nodeagent2.agentlib.PluginException;
import orca.nodeagent2.agentlib.PluginReturn;
import orca.nodeagent2.agentlib.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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
			l.info("JOIN call to " + name);
			l.debug("  with properties " + props);
			PluginReturn pr = PluginsRegistry.getInstance().join(name, props);
			return pr;
		} catch (PluginException pe) {
			l.error("Error invoking join on " + name + ": " + pe);
			return new PluginReturn(PluginErrorCodes.EXCEPTION.code, "join error: " + pe.getMessage(), null, null);
		} catch (Exception e) {
			l.error("Error invoking join on " + name + ": " + e);
			return new PluginReturn(PluginErrorCodes.EXCEPTION.code, "join error: " + e.getMessage(), null, null);
		}	
	}

	@RequestMapping(value="/leave/{pName}", method=RequestMethod.POST, consumes = { "application/json" })
	@ResponseBody 
	public PluginReturn leave(@PathVariable(value="pName") String name, @RequestBody LMParams params) {
		try {
			l.info("LEAVE call to " + name + " for " + params.getId());
			l.debug("  with properties " + params.getProperties());
			PluginReturn pr = PluginsRegistry.getInstance().leave(name, params.getId(), params.getProperties());
			return pr;
		} catch (PluginException pe) {
			l.error("Error invoking leave on " + name + ": " + pe);
			return new PluginReturn(PluginErrorCodes.EXCEPTION.code, "leave error: " + pe.getMessage(), params.getId(), null);
		} catch (Exception e) {
			l.error("Error invoking leave on " + name + ": " + e);
			return new PluginReturn(PluginErrorCodes.EXCEPTION.code, "leave error: " + e.getMessage(), params.getId(), null);
		}	
	}

	@RequestMapping(value="/modify/{pName}", method=RequestMethod.POST, consumes = { "application/json" })
	@ResponseBody
	public PluginReturn modify(@PathVariable(value="pName") String name, @RequestBody LMParams params) {
		try {
			l.info("MODIFY call to " + name + " for " + params.getId());
			l.debug("  with properties " + params.getProperties());
			PluginReturn pr = PluginsRegistry.getInstance().modify(name, params.getId(), params.getProperties());
			return pr;
		} catch (PluginException pe) {
			l.error("Error invoking modify on " + name + ": " + pe);
			return new PluginReturn(PluginErrorCodes.EXCEPTION.code, "modify error: " + pe.getMessage(), params.getId(), null);
		} catch (Exception e) {
			l.error("Error invoking modify on " + name + ": " + e);
			return new PluginReturn(PluginErrorCodes.EXCEPTION.code, "modify error: " + e.getMessage(), params.getId(), null);
		}	
	}

	@RequestMapping(value="/status/{pName}", method=RequestMethod.GET)
	public String status(@PathVariable(value="pName") String name) {
		try {
			l.info("STATUS call to " + name);
			return PluginsRegistry.getInstance().status(name);
		} catch (PluginException pe) {
			l.error("Error getting status for " + name + ": " + pe.getMessage());
			return "Error getting status for " + name + ": " + pe.getMessage();
		} catch (Exception e) {
			l.error("Error getting status for " + name + ": " + e.getMessage());
			return "Error getting status for " + name + ": " + e.getMessage();
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
			l.error("Error getting description for " + name + ": " + pe.getMessage());
			return "Error getting description for " + name + ": " + pe.getMessage();
		} catch (Exception e) {
			l.error("Error getting description for " + name + ": " + e.getMessage());
			return "Error getting description for " + name + ": " + e.getMessage();
		}
	}

}
