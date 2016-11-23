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
	private static final String LOG_FACTORY_GET_LOG = "getLog";
	private static final String LOG_FACTORY_CLASS = "org.apache.commons.logging.LogFactory";
	private static Map<String, Log> logs = new HashMap<String, Log>();
	
	/**
	 * Get a logger with name n from main classloader
	 * @param cl
	 * @param n
	 * @return
	 * @throws Exception
	 */
	public static Log getLog(String n) throws Exception {
		if (logs.containsKey(n))
			return logs.get(n);
		try {
			@SuppressWarnings("unchecked")
			//Class<LogFactory> lf = (Class<LogFactory>)cl.loadClass(LOG_FACTORY_CLASS);
			Class<LogFactory> lf = (Class<LogFactory>)Util.class.getClassLoader().loadClass(LOG_FACTORY_CLASS);
			
			Method gl = lf.getMethod(LOG_FACTORY_GET_LOG, String.class);
			
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
	
	/**
	 * Attempt to get a class from a classloader
	 * @param cl
	 * @param n
	 * @return
	 * @throws Exception
	 */
	public static Class<?> getCLClass(ClassLoader cl, String n) throws Exception {
		try {
			return cl.loadClass(n);
		} catch(ClassNotFoundException cnfe) {
			throw new Exception("Unable to load class " + n + " from main classloadeer");
		} 
	}
}
