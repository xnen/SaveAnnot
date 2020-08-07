package com.github.xnen.settings.settings.types.adapters;

public class MapAdapter<V> {
	@SuppressWarnings("unchecked")
	public <T> V safeGenericTypeCast(Object o) {
		return (V) o;
	}
}
