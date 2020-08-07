package com.github.xnen.settings.settings.types;

import java.util.Map.Entry;

import com.github.xnen.settings.settings.Settings;

public class SimpleProperty implements Entry<String, Object>, Updatable {

	private String displayName;
	
	private String key;
	private Object value;
	
	protected Class<?> clazz;
	
	private boolean updated;
	
	private double min;
	private double max;
	
	private boolean minMaxSet;
	
	public SimpleProperty(Class<?> clazz, String displayName, String name, Object value) {
		this.key = name;
		this.displayName = displayName;
		this.value = value;
		this.clazz = clazz;
	}
	
	public SimpleProperty(Class<?> clazz, String name, Object value) {
		this.key = name;
		this.displayName = name;
		this.value = value;
		this.clazz = clazz;
	}
	
	
	public SimpleProperty(Class<?> clazz, String name, Object value, double min, double max) {
		this(clazz, name, value);
		
		this.min = min;
		this.max = max;
		this.minMaxSet = true;
	}
	
	public SimpleProperty(Class<?> clazz, String displayName, String name, Object value, double min, double max) {
		this(clazz, displayName, name, value);
		
		this.min = min;
		this.max = max;
		this.minMaxSet = true;
	}
	
	public String getDisplayName() {
		return displayName == null ? this.getName() : this.displayName;
	}
	
	public boolean isMinMaxSet() {
		return minMaxSet;
	}
	
	public double getMax() {
		return max;
	}
	
	public double getMin() {
		return min;
	}
	
	public Class<?> getParentClass() {
		return this.clazz;
	}

	@Override
	public String getKey() {
		return this.key;
	}

	@Override
	public Object getValue() {
		return this.value;
	}
	
	/**
	 * Alias method for getKey()
	 */
	public String getName() {
		return this.getKey();
	}
	
	@Override
	public Object setValue(Object value) {
		this.value = value;
		this.updated = true;
		
		// UI (probably) updated the Entry, update the actual field in the class
		Settings.getInstance().getRegistrator().updateFields();
		
		return this.value;
	}

	@Override
	public boolean hasUpdates() {
		return this.updated;
	}

	@Override
	public void handledUpdate() {
		this.updated = false;
	}
	
	public ValueType getType() {
		try {
			boolean b = (boolean) this.value;
			return ValueType.BOOLEAN;
		} catch (ClassCastException e) {}
		try {
			int i = (int) this.value;
			return ValueType.INT;
		} catch (ClassCastException e) {}
		try {
			double d = (double) this.value;
			return ValueType.DOUBLE;
		} catch (ClassCastException e) {}
		try {
			StringStorage s = (StringStorage) this.value;
			return ValueType.STRINGSTORAGE;
		} catch (ClassCastException e) {}
		
		return ValueType.UNKNOWN;
	}
	
	public enum ValueType {
		BOOLEAN,
		INT,
		DOUBLE,
		STRINGSTORAGE,
		UNKNOWN
	}
}
