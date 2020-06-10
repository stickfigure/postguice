package com.voodoodyne.postguice.usertype;

import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;

/**
 * UserType which lets us store JsonNode in hibernate, converting to JSON natively.
 */
public class ListStringJSONUserType extends JSONUserType<List<String>> {
	public ListStringJSONUserType() {
		super(new TypeReference<List<String>>(){});
	}
}