package net.asintec.migrator;

/*
 * driverPropertiesPanel.java	( Java JRE 1.3 )	2000/02/14
 *
 * Copyright (c) 2001 by Daniel Fitzgerald, Toronto ON Canada. All Rights Reserved.
 * 
 * The author grants you a non-exclusive, royalty free, license to use, modify and redistribute this software in source and binary code form,
 * provided that this copyright notice and license appear on all copies of the software and that you does not utilize the software in a manner
 * which is disparaging to the author. This software is provided as is without warranty of any kind and as such, releases the author from any
 * potential and future liabilities.
 */

import org.pclg.tools.ToolBox;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Vector;

class	driverPropertiesPanel	extends	JPanel
{
    private static final long serialVersionUID = 6237950238991324937L;
//	private	int[ ]	scrollType		= { ResultSet.FETCH_FORWARD, ResultSet.FETCH_REVERSE, ResultSet.FETCH_UNKNOWN };
//	private	int[ ]	cursorType		= { ResultSet.TYPE_FORWARD_ONLY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.TYPE_SCROLL_SENSITIVE };
//	private	int[ ]	currencyType	= { ResultSet.CONCUR_READ_ONLY, ResultSet.CONCUR_UPDATABLE };
//	private	int[ ]	connectionType	= { Connection.TRANSACTION_NONE, Connection.TRANSACTION_READ_UNCOMMITTED, Connection.TRANSACTION_READ_COMMITTED, Connection.TRANSACTION_REPEATABLE_READ, Connection.TRANSACTION_SERIALIZABLE };

	private final String[ ]	DriverHead		= { "JDBC Method Support", "Value" };

	private JTable	sTable	= new JTable();
	private JTable	lTable	= new JTable();
	private JTable	rTable	= new JTable();
	private JTable	tTable	= new JTable();

	private TableColumn		tc;

	private ResultSetMetaData	rsmd;


void printKeys( final DatabaseMetaData md) throws SQLException {
	final ResultSet rset = md.getPrimaryKeys(null, null, "meta_tabla");
	while(rset.next()) {
		System.out.println(rset.getString(4));
	}
}

	public	driverPropertiesPanel( final DatabaseMetaData md )
	{
		try
		{
//printKeys(md);
			final Object[ ][ ]	DriverDataS	= {
					{ "getExtraNameCharacters", md.getExtraNameCharacters()},
					{ "getIdentifierQuoteString", md.getIdentifierQuoteString()},
					{ "getSearchStringEscape", md.getSearchStringEscape()},
					{ "getCatalogTerm", md.getCatalogTerm()},
					{ "getCatalogSeparator", md.getCatalogSeparator()},
					{ "getSchemaTerm", md.getSchemaTerm()},
					{ "getProcedureTerm", md.getProcedureTerm()},
					{ "getNumericFunctions", md.getNumericFunctions()},
					{ "getStringFunctions", md.getStringFunctions()},
					{ "getSystemFunctions", md.getSystemFunctions()},
					{ "getTimeDateFunctions", md.getTimeDateFunctions()},
					{ "getSQLKeywords", md.getSQLKeywords()}
				};

			sTable	= new JTable( new jdbcTableModel( DriverDataS, DriverHead ) );

			final Object[ ][ ]	DriverDataL	= {
					{ "allProceduresAreCallable", Boolean.valueOf(md.allProceduresAreCallable())},
					{ "allTablesAreSelectable", Boolean.valueOf(md.allTablesAreSelectable())},
					{ "dataDefinitionCausesTransactionCommit", Boolean.valueOf(md.dataDefinitionCausesTransactionCommit())},
					{ "dataDefinitionIgnoredInTransactions", Boolean.valueOf(md.dataDefinitionIgnoredInTransactions())},
					{ "doesMaxRowSizeIncludeBlobs", Boolean.valueOf(md.doesMaxRowSizeIncludeBlobs())},
					{ "isCatalogAtStart", Boolean.valueOf(md.isCatalogAtStart())},
					{ "isReadOnly", Boolean.valueOf(md.isReadOnly())},
					{ "nullPlusNonNullIsNull", Boolean.valueOf(md.nullPlusNonNullIsNull())},
					{ "nullsAreSortedAtEnd", Boolean.valueOf(md.nullsAreSortedAtEnd())},
					{ "nullsAreSortedAtStart", Boolean.valueOf(md.nullsAreSortedAtStart())},
					{ "nullsAreSortedHigh", Boolean.valueOf(md.nullsAreSortedHigh())},
					{ "nullsAreSortedLow", Boolean.valueOf(md.nullsAreSortedLow())},
					{ "storesLowerCaseIdentifiers", Boolean.valueOf(md.storesLowerCaseIdentifiers())},
					{ "storesLowerCaseQuotedIdentifiers", Boolean.valueOf(md.storesLowerCaseQuotedIdentifiers())},
					{ "storesMixedCaseIdentifiers", Boolean.valueOf(md.storesMixedCaseIdentifiers())},
					{ "storesMixedCaseQuotedIdentifiers", Boolean.valueOf(md.storesMixedCaseQuotedIdentifiers())},
					{ "storesUpperCaseIdentifiers", Boolean.valueOf(md.storesUpperCaseIdentifiers())},
					{ "storesUpperCaseQuotedIdentifiers", Boolean.valueOf(md.storesUpperCaseQuotedIdentifiers())},
					{ "supportsAlterTableWithAddColumn", Boolean.valueOf(md.supportsAlterTableWithAddColumn())},
					{ "supportsAlterTableWithDropColumn", Boolean.valueOf(md.supportsAlterTableWithDropColumn())},
					{ "supportsANSI92EntryLevelSQL", Boolean.valueOf(md.supportsANSI92EntryLevelSQL())},
					{ "supportsANSI92FullSQL", Boolean.valueOf(md.supportsANSI92FullSQL())},
					{ "supportsANSI92IntermediateSQL", Boolean.valueOf(md.supportsANSI92IntermediateSQL())},
					{ "supportsCatalogsInDataManipulation", Boolean.valueOf(md.supportsCatalogsInDataManipulation())},
					{ "supportsCatalogsInIndexDefinitions", Boolean.valueOf(md.supportsCatalogsInIndexDefinitions())},
					{ "supportsCatalogsInPrivilegeDefinitions", Boolean.valueOf(md.supportsCatalogsInPrivilegeDefinitions())},
					{ "supportsCatalogsInProcedureCalls", Boolean.valueOf(md.supportsCatalogsInProcedureCalls())},
					{ "supportsCatalogsInTableDefinitions", Boolean.valueOf(md.supportsCatalogsInTableDefinitions())},
					{ "supportsColumnAliasing", Boolean.valueOf(md.supportsColumnAliasing())},
					{ "supportsConvert", Boolean.valueOf(md.supportsConvert())},
					{ "supportsCoreSQLGrammar", Boolean.valueOf(md.supportsCoreSQLGrammar())},
					{ "supportsCorrelatedSubqueries", Boolean.valueOf(md.supportsCorrelatedSubqueries())},
					{ "supportsDataDefinitionAndDataManipulationTransactions", Boolean.valueOf(md.supportsDataDefinitionAndDataManipulationTransactions())},
					{ "supportsDataManipulationTransactionsOnly", Boolean.valueOf(md.supportsDataManipulationTransactionsOnly())},
					{ "supportsDifferentTableCorrelationNames", Boolean.valueOf(md.supportsDifferentTableCorrelationNames())},
					{ "supportsExpressionsInOrderBy", Boolean.valueOf(md.supportsExpressionsInOrderBy())},
					{ "supportsExtendedSQLGrammar", Boolean.valueOf(md.supportsExtendedSQLGrammar())},
					{ "supportsFullOuterJoins", Boolean.valueOf(md.supportsFullOuterJoins())},
					{ "supportsGroupBy", Boolean.valueOf(md.supportsGroupBy())},
					{ "supportsGroupByBeyondSelect", Boolean.valueOf(md.supportsGroupByBeyondSelect())},
					{ "supportsGroupByUnrelated", Boolean.valueOf(md.supportsGroupByUnrelated())},
					{ "supportsIntegrityEnhancementFacility", Boolean.valueOf(md.supportsIntegrityEnhancementFacility())},
					{ "supportsLikeEscapeClause", Boolean.valueOf(md.supportsLikeEscapeClause())},
					{ "supportsLimitedOuterJoins", Boolean.valueOf(md.supportsLimitedOuterJoins())},
					{ "supportsMinimumSQLGrammar", Boolean.valueOf(md.supportsMinimumSQLGrammar())},
					{ "supportsMixedCaseIdentifiers", Boolean.valueOf(md.supportsMixedCaseIdentifiers())},
					{ "supportsMixedCaseQuotedIdentifiers", Boolean.valueOf(md.supportsMixedCaseQuotedIdentifiers())},
					{ "supportsMultipleResultSets", Boolean.valueOf(md.supportsMultipleResultSets())},
					{ "supportsMultipleTransactions", Boolean.valueOf(md.supportsMultipleTransactions())},
					{ "supportsNonNullableColumns", Boolean.valueOf(md.supportsNonNullableColumns())},
					{ "supportsOpenCursorsAcrossCommit", Boolean.valueOf(md.supportsOpenCursorsAcrossCommit())},
					{ "supportsOpenCursorsAcrossRollback", Boolean.valueOf(md.supportsOpenCursorsAcrossRollback())},
					{ "supportsOpenStatementsAcrossCommit", Boolean.valueOf(md.supportsOpenStatementsAcrossCommit())},
					{ "supportsOpenStatementsAcrossRollback", Boolean.valueOf(md.supportsOpenStatementsAcrossRollback())},
					{ "supportsOrderByUnrelated", Boolean.valueOf(md.supportsOrderByUnrelated())},
					{ "supportsOuterJoins", Boolean.valueOf(md.supportsOuterJoins())},
					{ "supportsPositionedDelete", Boolean.valueOf(md.supportsPositionedDelete())},
					{ "supportsPositionedUpdate", Boolean.valueOf(md.supportsPositionedUpdate())},
					{ "supportsSchemasInDataManipulation", Boolean.valueOf(md.supportsSchemasInDataManipulation())},
					{ "supportsSchemasInIndexDefinitions", Boolean.valueOf(md.supportsSchemasInIndexDefinitions())},
					{ "supportsSchemasInPrivilegeDefinitions", Boolean.valueOf(md.supportsSchemasInPrivilegeDefinitions())},
					{ "supportsSchemasInProcedureCalls", Boolean.valueOf(md.supportsSchemasInProcedureCalls())},
					{ "supportsSchemasInTableDefinitions", Boolean.valueOf(md.supportsSchemasInTableDefinitions())},
					{ "supportsSelectForUpdate", Boolean.valueOf(md.supportsSelectForUpdate())},
					{ "supportsStoredProcedures", Boolean.valueOf(md.supportsStoredProcedures())},
					{ "supportsSubqueriesInComparisons", Boolean.valueOf(md.supportsSubqueriesInComparisons())},
					{ "supportsSubqueriesInExists", Boolean.valueOf(md.supportsSubqueriesInExists())},
					{ "supportsSubqueriesInIns", Boolean.valueOf(md.supportsSubqueriesInIns())},
					{ "supportsSubqueriesInQuantifieds", Boolean.valueOf(md.supportsSubqueriesInQuantifieds())},
					{ "supportsTableCorrelationNames", Boolean.valueOf(md.supportsTableCorrelationNames())},
					{ "supportsTransactions", Boolean.valueOf(md.supportsTransactions())},
					{ "supportsUnion", Boolean.valueOf(md.supportsUnion())},
					{ "supportsUnionAll", Boolean.valueOf(md.supportsUnionAll())},
					{ "usesLocalFilePerTable", Boolean.valueOf(md.usesLocalFilePerTable())},
					{ "usesLocalFiles", Boolean.valueOf(md.usesLocalFiles())}
				};

			lTable	= new JTable( new jdbcTableModel( DriverDataL, DriverHead ) );

			final Object[ ][ ]	DriverDataR	= {
					{ "getDefaultTransactionIsolation", Integer.valueOf(md.getDefaultTransactionIsolation())},
					{ "getDriverMajorVersion", Integer.valueOf(md.getDriverMajorVersion())},
					{ "getDriverMinorVersion", Integer.valueOf(md.getDriverMinorVersion())},
					{ "getMaxBinaryLiteralLength", Integer.valueOf(md.getMaxBinaryLiteralLength())},
					{ "getMaxCatalogNameLength", Integer.valueOf(md.getMaxCatalogNameLength())},
					{ "getMaxCharLiteralLength", Integer.valueOf(md.getMaxCharLiteralLength())},
					{ "getMaxColumnNameLength", Integer.valueOf(md.getMaxColumnNameLength())},
					{ "getMaxCursorNameLength", Integer.valueOf(md.getMaxCursorNameLength())},
					{ "getMaxIndexLength", Integer.valueOf(md.getMaxIndexLength())},
					{ "getMaxProcedureNameLength", Integer.valueOf(md.getMaxProcedureNameLength())},
					{ "getMaxSchemaNameLength", Integer.valueOf(md.getMaxSchemaNameLength())},
					{ "getMaxStatementLength", Integer.valueOf(md.getMaxStatementLength())},
					{ "getMaxTableNameLength", Integer.valueOf(md.getMaxTableNameLength())},
					{ "getMaxUserNameLength", Integer.valueOf(md.getMaxUserNameLength())},
					{ "getMaxColumnsInGroupBy", Integer.valueOf(md.getMaxColumnsInGroupBy())},
					{ "getMaxColumnsInIndex", Integer.valueOf(md.getMaxColumnsInIndex())},
					{ "getMaxColumnsInOrderBy", Integer.valueOf(md.getMaxColumnsInOrderBy())},
					{ "getMaxColumnsInSelect", Integer.valueOf(md.getMaxColumnsInSelect())},
					{ "getMaxColumnsInTable", Integer.valueOf(md.getMaxColumnsInTable())},
					{ "getMaxConnections", Integer.valueOf(md.getMaxConnections())},
					{ "getMaxRowSize", Integer.valueOf(md.getMaxRowSize())},
					{ "getMaxStatements", Integer.valueOf(md.getMaxStatements())},
					{ "getMaxTablesInSelect", Integer.valueOf(md.getMaxTablesInSelect())},

					{ "getDatabaseProductName", md.getDatabaseProductName()},
					{ "getDatabaseProductVersion", md.getDatabaseProductVersion()},
					{ "getDriverName", md.getDriverName()},
					{ "getDriverVersion", md.getDriverVersion()},
					{ "getURL", md.getURL()},
					{ "getUserName", md.getUserName()}
				};
			rTable	= new JTable( new jdbcTableModel( DriverDataR, DriverHead ) );

			genTypeInfoTable( md );
		}

		catch( SQLException se )
		{
			ToolBox.showInfo(se);
			while	( se != null )
			{
				System.out.println( se.getMessage() + '\n' + se.getSQLState() + '\n' + se.getErrorCode() + '\n');
				se	= se.getNextException();
			}
		}

		finally
		{
			setBorder( BorderFactory.createEtchedBorder( getBackground().brighter(), getBackground().darker() ) );

			final GridBagLayout		gbl	= new GridBagLayout();
			final GridBagConstraints	gbc	= new GridBagConstraints();

			setLayout( gbl );

			gbc.insets		= new Insets( 1, 1, 1, 1 );

			gbc.ipadx		= 1;
			gbc.ipady		= 1;
			gbc.weightx		= 1.5;
			gbc.weighty		= 1.5;

			gbc.fill		= GridBagConstraints.BOTH;

			final JScrollPane	lsp;
			final JScrollPane rsp;
			final JScrollPane ssp;
			final JScrollPane tsp;
			final JScrollBar	lsb;
			final JScrollBar rsb;
			final JScrollBar ssb;
			final JScrollBar tsb;

			gbc.anchor		= GridBagConstraints.WEST;
			gbc.gridwidth	= 1;	// GridBagConstraints.RELATIVE;

			setTableCharacteristics( lTable );
			lsp	= new JScrollPane( lTable );
			lsb	= lsp.createHorizontalScrollBar();
			lsp.setHorizontalScrollBarPolicy( ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED );
			add( lsp, gbc );

			gbc.anchor		= GridBagConstraints.EAST;
			gbc.gridwidth	= GridBagConstraints.REMAINDER;
			setTableCharacteristics( rTable );
			rsp	= new JScrollPane( rTable );
			rsb	= rsp.createHorizontalScrollBar();
			rsp.setHorizontalScrollBarPolicy( ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED );
			add( rsp, gbc );

			gbc.anchor		= GridBagConstraints.SOUTH;
			gbc.gridwidth	= GridBagConstraints.REMAINDER;
			setTableCharacteristics( sTable );
			sTable.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
			ssp	= new JScrollPane( sTable );
			ssb	= ssp.createHorizontalScrollBar();
			ssp.setHorizontalScrollBarPolicy( ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS );
			add( ssp, gbc );

			gbc.anchor		= GridBagConstraints.SOUTH;
			gbc.gridwidth	= GridBagConstraints.REMAINDER;

			tTable.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
			tsp	= new JScrollPane( tTable );
			tsb	= ssp.createHorizontalScrollBar();
			tsp.setHorizontalScrollBarPolicy( ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS );
			add( tsp, gbc );
		}
	}

	void	setTableCharacteristics(final JTable t)
	{
		int		tWidth	= 0;
		int		w		= 0;

		tc		= t.getColumn( "JDBC Method Support" );
		w		= getPreferredColumnWidth( tc, t ) + getPreferredColumnWidth( tc, t ) / 10;
		tWidth += w;
		tc.setMinWidth( w );

		tc		= t.getColumn( "Value" );
		w		= getPreferredColumnWidth( tc, t ) + getPreferredColumnWidth( tc, t ) / 10;
		tWidth += w;
		tc.setMinWidth( w );

		t.sizeColumnsToFit( 0 );	// JDK Bug : Needs to be called.
	}

	public	boolean	getScrollableTracksViewportWidth()
	{
		return false;
	}

	int	getPreferredColumnWidth(final TableColumn tc, final JTable t)
	{
		final int	wHeader		= getPreferredColumnHeaderWidth( tc, t );
		final int	wColumn 	= maxColumnCellWidth( tc, t );

		return	wHeader > wColumn ? wHeader : wColumn;
	}

	private	int	getPreferredColumnHeaderWidth( final TableColumn tc, final JTable t )
	{
		final TableCellRenderer	r	= t.getTableHeader().getDefaultRenderer();

		final Component		c	= r.getTableCellRendererComponent( t, tc.getHeaderValue(), false, false, 0, 0 );
		return c.getPreferredSize().width;
	}

	private	int	maxColumnCellWidth( final TableColumn col, final JTable t )
	{
		TableCellRenderer	r;
		Component		c;

		final int	mi		= col.getModelIndex();
		int	cWidth	= 0;
		int	cMaxWidth	= 0;

		for( int row = 0; row < t.getRowCount(); ++row )
		{
			r		= t.getCellRenderer( row, mi );
			c		= r.getTableCellRendererComponent( t, t.getValueAt( row, mi ), false, false, row, mi );

			cWidth	= c.getPreferredSize().width;
			cMaxWidth	= cWidth > cMaxWidth ? cWidth : cMaxWidth;
		}
		return	cMaxWidth;
	}

	private	void	genTypeInfoTable( final DatabaseMetaData md )
	{
		try
		{
			final ResultSet	rs	= md.getTypeInfo();

			Vector	rowData	= new Vector();
			final Vector	colHeadData	= new Vector();
			final Vector	TableData	= new Vector();

			int		tWidth;
			int		w;

			rsmd	= rs.getMetaData();

			for	( int i = 0; i < rsmd.getColumnCount(); i++ )
			{
				colHeadData.add( rsmd.getColumnName( i + 1 ) );
			}

			while	( rs.next() )
			{
				rowData		= new Vector();

				for	( int i = 0; i < rsmd.getColumnCount(); i++ )
				{
					switch ( rsmd.getColumnType( i + 1 ) )
					{
						case	1:	rs.getString( i + 1 );
								if	( rs.wasNull() ? rowData.add("") : rowData.add( rs.getString( i + 1 ) ) )
								{
                                }
								break;

						case	2:	final Float	f2 = new Float( rs.getFloat( i + 1 ) );
								if	( rs.wasNull() ? rowData.add("") : rowData.add( f2 ) )
								{
                                }
								break;

						case	3:	final Float	f3 = new Float( rs.getFloat( i + 1 ) );
								if	( rs.wasNull() ? rowData.add("") : rowData.add( f3 ) )
								{
                                }
								break;

						case	4:	final Integer	i4 = Integer.valueOf(rs.getInt(i + 1));
								if	( rs.wasNull() ? rowData.add("") : rowData.add( i4 ) )
								{
                                }
								break;

						case	5:	final Integer	i5 = Integer.valueOf(rs.getInt(i + 1));
								if	( rs.wasNull() ? rowData.add("") : rowData.add( i5 ) )
								{
                                }
								break;

						case	6:	final Float	f6 = new Float( rs.getFloat( i + 1 ) );
								if	( rs.wasNull() ? rowData.add("") : rowData.add( f6 ) )
								{
                                }
								break;

						case	7:	final Double	d7 = new Double( rs.getDouble( i + 1 ) );
								if	( rs.wasNull() ? rowData.add("") : rowData.add( d7 ) )
								{
                                }
								break;

						case	8:	final Float	f8 = new Float( rs.getFloat( i + 1 ) );
								if	( rs.wasNull() ? rowData.add("") : rowData.add( f8 ) )
								{
                                }
								break;

						case	91:	final Date	d91 = rs.getDate( i + 1 );
								if	( rs.wasNull() ? rowData.add("") : rowData.add( d91 ) )
								{
                                }
								break;

						case	92:	final Time	t92	= rs.getTime( i + 1 );
								if	( rs.wasNull() ? rowData.add("") : rowData.add( t92 ) )
								{
                                }
								break;

						case	93:	final Timestamp	ts93	= rs.getTimestamp( i + 1 );
								if	( rs.wasNull() ? rowData.add("") : rowData.add( ts93 ) )
								{
                                }
								break;

						case	12:	rs.getString( i + 1 );
								if	( rs.wasNull() ? rowData.add("") : rowData.add( rs.getString( i + 1 ) ) )
								{
                                }
								break;

						default:
							rowData.add("!");
							break;
					}
				}
				TableData.add( rowData );
			}
			
			tTable	= new JTable( TableData, colHeadData );

			for	( int	i = 0; i < colHeadData.size(); i++ )
			{
				tc	= tTable.getColumn( colHeadData.get( i ) );
				w	= getPreferredColumnWidth( tc, tTable ) + getPreferredColumnWidth( tc, tTable ) / 10;
	
				tc.setMinWidth( w );
			}
	
			tTable.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
			tTable.sizeColumnsToFit( 0 );
		}

		catch( SQLException se )
		{
            final StringBuilder eb = new StringBuilder();

			while	( se != null )
			{
				eb.append(se.getMessage()).append('\n').append(se.getSQLState())
						.append('\n').append(se.getErrorCode()).append('\n');
				se	= se.getNextException();
			}
			System.out.println( eb.toString() );
		}
	}

	public	JTable	getJdbcTable( final String t )
	{
		if	(t.equals("S") ) {
			return sTable;
		}

		if	(t.equals("L") ) {
			return lTable;
		}

		if	(t.equals("R") ) {
			return rTable;
		}

		if	(t.equals("T") ) {
			return tTable;
		}

		return null;
	}
}

class	jdbcTableModel	extends	DefaultTableModel
{
    private static final long serialVersionUID = 3079280392405524611L;

    jdbcTableModel(final Object[ ][ ] data, final String[ ] headings )
	{
		super( data, headings );
	}

	@Override
	public	boolean	isCellEditable( final int row, final int col )
	{
		final Class	columnClass	= getColumnClass( col );

		return	columnClass != ImageIcon.class;
	}

	@Override
	public	Class		getColumnClass( final int col )
	{
		final Vector	v	= (Vector)dataVector.elementAt( 0 );

		return	v.elementAt( col ).getClass();
	}
}