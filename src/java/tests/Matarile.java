package tests;

import org.pclg.tools.ExtensionFiltro;
import org.pclg.tools.ToolBox;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
	Mueve archivos .java de un directorio a otro, poninedoles ademas la clausula package correspondiente
*/
public class Matarile {
	///** La cantidad de espacios en un tab */
	//int tabSize;
	private final String sourceDirName;
	private final String targetDirName;
	private final String packageClause;

	private Matarile(final String[] args) {
		/*
		if(args.length < 2) {
			System.err.println("Uso: java tests.Matarile <nro de espacios por tab> <archivos>");
			System.exit(1);
		}
		tabSize = Integer.parseInt(args[0]);
		for(int i = 1; i < args.length; i++) {
			tabify(args[i]);
		}
		*/

		// de momento a mano
		sourceDirName = "C:/pablo/java/Asintec/Adra";
		targetDirName = "C:/pablo/java/_src/net/asintec/adra/client";
		packageClause = "package net.asintec.adra.client;";

		final File[] theFiles = new File(sourceDirName).listFiles(new ExtensionFiltro(new String[]{"java"}, "filtro", false));
		System.out.println("Procesando " + theFiles.length + " archivos");
		for(int ii = 0; ii < theFiles.length; ii++) {
			try {
				System.out.print("'Procesando' " + theFiles[ii].getName() + " ... ");
				processFile(theFiles[ii]);
				System.out.println("Listo");
			}
			catch(final IOException ex) {
				System.out.println("No pude");
				ToolBox.showInfo(ex);
			}
		}
	}

	void processFile(final File file) throws IOException {
		final File targetDir = new File(targetDirName);
		// Si no existe, crearlo
		if(!targetDir.exists()) {
			targetDir.mkdirs();
		}

		final BufferedReader reader = new BufferedReader(new FileReader(file));
		//PrintStream ps = new PrintStream(new FileOutputStream(new File(targetDir, file.getName())));
		final BufferedWriter ps = new BufferedWriter(new FileWriter(new File(targetDir, file.getName())));

		// pòner la linea de package
		//ps.println(//package);
		//ps.println(packageClause);
		//ps.println();

			ps.write("//package");
			ps.newLine();
			ps.write(packageClause);
			ps.newLine();
			ps.newLine();

		String line;
		while((line = reader.readLine()) != null) {
			//ps.println(line);
			ps.write(line);
			ps.newLine();
		}
		ps.close();
		reader.close();
	}
	
	public static void main(final String[] args) {
		new Matarile(args);
	}
}
