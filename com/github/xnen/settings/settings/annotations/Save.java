package com.github.xnen.settings.settings.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;

/**
 * @author import
 * 
 * Basic Save annotation used to specify that a field's value should be updated and saved. Also specifies that the value should appear in a user-interface
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Save {
	public static final double DEFAULT_MAX = Double.MAX_VALUE;
	public static final double DEFAULT_MIN = 0.0;
	
	public double min() default 0.0;
	public double max() default DEFAULT_MAX;
	
	public boolean visible() default false;
	
	public String displayName() default "";
}
