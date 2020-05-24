// ******************************** package
package org.pclg.xtras;

// Este es un paquete ad hoc para poner cosas que he obtenido de otras fuentes
//package org.pclg.xtras;

/*
 * DriverPropertiesPanel.java	( Java JRE 1.3 )	2000/02/14
 *
 * Copyright (c) 2001 by Daniel Fitzgerald, Toronto ON Canada. All Rights Reserved.
 * 
 * The author grants you a non-exclusive, royalty free, license to use, modify
 * and redistribute this software in source and binary code form,
 * provided that this copyright notice and license appear on all copies of the
 * software and that you does not utilize the software in a manner
 * which is disparaging to the author. This software is provided as is without
 * warranty of any kind and as such, releases the author from any
 * potential and future liabilities.
 */


import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.pclg.log.LoggerFactory;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public final class DriverPropertiesPanel extends JPanel {
	/** El logger. */
	private static final Logger LOGGER = LoggerFactory.makeLog4J();
    private static final long serialVersionUID = 4215878299471920545L;
//	private final int[] scrollType = {ResultSet.FETCH_FORWARD, ResultSet.FETCH_REVERSE, ResultSet.FETCH_UNKNOWN};
//	private final int[] cursorType = {ResultSet.TYPE_FORWARD_ONLY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.TYPE_SCROLL_SENSITIVE};
//	private final int[] currencyType = {ResultSet.CONCUR_READ_ONLY, ResultSet.CONCUR_UPDATABLE};
//	private final int[] connectionType = {Connection.TRANSACTION_NONE, Connection.TRANSACTION_READ_UNCOMMITTED, Connection.TRANSACTION_READ_COMMITTED, Connection.TRANSACTION_REPEATABLE_READ, Connection.TRANSACTION_SERIALIZABLE};

	private final String[] driverHead = {"JDBC Method Support", "Value"};

	private JTable sTable = new JTable();
	private JTable lTable = new JTable();
	private JTable rTable = new JTable();
	private JTable tTable = new JTable();

	private TableColumn tc;
	private static final Integer ZERO = Integer.valueOf(0);
	private static final int BUFFER_SIZE = 1024;


	private static void printKeys(final DatabaseMetaData md) throws SQLException {
		final ResultSet rset = md.getPrimaryKeys(null, null, "contactos");
		while (rset.next()) {
			LOGGER.log(Level.INFO, rset.getString(4));
		}
	}

	private DriverPropertiesPanel(final DatabaseMetaData md) {
		try {
			printKeys(md);
			createSTable(md);
			createLTable(md);
			createRTable(md);
			genTypeInfoTable(md);
			setUpGUI();
		} catch (final SQLException ex) {
			LOGGER.log(Level.ERROR, "", ex);
		}
	}


	private void setUpGUI() {
		setBorder(BorderFactory.createEtchedBorder(getBackground().brighter(), getBackground().darker()));

		final GridBagLayout gbl = new GridBagLayout();

		setLayout(gbl);

		final GridBagConstraints gbc = getConstraints();
		gbc.anchor = GridBagConstraints.WEST;
		gbc.gridwidth = 1;	// GridBagConstraints.RELATIVE;

		setTableCharacteristics(lTable);
		final JScrollPane lsp = new JScrollPane(lTable);
		lsp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		add(lsp, gbc);

		gbc.anchor = GridBagConstraints.EAST;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		setTableCharacteristics(rTable);
		final JScrollPane rsp = new JScrollPane(rTable);
		rsp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		add(rsp, gbc);

		gbc.anchor = GridBagConstraints.SOUTH;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		setTableCharacteristics(sTable);
		sTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		final JScrollPane ssp = new JScrollPane(sTable);
		ssp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		add(ssp, gbc);

		gbc.anchor = GridBagConstraints.SOUTH;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		tTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		final JScrollPane tsp = new JScrollPane(tTable);
		tsp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		add(tsp, gbc);
	}

	private static GridBagConstraints getConstraints() {
		final GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(1, 1, 1, 1);
		gbc.ipadx = 1;
		gbc.ipady = 1;
		gbc.weightx = 1.5;
		gbc.weighty = 1.5;
		gbc.fill = GridBagConstraints.BOTH;
		return gbc;
	}

	private void createRTable(final DatabaseMetaData md) throws SQLException {
		final Object[][] DriverDataR = {
				{"getDefaultTransactionIsolation", Integer.valueOf(md.getDefaultTransactionIsolation())},
				{"getDriverMajorVersion", Integer.valueOf(md.getDriverMajorVersion())},
				{"getDriverMinorVersion", Integer.valueOf(md.getDriverMinorVersion())},
				{"getMaxBinaryLiteralLength", Integer.valueOf(md.getMaxBinaryLiteralLength())},
				{"getMaxCatalogNameLength", Integer.valueOf(md.getMaxCatalogNameLength())},
				{"getMaxCharLiteralLength", Integer.valueOf(md.getMaxCharLiteralLength())},
				{"getMaxColumnNameLength", Integer.valueOf(md.getMaxColumnNameLength())},
				{"getMaxCursorNameLength", Integer.valueOf(md.getMaxCursorNameLength())},
				{"getMaxIndexLength", Integer.valueOf(md.getMaxIndexLength())},
				{"getMaxProcedureNameLength", Integer.valueOf(md.getMaxProcedureNameLength())},
				{"getMaxSchemaNameLength", Integer.valueOf(md.getMaxSchemaNameLength())},
				{"getMaxStatementLength", Integer.valueOf(md.getMaxStatementLength())},
				{"getMaxTableNameLength", Integer.valueOf(md.getMaxTableNameLength())},
				{"getMaxUserNameLength", Integer.valueOf(md.getMaxUserNameLength())},
				{"getMaxColumnsInGroupBy", Integer.valueOf(md.getMaxColumnsInGroupBy())},
				{"getMaxColumnsInIndex", Integer.valueOf(md.getMaxColumnsInIndex())},
				{"getMaxColumnsInOrderBy", Integer.valueOf(md.getMaxColumnsInOrderBy())},
				{"getMaxColumnsInSelect", Integer.valueOf(md.getMaxColumnsInSelect())},
				{"getMaxColumnsInTable", Integer.valueOf(md.getMaxColumnsInTable())},
				{"getMaxConnections", Integer.valueOf(md.getMaxConnections())},
				{"getMaxRowSize", Integer.valueOf(md.getMaxRowSize())},
				{"getMaxStatements", Integer.valueOf(md.getMaxStatements())},
				{"getMaxTablesInSelect", Integer.valueOf(md.getMaxTablesInSelect())},

				{"getDatabaseProductName", md.getDatabaseProductName()},
				{"getDatabaseProductVersion", md.getDatabaseProductVersion()},
				{"getDriverName", md.getDriverName()},
				{"getDriverVersion", md.getDriverVersion()},
				{"getURL", md.getURL()},
				{"getUserName", md.getUserName()}
		};
		rTable = new JTable(new JdbcTableModel(DriverDataR, driverHead));
	}

	private void createLTable(final DatabaseMetaData md) throws SQLException {
		final Object[][] DriverDataL = {
				{"allProceduresAreCallable", Boolean.valueOf(md.allProceduresAreCallable())},
				{"allTablesAreSelectable", Boolean.valueOf(md.allTablesAreSelectable())},
//				{"autoCommitFailureClosesAllResultSets", Boolean.valueOf(md.autoCommitFailureClosesAllResultSets())},
				{"dataDefinitionCausesTransactionCommit", Boolean.valueOf(md.dataDefinitionCausesTransactionCommit())},
				{"dataDefinitionIgnoredInTransactions", Boolean.valueOf(md.dataDefinitionIgnoredInTransactions())},
				{"deletesAreDetected(ResultSet.TYPE_FORWARD_ONLY)", Boolean.valueOf(md.deletesAreDetected(ResultSet.TYPE_FORWARD_ONLY))},
				{"deletesAreDetected(ResultSet.TYPE_SCROLL_INSENSITIVE)", Boolean.valueOf(md.deletesAreDetected(ResultSet.TYPE_SCROLL_INSENSITIVE))},
				{"deletesAreDetected(ResultSet.TYPE_SCROLL_SENSITIVE)", Boolean.valueOf(md.deletesAreDetected(ResultSet.TYPE_SCROLL_SENSITIVE))},
				{"doesMaxRowSizeIncludeBlobs", Boolean.valueOf(md.doesMaxRowSizeIncludeBlobs())},
				{"insertsAreDetected(ResultSet.TYPE_FORWARD_ONLY)", Boolean.valueOf(md.insertsAreDetected(ResultSet.TYPE_FORWARD_ONLY))},
				{"insertsAreDetected(ResultSet.TYPE_SCROLL_INSENSITIVE)", Boolean.valueOf(md.insertsAreDetected(ResultSet.TYPE_SCROLL_INSENSITIVE))},
				{"insertsAreDetected(ResultSet.TYPE_SCROLL_SENSITIVE)", Boolean.valueOf(md.insertsAreDetected(ResultSet.TYPE_SCROLL_SENSITIVE))},
				{"isCatalogAtStart", Boolean.valueOf(md.isCatalogAtStart())},
				{"isReadOnly", Boolean.valueOf(md.isReadOnly())},
				{"locatorsUpdateCopy", Boolean.valueOf(md.locatorsUpdateCopy())},
				{"nullPlusNonNullIsNull", Boolean.valueOf(md.nullPlusNonNullIsNull())},
				{"nullsAreSortedAtEnd", Boolean.valueOf(md.nullsAreSortedAtEnd())},
				{"nullsAreSortedAtStart", Boolean.valueOf(md.nullsAreSortedAtStart())},
				{"nullsAreSortedHigh", Boolean.valueOf(md.nullsAreSortedHigh())},
				{"nullsAreSortedLow", Boolean.valueOf(md.nullsAreSortedLow())},
				{"othersDeletesAreVisible(ResultSet.TYPE_FORWARD_ONLY)", Boolean.valueOf(md.othersDeletesAreVisible(ResultSet.TYPE_FORWARD_ONLY))},
				{"othersDeletesAreVisible(ResultSet.TYPE_SCROLL_INSENSITIVE)", Boolean.valueOf(md.othersDeletesAreVisible(ResultSet.TYPE_SCROLL_INSENSITIVE))},
				{"othersDeletesAreVisible(ResultSet.TYPE_SCROLL_SENSITIVE)", Boolean.valueOf(md.othersDeletesAreVisible(ResultSet.TYPE_SCROLL_SENSITIVE))},
				{"othersInsertsAreVisible(ResultSet.TYPE_FORWARD_ONLY)", Boolean.valueOf(md.othersInsertsAreVisible(ResultSet.TYPE_FORWARD_ONLY))},
				{"othersInsertsAreVisible(ResultSet.TYPE_SCROLL_INSENSITIVE)", Boolean.valueOf(md.othersInsertsAreVisible(ResultSet.TYPE_SCROLL_INSENSITIVE))},
				{"othersInsertsAreVisible(ResultSet.TYPE_SCROLL_SENSITIVE)", Boolean.valueOf(md.othersInsertsAreVisible(ResultSet.TYPE_SCROLL_SENSITIVE))},
				{"othersUpdatesAreVisible(ResultSet.TYPE_FORWARD_ONLY)", Boolean.valueOf(md.othersUpdatesAreVisible(ResultSet.TYPE_FORWARD_ONLY))},
				{"othersUpdatesAreVisible(ResultSet.TYPE_SCROLL_INSENSITIVE)", Boolean.valueOf(md.othersUpdatesAreVisible(ResultSet.TYPE_SCROLL_INSENSITIVE))},
				{"othersUpdatesAreVisible(ResultSet.TYPE_SCROLL_SENSITIVE)", Boolean.valueOf(md.othersUpdatesAreVisible(ResultSet.TYPE_SCROLL_SENSITIVE))},
				{"ownDeletesAreVisible(ResultSet.TYPE_FORWARD_ONLY)", Boolean.valueOf(md.ownDeletesAreVisible(ResultSet.TYPE_FORWARD_ONLY))},
				{"ownDeletesAreVisible(ResultSet.TYPE_SCROLL_INSENSITIVE)", Boolean.valueOf(md.ownDeletesAreVisible(ResultSet.TYPE_SCROLL_INSENSITIVE))},
				{"ownDeletesAreVisible(ResultSet.TYPE_SCROLL_SENSITIVE)", Boolean.valueOf(md.ownDeletesAreVisible(ResultSet.TYPE_SCROLL_SENSITIVE))},
				{"ownInsertsAreVisible(ResultSet.TYPE_FORWARD_ONLY)", Boolean.valueOf(md.ownInsertsAreVisible(ResultSet.TYPE_FORWARD_ONLY))},
				{"ownInsertsAreVisible(ResultSet.TYPE_SCROLL_INSENSITIVE)", Boolean.valueOf(md.ownInsertsAreVisible(ResultSet.TYPE_SCROLL_INSENSITIVE))},
				{"ownInsertsAreVisible(ResultSet.TYPE_SCROLL_SENSITIVE)", Boolean.valueOf(md.ownInsertsAreVisible(ResultSet.TYPE_SCROLL_SENSITIVE))},
				{"ownUpdatesAreVisible(ResultSet.TYPE_FORWARD_ONLY)", Boolean.valueOf(md.ownUpdatesAreVisible(ResultSet.TYPE_FORWARD_ONLY))},
				{"ownUpdatesAreVisible(ResultSet.TYPE_SCROLL_INSENSITIVE)", Boolean.valueOf(md.ownUpdatesAreVisible(ResultSet.TYPE_SCROLL_INSENSITIVE))},
				{"ownUpdatesAreVisible(ResultSet.TYPE_SCROLL_SENSITIVE)", Boolean.valueOf(md.ownUpdatesAreVisible(ResultSet.TYPE_SCROLL_SENSITIVE))},
				{"storesLowerCaseIdentifiers", Boolean.valueOf(md.storesLowerCaseIdentifiers())},
				{"storesLowerCaseQuotedIdentifiers", Boolean.valueOf(md.storesLowerCaseQuotedIdentifiers())},
				{"storesMixedCaseIdentifiers", Boolean.valueOf(md.storesMixedCaseIdentifiers())},
				{"storesMixedCaseQuotedIdentifiers", Boolean.valueOf(md.storesMixedCaseQuotedIdentifiers())},
				{"storesUpperCaseIdentifiers", Boolean.valueOf(md.storesUpperCaseIdentifiers())},
				{"storesUpperCaseQuotedIdentifiers", Boolean.valueOf(md.storesUpperCaseQuotedIdentifiers())},
				{"supportsAlterTableWithAddColumn", Boolean.valueOf(md.supportsAlterTableWithAddColumn())},
				{"supportsAlterTableWithDropColumn", Boolean.valueOf(md.supportsAlterTableWithDropColumn())},
				{"supportsANSI92EntryLevelSQL", Boolean.valueOf(md.supportsANSI92EntryLevelSQL())},
				{"supportsANSI92FullSQL", Boolean.valueOf(md.supportsANSI92FullSQL())},
				{"supportsANSI92IntermediateSQL", Boolean.valueOf(md.supportsANSI92IntermediateSQL())},
				{"supportsBatchUpdates", Boolean.valueOf(md.supportsBatchUpdates())},
				{"supportsCatalogsInDataManipulation", Boolean.valueOf(md.supportsCatalogsInDataManipulation())},
				{"supportsCatalogsInIndexDefinitions", Boolean.valueOf(md.supportsCatalogsInIndexDefinitions())},
				{"supportsCatalogsInPrivilegeDefinitions", Boolean.valueOf(md.supportsCatalogsInPrivilegeDefinitions())},
				{"supportsCatalogsInProcedureCalls", Boolean.valueOf(md.supportsCatalogsInProcedureCalls())},
				{"supportsCatalogsInTableDefinitions", Boolean.valueOf(md.supportsCatalogsInTableDefinitions())},
				{"supportsColumnAliasing", Boolean.valueOf(md.supportsColumnAliasing())},
				{"supportsConvert", Boolean.valueOf(md.supportsConvert())},
				{"supportsCoreSQLGrammar", Boolean.valueOf(md.supportsCoreSQLGrammar())},
				{"supportsCorrelatedSubqueries", Boolean.valueOf(md.supportsCorrelatedSubqueries())},
				{"supportsDataDefinitionAndDataManipulationTransactions", Boolean.valueOf(md.supportsDataDefinitionAndDataManipulationTransactions())},
				{"supportsDataManipulationTransactionsOnly", Boolean.valueOf(md.supportsDataManipulationTransactionsOnly())},
				{"supportsDifferentTableCorrelationNames", Boolean.valueOf(md.supportsDifferentTableCorrelationNames())},
				{"supportsExpressionsInOrderBy", Boolean.valueOf(md.supportsExpressionsInOrderBy())},
				{"supportsExtendedSQLGrammar", Boolean.valueOf(md.supportsExtendedSQLGrammar())},
				{"supportsFullOuterJoins", Boolean.valueOf(md.supportsFullOuterJoins())},
				{"supportsGetGeneratedKeys", Boolean.valueOf(md.supportsGetGeneratedKeys())},
				{"supportsGroupBy", Boolean.valueOf(md.supportsGroupBy())},
				{"supportsGroupByBeyondSelect", Boolean.valueOf(md.supportsGroupByBeyondSelect())},
				{"supportsGroupByUnrelated", Boolean.valueOf(md.supportsGroupByUnrelated())},
				{"supportsIntegrityEnhancementFacility", Boolean.valueOf(md.supportsIntegrityEnhancementFacility())},
				{"supportsLikeEscapeClause", Boolean.valueOf(md.supportsLikeEscapeClause())},
				{"supportsLimitedOuterJoins", Boolean.valueOf(md.supportsLimitedOuterJoins())},
				{"supportsMinimumSQLGrammar", Boolean.valueOf(md.supportsMinimumSQLGrammar())},
				{"supportsMixedCaseIdentifiers", Boolean.valueOf(md.supportsMixedCaseIdentifiers())},
				{"supportsMixedCaseQuotedIdentifiers", Boolean.valueOf(md.supportsMixedCaseQuotedIdentifiers())},
				{"supportsMultipleOpenResults", Boolean.valueOf(md.supportsMultipleOpenResults())},
				{"supportsMultipleResultSets", Boolean.valueOf(md.supportsMultipleResultSets())},
				{"supportsMultipleTransactions", Boolean.valueOf(md.supportsMultipleTransactions())},
				{"supportsNamedParameters", Boolean.valueOf(md.supportsNamedParameters())},
				{"supportsNonNullableColumns", Boolean.valueOf(md.supportsNonNullableColumns())},
				{"supportsOpenCursorsAcrossCommit", Boolean.valueOf(md.supportsOpenCursorsAcrossCommit())},
				{"supportsOpenCursorsAcrossRollback", Boolean.valueOf(md.supportsOpenCursorsAcrossRollback())},
				{"supportsOpenStatementsAcrossCommit", Boolean.valueOf(md.supportsOpenStatementsAcrossCommit())},
				{"supportsOpenStatementsAcrossRollback", Boolean.valueOf(md.supportsOpenStatementsAcrossRollback())},
				{"supportsOrderByUnrelated", Boolean.valueOf(md.supportsOrderByUnrelated())},
				{"supportsOuterJoins", Boolean.valueOf(md.supportsOuterJoins())},
				{"supportsPositionedDelete", Boolean.valueOf(md.supportsPositionedDelete())},
				{"supportsPositionedUpdate", Boolean.valueOf(md.supportsPositionedUpdate())},
				{"supportsResultSetHoldability(ResultSet.HOLD_CURSORS_OVER_COMMIT)", Boolean.valueOf(md.supportsResultSetHoldability(ResultSet.HOLD_CURSORS_OVER_COMMIT))},
				{"supportsResultSetHoldability(ResultSet.CLOSE_CURSORS_AT_COMMIT)", Boolean.valueOf(md.supportsResultSetHoldability(ResultSet.CLOSE_CURSORS_AT_COMMIT))},
				{"supportsResultSetType(ResultSet.TYPE_FORWARD_ONLY)", Boolean.valueOf(md.supportsResultSetType(ResultSet.TYPE_FORWARD_ONLY))},
				{"supportsResultSetType(ResultSet.TYPE_SCROLL_INSENSITIVE)", Boolean.valueOf(md.supportsResultSetType(ResultSet.TYPE_SCROLL_INSENSITIVE))},
				{"supportsResultSetType(ResultSet.TYPE_SCROLL_SENSITIVE)", Boolean.valueOf(md.supportsResultSetType(ResultSet.TYPE_SCROLL_SENSITIVE))},
				{"supportsSavepoints", Boolean.valueOf(md.supportsSavepoints())},
				{"supportsSchemasInDataManipulation", Boolean.valueOf(md.supportsSchemasInDataManipulation())},
				{"supportsSchemasInIndexDefinitions", Boolean.valueOf(md.supportsSchemasInIndexDefinitions())},
				{"supportsSchemasInPrivilegeDefinitions", Boolean.valueOf(md.supportsSchemasInPrivilegeDefinitions())},
				{"supportsSchemasInProcedureCalls", Boolean.valueOf(md.supportsSchemasInProcedureCalls())},
				{"supportsSchemasInTableDefinitions", Boolean.valueOf(md.supportsSchemasInTableDefinitions())},
				{"supportsSelectForUpdate", Boolean.valueOf(md.supportsSelectForUpdate())},
				{"supportsStatementPooling", Boolean.valueOf(md.supportsStatementPooling())},
//				{"supportsStoredFunctionsUsingCallSyntax", Boolean.valueOf(md.supportsStoredFunctionsUsingCallSyntax())},
				{"supportsStoredProcedures", Boolean.valueOf(md.supportsStoredProcedures())},
				{"supportsSubqueriesInComparisons", Boolean.valueOf(md.supportsSubqueriesInComparisons())},
				{"supportsSubqueriesInExists", Boolean.valueOf(md.supportsSubqueriesInExists())},
				{"supportsSubqueriesInIns", Boolean.valueOf(md.supportsSubqueriesInIns())},
				{"supportsSubqueriesInQuantifieds", Boolean.valueOf(md.supportsSubqueriesInQuantifieds())},
				{"supportsTableCorrelationNames", Boolean.valueOf(md.supportsTableCorrelationNames())},
				{"supportsTransactionIsolationLevel(Connection.TRANSACTION_NONE)", Boolean.valueOf(md.supportsTransactionIsolationLevel(Connection.TRANSACTION_NONE))},
				{"supportsTransactionIsolationLevel(Connection.TRANSACTION_READ_UNCOMMITTED)", Boolean.valueOf(md.supportsTransactionIsolationLevel(Connection.TRANSACTION_READ_UNCOMMITTED))},
				{"supportsTransactionIsolationLevel(Connection.TRANSACTION_READ_COMMITTED)", Boolean.valueOf(md.supportsTransactionIsolationLevel(Connection.TRANSACTION_READ_COMMITTED))},
				{"supportsTransactionIsolationLevel(Connection.TRANSACTION_REPEATABLE_READ)", Boolean.valueOf(md.supportsTransactionIsolationLevel(Connection.TRANSACTION_REPEATABLE_READ))},
				{"supportsTransactionIsolationLevel(Connection.TRANSACTION_SERIALIZABLE)", Boolean.valueOf(md.supportsTransactionIsolationLevel(Connection.TRANSACTION_SERIALIZABLE))},
				{"supportsTransactions", Boolean.valueOf(md.supportsTransactions())},
				{"supportsUnion", Boolean.valueOf(md.supportsUnion())},
				{"supportsUnionAll", Boolean.valueOf(md.supportsUnionAll())},
				{"updatesAreDetected(ResultSet.TYPE_FORWARD_ONLY)", Boolean.valueOf(md.updatesAreDetected(ResultSet.TYPE_FORWARD_ONLY))},
				{"updatesAreDetected(ResultSet.TYPE_SCROLL_INSENSITIVE)", Boolean.valueOf(md.updatesAreDetected(ResultSet.TYPE_SCROLL_INSENSITIVE))},
				{"updatesAreDetected(ResultSet.TYPE_SCROLL_SENSITIVE)", Boolean.valueOf(md.updatesAreDetected(ResultSet.TYPE_SCROLL_SENSITIVE))},
				{"usesLocalFilePerTable", Boolean.valueOf(md.usesLocalFilePerTable())},
				{"usesLocalFiles", Boolean.valueOf(md.usesLocalFiles())}
		};

		lTable = new JTable(new JdbcTableModel(DriverDataL, driverHead));
	}

	private void createSTable(final DatabaseMetaData md) throws SQLException {
		final Object[][] DriverDataS = {
				{"getExtraNameCharacters", md.getExtraNameCharacters()},
				{"getIdentifierQuoteString", md.getIdentifierQuoteString()},
				{"getSearchStringEscape", md.getSearchStringEscape()},
				{"getCatalogTerm", md.getCatalogTerm()},
				{"getCatalogSeparator", md.getCatalogSeparator()},
				{"getSchemaTerm", md.getSchemaTerm()},
				{"getProcedureTerm", md.getProcedureTerm()},
				{"getNumericFunctions", md.getNumericFunctions()},
				{"getStringFunctions", md.getStringFunctions()},
				{"getSystemFunctions", md.getSystemFunctions()},
				{"getTimeDateFunctions", md.getTimeDateFunctions()},
				{"getSQLKeywords", md.getSQLKeywords()}
		};

		sTable = new JTable(new JdbcTableModel(DriverDataS, driverHead));
	}

	private void setTableCharacteristics(final JTable t) {
		int tWidth;

		tc = t.getColumn("JDBC Method Support");
		tWidth = getPreferredColumnWidth(tc, t) + getPreferredColumnWidth(tc, t) / 10;
		tc.setMinWidth(tWidth);

		tc = t.getColumn("Value");
		tWidth = getPreferredColumnWidth(tc, t) + getPreferredColumnWidth(tc, t) / 10;
		tc.setMinWidth(tWidth);

		t.sizeColumnsToFit(0);	// JDK Bug : Needs to be called.
	}

//	private boolean getScrollableTracksViewportWidth() {
//		return false;
//	}

	private static int getPreferredColumnWidth(final TableColumn tableColumn,
			final JTable table) {
		final int wHeader = getPreferredColumnHeaderWidth(tableColumn, table);
		final int wColumn = maxColumnCellWidth(tableColumn, table);

		return wHeader > wColumn ? wHeader : wColumn;
	}

	private static int getPreferredColumnHeaderWidth(final TableColumn tableColumn,
			final JTable table) {
		final TableCellRenderer r = table.getTableHeader().getDefaultRenderer();
		final Component c = r.getTableCellRendererComponent(table,
				tableColumn.getHeaderValue(), false, false, 0, 0);
		return c.getPreferredSize().width;
	}

	private static int maxColumnCellWidth(final TableColumn col, final JTable t) {
		TableCellRenderer r;
		Component c;

		final int mi = col.getModelIndex();
		int cWidth;
		int cMaxWidth = 0;

		for (int row = 0; row < t.getRowCount(); ++row) {
			r = t.getCellRenderer(row, mi);
			c = r.getTableCellRendererComponent(t, t.getValueAt(row, mi), false, false, row, mi);

			cWidth = c.getPreferredSize().width;
			cMaxWidth = cWidth > cMaxWidth ? cWidth : cMaxWidth;
		}
		return cMaxWidth;
	}

	private void genTypeInfoTable(final DatabaseMetaData md) {
		try {
			final ResultSet rs = md.getTypeInfo();
			final List<String> colHeadData = new ArrayList<String>(BUFFER_SIZE);
			final List<List<String>> tableData = new ArrayList<List<String>>(BUFFER_SIZE);
			final ResultSetMetaData rsmd = rs.getMetaData();
			final int columnCount = rsmd.getColumnCount();

			for (int i = 0; i < columnCount; i++) {
				colHeadData.add(rsmd.getColumnName(i + 1));
			}

			while (rs.next()) {
				final List rowData = new ArrayList(BUFFER_SIZE);

				for (int i = 0; i < columnCount; i++) {
					switch (rsmd.getColumnType(i + 1)) {
					case Types.CHAR:
					case Types.VARCHAR:
						final String str = rs.getString(i + 1);
						rowData.add(rs.wasNull() ? "" : str);
						break;
					case Types.FLOAT:
					case Types.DOUBLE:
					case Types.NUMERIC:
					case Types.DECIMAL:
						final Float f2 = new Float(rs.getFloat(i + 1));
						rowData.add(rs.wasNull() ? "" : f2.toString());
						break;
					case Types.INTEGER:
					case Types.SMALLINT:
						final Integer i4 = Integer.valueOf(rs.getInt(i + 1));
						rowData.add(rs.wasNull() ? ZERO : i4);
						break;
					case Types.REAL:
						final Double d7 = new Double(rs.getDouble(i + 1));
						rowData.add(rs.wasNull() ? "" : d7.toString());
						break;
					case Types.BOOLEAN:
						final Boolean bool = Boolean.valueOf(rs.getBoolean(i + 1));
						rowData.add(rs.wasNull() ? Boolean.FALSE : bool);
						break;
					case Types.DATE:
						final Date d91 = rs.getDate(i + 1);
						rowData.add(rs.wasNull() ? "" : d91.toString());
						break;
					case Types.TIME:
						final Time t92 = rs.getTime(i + 1);
						rowData.add(rs.wasNull() ? "" : t92.toString());
						break;
					case Types.TIMESTAMP:
						final Timestamp ts93 = rs.getTimestamp(i + 1);
						rowData.add(rs.wasNull() ? "" : ts93.toString());
						break;
					default:
						rowData.add("¡" + rsmd.getColumnType(i + 1) + '!');
						break;
					}
				}
				tableData.add(rowData);
			}
			createTypesTable(tableData, colHeadData);
		} catch (final SQLException ex) {
			LOGGER.log(Level.ERROR, "", ex);
		}
	}

	private void createTypesTable(final List<List<String>> tableData,
			final List<String> colHeadData) {
		final TableModel tableModel = new JdbcTableModel(tableData, colHeadData);
		tTable = new JTable(tableModel);

		for (final Object aColHeadData : colHeadData) {
			tc = tTable.getColumn(aColHeadData);
			final int width = getPreferredColumnWidth(tc, tTable)
					+ getPreferredColumnWidth(tc, tTable) / 10;

			tc.setMinWidth(width);
		}

		tTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tTable.sizeColumnsToFit(0);
	}

	private JTable getJdbcTable(final char t) {
		JTable table = null;
		switch (t) {
		case 'S':
			table = sTable;
			break;
		case 'L':
			table = lTable;
			break;
		case 'R':
			table = rTable;
			break;
		case 'T':
			table = tTable;
			break;
		}

		return table;
	}

	public static void showStandalone(final Connection connection,
			final String title)
			throws SQLException {
		final DriverPropertiesPanel driverPropertiesPanel =
				new DriverPropertiesPanel(connection.getMetaData());
		final JFrame frame = new JFrame(title);
		frame.getContentPane().add(driverPropertiesPanel, BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		frame.setSize(1200, 950);
		frame.setVisible(true);
	}
}