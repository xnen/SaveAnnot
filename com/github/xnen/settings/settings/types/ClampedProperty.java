package com.github.xnen.settings.settings.types;

public class ClampedProperty extends SimpleProperty {

	private double min, max;
	
	public ClampedProperty(Class<?> clazz, String name, double value, double min, double max) {
		super(clazz, name, value);
		this.min = min;
		this.max = Math.max(this.min, max);
	}
	
	public ClampedProperty(Class<?> clazz, String displayName, String name, double value, double min, double max) {
		super(clazz, displayName, name, value);
		this.min = min;
		this.max = Math.max(this.min, max);
	}
	
	public double getMin() {
		return min;
	}
	
	public double getMax() {
		return max;
	}
}
