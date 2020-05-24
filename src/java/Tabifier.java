import org.pclg.tools.ToolBox;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

public class Tabifier {
	/** La cantidad de espacios en un tab */
	private final int tabSize;

	private Tabifier(final String[] args) {
		if(args.length < 2) {
			System.err.println("Uso: java Tabifier <nro de espacios por tab> <archivos>");
			System.exit(1);
		}
		tabSize = Integer.parseInt(args[0]);
		for(int i = 1; i < args.length; i++) {
			tabify(args[i]);
		}
	}

	void tabify(final String filename) {
		try {
			System.out.print("'Tabificando' " + filename + " ... ");
			final BufferedReader reader = new BufferedReader(new FileReader(filename));
			final PrintStream ps = new PrintStream(new FileOutputStream(filename + ".1"));
			String line;
			while((line = reader.readLine()) != null) {
outer:			for(int i = 0; i < line.length() - 2; i++) {
					if(line.charAt(i) != ' ' && line.charAt(i) != '\t') {
						break;
					}
					if(line.charAt(i) == '\t') {
						continue;
					}
					//if(line.charAt(i) == ' ' && line.charAt(i + 1) == ' ' && line.charAt(i + 2) == ' ') {
					for(int j = i; j < i + tabSize; j++) {
						if(line.charAt(j) != ' ') {
							continue outer;
						}
					}
					line = ToolBox.replace(line, i, tabSize, "\t");
				}
				if(line.trim().equals("")) {
					ps.println();
				}
				else {
					ps.println(line);
				}
			}
			ps.close();
			reader.close();
			System.out.println("Listo");
		}
		catch(final IOException ex) {
			System.out.println("No pude");
			ToolBox.showInfo(ex);
		}
	}
	
	public static void main(final String[] args) {
		new Tabifier(args);
	}
}
