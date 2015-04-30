package orca.nodeagent2.agent.core;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;

/**
 * Handle the mechanics of loading the foreign jars
 * @author ibaldin
 *
 */
public class JarHandler {

	ClassLoader plc = null;
	String name = null;
	String className = null;
	
	public Class<?> loadJar(String n, String c) throws Exception {
		name = n;
		className = c;
		try {
			File f = new File(name);
			if (!f.exists())
				throw new Exception("Jar file " + name + " not found");
			String jarUrl = "jar:file:" + name + "!/";
			//URLClassLoader childCl = new URLClassLoader(new URL[]{ new URL(jarUrl)} , Thread.currentThread().getContextClassLoader());
			plc = new ParentLastClassLoader(Arrays.asList(new URL[] { new URL(jarUrl)} ));
			//ChildFirstURLClassLoader cfc = new ChildFirstURLClassLoader(new URL[] { new URL(jarUrl)}, JarHandler.class.getClassLoader());
			URLClassLoader childCl = new URLClassLoader(new URL[]{ new URL(jarUrl)}, null);
			Class<?> classToLoad = (Class<?>)Class.forName (className, true, plc);
			if (classToLoad == null) {
				throw new Exception("Failed to load class " + className + " from jar file " + name);
			}
			return classToLoad;
		} catch (MalformedURLException mle) {
			throw new Exception("Invalid jar file name: " + name);
		} catch (ClassNotFoundException e) {
			throw new Exception("Unable to find class " + className + " in jar " + name);
		} catch (Exception ee) {
			ee.printStackTrace();
			throw new Exception("Unable to load class " + className + " in jar " + name + " due to " + ee);
		} 
	}

	public ClassLoader getClassLoader() throws Exception {
		if (plc == null)
			throw new Exception("Trying to get uninitialized classloader");
		return plc;
	}
	
}
