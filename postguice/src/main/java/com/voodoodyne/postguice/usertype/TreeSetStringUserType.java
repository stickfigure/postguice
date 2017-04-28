package com.voodoodyne.postguice.usertype;

import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;
import java.util.TreeSet;

/**
 * UserType which lets us store JsonNode in hibernate, converting to JSON natively.
 */
public class TreeSetStringUserType extends JSONUserType<List<String>> {
	public TreeSetStringUserType() {
		super(new TypeReference<TreeSet<String>>(){});
	}
}