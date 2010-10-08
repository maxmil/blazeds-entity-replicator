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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Max Pimm
 * 
 *         Annotation that defines which properties in the object graph returned
 *         by this method should be excluded from replication.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ReplicateResult {

	/**
	 * Properties that should be excluded from replication.
	 * 
	 * @return
	 */
	ReplicateProperty[] exclude() default {};

	/**
	 * When false (default value) lazy associations that have not been
	 * initialized will be excluded.
	 * 
	 * @return
	 */
	boolean fetchEager() default true;
}
