package orca.nodeagent2.agent.core;

import java.lang.reflect.Constructor;
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

	public PluginReturn join(Date until, Properties callerProperties) throws PluginException {
		ClassLoader cl = null;
		try {
			cl = Thread.currentThread().getContextClassLoader();
			Thread.currentThread().setContextClassLoader(getClassLoader());
			return instance.join(until, callerProperties);
		} catch (IllegalArgumentException e) {
			throw new PluginException("Unable to invoke join method in " + pluginClass.getName() + " due to IllegalArgumentException: " + e);
		} catch (Exception ee) {
			throw new PluginException("Unable to invoke join method in " + pluginClass.getName() + " due to Exception: " + ee);
		} finally {
			if (cl != null)
				Thread.currentThread().setContextClassLoader(cl);
		}
	}

	public PluginReturn leave(ReservationId resId, Properties callerProperties, Properties schedProperties) throws PluginException {
		ClassLoader cl = null;
		try {
			cl = Thread.currentThread().getContextClassLoader();
			Thread.currentThread().setContextClassLoader(getClassLoader());
			return instance.leave(resId, callerProperties, schedProperties);
		} catch (IllegalArgumentException e) {
			throw new PluginException("Unable to invoke leave method in " + pluginClass.getName() + " due to IllegalArgumentException: " + e);
		} catch (Exception ee) {
			throw new PluginException("Unable to invoke leave method in " + pluginClass.getName() + " due to Exception: " + ee);
		} finally {
			if (cl != null)
				Thread.currentThread().setContextClassLoader(cl);
		}
	}

	public PluginReturn modify(ReservationId resId, Properties callerProperties, Properties schedProperties) throws PluginException {
		ClassLoader cl = null;
		try {
			cl = Thread.currentThread().getContextClassLoader();
			Thread.currentThread().setContextClassLoader(getClassLoader());
			return instance.modify(resId, callerProperties, schedProperties);
		} catch (IllegalArgumentException e) {
			throw new PluginException("Unable to invoke modify method in " + pluginClass.getName() + " due to IllegalArgumentException: " + e);
		} catch (Exception ee) {
			throw new PluginException("Unable to invoke modify method in " + pluginClass.getName() + " due to Exception: " + ee);
		} finally {
			if (cl != null)
				Thread.currentThread().setContextClassLoader(cl);
		}
	}

	public PluginReturn renew(ReservationId resId, Date until, Properties joinProperties, Properties schedProperties) throws PluginException {
		ClassLoader cl = null;
		try {
			cl = Thread.currentThread().getContextClassLoader();
			Thread.currentThread().setContextClassLoader(getClassLoader());
			return instance.renew(resId, until, joinProperties, schedProperties);
		} catch (IllegalArgumentException e) {
			throw new PluginException("Unable to invoke renew method in " + pluginClass.getName() + " due to IllegalArgumentException: " + e);
		} catch (Exception ee) {
			throw new PluginException("Unable to invoke renew method in " + pluginClass.getName() + " due to Exception: " + ee);
		} finally {
			if (cl != null)
				Thread.currentThread().setContextClassLoader(cl);
		}
	}

	public PluginReturn status(ReservationId resId, Properties schedProperties) throws PluginException {
		ClassLoader cl = null;
		try {
			cl = Thread.currentThread().getContextClassLoader();
			Thread.currentThread().setContextClassLoader(getClassLoader());
			return instance.status(resId, schedProperties);
		} catch (IllegalArgumentException e) {
			throw new PluginException("Unable to invoke status method in " + pluginClass.getName() + " due to IllegalArgumentException: " + e);
		} catch (Exception ee) {
			throw new PluginException("Unable to invoke status method in " + pluginClass.getName() + " due to Exception: " + ee);
		} finally {
			if (cl != null)
				Thread.currentThread().setContextClassLoader(cl);
		}
	}

	public void initialize(String fName, Properties configProperties) throws PluginException {
		ClassLoader cl = null;
		try {
			cl = Thread.currentThread().getContextClassLoader();
			Thread.currentThread().setContextClassLoader(getClassLoader());
			instance.initialize(fName, configProperties);
			return;
		} catch (IllegalArgumentException e) {
			throw new PluginException("Unable to invoke init method in " + pluginClass.getName() + " due to IllegalArgumentException: " + e);
		} catch (Exception ee) {
			throw new PluginException("Unable to invoke init method in " + pluginClass.getName() + " due to Exception: " + ee);
		} finally {
			if (cl != null)
				Thread.currentThread().setContextClassLoader(cl);
		}
	}
}
