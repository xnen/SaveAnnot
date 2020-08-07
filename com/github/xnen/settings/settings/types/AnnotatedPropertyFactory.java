package com.github.xnen.settings.settings.types;

import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.xnen.settings.settings.annotations.Save;

public class AnnotatedPropertyFactory {
	
	/**
	 * Create a SimpleProperty based on a field, the context it came from, and the annotation parameters
	 * 
	 * If the field already contains a SimpleProperty, it will simply be returned without creating a new one.
	 * 
	 */
	public static SimpleProperty createAnnotatedProperty(Save saveAnnotation, Object obj, Field field) throws IllegalArgumentException, IllegalAccessException {
		final Object o = field.get(obj);
		
		if (o instanceof SimpleProperty) {
			return (SimpleProperty) o;
		}

		if (saveAnnotation.max() != Save.DEFAULT_MAX || saveAnnotation.min() != Save.DEFAULT_MIN) {
			if (o instanceof Double) {
				return new ClampedProperty(obj.getClass(), saveAnnotation.displayName(), field.getName(), (Double) o, saveAnnotation.min(), saveAnnotation.max());
			} else if (o instanceof Float) {
				return new ClampedProperty(obj.getClass(), saveAnnotation.displayName(), field.getName(), (Float) o, saveAnnotation.min(), saveAnnotation.max());
			} else if (o instanceof Integer) {
				return new ClampedProperty(obj.getClass(), saveAnnotation.displayName(), field.getName(), (Integer) o, saveAnnotation.min(), saveAnnotation.max());
			} else {
				Logger.getAnonymousLogger().log(Level.SEVERE, "MinMax Annotation @" + obj.getClass() + "::" + field.getName() + " is not a float or double!");
			}
		}

		return new SimpleProperty(obj.getClass(), saveAnnotation.displayName(), field.getName(), o, saveAnnotation.min(), saveAnnotation.max());
	}
}
