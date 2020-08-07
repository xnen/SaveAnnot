package com.github.xnen.settings.settings.types;

import java.util.HashMap;
import java.util.List;

import com.github.xnen.settings.settings.types.adapters.MapAdapter;

public class MultiProperty extends SimpleProperty {

	/**
	 * @author import
	 *
	 * MultiProperty class to hold a map of objects to their property, and conveniently register their defaults
	 */
	
	private MultiProperty(Class<?> clazz, String name) {
		super(clazz, name, new HashMap<Object, SimpleProperty>());
	}
	
	public static MultiProperty create(Class<?> clazz, String name) {
		return new MultiProperty(clazz, name);
	}

	/**
	 * Convenience method to add a list of types to the property map
	 */
	public void addAllDefaults(List<?> objects, Object defaultValue) {
		objects.forEach(e -> this.addDefault(e, defaultValue));
	}

	/**
	 * Add a {@link SimpleProperty} with a generic default value to the property map
	 */
	public void addDefault(Object object, Object defaultValue) {
		this.getMap().put(object, new SimpleProperty(this.clazz, this.getKey() + ":" + object.getClass().getName(), defaultValue));
	}
	
	/**
	 * Add a {@link SimpleProperty} with a generic default value to the property map
	 */
	public void addDefaultClamped(String name, double object, double defaultValue, double min, double max) {
		this.getMap().put(object, new SimpleProperty(this.clazz, this.getKey() + ":" + name, defaultValue, min, max));
	}

	/**
	 * Add an object with a specific property type to the property map
	 */
	public void addDefaultProperty(Object object, SimpleProperty property) {
		this.getMap().put(object, property);
	}

	/**
	 * Attempt to get the value from the inputed object
	 */
	public SimpleProperty grabEntryValue(Object property) {
		return this.getMap().get(property);
	}

	@Override
	public Object setValue(Object value) {
		new Exception("MultiProperty does not support setting values!").printStackTrace();
		return this.getValue();
	}

	private HashMap<Object, SimpleProperty> getMap() {
		return this.adapter.safeGenericTypeCast(this.getValue());
	}
	
	@Override
	public boolean hasUpdates() {
		return false; // MultiProperties are mutable and fields do not need updated by the registrator
	}
	
	private MapAdapter<HashMap<Object, SimpleProperty>> adapter = new MapAdapter<>();
}