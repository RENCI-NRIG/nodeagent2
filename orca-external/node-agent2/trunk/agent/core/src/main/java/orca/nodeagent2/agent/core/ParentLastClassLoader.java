package orca.nodeagent2.agent.core;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

/**
 * From http://stackoverflow.com/questions/5445511/how-do-i-create-a-parent-last-child-first-classloader-in-java-or-how-to-overr
 * 
 * A parent-last classloader that will try the child classloader first and then the parent.
 * This takes a fair bit of doing because java really prefers parent-first.
 * 
 * For those not familiar with class loading trickery, be wary
 */
class ParentLastClassLoader extends ClassLoader {
	private ChildURLClassLoader childClassLoader;

	/**
	 * This class allows me to call findClass on a classloader
	 */
	private static class FindClassClassLoader extends ClassLoader {
		public FindClassClassLoader(ClassLoader parent) {
			super(parent);
		}

		@Override
		public Class<?> findClass(String name) throws ClassNotFoundException {
			return super.findClass(name);
		}
	}

	/**
	 * This class delegates (child then parent) for the findClass method for a URLClassLoader.
	 * We need this because findClass is protected in URLClassLoader
	 */
	private static class ChildURLClassLoader extends URLClassLoader {
		private FindClassClassLoader realParent;

		public ChildURLClassLoader( URL[] urls, FindClassClassLoader realParent ) {
			super(urls, null);

			this.realParent = realParent;
		}

		String[] checkParent = { "org.slf4j", "orca.nodeagent2.agentlib", "org.apache.commons.logging" };
		
		@Override
		public Class<?> findClass(String name) throws ClassNotFoundException
		{
			System.out.println("LOOKING IN CHILD " +name );
			try {
				Class<?> loaded = super.findLoadedClass(name);
	            if ( loaded != null )
	                return loaded;
	            
	            for (String ch: checkParent) {
	            	if (name.startsWith(ch))
	            		throw new ClassNotFoundException();
	            }

				// first try to use the URLClassLoader findClass
				return super.findClass(name);
			} catch( ClassNotFoundException e ) {
				// if that fails, we ask our real parent classloader to load the class (we give up)
				return realParent.loadClass(name);
			}
		}
	}

	public ParentLastClassLoader(List<URL> classpath) {
		super(Thread.currentThread().getContextClassLoader());

		URL[] urls = classpath.toArray(new URL[classpath.size()]);

		childClassLoader = new ChildURLClassLoader( urls, new FindClassClassLoader(this.getParent()) );
	}

	@Override
	protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		try {
			System.out.println("LOADING CLASS " + name);
			// first we try to find a class inside the child classloader
			return childClassLoader.findClass(name);
		} catch( ClassNotFoundException e ) {
			// didn't find it, try the parent
			System.out.println("LOOKING IN PARENT " + name); 
			return super.loadClass(name, resolve);
		}
	}
}
