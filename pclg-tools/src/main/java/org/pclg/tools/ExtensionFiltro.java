// ******************************** package
package org.pclg.tools;

// ******************************** imports
/**
   Implementa un filtro segun las extensiones de los archivos
*/

import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class ExtensionFiltro extends FileFilter
							 implements FilenameFilter {
	/** Las extensiones que acepta este ExtensionFiltro */
   private String[] extensiones;

	/** Una descripcion de este ExtensionFiltro */
   private String description;

	/** Indica si este ExtensionFiltro acepta directorios o no (por omisión sí) */
	private boolean acceptDirs = true;

   //////////////////////////////////////
   // Constructores
   //////////////////////////////////////
   public ExtensionFiltro() {
   }

   public ExtensionFiltro(final String[] s) {
		extensiones = s;
   }

   public ExtensionFiltro(final String desc) {
		description = desc;
   }

   public ExtensionFiltro(final String[] s, final String desc) {
		extensiones = s;
		description = desc;
   }

   public ExtensionFiltro(final String[] s, final String desc, final boolean acceptDirs) {
		extensiones = s;
		description = desc;
		  this.acceptDirs = acceptDirs;
   }

   //////////////////////////////////////
   //
   //////////////////////////////////////
   /**
	 Acepta directorios y lo que haya en el filtro
   */
   @Override
   public boolean accept(final File f) {
	  return accept(f.getParentFile(), f.getName());
   }

	/**
	  Acepta directorios y lo que haya en el filtro
	*/
	@Override
	public boolean accept(final File  dir, final String  name) {
		try {
			// Si no hay filtro, aceptamos todo
			if(extensiones == null) {
				return true;
			}
			if(extensiones.length == 0) {
				return true;
			}

			// Si es un directorio lo aceptamos dependiendo de la configuracion
			if(acceptDirs && new File(dir, name).isDirectory()) {
				return true;
			}

			final String ext = name.substring(name.lastIndexOf('.') + 1);
			// OJO: aceptamos los archivo que terminan con '.ext' y los que se llaman simplemente 'ext' para alguna de las ext del array
			for(int i = 0; i < extensiones.length; i++) {
				if(ext.equalsIgnoreCase(extensiones[i])) {
					return true;
				}
			}
		}
		catch(final Exception ex) {
			ToolBox.showInfo(ex);
		}
		return false;
	}

   /**
	  Acepta un string con las extensiones separadas por comas, ' '
   */
   public void setExtensions(final String s) {
	  final List list = new ArrayList();
	  final StringTokenizer st = new StringTokenizer(s, ", ");
	  while(st.hasMoreTokens()) {
		 list.add(st.nextToken());
	  }
	  extensiones = new String[list.size()];
	  list.toArray(extensiones);
   }

   public void setExtensions(final String[] s) {
	  extensiones = s;
   }

   public void setDescription(final String s) {
	  description = s;
   }
   @Override
   public String getDescription() {
	  return description;
   }
}
