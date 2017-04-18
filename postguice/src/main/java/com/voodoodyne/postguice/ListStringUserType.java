package com.voodoodyne.postguice;

import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;

/**
 * UserType which lets us store JsonNode in hibernate, converting to JSON natively.
 */
public class ListStringUserType extends JSONUserType<List<String>> {
	public ListStringUserType() {
		super(new TypeReference<List<String>>(){});
	}
}