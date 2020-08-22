package tests;
/*==============================================================================================

I was searching for a way to connect to an excel file without having to set the file up as a Data Source in windows and I found a way that works! Here
is the main line. 

db = DriverManager.getConnection("jdbc:odbc:Driver={Microsoft Excel Driver 
								 (*.xls)};DBQ=c:/temp/test2.xls;DriverID=22;READONLY=false","","") 
								 ; 

Instead of having to set test2 up as a Data Source I can instead create the DNS on the fly in this way. 

I would bet that this will work for other data sources such as access, oracle etc. The only thing is you must be sure the Driver is already set up in
ODBC. 



==============================================================================================

The included code, Excel.java, has everything you need to create a table in Excel, insert data, and select it back. You can perform other functions as
well, but this will get you started. 

Hope it helps. 

Tim 

--- 
*/

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

/**************************************************************** 
********** 

  Name: Excel.java 
  
	Purpose: To demonstrate how to use ODBC and Excel to create a table, 
	insert data into it, and select it back out. 
	
	  Version: Developed using JDK 1.3, but also works with JDK 1.2.2 
	  
		Instructions: 
		
		  1) Create a new Excel spreadsheet 
		  
			2) Create a new ODBC data source that points to this spreadsheet 
			
			  a) Go to Control Panel 
			  b) Open "ODBC Data sources (32-bit) (wording may be slightly 
			  different for different platforms) 
			  c) Under "User DSN" tab, press "Add" button 
			  d) Select the "Microsoft Excel Driver (*.xls)" and press 
			  "Finish" button 
			  e) Enter "Data Source Name" of "TestExcel" 
			  f) Press "Select Workbook" button 
			  g) Locate and select the spreadsheet you created in Step 1 
			  h) Unselect the "Read Only" checkbox 
			  i) Press "Ok" button 
			  
				3) Compile and run Excel.java 
				
				  4) Open Excel spreadsheet and you will find a newly created 
				  sheet, GOOD_DAY, with three rows of data. 
				  
					Notes: 
					If you want to select data from a spreadsheet that was NOT 
					created via JDBC-ODBC (i.e. you entered data manually into 
					a spreadsheet and want to select it out), you must reference 
					the sheet name as "[sheetname$]". 
					
					  When you create the table and insert the data using Java, you 
					  must reference the sheet name as "sheetname". 
					  
						Also, do not have the spreadsheet open when you are running 
						the program. You can get locking conflicts. 
						
						  ***************************************************************** 
*********/ 

public class ExcelJDBC {
	private ExcelJDBC()
	{ 
		setDefaults(); 
	} 
	
	private static void m(final String pMessage)
	{ 
		System.out.println(pMessage); 
	} 
	
	private void setDefaults() 
	{ 
		setDriver("sun.jdbc.odbc.JdbcOdbcDriver"); 
		setUrl("jdbc:odbc"); 
		
		// ODBC data source named "TestExcel" defined from Control Panel 
		setDataSource("C:/tmp/ExcelFile.xls");
		
		setTableName("GOOD_DAY"); 
	} 
	
	void openDatabase()
	{ 
		
		final String lConnectStr = getUrl()+":"+getDataSource();
		
		try { 
			Class.forName(getDriver()); 
			//gConnection = DriverManager.getConnection (lConnectStr);
			gConnection = DriverManager.getConnection("jdbc:odbc:Driver={Microsoft Excel Driver (*.xls)}; DBQ="
			            + getDataSource() + ";DriverID=22;READONLY=false", "", "");
		} 
		catch (final Exception e) {
			m("openDatabase(): "+e.getMessage()); 
		} 
	} 
	
	private void closeDatabase() 
	{ 
		try { 
			getConnection().close(); 
		} 
		catch (final Exception e) {
			m("closeDatabase(): "+e.getMessage()); 
		} 
	} 
	
	private void createTable() 
	{ 
		m("createTable() begin"); 
		
		Statement lStat = null; 
		
		try { 
			lStat = getConnection().createStatement(); 
			
			lStat.execute("CREATE TABLE "+getTableName()+" (" 
				+" ID INTEGER" 
				+" ,NAME VARCHAR" 
				+")"); 
		} 
		catch (final Exception e) {
			m("createTable(): "+e.getMessage()); 
		} 
		
		m("createTable() end"); 
	} 
	
	private void doInsert() 
	{ 
		m("doInsert() begin"); 
		
		Statement lStat = null; 
		
		try { 
			lStat = getConnection().createStatement(); 
			
			lStat.executeUpdate("INSERT INTO " 
				+getTableName()+"(ID,NAME) VALUES (10,'KANGAROO')"); 
			lStat.executeUpdate("INSERT INTO " 
				+getTableName()+"(ID,NAME) VALUES (20,'KOALA')"); 
			lStat.executeUpdate("INSERT INTO " 
				+getTableName()+"(ID,NAME) VALUES (30,'PAUL HOGAN')"); 
			
			lStat.close(); 
		} 
		catch (final Exception e) {
			m("doInsert(): "+e.getMessage()); 
		} 
		
		m("doInsert() end"); 
	} 
	
	private void doQuery() 
	{ 
		m("doQuery() begin"); 
		
		try { 
			final Statement lStat = getConnection().createStatement();
			final ResultSet lRes = lStat.executeQuery(
				"SELECT * FROM "+getTableName() 
				); 
			final ResultSetMetaData lMeta = lRes.getMetaData();
			
			// print out the column headers separated by commas 
			for (int i = 1; i <= lMeta.getColumnCount(); ++i) 
			{ 
				if (i > 1) 
					System.out.print(", "); 
				
				final String lValue = lMeta.getColumnName(i);
				System.out.print(lValue); 
			} 
			System.out.println(""); 
			
			// print out the data separated by commas 
			while (lRes.next()) 
			{ 
				for (int i=1; i<=lMeta.getColumnCount(); ++i) 
				{ 
					if (i > 1) 
						System.out.print(", "); 
					
					final String lValue = lRes.getString(i);
					System.out.print(lValue); 
				} 
				System.out.println(""); 
			} 
			
			lRes.close(); 
			lStat.close(); 
		} 
		catch (final Exception e) {
			m("doQuery(): "+e.getMessage()); 
		} 
		
		m("doQuery() end"); 
	} 
	
	private void run() 
	{ 
		openDatabase(); 
		
		createTable(); 
		doInsert(); 
		doQuery(); 
		
		closeDatabase(); 
	} 
	
	public static void main(final String[] args)
	{ 
		m("main() begin"); 
		
		final ExcelJDBC lExcel = new ExcelJDBC();
		
		lExcel.run(); 
		
		m("main() end"); 
		
		System.exit(0); 
	} 
	
	void setTableName(final String pValue)
	{ 
		gTableName = pValue; 
	} 
	
	String getTableName()
	{ 
		return(gTableName); 
	} 
	
	public void setSql(final String pValue)
	{ 
		gSql = pValue; 
	} 
	
	public String getSql() 
	{ 
		return(gSql); 
	} 
	
	Connection getConnection()
	{ 
		return(gConnection); 
	} 
	
	String getDataSource()
	{ 
		return(gDataSource); 
	} 
	
	void setDataSource(final String pValue)
	{ 
		gDataSource = pValue; 
	} 
	
	void setDriver(final String pValue)
	{ 
		gDriver = pValue; 
	} 
	
	void setUrl(final String pValue)
	{ 
		gUrl = pValue; 
	} 
	
	String getDriver ()
	{ 
		return (gDriver); 
	} 
	
	String getUrl ()
	{ 
		return (gUrl); 
	} 
	
	private Connection gConnection;
	
	private String gDataSource,gTableName,gSql,gDriver,gUrl;
	
} 
