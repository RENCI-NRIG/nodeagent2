package org.renci.nodeagent2.agent.config;

import java.io.File;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.renci.nodeagent2.agent.config.xsd.AgentConfigType;
import org.renci.nodeagent2.agent.config.xsd.PluginType;
import org.renci.nodeagent2.agent.config.xsd.PluginsType;
import org.renci.nodeagent2.agent.config.xsd.UnitChoice;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ConfigParser extends ParserHelper {
	Log l;
	String from;
	boolean initialized = false;

	AgentConfigType root = null;
	Map<String, PluginType> plugins = new HashMap<String, PluginType>();

	protected static final String PKG_LIST = "org.renci.nodeagent2.agent.config.xsd";
	protected static final String[] SCHEMA_LIST = { "AgentConfig.xsd" };

	public ConfigParser(String fname) throws Exception {
		l = LogFactory.getLog(this.getClass().getName());
		l.debug("Initializing " + this.getClass().getCanonicalName() + " with " + fname);
		File f = null;
		Scanner s = null;
		try {
			f = new File(fname);
			s = new Scanner(f);
			String text = s.useDelimiter("\\A").next();

			root = (AgentConfigType)validateXSDAndParse(text, PKG_LIST, AgentConfigType.class, SCHEMA_LIST, true, l);

			PluginsType pts = root.getPlugins();
			List<PluginType> cPlugins = pts.getPlugin();

			for(PluginType pt: cPlugins) {
				l.info("Detected plugin " + pt.getName() + " with jar " + pt.getJar());
				plugins.put(pt.getName(), pt);
				// check plugin periods against tick length
				if (pt.getSchedulePeriod().getUnit().equals(root.getTick().getUnit())) {
					if (pt.getSchedulePeriod().getLength() % root.getTick().getLength() != 0)
						throw new Exception("NodeAgent tick does not divide plugin " + pt.getName() + " period evenly");
				}
				Calendar plugin = Calendar.getInstance();
				Calendar tick = (Calendar)plugin.clone();
				Calendar tickCheck = (Calendar)plugin.clone();
				// make sure tick is not longer than plugin period
				plugin.add(convertToCalendarUnits(pt.getSchedulePeriod().getUnit()), pt.getSchedulePeriod().getLength());
				tick.add(convertToCalendarUnits(root.getTick().getUnit()), root.getTick().getLength());
				if (tick.after(plugin))
					throw new Exception("NodeAgent tick too long for plugin " + pt.getName());
				
				// make sure tick longer than one second (because we subtract one second on every renew)
				tickCheck.add(Calendar.SECOND, 1);
				if (tickCheck.equals(tick))
					throw new Exception("NodeAgent tick too short (1 sec)");
			}

			initialized = true;

		} catch(Exception e) {
			throw new Exception(e);
		} finally {
			if (s != null)
				s.close();
		}
	}

	/**
	 * Convert XML Config Unit Choice into Calendar time units
	 * @param uc
	 * @return
	 */
	private static int convertToCalendarUnits(UnitChoice uc) throws Exception {
		if (uc == null)
			throw new Exception("Unable to convert time unit null");
		switch(uc) {
		case WEEK: return Calendar.WEEK_OF_YEAR;
		case DAY: return Calendar.DAY_OF_YEAR;
		case HOUR: return Calendar.HOUR;
		case MINUTE: return Calendar.MINUTE;
		case SECOND: return Calendar.SECOND;
		default: return 0;
		}
	}
	
	private static TimeUnit convertToTimeUnits(UnitChoice uc) throws Exception {
		if (uc == null)
			throw new Exception ("Unable to convert time unit null");
		switch(uc) {
		case WEEK: 
			throw new Exception("Unable to convert unit Week to Java, please use days or smaller");
		case DAY: return TimeUnit.DAYS;
		case HOUR: return TimeUnit.HOURS;
		case MINUTE: return TimeUnit.MINUTES;
		case SECOND: return TimeUnit.SECONDS;
		default: return TimeUnit.SECONDS;
		}
	}
	
	public boolean isInitialized() {
		return initialized;
	}

	/**
	 * Get the node-agent password in configuration file
	 * @return
	 */
	public String getPassword() {
		return root.getPassword();
	}
	
	/**
	 * What is the tick
	 * @return
	 */
	public Integer getTickLength() {
		return root.getTick().getLength();
	}

	public UnitChoice getTickUnit() {
		return root.getTick().getUnit();
	}
	
	/**
	 * Conver to calendar unit measurements
	 * @return
	 * @throws Exception
	 */
	public int getTickCalendarUnit() throws Exception {
		return convertToCalendarUnits(getTickUnit());
	}
	
	/**
	 * Convert to java concurrency unit measurements
	 * @return
	 * @throws Exception
	 */
	public TimeUnit getTickTimeUnit() throws Exception {
		return convertToTimeUnits(getTickUnit());
	}
	
	/**
	 * What is the schedule length for the plugin
	 * @param name
	 * @return
	 */
	public Integer getSchedulePeriod(String name) throws Exception {
		if (!plugins.containsKey(name))
			throw new Exception("Unable to find plugin " + name);
		return plugins.get(name).getSchedulePeriod().getLength();
	}
	
	public UnitChoice getSchedulePeriodUnit(String name) throws Exception {
		if (!plugins.containsKey(name))
			throw new Exception("Unable to find plugin " + name);
		return plugins.get(name).getSchedulePeriod().getUnit();
	}
	
	public int getSchedulePeriodCalendarUnit(String name) throws Exception {
		return convertToCalendarUnits(getSchedulePeriodUnit(name));
	}
	
	/**
	 * Return an unmodifiable copy of plugins map
	 * @return
	 */
	public Map<String, PluginType> getPlugins() {
		return Collections.unmodifiableMap(plugins);
	}
	
	/**
	 * Return advance ticks specified in the configuration (or 1 if none is specified)
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public int getRenewAdvanceTicks(String name) throws Exception {
		if (plugins.containsKey(name) && (plugins.get(name).getRenewAdvanceTicks() != null))
			return plugins.get(name).getRenewAdvanceTicks();
		else return 1;
	}
	
	public static void main(String argv[]) {
		try {
			ConfigParser cp = new ConfigParser("/Users/ibaldin/workspace-nodeagent2/node-agent2/agent/config/src/main/resources/orca/nodeagent2/agent/config/xsd/test-config.xml");
			System.out.println(cp.getPassword());
			
			for(Map.Entry<String, PluginType> te: cp.getPlugins().entrySet()) {
				System.out.println("Plugin " + te.getValue().getName() + ": " + 
						cp.getSchedulePeriod(te.getValue().getName()) + " " + 
						cp.getSchedulePeriodUnit(te.getValue().getName()));
			}
		} catch (Exception e) {
			System.err.println(e);
			e.printStackTrace();
		}
		// list inside jar
		//		try {
		//			// Load the directory as a resource
		//			URL dir_url = ClassLoader.getSystemResource("orca/agent/config/xsd");
		//			// Turn the resource into a File object
		//			File dir = new File(dir_url.toURI());
		//			// List the directory
		//			String[] files = dir.list();
		//			for(String f: files) {
		//				System.out.println("\t" + f);
		//			}
		//		} catch (Exception e) {
		//			System.err.println(e);
		//			e.printStackTrace();
		//		}
	}

}
