/*
 * Copyright 2002-2005 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.blazeds.replicator;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.beanlib.hibernate3.Hibernate3BeanReplicator;

import org.springframework.aop.support.AopUtils;

import flex.messaging.FactoryInstance;
import flex.messaging.messages.Message;
import flex.messaging.messages.RemotingMessage;
import flex.messaging.services.remoting.RemotingDestination;
import flex.messaging.services.remoting.adapters.JavaAdapter;

/**
 * @author Max Pimm
 * 
 *         Custom adapter that avoids serializing whole object graphs using
 *         beanlib and custom annotations.
 * 
 *         A method that returns a result serialized by Blaze DS may use the
 *         {@link ReplicateResult} annotation.
 * 
 *         This annotation defines:
 * 
 *         1) Whether uninitialized lazy collections should be fetched o
 *         excluded from the result
 * 
 *         2) A list of other properties to exclude from the result using the
 *         {@link ReplicateProperty} annotation.
 * 
 *         Internally this class uses beanlib to replicate the result replacing
 *         excluded properties and collections with null.
 * 
 *         See anotations {@link ReplicateResult} and {@link ReplicateProperty}
 */
public class HibernateAdapter extends JavaAdapter {

	Hibernate3BeanReplicator replicator;

	Map<Method, ReplicationPropertyFilter> filterCache;

	@Override
	public Object invoke(Message message) {

		// Init filter cache on first visit
		if (filterCache == null) {
			filterCache = new HashMap<Method, ReplicationPropertyFilter>();
		}

		// Invoke incomming service call via blazeds
		Object result = super.invoke(message);
		if (result == null) {
			return null;
		}

		// Get the method and check if filter is already in the cache
		Method method = getMethod(message);
		ReplicationPropertyFilter filter;
		if (filterCache.containsKey(method)) {

			filter = filterCache.get(method);
			if (filter == null) {
				// If no filter defined for this method do nothing
				return result;
			} else {
				replicator = new Hibernate3BeanReplicator(null, null, filter);
			}
		} else {

			// Get the annotation
			if (!method.isAnnotationPresent(ReplicateResult.class)) {
				// Add null value to cache to denote that the result from this
				// method does not need replication
				filterCache.put(method, null);
				return result;
			} else {
				// Initialize a new filter for this method using the replication
				// configuration
				ReplicateResult replicationConfig = method
						.getAnnotation(ReplicateResult.class);
				filter = new ReplicationPropertyFilter(replicationConfig);
				filterCache.put(method, filter);
				replicator = new Hibernate3BeanReplicator(null, null, filter);
			}
		}

		// Nullify lazy collections if fetch eager set to false
		if (!filter.isFetchEager()) {
			NullifyLazyTransformer.Factory factory = new NullifyLazyTransformer.Factory();
			replicator.initCustomTransformerFactory(factory);
		}

		result = replicator.copy(result);
		return result;
	}

	private Method getMethod(Message message) {

		// Get class of method to be invoked
		RemotingDestination remotingDestination = (RemotingDestination) getDestination();
		FactoryInstance factoryInstance = remotingDestination
				.getFactoryInstance();
		Object instance = createInstance(factoryInstance.getInstanceClass());
		Class<?> targetClass;
		if (AopUtils.isAopProxy(instance) || AopUtils.isCglibProxy(instance)) {
			targetClass = AopUtils.getTargetClass(instance);
		} else {
			targetClass = instance.getClass();
		}

		// Get method to be invoked
		String operation = ((RemotingMessage) message).getOperation();
		@SuppressWarnings("rawtypes")
		List parameters = ((RemotingMessage) message).getParameters();
		Method method = remotingDestination.getMethodMatcher().getMethod(
				targetClass, operation, parameters);

		return method;
	}
}
