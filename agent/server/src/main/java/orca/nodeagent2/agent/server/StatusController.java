package orca.nodeagent2.agent.server;

import orca.nodeagent2.agent.config.Config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatusController {
	Log l;
	
	public StatusController() {
		l = LogFactory.getLog("statusController");
	}
	
	@RequestMapping(value="/status", method=RequestMethod.GET)
	public StatusBean status(@RequestParam(value="name") String name) {
		try {
			return new StatusBean(name, Config.getInstance().getPlugins());
		} catch (Exception e) {
			l.error("Unable to populate status bean " + e);
			return null;
		}
	}
}
