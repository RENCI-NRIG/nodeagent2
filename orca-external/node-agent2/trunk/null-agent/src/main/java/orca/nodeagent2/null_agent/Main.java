package orca.nodeagent2.null_agent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import orca.nodeagent2.agentlib.Plugin;
import orca.nodeagent2.agentlib.PluginException;
import orca.nodeagent2.agentlib.PluginReturn;
import orca.nodeagent2.agentlib.Properties;
import orca.nodeagent2.agentlib.ReservationId;
import orca.nodeagent2.agentlib.Util;

import org.apache.commons.logging.Log;

/**
 * Null plugin as an example for other plugins. 
 * @author ibaldin
 *
 */
public class Main implements Plugin {
	ClassLoader coreLoader;
	Log log;
	
	public PluginReturn join(Properties inPropeties) {
		log.info("JOIN");
		Map<String, Object> retProps = new HashMap<String, Object>();
		retProps.put("key1", "val1");
		retProps.put("key2", "val2");
		List<String> val3 = new ArrayList<String>();
		val3.add("string1");
		val3.add("string2");
		retProps.put("key3", val3);
		return new PluginReturn(new ReservationId("null-reservation-001"), retProps);
	}

	public PluginReturn leave(ReservationId resId,
			Properties inProperties) {
		log.info("LEAVE");
		Map<String, Object> retProps = new HashMap<String, Object>();
		retProps.put("key11", "val11");
		retProps.put("key21", "val21");
		List<String> val3 = new ArrayList<String>();
		val3.add("string11");
		val3.add("string21");
		retProps.put("key31", val3);
		return new PluginReturn(new ReservationId("null-reservation-001"), retProps);
	}

	public PluginReturn modify(ReservationId resId,
			Properties inProperties) {
		log.info("MODIFY");
		Map<String, Object> retProps = new HashMap<String, Object>();
		retProps.put("key12", "val12");
		retProps.put("key22", "val22");
		List<String> val3 = new ArrayList<String>();
		val3.add("string12");
		val3.add("string22");
		retProps.put("key32", val3);
		return new PluginReturn(new ReservationId("null-reservation-001"), retProps);
	}

	public PluginReturn renew(ReservationId resId,
			Properties inProperties) {
		log.info("RENEW");
		Map<String, Object> retProps = new HashMap<String, Object>();
		retProps.put("key13", "val13");
		retProps.put("key23", "val23");
		List<String> val3 = new ArrayList<String>();
		val3.add("string13");
		val3.add("string23");
		retProps.put("key33", val3);
		return new PluginReturn(new ReservationId("null-reservation-001"), retProps);
	}

	public String status() {
		log.info("STATUS");
		return "This is the status of the Null plugin";
	}

	public void initialize(String config, Properties inProperties, ClassLoader cl) throws PluginException {
		coreLoader = cl;
		try {
			// this is how you get hold of the logger
			log = Util.getLog(cl, "null-plugin");
		} catch (Exception e) {
			e.printStackTrace();
		}
		log.info("Initializing plugin " + inProperties);
	}

}
