package misc.ingedigit.IntraApp;

import java.awt.TextField;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

class Table{
	ResultSet Cursor;
	ResultSetMetaData TableInfo;
        private Statement  SQL;
	int iColumnCount;
	private int iRecordCount;
	private int iRecordNumber;
        String sNomTabla;
	String[] sFieldLabels;
	private String[] sFieldTypes;
	
        Table(final String NomTabla, final String[] psFieldLabels, final String[] psFieldTypes,
				final Connection Db){
	   try{
		sNomTabla = NomTabla;
		   sFieldLabels = psFieldLabels;
		   sFieldTypes = psFieldTypes;

		SQL = Db.createStatement();

		Cursor = SQL.executeQuery( " Select * from " + NomTabla );          

  		TableInfo = Cursor.getMetaData();

  		iColumnCount = TableInfo.getColumnCount();

		RefreshTable();

 		System.out.println ("Creada Tabla: " + NomTabla );

	    }
	    catch(final Exception e){
	  	 System.out.println ("Error Construyendo Objeto Tabla...");	
		 e.printStackTrace();
	    } 
	}

	
	
	void Requery(final String Condicion){
           try{
                // Carga la tabla con una condicion dada
                
                System.out.println("SELECT * FROM " + sNomTabla + " WHERE " + Condicion);
                Cursor = SQL.executeQuery("Select * from " + sNomTabla + " WHERE " + Condicion);

           }
	   catch(final Exception e){
                 System.out.println ("Error creando el nuevo query..."); 
		 e.printStackTrace();
           } 

        }
        
        void ReloadTable(){
           try{
                // carga la tabla sin condicion y no nos ubica el primer registro
                Cursor = SQL.executeQuery( " Select * from " + sNomTabla );
           }
	   catch(final Exception e){
                 System.out.println ("Error Cargando la Tabla..."); 
		 e.printStackTrace();
           } 

        }

	void RefreshTable(){
	  try{
  		// carga la tabla sin condicion y nos ubica en el primer registro
		Cursor = SQL.executeQuery( " Select * from " + sNomTabla ); 

		// Calculamos el numero de registros;
		iRecordCount = 0;         		
		while(Cursor.next()){
			iRecordCount++;
		}
		if (iRecordCount > 0 ){
 			Cursor = SQL.executeQuery( " Select * from " + sNomTabla );
			Cursor.next();
			iRecordNumber = 1;
		}
	   }
	   catch(final Exception e){
	  	 System.out.println ("Error Refrescando la Tabla...");	
		 e.printStackTrace();
           } 
	
	}

	// Metodos para el mantenimiento 

	boolean Insert(final TextField [] Fields){
	   try{
		String sValues = "";
		
		for (int i = 0; i< (iColumnCount); i ++) {
			sValues += (i < iColumnCount - 1) ? "'" + Fields[i].getText() + "',"
					: "'" + Fields[i].getText() + "'";
		}
			
		SQL.executeUpdate("Insert into "+ sNomTabla + "  Values (" +  sValues + ")" );
		
		System.out.println("Inserted...!");	 
		return true;

	   }
	   catch(final Exception e){
	  	 System.out.println ("Error insertando en la Tabla...");	
		 e.printStackTrace();
		return false;
           } 
	}

	boolean  Update(final TextField[] Fields){
	   try{
		String sValues = "";
		String sComa = ",";
		for (int i = 0; i < iColumnCount; i ++){
			  sComa =  (i == (iColumnCount-1)) ? "": sComa;
			  
			  sValues += TableInfo.getColumnLabel(i+1) + "=" + "'" + Fields[i].getText() + "'" + sComa; 
		}	

		System.out.println(sValues);

		SQL.executeUpdate("Update "+ sNomTabla + " SET " +  sValues + " WHERE " + TableInfo.getColumnLabel(1) + "=" + "'" + Fields[0].getText() + "'" );
		RefreshTable();
		System.out.println("Updated...!");	 
 		return true;

	   }
	   catch(final Exception e){
	  	 System.out.println ("Error Actualizando el Registro...");	
		 e.printStackTrace();
		return false;
           } 
	}

	boolean Delete(final TextField []Fields){
	   try{ 
	   	final String sValues = "";
	   	SQL.executeUpdate("DELETE FROM " + sNomTabla + " WHERE " + TableInfo.getColumnLabel(1) + " = " + Fields[0].getText());
	   	System.out.println("Deleted...!");
	   	RefreshTable();
		return true;
	   }		   
	   catch(final Exception e){
	  	 System.out.println ("Error Eliminando el Registro...");	
		 e.printStackTrace();
		return false;
           } 
	}


	boolean Seek(final String Clave){
	   try{ 
	   	final String sValues = "";
	   	Cursor = SQL.executeQuery("Select * from " + sNomTabla +  " WHERE " + TableInfo.getColumnLabel(1) + " = '" + Clave + "'");
		System.out.println("Select * from " + sNomTabla +  " WHERE " + TableInfo.getColumnLabel(1) + " = '" + Clave + "'");			

		if (Cursor.next()){			
 	   		System.out.println("Record Found ...");			
			return true;
		}
		else {
			System.out.println("Record Not Found ...");			
			return false;

		}
	   }		   
	   catch(final Exception e){
	  	 System.out.println ("Error Buscando el  Registro...");	
		 e.printStackTrace();
		return false;
           } 
	}

	void NextRecord(){  
	   try{
		Cursor.next();
		iRecordNumber++;
	   }
	   catch(final Exception e){
	  	 System.out.println ("Error Obteniendo registro...");	
		 e.printStackTrace();
           } 
	}

	private void PrevRecord(){

	}

	String []  getCurRecord(){		
	   final String [] sRow = new String[iColumnCount];
	   try{

		for (int i = 0; i < iColumnCount; i++) {
			sRow[i] = Cursor.getString(i + 1);
		}
	   }
	   catch(final Exception e){
	  	 System.out.println ("Error Obteniendo registro...");	
		 e.printStackTrace();
           } 
	   return sRow;	   

	}

}
