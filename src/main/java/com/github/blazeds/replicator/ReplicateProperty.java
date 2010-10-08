/**
 * 
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
 *         Annotation that specifies a property of a class.
 * 
 */
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ReplicateProperty {

	/**
	 * The class owning the property.
	 * 
	 * @return
	 */
	Class<?> clazz();

	/**
	 * The name of the property.
	 * 
	 * @return
	 */
	String property();
}
