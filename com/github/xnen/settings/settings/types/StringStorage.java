package com.github.xnen.settings.settings.types;

public class StringStorage {

	private int currentIndex;
	private String[] array;

	public StringStorage(int defaultIndex, String...strings) {
		this.array = strings;
		this.currentIndex = defaultIndex;
	}
	
	public int getCurrentIndex() {
		return currentIndex;
	}
	
	public void increment() {
		if (++this.currentIndex >= array.length) {
			this.currentIndex = 0;
		}
	}

	public void decrement() {
		if (--this.currentIndex < 0) {
			this.currentIndex = array.length - 1;
		}
	}

	public void setIndex(int index) {
		this.currentIndex = index;
	}
	
	public String getString() {
		return this.array[this.currentIndex];
	}

	public int length() {
		return this.array.length;
	}
}
