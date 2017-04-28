package com.voodoodyne.postguice.usertype;

import com.fasterxml.jackson.core.type.TypeReference;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * UserType which lets us store a Map in hibernate, converting to JSON natively.
 */
public class MapUserType extends JSONUserType<Map<String, Object>> {
	public MapUserType() {
		super(new TypeReference<LinkedHashMap<String, Object>>(){});
	}
}