package com.voodoodyne.postguice;

import org.hibernate.dialect.PostgreSQL95Dialect;

import java.sql.Types;

/**
 * Apparently hibernate's 9.5 dialect doesn't support jsonb. Weird.
 */
public class PostguicePostgreSQL96Dialect extends PostgreSQL95Dialect {
	public PostguicePostgreSQL96Dialect() {
		this.registerColumnType(Types.JAVA_OBJECT, "jsonb");
	}
}