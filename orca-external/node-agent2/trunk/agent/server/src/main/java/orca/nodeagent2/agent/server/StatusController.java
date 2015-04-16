package orca.nodeagent2.agent.server;

import java.util.Arrays;
import java.util.Date;
import java.util.Random;

import orca.nodeagent2.agent.config.Config;
import orca.nodeagent2.agent.config.xsd.PluginType;
import orca.nodeagent2.agent.server.persistence.PersistenceException;
import orca.nodeagent2.agent.server.persistence.ScheduleEntry;
import orca.nodeagent2.agent.server.persistence.SchedulePersistence;
import orca.nodeagent2.agentlib.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatusController {
	Log l;
	
	final SchedulePersistence sp;
	
	// remember that Spring beans are singletons (in Spring sense) by default
	@Autowired
	public StatusController(SchedulePersistence sp) {
		this.sp = sp;
		l = LogFactory.getLog("statusController");
	}
	
	
	public static class RandomString {

		private static final char[] symbols;

		static {
			StringBuilder tmp = new StringBuilder();
			for (char ch = '0'; ch <= '9'; ++ch)
				tmp.append(ch);
			for (char ch = 'a'; ch <= 'z'; ++ch)
				tmp.append(ch);
			symbols = tmp.toString().toCharArray();
		}   

		private final Random random = new Random();

		private final char[] buf;

		public RandomString(int length) {
			if (length < 1)
				throw new IllegalArgumentException("length < 1: " + length);
			buf = new char[length];
		}

		public String nextString() {
			for (int idx = 0; idx < buf.length; ++idx) 
				buf[idx] = symbols[random.nextInt(symbols.length)];
			return new String(buf);
		}
	}

	private void _rundb() throws PersistenceException {
		Properties props = new Properties();
		RandomString rstr = new RandomString(10);
		props.put("This", rstr.nextString());
		props.put("other", rstr.nextString());
		
		sp.saveDeadline("myplugin", new Date(), props);
		
		System.out.println("ScheduleEntries found with findAll():");
		System.out.println("-------------------------------");
		for (ScheduleEntry se : sp.getAll()) {
			System.out.println(se);
		}
		System.out.println();
	}
	
	/**
	 * List configured plugins (or specific plugin by name)
	 * @param name
	 * @return
	 */
	@RequestMapping(value="/plugins/{pName}", method=RequestMethod.GET)
	public StatusBean status(@PathVariable(value="pName") String name) {
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
			l.error("Unable to populate status bean " + e);
			return null;
		}
	}
	
	@RequestMapping(value="/plugins", method=RequestMethod.GET)
	public StatusBean status() {
		try {
			l.info("Status request for all plugins");
			return new StatusBean(null, Config.getInstance().getPluginsAsList());
		} catch (Exception e) {
			l.error("Unable to populate status bean " + e);
			return null;
		}
	}
	
	/**
	 * List the schedule in the database
	 * @return
	 */
	@RequestMapping(value="/schedule", method=RequestMethod.GET)
	public DBBean schedule() {
		try {
			l.info("DB request ");
			return new DBBean(sp.getAll());
		} catch (Exception e) {
			l.error("Unable to populate db bean " + e);
			return null;
		}
	}
}
