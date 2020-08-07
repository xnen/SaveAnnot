package com.github.xnen.settings.settings.types.adapters;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.github.xnen.settings.settings.types.StringStorage;
import com.google.gson.internal.LinkedTreeMap;

public class FieldTypeAdapter {

	@SuppressWarnings("unchecked")
	public boolean adaptAndAssign(Field field, Object context, Object obj) {
		try {
			if (field.getType().getName().equals(boolean.class.getName())) {
				try {
					field.setBoolean(context, (boolean) obj);
				} catch (ClassCastException e) {
					field.setBoolean(context, Boolean.parseBoolean("" + obj));
				}
			} else if (field.getType().getName().equals(byte.class.getName())) {
				try {
					field.setByte(context, ((Double) obj).byteValue());
				} catch (ClassCastException e) {
					field.setByte(context, Byte.parseByte("" + obj));
				}
			} else if (field.getType().getName().equals(char.class.getName())) {
				field.setChar(context, (char) obj);
			} else if (field.getType().getName().equals(double.class.getName())) {
				try {
					field.setDouble(context, ((Double) obj).doubleValue());
				} catch (ClassCastException e) {
					field.setDouble(context, Double.parseDouble("" + obj));
				}
			} else if (field.getType().getName().equals(float.class.getName())) {
				try {
					field.setFloat(context, ((Double) obj).floatValue());
				} catch (ClassCastException e) {
					field.setFloat(context, Float.parseFloat("" + obj));
				}
			} else if (field.getType().getName().equals(int.class.getName())) {
				try {
					field.setInt(context,  ((Double) obj).intValue());
				} catch (ClassCastException e) {
					field.setInt(context, Integer.parseInt("" + obj));
				}
			} else if (field.getType().getName().equals(long.class.getName())) {
				try {
					field.setLong(context, ((Double) obj).longValue());
				} catch (ClassCastException e) {
					field.setLong(context, Long.parseLong("" + obj));
				}
			} else if (field.getType().getName().equals(short.class.getName())) {
				try {
					field.setShort(context, ((Double) obj).shortValue());
				} catch (ClassCastException e) {
					field.setShort(context, Short.parseShort("" + obj));
				}
			} else if (field.getType().getCanonicalName().equals("java.lang.String[]")) {
				try {
					List<String> newArrayList = (List<String>) obj;
					field.set(context, newArrayList.toArray(new String[newArrayList.size()]));
				} catch (ClassCastException e) {
					e.printStackTrace();
					return false;
				}
			} else if (obj instanceof StringStorage) {
					field.set(context, obj);
			} else if (field.getType().getCanonicalName().equals("java.util.List")) {
				field.set(context, obj);
			} else if (field.getType().getCanonicalName().equals("java.lang.String")) {
				field.set(context, obj);
			} else {
			 	System.out.println("Unknown field type: " + field.getType().getCanonicalName());
				field.set(context, obj);
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return false;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}
}
