package com.voodoodyne.postguice.test;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ContainerExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Store;

/**
 * Creates a template database with the latest migrations
 */
public class FlywayMigrator implements BeforeAllCallback{
	@Override
	public void beforeAll(final ContainerExtensionContext context) throws Exception {
		final Store store = context.getStore();

		final Flyway flyway = new Flyway();
		//flyway.setDataSource();
	}
}