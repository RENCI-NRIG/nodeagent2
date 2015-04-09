package orca.nodeagent2.agentlib;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Various useful utility functions for plugins
 * @author ibaldin
 *
 */
public class Util {
	private static Map<String, Log> logs = new HashMap<String, Log>();
	
	// Used on main classloader to get the Logger that's
	public static Log getLog(ClassLoader cl, String n) throws Exception {
		if (logs.containsKey(n))
			return logs.get(n);
		try {
			@SuppressWarnings("unchecked")
			Class<LogFactory> lf = (Class<LogFactory>)cl.loadClass("org.apache.commons.logging.LogFactory");
			
			Method gl = lf.getMethod("getLog", String.class);
			
			Log t = (Log)gl.invoke(null, n);
			if (t != null)
				logs.put(n, t);
			else
				throw new Exception("Unable to instantiate logger " + n);
			return t;
		} catch(ClassNotFoundException cnfe) {
			throw new Exception("Unable to load logger class from main classloadeer");
		} 
	}
}
