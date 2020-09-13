package org.pclg.agenda.plugins;

import org.apache.log4j.Logger;
import org.pclg.log.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class SimpleQueryPlugin implements Plugin {
	/** El logger. */
	private static final Logger LOGGER = LoggerFactory.makeLog4J();

	@Override
	public void execute(final Properties properties, final Connection conn, final String... args) throws SQLException {
		if (args.length == 0) {
			LOGGER.warn("No se han pasado par�metros");
			return;
		}
		final StringBuilder builder = new StringBuilder();
		for (final String arg : args) {
			builder.append(arg).append(' ');
		}
		LOGGER.warn(builder);
		if (args[0].trim().equalsIgnoreCase("SELECT")) {
			try (final PreparedStatement pstmt = conn.prepareStatement(builder.toString());
					final ResultSet rset = pstmt.executeQuery()) {
				final int columnCount = rset.getMetaData().getColumnCount();
				while (rset.next()) {
					builder.setLength(0);
					for (int ii = 1; ii < columnCount; ii++) {
						builder.append(rset.getString(ii)).append('\t');
					}
					builder.append(rset.getString(columnCount));
					LOGGER.warn(builder.toString());
				}
			} catch (final SQLException ex) {
				LOGGER.error(builder.toString() + " :", ex);
			}
		} else {
			try (final PreparedStatement pstmt = conn.prepareStatement(builder.toString())) {
				final int count = pstmt.executeUpdate();
				LOGGER.warn(String.format("%d filas insertadas/actualizadas/suprimidas", count));
			} catch (final SQLException ex) {
				LOGGER.error(builder.toString() + " :", ex);
			}
		}
	}
}
