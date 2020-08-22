package org.pclg.tools;

import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.io.FilenameFilter;
import java.io.Serializable;
import java.util.regex.Pattern;

/**
 * Implementa un filtro segun una expresión regular
 */
public class RegexFilter extends FileFilter implements FilenameFilter, Serializable {
	private Pattern pattern;

	/** Una descripcion de este RegexFilter */
	private String description;

	/** Indica si este RegexFilter acepta directorios o no (por omisión sí) */
	private boolean acceptDirs = true;

	//////////////////////////////////////
	// Constructores
	//////////////////////////////////////
	public RegexFilter() {
	}

	public RegexFilter(final String regex) {
		if (regex != null && regex.length() > 0) {
			pattern = Pattern.compile(regex);
		}
	}

	public RegexFilter(final String regex, final String desc) {
		this(regex);
		description = desc;
	}

	public RegexFilter(final String regex, final String desc, final boolean acceptDirs) {
		this(regex, desc);
		this.acceptDirs = acceptDirs;
	}

	//////////////////////////////////////
	//
	//////////////////////////////////////

	/**
	 * Acepta directorios y lo que haya en el filtro
	 */
	@Override
	public boolean accept(final File f) {
		return (accept(f.getParentFile(), f.getName()));
	}

	/**
	 * Acepta directorios y lo que haya en el filtro
	 */
	@Override
	public boolean accept(final File dir, final String name) {
		// Si no hay filtro, aceptamos todo
		// Si es un directorio lo aceptamos dependiendo de la configuracion
		// Si no, verificamos que cumpla con el patrón
		return pattern == null
			|| acceptDirs && new File(dir, name).isDirectory()
			|| pattern.matcher(name).matches();
	}

	public void setRegex(final String regex) {
		if (regex != null && regex.length() > 0) {
			pattern = Pattern.compile(regex);
		} else {
			pattern = null;
		}
	}

	public void setDescription(final String s) {
		description = s;
	}

	@Override
	public String getDescription() {
		return (description);
	}
}
