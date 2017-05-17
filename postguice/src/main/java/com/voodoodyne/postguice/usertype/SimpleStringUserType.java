package com.voodoodyne.postguice.usertype;

import com.google.common.base.Objects;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * Hibernate {@link UserType} that maps to a simple string column. Your class must be immutable.
 *
 * Create a subclass for any specific type you want converted to JSON.
 *
 * @param <T> is the type of the class you want to convert to string with this
 */
abstract public class SimpleStringUserType<T extends Serializable> implements UserType, Serializable {

	private static final long serialVersionUID = 2020526952561198860L;

	private static final int[] SQL_TYPES = new int[]{Types.VARCHAR};

	private final Class<T> classType;

	public SimpleStringUserType(final Class<T> classType) {
		this.classType = classType;
	}

	/** Implement this to convert from the string value in the column to your user type */
	protected abstract T construct(final String value);

	@Override
	public Object assemble(Serializable cached, Object owner) throws HibernateException {
		return cached;
	}

	@Override
	public Object deepCopy(Object value) throws HibernateException {
		return value;
	}

	@Override
	public Serializable disassemble(Object value) throws HibernateException {
		return (Serializable)value;
	}

	@Override
	public boolean equals(Object x, Object y) throws HibernateException {
		return Objects.equal(x, y);
	}

	@Override
	public int hashCode(Object x) throws HibernateException {
		return Objects.hashCode(x);
	}

	@Override
	public boolean isMutable() {
		return false;
	}

	@Override
	public Object nullSafeGet(final ResultSet rs, final String[] names, final SharedSessionContractImplementor session, final Object owner) throws HibernateException, SQLException {
		String value = rs.getString(names[0]);

		if (value == null) {
			return null;
		} else {
			return construct(value);
		}
	}

	@Override
	public void nullSafeSet(final PreparedStatement st, final Object value, final int index, final SharedSessionContractImplementor session) throws HibernateException, SQLException {
		if (value == null) {
			st.setNull(index, Types.VARCHAR);
		} else {
			st.setString(index, value.toString());
		}
	}

	@Override
	public Object replace(Object original, Object target, Object owner) throws HibernateException {
		return original;
	}

	@Override
	public Class<?> returnedClass() {
		return this.classType;
	}

	@Override
	public int[] sqlTypes() {
		return SQL_TYPES;
	}
}