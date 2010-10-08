package com.github.blazeds.replicator;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sf.beanlib.spi.PropertyFilter;

/**
 * @author Max Pimm
 * 
 *         Property filter used by beanlib to veto the replication of properties
 *         in an object graph
 * 
 */
public class ReplicationPropertyFilter implements PropertyFilter {

	private Map<Class<?>, Set<String>> excludes;

	private boolean fetchEager;

	public ReplicationPropertyFilter(ReplicateResult replicationConfig) {
		super();
		fetchEager = replicationConfig.fetchEager();
		excludes = new HashMap<Class<?>, Set<String>>();
		for (ReplicateProperty prop : replicationConfig.exclude()) {
			addToMap(excludes, prop.clazz(), prop.property());
		}
	}

	@Override
	public boolean propagate(String propertyName, Method readerMethod) {

		Class<?> clazz = readerMethod.getDeclaringClass();
		
		if (excludes.size() > 0) {
			return !contains(excludes, clazz, propertyName);
		} else {
			return true;
		}
	}

	private void addToMap(Map<Class<?>, Set<String>> map, Class<?> clazz,
			String property) {
		Set<String> propNames;
		if (map.containsKey(clazz)) {
			propNames = map.get(clazz);
			propNames.add(property);
		} else {
			propNames = new HashSet<String>();
			propNames.add(property);
			map.put(clazz, propNames);
		}
	}

	private boolean contains(Map<Class<?>, Set<String>> map, Class<?> clazz,
			String property) {
		if (!map.containsKey(clazz)) {
			return false;
		} else {
			return map.get(clazz).contains(property);
		}
	}

	public boolean isFetchEager() {
		return fetchEager;
	}

}
