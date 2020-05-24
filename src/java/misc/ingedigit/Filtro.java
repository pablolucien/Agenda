package misc.ingedigit;

import org.pclg.tools.FileTools;

import java.io.File;
import java.io.FilenameFilter;
import java.util.StringTokenizer;

class Filtro implements FilenameFilter {
	private final String[] extensiones;

	Filtro(final String[] s) {
		extensiones = s;
	}

	Filtro(final String s) {
		final StringTokenizer st = new StringTokenizer(s);
		extensiones = new String[st.countTokens()];
		for (int i = 0; i < extensiones.length; i++) {
			extensiones[i] = st.nextToken();
		}
	}

	@Override
	public boolean accept(final File dir, final String name) {
		final String ext = FileTools.splittName(name).extension;
		for (final String extension : extensiones) {
			if (ext.equalsIgnoreCase(extension)) {
				return true;
			}
		}
		return false;
	}
}
