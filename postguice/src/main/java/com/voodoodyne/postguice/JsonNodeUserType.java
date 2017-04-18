package com.voodoodyne.postguice;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * UserType which lets us store JsonNode in hibernate, converting to JSON natively.
 */
public class JsonNodeUserType extends JSONUserType<JsonNode> {
	public JsonNodeUserType() {
		super(JsonNode.class);
	}
}