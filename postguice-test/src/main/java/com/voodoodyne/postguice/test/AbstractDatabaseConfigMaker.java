package com.voodoodyne.postguice.test;

import com.voodoodyne.postguice.DatabaseConfig;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.Driver;

import javax.inject.Provider;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Map;

/**
 * This allows us to clone a template database for testing.  By treating the credentials as singleton
 * in the test harness, we get a fresh database for every test run.
 *
 * In your test module, bind your concrete subclass like this:
 * this.bind(DatabaseConfig.class).toProvider(DatabaseConfigMaker.class).in(Singleton.class);
 */
@Slf4j
abstract public class AbstractDatabaseConfigMaker implements Provider<DatabaseConfig> {

	/** */
	abstract protected DatabaseConfig templateDatabaseConfig();

	@Override
	@SneakyThrows
	public DatabaseConfig get() {
		final DatabaseConfig templateConfig = templateDatabaseConfig();

		final String templateDatabaseName = templateConfig.getUrl().split(":")[2];
		final String cloneDatabaseName = "zz_" + System.currentTimeMillis();

		log.info("********** Creating test database: " + cloneDatabaseName);

		Class.forName(Driver.class.getName());

		try (final Connection conn = DriverManager.getConnection(templateConfig.getUrl(), templateConfig.getUser(), templateConfig.getPassword())) {
			try (final Statement statement = conn.createStatement()) {
				statement.execute("CREATE DATABASE " + cloneDatabaseName + " TEMPLATE \"" + templateDatabaseName + "\"");
			}
		}

		return new DatabaseConfig() {
			@Override
			public String getUrl() {
				return "jdbc:postgresql:" + cloneDatabaseName;
			}

			@Override
			public String getUser() {
				return templateConfig.getUser();
			}

			@Override
			public String getPassword() {
				return templateConfig.getPassword();
			}

			@Override
			public Map<String, String> getProperties() {
				return templateConfig.getProperties();
			}
		};
	}

}
