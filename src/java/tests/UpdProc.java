package tests;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

class UpdProc {
    public static void main(final String[] argv) {
        try {
            Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
			final String user = "Administrador";
			final String pwd = "asintec72";
			final String url = "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb)};DBQ=" + "C:\\migrator\\test\\AccessTest.mdb" + ";UID=" + user + ";PWD=" + pwd;
			final Connection connection = DriverManager.getConnection(url);
			final Statement stmt = connection.createStatement();
			final ResultSet rs = stmt.executeQuery("SELECT ID, BASE0, IVA0, BASE1, IVA1, BASE2, IVA2, BASE3, IVA3, BASE4, IVA4, TOTALFRA FROM FCFACT");
			final String updSentence = "UPDATE fcfact SET Base0 = ?, Iva0 = ?, Base1 = ?, Iva1 = ?, Base2 = ?, Iva2 = ?, Base3 = ?, Iva3 = ?, Base4 = ?, Iva4 = ? WHERE Id = ?";
			final PreparedStatement pstmt = connection.prepareStatement(updSentence);
			while(rs.next()) {
				int pos = 0;
				final String id = rs.getString(++pos);
				final double base0 = rs.getDouble(++pos);
				final double iva0 = rs.getDouble(++pos);
				final double base1 = rs.getDouble(++pos);
				final double iva1 = rs.getDouble(++pos);
				final double base2 = rs.getDouble(++pos);
				final double iva2 = rs.getDouble(++pos);
				final double base3 = rs.getDouble(++pos);
				final double iva3 = rs.getDouble(++pos);
				final double base4 = rs.getDouble(++pos);
				final double iva4 = rs.getDouble(++pos);
				
//				System.out.println(id);
//				System.out.println(base0);
//				System.out.println(iva0);

				// Poner en 0 todos los valores
				for(int i = 0; i < 5 * 2; i++) {
					pstmt.setDouble(i + 1, 0);
				}

				adjust(base0, iva0, pstmt);
				adjust(base1, iva1, pstmt);
				adjust(base2, iva2, pstmt);
				adjust(base3, iva3, pstmt);
				adjust(base4, iva4, pstmt);

				pstmt.setString(11, id);
				pstmt.execute();
//				System.out.println("Actualizado: " + id);
			}


            stmt.close();
            connection.close();
			System.out.println("Actualizado");
        }
		catch (final ClassNotFoundException e) {
            e.printStackTrace();

        }
		catch( final Exception e ) {
            e.printStackTrace();
        }
    }

	
	private static void adjust(final double base0, final double iva0, final PreparedStatement pstmt) throws SQLException {
				if(base0 != 0) {
					final double ratio = Math.abs((iva0 * 100) / base0);
					if(ratio > 11.5) {								// 16%  = tipo 2
						//System.out.println("ratio: " + ratio);
						pstmt.setDouble(5, base0);
						pstmt.setDouble(6, iva0);
					}
					else if(ratio > 5.75) {							// 7%  = tipo 1
						//System.out.println("ratio: " + ratio);
						pstmt.setDouble(3, base0);
						pstmt.setDouble(4, iva0);
					}
					else if(ratio > 4.25) {							// 4.5%  = tipo 4
						//System.out.println("ratio: " + ratio);
						pstmt.setDouble(9, base0);
						pstmt.setDouble(10, iva0);
					}
					else if(ratio > 2) {							// 4%  = tipo 3
						//System.out.println("ratio: " + ratio);
						pstmt.setDouble(7, base0);
						pstmt.setDouble(8, iva0);
					}
					else {											// 0%  = tipo 0
						//System.out.println("ratio: " + ratio);
						pstmt.setDouble(1, base0);
						pstmt.setDouble(2, iva0);
					}
				}
	}
}
