package orca.nodeagent2.agent.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import orca.nodeagent2.agentlib.Plugin;
import orca.nodeagent2.agentlib.PluginException;
import orca.nodeagent2.agentlib.PluginReturn;
import orca.nodeagent2.agentlib.Properties;
import orca.nodeagent2.agentlib.ReservationId;

public class PluginJarHandler extends JarHandler {
	protected Class<Plugin> pluginClass;
	protected Map<String, Method> pluginMethod = new HashMap<String, Method>();
	protected Plugin instance;

	// Each plugin defines these methods
	public enum PluginMethod {
		JOIN("join"), LEAVE("leave"), 
		MODIFY("modify"), RENEW("renew"), 
		STATUS("status"), INITIALIZE("initialize");

		String name;

		PluginMethod(String s) {
			name = s;
		}

		String getName() {
			return name;
		}
	}

	@SuppressWarnings("unchecked")
	public PluginJarHandler(String jarName, String className) throws Exception {
		pluginClass = (Class<Plugin>)loadJar(jarName, className);

		Class<?>[] pluginInterfaces = pluginClass.getInterfaces();
		boolean implementsPlugin = false;
		for (Class<?> intf: pluginInterfaces) {
			if (intf.equals(Plugin.class)) {
				implementsPlugin = true;
				break;
			}
		}

		if (!implementsPlugin)
			throw new Exception("Class " + className + " from " + jarName + " does not implement Plugin interface");

		// check all methods upfront
		try {
			for(Method m: Plugin.class.getMethods()) {
				Method mm = pluginClass.getDeclaredMethod(m.getName(), m.getParameterTypes());
				pluginMethod.put(m.getName(), mm);
			}
		} catch (NoSuchMethodException nsme) {
			throw new Exception("Class " + className + " does not properly implement Plugin interface: " + nsme);
		}

		try {
			Constructor<Plugin> ctor = pluginClass.getDeclaredConstructor();
			instance = ctor.newInstance();
		} catch (NoSuchMethodException nsme) {
			throw new Exception("Class " + className + " does not implement the proper constructor Plugin()");
		}
	}

	public PluginReturn join(Date until, Properties inProperties) throws PluginException {
		try {
			return (PluginReturn)pluginMethod.get(PluginMethod.JOIN.getName()).invoke(instance, until, inProperties);
		} catch (IllegalAccessException e) {
			throw new PluginException("Unable to invoke join method in " + pluginClass.getName() + " due to IllegalAccessException: " + e);
		} catch (IllegalArgumentException e) {
			throw new PluginException("Unable to invoke join method in " + pluginClass.getName() + " due to IllegalArgumentException: " + e);
		} catch (InvocationTargetException e) {
			throw new PluginException("Unable to invoke join method in " + pluginClass.getName() + " due to InvocationTargetException: " + e);
		}
	}

	public PluginReturn leave(ReservationId resId, Properties inProperties) throws PluginException {
		try {
			return (PluginReturn)pluginMethod.get(PluginMethod.LEAVE.getName()).invoke(instance, resId, inProperties);
		} catch (IllegalAccessException e) {
			throw new PluginException("Unable to invoke leave method in " + pluginClass.getName() + " due to IllegalAccessException: " + e);
		} catch (IllegalArgumentException e) {
			throw new PluginException("Unable to invoke leave method in " + pluginClass.getName() + " due to IllegalArgumentException: " + e);
		} catch (InvocationTargetException e) {
			throw new PluginException("Unable to invoke leave method in " + pluginClass.getName() + " due to InvocationTargetException: " + e);
		}
	}

	public PluginReturn modify(ReservationId resId, Properties inProperties) throws PluginException {
		try {
			return (PluginReturn)pluginMethod.get(PluginMethod.MODIFY.getName()).invoke(instance, resId, inProperties);
		} catch (IllegalAccessException e) {
			throw new PluginException("Unable to invoke modify method in " + pluginClass.getName() + " due to IllegalAccessException: " + e);
		} catch (IllegalArgumentException e) {
			throw new PluginException("Unable to invoke modify method in " + pluginClass.getName() + " due to IllegalArgumentException: " + e);
		} catch (InvocationTargetException e) {
			throw new PluginException("Unable to invoke modify method in " + pluginClass.getName() + " due to InvocationTargetException: " + e);
		}
	}

	public PluginReturn renew(ReservationId resId, Date until, Properties inProperties, Properties joinProperties) throws PluginException {
		try {
			return (PluginReturn)pluginMethod.get(PluginMethod.RENEW.getName()).invoke(instance, resId, until, inProperties, joinProperties);
		} catch (IllegalAccessException e) {
			throw new PluginException("Unable to invoke renew method in " + pluginClass.getName() + " due to IllegalAccessException: " + e);
		} catch (IllegalArgumentException e) {
			throw new PluginException("Unable to invoke renew method in " + pluginClass.getName() + " due to IllegalArgumentException: " + e);
		} catch (InvocationTargetException e) {
			throw new PluginException("Unable to invoke renew method in " + pluginClass.getName() + " due to InvocationTargetException: " + e);
		}
	}

	public String status() throws PluginException {
		try {
			return (String)pluginMethod.get(PluginMethod.STATUS.getName()).invoke(instance);
		} catch (IllegalAccessException e) {
			throw new PluginException("Unable to invoke status method in " + pluginClass.getName() + " due to IllegalAccessException: " + e);
		} catch (IllegalArgumentException e) {
			throw new PluginException("Unable to invoke status method in " + pluginClass.getName() + " due to IllegalArgumentException: " + e);
		} catch (InvocationTargetException e) {
			throw new PluginException("Unable to invoke status method in " + pluginClass.getName() + " due to InvocationTargetException: " + e);
		}
	}

	public void initialize(String fName, Properties inProperties) throws PluginException {
		try {
			pluginMethod.get(PluginMethod.INITIALIZE.getName()).invoke(instance, fName, inProperties, PluginJarHandler.class.getClassLoader());
			return;
		} catch (IllegalAccessException e) {
			throw new PluginException("Unable to invoke status method in " + pluginClass.getName() + " due to IllegalAccessException: " + e);
		} catch (IllegalArgumentException e) {
			throw new PluginException("Unable to invoke status method in " + pluginClass.getName() + " due to IllegalArgumentException: " + e);
		} catch (InvocationTargetException e) {
			throw new PluginException("Unable to invoke status method in " + pluginClass.getName() + " due to InvocationTargetException: " + e);
		}
	}
	
	public static void main(String argv[]) {

		try {
			PluginJarHandler pjh = new PluginJarHandler("/Users/ibaldin/workspace-nodeagent2/node-agent2/null-agent/target/null-agent-0.1-SNAPSHOT.jar", 
					"orca.nodeagent2.null_agent.Main");
			pjh.join(new Date(), null);
		} catch (PluginException e) {
			System.err.println("PLUGIN EXCEPTION: " + e);
		} catch (Exception e) {
			System.err.println("EXCEPTION " + e);
		}

	}
}
