// ******************************** package
package net.asintec.migrator;


// ******************************** imports
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
	DatabaseFileChooser <BR>
	Un FileChooser para Migrator
	@author El Coyote Cojo
	@version 2002.may.29 10:55:12, CEST
*/
class DatabaseFileChooser extends JFileChooser {
    private static final long serialVersionUID = 6043979642516504981L;
    // ******************************** Variables de clase

	// ******************************** Variables de instancia

	// ******************************** Constructores

	/**
		-author El Coyote Cojo
		-version 2002.may.29 10:55:12, CEST
	*/
	public DatabaseFileChooser(final File dir) {
		super(dir);
		init();
	}

	/**
		-author El Coyote Cojo
		-version 2002.may.29 10:55:12, CEST
	*/
	public DatabaseFileChooser(final String dir) {
		super(dir);
		init();
	}

	// ******************************** Metodos de instancia

	/**
		Inicializa este objeto.
		Debe ser private porque es llamado por el constructor
		-author El Coyote Cojo
		-version 2002.may.29 10:55:12, CEST
	*/
	private void init() {
		addChoosableFileFilter(
			new FileFilter() {
				@Override
				public boolean accept(final File f) {
					if(f.isDirectory()) {
						return (true);
					}
					if(f.getName().toLowerCase().endsWith(".xls")) {
						return (true);
					}
					return(false);
				}

				@Override
				public String getDescription() {
					return("Hojas de Excel");
				}
		});
		addChoosableFileFilter(
			new FileFilter() {
				@Override
				public boolean accept(final File f) {
					if(f.isDirectory()) {
						return (true);
					}
					if(f.getName().toLowerCase().endsWith(".mdb")) {
						return (true);
					}
					return(false);
				}

				@Override
				public String getDescription() {
					return("Bases de datos Access");
				}
		});
	}
}
