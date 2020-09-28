package org.pclg.dbutil;

import org.apache.logging.log4j.Logger;
import org.pclg.log.LoggerFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


class VersatilePreparedStatement extends VersatileStatement implements PreparedStatement {
	/** El logger. */
	private static final Logger LOGGER = LoggerFactory.makeLog4J();
	private final PreparedStatement delegate;
	private final String sql;
    private final List<String> params = new ArrayList<>();
	private static final String MESSAGE = "Executing: %s with params: %s";
	private final VersatileProcessor preprocessor;
	private final VersatileProcessor postprocessor;

	VersatilePreparedStatement(final PreparedStatement delegate, final String sql, final VersatileProcessor preprocessor, final VersatileProcessor postprocessor) {
		super(delegate, preprocessor, postprocessor);
		this.delegate = delegate;
        this.preprocessor = preprocessor;
        this.postprocessor = postprocessor;
		this.sql = sql;
	}

	@Override
	public void setString(final int parameterIndex, final String x) throws SQLException {
		delegate.setString(parameterIndex, x);
		saveParameter(parameterIndex, x);
	}

	@Override
	public void setInt(final int parameterIndex, final int x) throws SQLException {
		delegate.setInt(parameterIndex, x);
		saveParameter(parameterIndex, String.valueOf(x));
	}

	private void saveParameter(final int parameterIndex, final String x) {
		while (parameterIndex > params.size()) {
			params.add("");
		}
		params.set(parameterIndex - 1, x);
	}

	@Override
	public ResultSet executeQuery() throws SQLException {
		LOGGER.debug(String.format(MESSAGE, sql, params));
		// WARNING: Do not use the pre and postprocessor here!
		return new VersatileResultSet(delegate.executeQuery());
	}

	@Override
	public void addBatch() throws SQLException {
		delegate.addBatch();
	}

	@Override
	public void clearParameters() throws SQLException {
		delegate.clearParameters();
	}

	@Override
	public boolean execute() throws SQLException {
		LOGGER.debug(String.format(MESSAGE, sql, params));
		preprocessor.go();
        final boolean result = delegate.execute();
        postprocessor.go();
        return result;
	}

	@Override
	public int executeUpdate() throws SQLException {
        LOGGER.debug(String.format(MESSAGE, sql, params));
		preprocessor.go();
        final int result = delegate.executeUpdate();
        postprocessor.go();
        return result;
	}

	@Override
	public ResultSetMetaData getMetaData() throws SQLException {
		return delegate.getMetaData();
	}

	@Override
	public ParameterMetaData getParameterMetaData() throws SQLException {
		return delegate.getParameterMetaData();
	}

	@Override
	public void setArray(final int i, final Array x) throws SQLException {
		delegate.setArray(i, x);
	}

	@Override
	public void setAsciiStream(final int parameterIndex, final InputStream x, final int length) throws SQLException {
		delegate.setAsciiStream(parameterIndex, x, length);
	}

	@Override
	public void setBigDecimal(final int parameterIndex, final BigDecimal x) throws SQLException {
		delegate.setBigDecimal(parameterIndex, x);
	}

	@Override
	public void setBinaryStream(final int parameterIndex, final InputStream x, final int length) throws SQLException {
		delegate.setBinaryStream(parameterIndex, x, length);
	}

	@Override
	public void setBlob(final int i, final Blob x) throws SQLException {
		delegate.setBlob(i, x);
	}

	@Override
	public void setBoolean(final int parameterIndex, final boolean x) throws SQLException {
		delegate.setBoolean(parameterIndex, x);
	}

	@Override
	public void setByte(final int parameterIndex, final byte x) throws SQLException {
		delegate.setByte(parameterIndex, x);
	}

	@Override
	public void setBytes(final int parameterIndex, final byte[] x) throws SQLException {
		delegate.setBytes(parameterIndex, x);
	}

	@Override
	public void setCharacterStream(final int parameterIndex, final Reader reader, final int length) throws SQLException {
		delegate.setCharacterStream(parameterIndex, reader, length);
	}

	@Override
	public void setClob(final int i, final Clob x) throws SQLException {
		delegate.setClob(i, x);
	}

	@Override
	public void setDate(final int parameterIndex, final Date x, final Calendar cal) throws SQLException {
		delegate.setDate(parameterIndex, x, cal);
	}

	@Override
	public void setDate(final int parameterIndex, final Date x) throws SQLException {
		delegate.setDate(parameterIndex, x);
	}

	@Override
	public void setDouble(final int parameterIndex, final double x) throws SQLException {
		delegate.setDouble(parameterIndex, x);
	}

	@Override
	public void setFloat(final int parameterIndex, final float x) throws SQLException {
		delegate.setFloat(parameterIndex, x);
	}

	@Override
	public void setLong(final int parameterIndex, final long x) throws SQLException {
		delegate.setLong(parameterIndex, x);
	}

	@Override
	public void setNull(final int paramIndex, final int sqlType, final String typeName) throws SQLException {
		delegate.setNull(paramIndex, sqlType, typeName);
	}

	@Override
	public void setNull(final int parameterIndex, final int sqlType) throws SQLException {
		delegate.setNull(parameterIndex, sqlType);
	}

	@Override
	public void setObject(final int parameterIndex, final Object x, final int targetSqlType, final int scale) throws SQLException {
		delegate.setObject(parameterIndex, x, targetSqlType, scale);
	}

	@Override
	public void setObject(final int parameterIndex, final Object x, final int targetSqlType) throws SQLException {
		delegate.setObject(parameterIndex, x, targetSqlType);
	}

	@Override
	public void setObject(final int parameterIndex, final Object x) throws SQLException {
		delegate.setObject(parameterIndex, x);
	}

	@Override
	public void setRef(final int i, final Ref x) throws SQLException {
		delegate.setRef(i, x);
	}

	@Override
	public void setShort(final int parameterIndex, final short x) throws SQLException {
		delegate.setShort(parameterIndex, x);
	}

	@Override
	public void setTime(final int parameterIndex, final Time x, final Calendar cal) throws SQLException {
		delegate.setTime(parameterIndex, x, cal);
	}

	@Override
	public void setTime(final int parameterIndex, final Time x) throws SQLException {
		delegate.setTime(parameterIndex, x);
	}

	@Override
	public void setTimestamp(final int parameterIndex, final Timestamp x, final Calendar cal) throws SQLException {
		delegate.setTimestamp(parameterIndex, x, cal);
	}

	@Override
	public void setTimestamp(final int parameterIndex, final Timestamp x) throws SQLException {
		delegate.setTimestamp(parameterIndex, x);
	}

	@Override
	public void setUnicodeStream(
		final int parameterIndex, final InputStream x, final int length) throws SQLException {
		delegate.setCharacterStream(parameterIndex, new InputStreamReader(x), length);
	}

	@Override
	public void setURL(final int parameterIndex, final URL x) throws SQLException {
		delegate.setURL(parameterIndex, x);
	}

	@Override
	public void setAsciiStream(final int parameterIndex, final InputStream x, final long length) throws SQLException {
		delegate.setAsciiStream(parameterIndex, x, length);
	}

	@Override
	public void setAsciiStream(final int parameterIndex, final InputStream x) throws SQLException {
		delegate.setAsciiStream(parameterIndex, x);
	}

	@Override
	public void setBinaryStream(final int parameterIndex, final InputStream x, final long length) throws SQLException {
		delegate.setBinaryStream(parameterIndex, x, length);
	}

	@Override
	public void setBinaryStream(final int parameterIndex, final InputStream x) throws SQLException {
		delegate.setBinaryStream(parameterIndex, x);
	}

	@Override
	public void setBlob(final int parameterIndex, final InputStream inputStream, final long length) throws SQLException {
		delegate.setBlob(parameterIndex, inputStream, length);
	}

	@Override
	public void setBlob(final int parameterIndex, final InputStream inputStream) throws SQLException {
		delegate.setBlob(parameterIndex, inputStream);
	}

	@Override
	public void setCharacterStream(final int parameterIndex, final Reader reader, final long length) throws SQLException {
		delegate.setCharacterStream(parameterIndex, reader, length);
	}

	@Override
	public void setCharacterStream(final int parameterIndex, final Reader reader) throws SQLException {
		delegate.setCharacterStream(parameterIndex, reader);
	}

	@Override
	public void setClob(final int parameterIndex, final Reader reader, final long length) throws SQLException {
		delegate.setClob(parameterIndex, reader, length);
	}

	@Override
	public void setClob(final int parameterIndex, final Reader reader) throws SQLException {
		delegate.setClob(parameterIndex, reader);
	}

	@Override
	public void setNCharacterStream(final int parameterIndex, final Reader value, final long length) throws SQLException {
		delegate.setNCharacterStream(parameterIndex, value, length);
	}

	@Override
	public void setNCharacterStream(final int parameterIndex, final Reader value) throws SQLException {
		delegate.setNCharacterStream(parameterIndex, value);
	}

	@Override
	public void setNClob(final int parameterIndex, final NClob value) throws SQLException {
		delegate.setNClob(parameterIndex, value);
	}

	@Override
	public void setNClob(final int parameterIndex, final Reader reader, final long length) throws SQLException {
		delegate.setNClob(parameterIndex, reader, length);
	}

	@Override
	public void setNClob(final int parameterIndex, final Reader reader) throws SQLException {
		delegate.setNClob(parameterIndex, reader);
	}

	@Override
	public void setNString(final int parameterIndex, final String value) throws SQLException {
		delegate.setNString(parameterIndex, value);
	}

	@Override
	public void setRowId(final int parameterIndex, final RowId x) throws SQLException {
		delegate.setRowId(parameterIndex, x);
	}

	@Override
	public void setSQLXML(final int parameterIndex, final SQLXML xmlObject) throws SQLException {
		delegate.setSQLXML(parameterIndex, xmlObject);
	}
}
