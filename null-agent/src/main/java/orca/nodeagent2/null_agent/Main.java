package orca.nodeagent2.null_agent;

import java.util.Date;
import java.util.Random;

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
	RandomString rstr;
	Log log;
	
	
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
	
	public PluginReturn join(Date until, Properties inProperties) {
		
		log.info("JOIN until " + until + " with " + inProperties);

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
		retProps.put("key3", "string35");
		return new PluginReturn(new ReservationId(rstr.nextString()), retProps);
	}

	public PluginReturn leave(ReservationId resId, Properties inProperties) {
		log.info("LEAVE for " + resId + " with " + inProperties);
		Properties retProps = new Properties();
		retProps.put("key11", "val11");
		retProps.put("key21", "val21");
		retProps.put("key31", "stgring46");
		return new PluginReturn(resId, retProps);
	}

	public PluginReturn modify(ReservationId resId, Properties inProperties) {
		log.info("MODIFY for " + resId + " with " + inProperties);
		Properties retProps = new Properties();
		retProps.put("key12", "val12");
		retProps.put("key22", "val22");
		retProps.put("key32", "String67");
		return new PluginReturn(resId, retProps);
	}

	public PluginReturn renew(ReservationId resId, Date until, Properties inProperties, Properties joinProperties) {
		log.info("RENEW  for " + resId + " until " + until + " with " + inProperties + " and " + joinProperties);
		Properties retProps = new Properties();
		retProps.put("key13", "val13");
		retProps.put("key23", "val23");
		retProps.put("key33", "string78");
		return new PluginReturn(resId, retProps);
	}

	public PluginReturn status(ReservationId resId) {
		log.info("STATUS for " + resId);
		return new PluginReturn(resId, null);
	}

	public void initialize(String config, Properties inProperties, ClassLoader cl) throws PluginException {
		coreLoader = cl;
		try {
			// this is how you get hold of the logger
			log = Util.getLog(cl, "null-plugin");
			rstr = new RandomString(10);
		} catch (Exception e) {
			e.printStackTrace();
		}
		log.info("Initializing plugin " + inProperties);
	}

}
