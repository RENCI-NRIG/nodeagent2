package orca.nodeagent2.null_agent;

import java.util.ArrayList;
import java.util.List;

import orca.nodeagent2.agentlib.Plugin;
import orca.nodeagent2.agentlib.PluginException;
import orca.nodeagent2.agentlib.PluginReturn;
import orca.nodeagent2.agentlib.Properties;
import orca.nodeagent2.agentlib.ReservationId;
import orca.nodeagent2.agentlib.Util;

import org.apache.commons.logging.Log;

import com.google.gson.Gson;

/**
 * Null plugin as an example for other plugins. It shows that other dependencies can also be 
 * brought in as long as the resulting jar is built as 'jar-with-dependencies'. See the pom.xml
 * file for more.
 * @author ibaldin
 *
 */
public class Main implements Plugin {
	ClassLoader coreLoader;
	Log log;
	
	public PluginReturn join(Properties inProperties) {
		
		log.info("JOIN " + inProperties);

		Gson gson = new Gson();
		gson.toJson(1);           
		gson.toJson("abcd");      
		gson.toJson(new Long(10)); 
		int[] values = { 1 };
		gson.toJson(values);   
		log.info("GSON " + gson.toJson(values));
		
		Properties retProps = new Properties();
		retProps.put("key1", "val1");
		retProps.put("key2", "val2");
		List<String> val3 = new ArrayList<String>();
		val3.add("string1");
		val3.add("string2");
		retProps.put("key3", val3);
		return new PluginReturn(new ReservationId("null-reservation-001"), retProps);
	}

	public PluginReturn leave(ReservationId resId, Properties inProperties) {
		log.info("LEAVE for " + resId + " with " + inProperties);
		Properties retProps = new Properties();
		retProps.put("key11", "val11");
		retProps.put("key21", "val21");
		List<String> val3 = new ArrayList<String>();
		val3.add("string11");
		val3.add("string21");
		retProps.put("key31", val3);
		return new PluginReturn(new ReservationId("null-reservation-001"), retProps);
	}

	public PluginReturn modify(ReservationId resId, Properties inProperties) {
		log.info("MODIFY for " + resId + " with " + inProperties);
		Properties retProps = new Properties();
		retProps.put("key12", "val12");
		retProps.put("key22", "val22");
		List<String> val3 = new ArrayList<String>();
		val3.add("string12");
		val3.add("string22");
		retProps.put("key32", val3);
		return new PluginReturn(new ReservationId("null-reservation-001"), retProps);
	}

	public PluginReturn renew(ReservationId resId, Properties inProperties) {
		log.info("RENEW  for " + resId + " with " + inProperties);
		Properties retProps = new Properties();
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
