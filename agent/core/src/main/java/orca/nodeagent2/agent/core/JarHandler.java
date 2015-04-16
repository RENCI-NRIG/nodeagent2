package orca.nodeagent2.agent.core;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Handle the mechanics of loading the foreign jars
 * @author ibaldin
 *
 */
public class JarHandler {

	public static Class<?> loadJar(String name, String className) throws Exception {
		try {
			File f = new File(name);
			if (!f.exists())
				throw new Exception("Jar file " + name + " not found");
			String jarUrl = "jar:file:" + name + "!/";
			URLClassLoader childCl = new URLClassLoader(new URL[]{ new URL(jarUrl)} , JarHandler.class.getClassLoader());
			Class<?> classToLoad = (Class<?>)Class.forName (className, true, childCl);
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

}