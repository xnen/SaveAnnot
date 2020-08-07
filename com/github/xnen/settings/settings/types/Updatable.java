package com.github.xnen.settings.settings.types;

public interface Updatable {
	public boolean hasUpdates();
	public void handledUpdate();
}
