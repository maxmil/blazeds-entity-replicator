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
