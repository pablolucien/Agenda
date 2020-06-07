/*
 * Creado el 28-feb-2008
 */
package org.pclg.dictionary;

import org.pclg.runtime.RuntimeControl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author Un autor en busca de personajes.
 */
final class DictionaryDB {

	/**
	 *
	 */
	private DictionaryDB() {
	}

	/**
	 * @return
	 * @throws DictionaryDBException
	 */
	public static Connection connect2Db() throws DictionaryDBException {
		try {
			Class.forName(DictionaryPropertites.getProperty("driver.name"));
			final String url = DictionaryPropertites.getProperty("db.url");
			final String user = DictionaryPropertites.getProperty("userID");
			final String pwd = DictionaryPropertites.getProperty("passwd");
			final Connection conn = DriverManager.getConnection(url, user, pwd);
			conn.setAutoCommit(false);
            RuntimeControl.registerShutdownHook(() -> {
                try {
                    conn.setAutoCommit(true);    // hay que hacerlo antes del close() o casca.
                    conn.close();
                } catch (final SQLException e) {
                    e.printStackTrace();
                }
            });
            return conn;
		} catch (final SQLException ex) {
			throw new DictionaryDBException(ex);
		} catch (final ClassNotFoundException ex) {
			throw new DictionaryDBException(ex);
		}
	}

	/**
	 * @throws DictionaryDBException
	 * @throws SQLException
	 *
	 */
	public static void createTables(final Connection conn) throws DictionaryDBException {
		final Statement statement;
		try {
			statement = conn.createStatement();
			try {
				statement.execute("DROP TABLE entries");
				statement.execute("DROP TABLE dudosas");
				statement.execute("DROP TABLE sinonimos");
			} catch (final SQLException ex1) {
				ex1.printStackTrace();
			}
			statement.execute("CREATE TABLE entries (word CHAR(20) PRIMARY KEY, definition VARCHAR(1024))");
//			statement.execute("CREATE TABLE nuevas (word CHAR(20) PRIMARY KEY, definition VARCHAR(1024))");
			statement.execute("CREATE TABLE dudosas (word CHAR(20) PRIMARY KEY, definition VARCHAR(1024))");
			statement.execute("CREATE TABLE sinonimos (word CHAR(20), grupo INTEGER)");
//			statement.execute("INSERT INTO entries  VALUES (SELECT * FROM nuevas)");
			conn.commit();
			statement.close();
		} catch (final SQLException ex) {
			throw new DictionaryDBException(ex);
		}
	}

	/**
	 * @throws IOException
	 * @throws DictionaryDBException
	 *
	 */
	public static void populateDb(final Connection conn) throws IOException, DictionaryDBException {
		BufferedReader reader = new BufferedReader(new FileReader(DictionaryPropertites.getProperty("textfile.path")));
		DictionaryEntry entry;
		while ((entry = readWord(reader)) != null) {
			saveWord(conn, entry);
		}
		reader.close();
		reader = new BufferedReader(new FileReader(DictionaryPropertites.getProperty("sinonymfile.path")));
		String lista;
		int group = 0;
		while ((lista = reader.readLine()) != null) {
		    addSinonyms(conn, ++group, lista.split(", "));
		}
		reader.close();
	}

	/**
     * @param conn
     * @param i
     * @param strings
	 * @throws DictionaryDBException
     */
    private static void addSinonyms(final Connection conn, final int group, final String[] lista) throws DictionaryDBException {
		try {
	        final PreparedStatement statement = conn.prepareStatement("INSERT INTO " + "sinonimos" + " (word, grupo) VALUES (?, ?)");
    		statement.setInt(2, group);
	        for (int ii = 0; ii < lista.length; ii++) {
                statement.setString(1, lista[ii]);
	    		statement.executeUpdate();
	        }
	        statement.close();
	        conn.commit();
        } catch (final SQLException ex) {
            ex.printStackTrace();
            throw new DictionaryDBException(ex);
        }
    }

    /**
	 * Lee una palabra del fichero. 
	 */
	private static DictionaryEntry readWord(final BufferedReader reader) {
		DictionaryEntry entry = null;
		try {
			final String word = reader.readLine();
			if (word != null) {
				String definition = "";
				String continuacion;
				do {
					continuacion = reader.readLine()/*.trim()*/;
					definition += continuacion + '\n';
				} while (continuacion != null && !continuacion.endsWith("."));
				entry = new DictionaryEntry(word, definition);
			}
		} catch (final IOException e1) {
			e1.printStackTrace();
		}
		return entry;
	}

	/**
	 * @param conn
	 * @param word
	 * @throws DictionaryDBException
	 */
	static void saveWord(final Connection conn, final DictionaryEntry word) throws DictionaryDBException {
		saveWord(conn, "entries", word);
	}

	/**
	 * Guarda una palabra en la base de datos.
	 * @param table
	 * @throws DictionaryDBException
	 */
	static void saveWord(final Connection conn, final String table, final DictionaryEntry entry) throws DictionaryDBException {
		final String word = entry.getWord();
		if (!word.equals("")) {
			try {
				final PreparedStatement statement = conn.prepareStatement("INSERT INTO " + table + " (word, definition) VALUES (?, ?)");
				statement.setString(1, word);
				statement.setString(2, entry.getDefinition());
				try {
					statement.execute();
				} catch (final SQLException ex) {
					final String state = ex.getSQLState();
					if (state.equals("S1000")) {
						final String definition = getDescription(conn, table, word);
						if (!new DictionaryEntry(word, definition).equals(entry)) {
//							System.err.println("Inner->: entry = " + entry + ", ex.getErrorCode():" + ex.getErrorCode() + ", ex.getSQLState():" + state);
							final PreparedStatement statement2 = conn.prepareStatement("UPDATE " + table + " SET definition = definition + ? WHERE word = ?");
							statement2.setString(1, "\n-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-\n" + entry.getDefinition());
							statement2.setString(2, word);
							statement2.execute();
							statement2.close();
						} else {
//							System.err.println("Duplicate entry = " + entry);
						}
					} else {
						throw ex;
					}
				}
				statement.close();
				conn.commit();
			} catch (final SQLException ex) {
				System.err.println("Outer: entry = " + entry + ", ex.getErrorCode():" + ex.getErrorCode() + ", ex.getSQLState():" + ex.getSQLState());
				throw new DictionaryDBException(ex);
//			} catch (NullPointerException ex) {
//				System.err.println("NullPointerException: " + entry);
//				throw ex;
			}
		}
	}

	/**
	 * @param table
	 * @return
	 */
	public static List loadWords(final Connection conn)  {
		return loadWords(conn, "entries");
	}

	/**
	 * @param table
	 * @return
	 */
	public static List loadWords(final Connection conn, final String table)  {
		final List wordList = new ArrayList();
		try {
			final Statement statement;
			statement = conn.createStatement();
			final ResultSet rset = statement.executeQuery("SELECT word FROM " + table + " ORDER BY word");
//			ResultSet rset = statement.executeQuery("SELECT word, definition FROM " + table + " ORDER BY word");
			while (rset.next()) {
				final String word = rset.getString(1);
//				String definition = rset.getString(2);
				wordList.add(word);
//				wordList.add(new DictionaryEntry(word, definition));
				//listModel.addBatch(index++, new DictionaryEntry(word, definition));
			}
			rset.close();
			statement.close();
		} catch (final SQLException ex) {
			ex.printStackTrace();
		}
		return wordList;
	}

	/**
	 * @param word
	 * @return
	 * @throws DictionaryDBException
	 */
	public static String getDescription(final Connection conn, final String table, final String word) throws DictionaryDBException {
		String description = null;
		try {
			final PreparedStatement statement = conn.prepareStatement("SELECT definition FROM " + table + " WHERE word = ?");
			statement.setString(1, word);
			final ResultSet rset = statement.executeQuery();
			if (rset.next()) {
				description = rset.getString(1);
			}
			rset.close();
			statement.close();
		} catch (final SQLException ex) {
			throw new DictionaryDBException(ex);
		}
		return description;
	}

	/**
	 * @param conn
	 * @param currentWord
	 */
	public static void moverDudosa(final Connection conn, final String word) throws DictionaryDBException {
		try {
			final DictionaryEntry entry = new DictionaryEntry(word, getDescription(conn, "entries", word));
			saveWord(conn, "dudosas", entry);
			deleteWord(conn, "entries", word);
			conn.commit();
		} catch (final SQLException ex) {
			throw new DictionaryDBException(ex);
		}
	}

	/**
	 * @param conn
	 * @param string
	 * @param word
	 * @throws SQLException
	 */
	private static void deleteWord(final Connection conn, final String table, final String word) throws SQLException {
		final PreparedStatement statement = conn.prepareStatement("DELETE FROM " + table + " WHERE word = ?");
		statement.setString(1, word);
		statement.execute();
	}

    /**
     * @param string
     * @return
     */
    public static List obtenerSinonimos(final Connection conn, final String word) {
        final ArrayList list = new ArrayList();
		try {
		    final PreparedStatement statement = conn.prepareStatement("SELECT word FROM sinonimos WHERE grupo IN (SELECT grupo from sinonimos where word = ?)");
            statement.setString(1, word);
    		final ResultSet rset = statement.executeQuery();
    		while (rset.next()) {
    		    list.add(rset.getString(1));
    		}
    		rset.close();
    		statement.close();
        } catch (final SQLException ex) {
            ex.printStackTrace();
        }
        return list;
    }
}
