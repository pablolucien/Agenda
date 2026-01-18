package org.pclg.agenda.datasource;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.logging.Logger;

final class DummyDataSource implements DataSource {
	//Ver si derby ofrece una implementacion
	// ¡Sí!
	@Override
	public Connection getConnection() {
		return null;
	}

	@Override
	public Connection getConnection(final String username, final String password) {
		return null;
	}

	@Override
	public PrintWriter getLogWriter() {
		return null;
	}

	@Override
	public void setLogWriter(final PrintWriter out) {
	}

	@Override
	public void setLoginTimeout(final int seconds) {
	}

	@Override
	public int getLoginTimeout() {
		return 0;
	}

	//@Override
	@Override
	public Logger getParentLogger() {
		return null;
	}

	@Override
	public <T> T unwrap(final Class<T> iface) {
		return null;
	}

	@Override
	public boolean isWrapperFor(final Class<?> iface) {
		return false;
	}
}