package org.renci.nodeagent2.agentlib;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Because of type erasure and the need to use reflection in plugins, we need
 * to define this as a class
 * @author ibaldin
 *
 */
public class Properties implements Map<String, String>, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Map<String, String> map = new HashMap<String, String>();
	
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		for (Map.Entry<String,String> e: map.entrySet()) {
			sb.append("  " + e.getKey() + ": " + e.getValue() + ", ");
		}
		return sb.toString();
	}

	public int size() {
		return map.size();
	}


	public boolean isEmpty() {
		return map.isEmpty();
	}


	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}


	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}


	public String get(Object key) {
		return map.get(key);
	}


	public String put(String key, String value) {
		return map.put(key, value);
	}


	public String remove(Object key) {
		return map.remove(key);
	}


	public void putAll(Map<? extends String, ? extends String> m) {
		map.putAll(m);
	}


	public void clear() {
		map.clear();
	}


	public Set<String> keySet() {
		return map.keySet();
	}


	public Collection<String> values() {
		return map.values();
	}


	public Set<java.util.Map.Entry<String, String>> entrySet() {
		return map.entrySet();
	}
}
