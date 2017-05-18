package com.voodoodyne.postguice;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.hibernate.Session;

import javax.inject.Singleton;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Singleton which will efficiently batch sequence values so most calls do not require database hits
 */
@Singleton
@RequiredArgsConstructor
public class Sequence {
	private final String name;
	private final int batchSize;

	private final Queue<Long> pending = new ArrayDeque<>();

	/** Get the next sequence, possibly filling the batch */
	public synchronized long next() {
		if (pending.isEmpty()) {
			addBatch();
		}

		return pending.remove();
	}

	@SneakyThrows
	private void addBatch() {
		final String query = "select nextval('" + name + "') from generate_series(1, " + batchSize + ")";

		final Session session = EM.em().unwrap(Session.class);
		session.doWork(connection -> {
			try (final PreparedStatement stmt = connection.prepareStatement(query)) {
				try (final ResultSet rs = stmt.executeQuery()) {
					while (rs.next()) {
						final long value = rs.getLong(1);
						pending.add(value);
					}
				}
			}
		});
	}
}