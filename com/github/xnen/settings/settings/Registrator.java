package com.github.xnen.settings.settings;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.xnen.settings.settings.annotations.Save;
import com.github.xnen.settings.settings.types.AnnotatedPropertyFactory;
import com.github.xnen.settings.settings.types.MultiProperty;
import com.github.xnen.settings.settings.types.SimpleProperty;
import com.github.xnen.settings.settings.types.adapters.FieldTypeAdapter;

public class Registrator {

	private final Settings settings;

	/**
	 * @author import
	 * 
	 * Registrator class for the 'Save' annotation. Used to track and update values that are annotated and update the UI Bridge {@link SimpleProperty} objects.
	 * 
	 */
	public Registrator(Settings settings) {
		this.settings = settings;
	}

	public List<Object> getRegisteredSettingListeners() {
		return registeredSettingListeners;
	}

	private SimpleProperty matchField(Class<?> clazz, Save save, Field field) {
		for (SimpleProperty simpleProperty : this.settings.getUIBridge()) {
			if (simpleProperty.hasUpdates() && simpleProperty.getParentClass().equals(clazz) && simpleProperty.getName() != null && (simpleProperty.getDisplayName().equals(field.getName()) || simpleProperty.getDisplayName().equals(save.displayName()))) {
				return simpleProperty;
			}
		}

		return null;
	}

	private Field[] getAllFields(Class<?> clazz) {
		Field[] declaredFields = clazz.getDeclaredFields();
		Field[] clazzFields = clazz.getFields();

		if (!clazz.getSuperclass().getCanonicalName().equals("java.lang.Object")) {
			Field[] moreFields = getAllFields(clazz.getSuperclass());
			return ArrayUtils.addAll(ArrayUtils.addAll(declaredFields, clazzFields), moreFields);
		}

		return ArrayUtils.addAll(declaredFields, clazzFields);
	}

	public void pollFields() {
		for (Object obj : this.registeredSettingListeners) {
			Class<?> clazz = obj.getClass();

			List<String> referenceNames = new ArrayList<>();
			
			for (Field field : this.getAllFields(clazz)) {
				Save s = field.getAnnotation(Save.class);
				if (s == null) continue;

				boolean accessible = Registrator.ensureAccessible(field);
				SimpleProperty simpleProperty = null;

				try {
					simpleProperty = AnnotatedPropertyFactory.createAnnotatedProperty(s, obj, field);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}

				if (simpleProperty != null) {
					Object mapObj;

					if (simpleProperty instanceof MultiProperty) {
						mapObj = (MultiProperty) simpleProperty;
					} else {
						mapObj = simpleProperty.getValue();
					}

					String key = this.getKeyFor(obj, field, s);

					boolean flag = false;

					for (SimpleProperty sp : this.settings.getUIBridge()) {
						if (sp.getParentClass().equals(simpleProperty.getParentClass()) && sp.getName().equals(simpleProperty.getName())) {
							flag = true;
						}
					}

					if (!flag) {
						if (s.visible()) {
							if (!referenceNames.contains(simpleProperty.getName())) {
								this.settings.getUIBridge().add(simpleProperty);
								referenceNames.add(simpleProperty.getName());
							} else {
								Logger.getAnonymousLogger().log(Level.WARNING, "Duplicate Annotation Name found for field " + clazz.getName() + "::" + field.getName());
							}
						}
					}

					if (!this.settings.getMap().containsKey(key)) {
						this.settings.getMap().put(key, mapObj);
					}

					field.setAccessible(accessible);
				}
			}
		}
	}

	/**
	 * Loop all registered setting listeners and set their fields with values loaded from GSON in the simple setting map
	 */
	public void setAllFields() {
		for (Object obj : this.registeredSettingListeners) {
			Class<?> clazz = obj.getClass();

			for (Field field : this.getAllFields(clazz)) {
				Save s = field.getAnnotation(Save.class);
				if (s == null) continue;

				Object mapValue = this.settings.getMap().get(this.getKeyFor(obj, field, s));

				if (mapValue != null) {
					boolean accessible = Registrator.ensureAccessible(field);
					boolean fieldSet = this.fieldTypeAdapter.adaptAndAssign(field, obj, mapValue);

					if (!fieldSet) {
						try {
							System.out.println("Unable to set field " + field.getName());
							this.settings.getMap().put(this.getKeyFor(obj, field, s), field.get(obj));
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						}
					}
					field.setAccessible(accessible);
				} else {
					System.out.println("No data for key " + this.getKeyFor(obj, field, s));
				}
			} 
		}
	}

	/**
	 * Loop all registered listeners for properties that request to be updated to their respective fields
	 */
	public void updateFields() {		
		boolean b = false;
		for (Object obj : this.registeredSettingListeners) {
			Class<?> clazz = obj.getClass();

			for (Field field : this.getAllFields(clazz)) {
				Save s = field.getAnnotation(Save.class);
				if (s == null) continue;

				SimpleProperty sp = this.matchField(clazz, s, field);

				if (sp != null) {
					b = true;
					boolean accessible = Registrator.ensureAccessible(field);
					boolean fieldSet = this.fieldTypeAdapter.adaptAndAssign(field, obj, sp.getValue()); 

					if (!fieldSet) {
						try {
							this.settings.getMap().put(this.getKeyFor(obj, field, s), field.get(obj));
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						}
					}

					field.setAccessible(accessible);
					sp.handledUpdate();
				}
			}
		}
		if (!b) { 
            // TODO ** Error handling
			System.out.println("Failure: Could not set Field.");
		}
	}

	private String getKeyFor(Object instance, Field field, Save save) {
		return instance.getClass().getName() + ":" + field.getName() + ":" + save.displayName();
	}

	/**
	 * @return if the registrator has any classes registered
	 */
	public boolean hasRegisteredClasses() {
		return this.registeredSettingListeners.size() > 0;
	}

	public static boolean ensureAccessible(Field field) {
		boolean b = field.isAccessible();
		field.setAccessible(true);
		return b;
	}

	private FieldTypeAdapter fieldTypeAdapter = new FieldTypeAdapter();
	private List<Object> registeredSettingListeners = new ArrayList<>();

}
