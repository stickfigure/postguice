package com.voodoodyne.postguice;

import java.util.Map;

/**
 * Bind an implementation of this to define your database configuration
 */
public interface DatabaseConfig {
	String getUrl();

	String getUser();

	String getPassword();

	Map<String, String> getProperties();
}
