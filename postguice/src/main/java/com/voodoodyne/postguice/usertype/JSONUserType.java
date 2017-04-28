package com.voodoodyne.postguice.usertype;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Objects;
import com.googlecode.gentyref.GenericTypeReflector;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

import java.io.IOException;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * Hibernate {@link UserType} implementation to handle JSON objects. Create a subclass
 * for any specific type you want converted to JSON.
 *
 * http://www.thoughts-on-java.org/persist-postgresqls-jsonb-data-type-hibernate/
 * http://fabriziofortino.github.io/articles/hibernate-json-usertype/
 * http://blog.janjonas.net/2010-04-27/hibernate-json-usertype-persist-json-objects
 * http://java.dzone.com/articles/annotating-custom-types
 *
 * @param <T> is the type of the class you want to convert to json with this
 */
public class JSONUserType<T> implements UserType, Serializable {

	private static final long serialVersionUID = 949847068189119668L;

	private static final ObjectMapper MAPPER = new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

	private static final int[] SQL_TYPES = new int[]{Types.JAVA_OBJECT};

	private final Class<T> classType;
	private final JavaType javaType;

	public JSONUserType(final Class<T> classType) {
		this.classType = classType;
		this.javaType = MAPPER.getTypeFactory().constructType(classType);
	}

	public JSONUserType(final TypeReference<?> typeReference) {
		//noinspection unchecked
		this.classType = (Class<T>)GenericTypeReflector.erase(typeReference.getType());
		this.javaType = MAPPER.getTypeFactory().constructType(typeReference);
	}

	@Override
	public Object assemble(Serializable cached, Object owner) throws HibernateException {
		return this.deepCopy(cached);
	}

	@Override
	public Object deepCopy(Object value) throws HibernateException {
		if (value == null)
			return null;

		try {
			return MAPPER.readValue(MAPPER.writeValueAsString(value), this.javaType);
		} catch (IOException e) {
			throw new HibernateException("Unable to deep copy object", e);
		}
	}

	@Override
	public Serializable disassemble(Object value) throws HibernateException {
		try {
			return MAPPER.writeValueAsString(value);
		} catch (JsonProcessingException e) {
			throw new HibernateException("Unable to disassemble object", e);
		}
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
		return true;
	}

	@Override
	public Object nullSafeGet(final ResultSet rs, final String[] names, final SharedSessionContractImplementor session, final Object owner) throws HibernateException, SQLException {
		String value = rs.getString(names[0]);

		if (value == null) {
			return null;
		} else {
			try {
				return MAPPER.readValue(value, this.javaType);
			} catch (IOException e) {
				throw new HibernateException("Unable to read object from result set", e);
			}
		}
	}

	@Override
	public void nullSafeSet(final PreparedStatement st, final Object value, final int index, final SharedSessionContractImplementor session) throws HibernateException, SQLException {
		if (value == null) {
			st.setNull(index, Types.OTHER);
		} else {
			try {
				st.setObject(index, MAPPER.writeValueAsString(value), Types.OTHER);
			} catch (JsonProcessingException e) {
				throw new HibernateException("Unable to set object to result set", e);
			}
		}
	}

	@Override
	public Object replace(Object original, Object target, Object owner) throws HibernateException {
		return this.deepCopy(original);
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