package com.voodoodyne.postguice;

import com.google.common.base.Preconditions;
import org.hibernate.QueryException;
import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.type.BooleanType;
import org.hibernate.type.Type;

import java.util.List;

/**
 * Expects two arguments - the name of the field, and the search query.
 * Always uses 'simple' config.
 */
public class SimpleFullTextSearchFunction implements SQLFunction {
	@Override
	public boolean hasArguments() {
		return true;
	}

	@Override
	public boolean hasParenthesesIfNoArguments() {
		return false;
	}

	@Override
	public Type getReturnType(final Type firstArgumentType, final Mapping mapping) throws QueryException {
		return new BooleanType();
	}

	@Override
	public String render(final Type firstArgumentType, final List arguments, final SessionFactoryImplementor factory) throws QueryException {
		Preconditions.checkState(arguments.size() == 2, "The function must be passed 2 arguments: field, query");

		final String field = (String)arguments.get(0);
		final String query = (String)arguments.get(1);

		return String.format("to_tsvector('simple', %s) @@ to_tsquery('simple', %s)", field, query);
	}
}