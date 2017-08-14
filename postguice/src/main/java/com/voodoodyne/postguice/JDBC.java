/*
 */

package com.voodoodyne.postguice;

import lombok.SneakyThrows;
import org.hibernate.Session;

import javax.persistence.Query;
import java.sql.Array;
import java.util.Collection;

/**
 * Utilities for working with JDBC
 */
public class JDBC
{
	/**
	 * @return a JDBC text array of the given values
	 */
	@SneakyThrows
	public static Array textArray(final Query query, final Collection<String> values) {
		return query.unwrap(Session.class)
				.doReturningWork(connection -> connection.createArrayOf("text", values.toArray(new String[values.size()])));
	}
}
