package com.github.xnen.settings.settings;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;

import com.github.xnen.settings.settings.types.SimpleProperty;
import com.google.common.base.Verify;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class Settings {

	private Registrator registrator;
	private final File settingsFile;

	private Settings(String fileName) {
		this.settingsFile = new File(fileName);
	}

	private void init() {
		this.registrator = new Registrator(this);
	}

	/**
	 * Register a class for saving, loading, and updating fields that are annotated with {@link net.gigantic.hack.settingsnew.annotations.Save}
	 */
	public void register(Object annotatedClassObject) {
		Verify.verifyNotNull(annotatedClassObject);
		this.getRegistrator().getRegisteredSettingListeners().add(annotatedClassObject);
	}

	/**
	 * Save the map to the file specified in the constructor of {@link Settings}
	 * @throws IOException if unable to write json file
	 */
	public void save() {
		try {	
			// Clear and update with latest field data to save
			this.clearSettings();
			this.getRegistrator().pollFields();
			
			FileWriter writer = new FileWriter(this.settingsFile);
			writer.write(new GsonBuilder().setPrettyPrinting().create().toJson(this.map));
			writer.close();
		} catch (Exception e) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "Could not save settings.");
			e.printStackTrace();
		}
	}

	/**
	 * Load the map from a previously saved JSON file as specified in the constructor of {@link Settings}
	 * If the file could not be loaded (IOException), a new map will be created and used.
	 */
	public void load() {
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(this.settingsFile));
			this.map = new GsonBuilder().setPrettyPrinting().create().fromJson(bufferedReader, this.getMapParameters());
			bufferedReader.close();
		} catch (IOException exception) {
			if (exception instanceof FileNotFoundException) {
				this.clearSettings();
				Logger.getAnonymousLogger().log(Level.INFO, "No settings were found!");
			}
		}
		
		this.getRegistrator().pollFields();

		// Check if the registrator has any classes, and loop all fields to set their loaded values
		if (this.getRegistrator().hasRegisteredClasses()) {
			this.getRegistrator().setAllFields();
		} else {
			Logger.getAnonymousLogger().log(Level.WARNING, "No classes were registered in the Settings system!");
		}

		// Save to update any malformed objects to their new data types
		this.save();
	}

	/**
	 * @return the basic setting map which stores most data related to fields in the client
	 */
	public Map<String, Object> getMap() {
		return this.map;
	}

	/**
	 * @return a simple list of properties used for displays and modifications in a user interface
	 */
	public List<SimpleProperty> getUIBridge() {
		return this.uiBridge;
	}

	public void clearSettings() {
		this.map.clear();
		this.uiBridge.clear();
	}

	// Basic map to store data related to fields in the client
	private Map<String, Object> map = new HashMap<>();

	// List to update and save settings from a user interface
	private List<SimpleProperty> uiBridge = new ArrayList<>();

	/** Main class registrator for client settings, contains methods to read and write to annotated fields */
	public Registrator getRegistrator() {
		return this.registrator;
	}

	private Type getMapParameters() {
		return new TypeToken<Map<String, Object>>() { /* unused */ }.getType();
	}

	/* Settings singleton */
	public static Settings settings;

	public static Settings createInstance(String fileName) {
		if (settings == null) {
			settings = new Settings(fileName);
		}
		
		settings.init();
		settings.clearSettings();
		
		return settings;
	}

	public static Settings getInstance() {
		if (settings == null) { 
			throw new NullPointerException("Settings was not properly instantiated! Call 'createInstance' first with the settings file name.");
		}

		return settings;
	}
}
