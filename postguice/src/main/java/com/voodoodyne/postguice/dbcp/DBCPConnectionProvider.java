package com.voodoodyne.postguice.dbcp;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.hibernate.HibernateException;
import org.hibernate.cfg.Environment;
import org.hibernate.engine.jdbc.connections.internal.ConnectionProviderInitiator;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.service.UnknownUnwrapTypeException;
import org.hibernate.service.spi.Configurable;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

/**
 * If you want to use DBCP as a connection pool on Google App Engine, you can use the old DBCP 1.4
 * and this provider for JPA.
 *
 * See persistence.xml (and hardcoded defaults below) for configuration. If you want to change the hardcoded defaults
 * below, subclass this and override the values in the constructor.
 *
 * <pre>
 * {@code
 *  <property name="hibernate.connection.provider_class" value="com.voodoodyne.postguice.dbcp.DBCPConnectionProvider" />
 * }
 * </pre>
 *
 * @link http://grepcode.com/file_/repo1.maven.org/maven2/hibernate/hibernate/2.1.8/net/sf/hibernate/connection/DBCPConnectionProvider.java/?v=source
 * @link https://github.com/brettwooldridge/HikariCP/blob/dev/src/main/java/com/zaxxer/hikari/hibernate/HikariConnectionProvider.java
 */
@Slf4j
public class DBCPConnectionProvider implements ConnectionProvider, Configurable {

	private static final long serialVersionUID = 1L;

	private Integer isolation;
	private DataSource dataSource;

	private static final String validationQuery = "SELECT 1";

	@Getter @Setter
	private int dbcpMaxActive = 20;
	@Getter @Setter
	private byte dbcpWhenExhausted = 1;
	@Getter @Setter
	private long dbcpMaxWait = 60000;
	@Getter @Setter
	private int dbcpMaxIdle = 5;
	@Getter @Setter
	private boolean dbcpValidationOnBorrow = true;
	@Getter @Setter
	private boolean dbcpValidationOnReturn = true;

	@Override
	public Connection getConnection() throws SQLException {
		final Connection connection = dataSource.getConnection();

		if (isolation != null) {
			connection.setTransactionIsolation(isolation);
		}
		if (connection.getAutoCommit()) {
			connection.setAutoCommit(false);
		}

		return connection;
	}

	@Override
	public void configure(@SuppressWarnings("rawtypes") Map configurationValues) {
		final String jdbcDriverClass = (String) configurationValues.get(Environment.DRIVER);
		final String jdbcUrl = (String) configurationValues.get(Environment.URL);

		log.info("DBCP using driver: " + jdbcDriverClass + " at URL: " + jdbcUrl);
		log.info("Connection properties: " + configurationValues);

		if (jdbcDriverClass == null) {
			log.warn("No JDBC Driver class was specified by property " + Environment.DRIVER);
		} else {
			try {
				Class.forName(jdbcDriverClass);
			} catch (ClassNotFoundException e) {
				final String msg = "JDBC Driver class not found: " + jdbcDriverClass;
				log.error(msg);
				throw new HibernateException(msg);
			}
		}

		try {
			final ObjectPool connectionPool = new GenericObjectPool(
					null,
					dbcpMaxActive,
					dbcpWhenExhausted,
					dbcpMaxWait,
					dbcpMaxIdle,
					dbcpValidationOnBorrow,
					dbcpValidationOnReturn
			);

			final Properties connectionProps = new Properties();
			for (final Object key : configurationValues.keySet()) {
				connectionProps.put(key, configurationValues.get(key));
			}
			final String user = (String) configurationValues.get(Environment.USER);
			final String pass = (String) configurationValues.get(Environment.PASS);
			connectionProps.put("user", user);
			connectionProps.put("password", pass);

			final ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(jdbcUrl, connectionProps);

			new PoolableConnectionFactory(
					connectionFactory,
					connectionPool,
					null,
					validationQuery,
					false,
					false);

			this.dataSource = new PoolingDataSource(connectionPool);

		} catch (Exception e) {
			log.error("Could not instantiate DBCP connection pool", e);
			throw new HibernateException("Could not instantiate DBCP connection pool", e);
		}

		final String i = (String) configurationValues.get(Environment.ISOLATION);
		if (i == null) {
			isolation = null;
		} else {
			isolation = new Integer(i);
			log.info("JDBC isolation level: " + ConnectionProviderInitiator.toIsolationNiceName(isolation));
		}
	}

	@Override
	public void closeConnection(Connection conn) throws SQLException {
		conn.close();
	}

	@Override
	public boolean supportsAggressiveRelease() {
		return false;
	}

	@Override
	public boolean isUnwrappableAs(@SuppressWarnings("rawtypes") Class unwrapType) {
		return ConnectionProvider.class.equals(unwrapType) || DBCPConnectionProvider.class.isAssignableFrom(unwrapType);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T unwrap(Class<T> unwrapType) {
		if (ConnectionProvider.class.equals(unwrapType) || DBCPConnectionProvider.class.isAssignableFrom(unwrapType)) {
			return (T) this;
		}
		if (DataSource.class.isAssignableFrom(unwrapType)) {
			return (T) this.dataSource;
		}
		throw new UnknownUnwrapTypeException(unwrapType);
	}
}
