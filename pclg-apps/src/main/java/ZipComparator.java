import org.pclg.tools.Dir;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipComparator {
   public static void main(final String [] args) {
      final String inFilename;
      String outFilename;
      final byte[] buf = new byte[1024];
      int len;
      try {
/*
         // This example creates a ZIP file with one entry.
         inFilename = "mp3.txt";
         outFilename = "kk.zip";
         FileInputStream in1 = new FileInputStream(inFilename);
         ZipOutputStream out1 = new ZipOutputStream(new FileOutputStream(outFilename));
         // Add ZIP entry to output stream.
         out1.putNextEntry(new ZipEntry(inFilename));
         while((len = in1.read(buf)) > 0) {
            out1.write(buf, 0, len);
         }
         out1.closeEntry();
         out1.close();
         in1.close();
*/

         // En un arreglo de File ponemos todos los archivos del arbol
         final List<File> archivo;
         final String[] cacheName;
         final long[]    cacheSize;
         final long[]    cacheTime;
         try {
            archivo = (new Dir(Dir.NULL_EXCLUDE_LIST)).listarArchivos(new File("c:/pablo/image0"), true);
            cacheName = new String[archivo.size()];
            cacheSize = new long[archivo.size()];
            cacheTime = new long[archivo.size()];
            for(int i = 0; i < archivo.size(); i++) {
               cacheSize[i] = archivo.get(i).length();
               cacheName[i] = archivo.get(i).getName();
               cacheTime[i] = archivo.get(i).lastModified();
            }
         }
         catch(final IOException ex) {
            System.err.println("Si pasas el nombre de un directorio es mas de pinga");
            return;
         }

         // Listing the Contents of a ZIP File
         inFilename = "dmf2.zip";
         final ZipFile zf = new ZipFile(inFilename);
         System.out.println("contenido de: " + inFilename);
         int current = 0;
         for(final Enumeration entries = zf.entries(); entries.hasMoreElements();) {
            final ZipEntry entry = (ZipEntry) entries.nextElement();
            final String name = entry.getName();
            final long   time = entry.getTime();
            final long   size = entry.getSize();
//            System.out.println(name + "\t\t" + size + "\t" + new Date(time) + "\t" + time);
            System.err.println("current: " + ++current);
            int likellyness = 0;
            for(int i = 0; i < archivo.size(); i++) {
               likellyness = 0;
               if(size != cacheSize[i]) {
				   continue;
			   }
               likellyness++;
               if(name.equalsIgnoreCase(cacheName[i])) {
				   likellyness++;
			   }
               if(time == cacheTime[i]) {
				   likellyness++;
			   }
               if(likellyness > 0) {
				   System.out.println(new StringBuilder().append(likellyness)
                       .append(' ').append(entry.getName()).append('\t')
                       .append(archivo.get(i).getAbsolutePath()).toString());
			   }
            }
         }

/*
         // This example reads a ZIP file and decompresses the first entry.
         inFilename = "kk.zip";
         outFilename = "outfile";
         ZipInputStream in = new ZipInputStream(new FileInputStream(inFilename));
         OutputStream out = new FileOutputStream(outFilename);

         ZipEntry entry;
         if((entry = in.getNextEntry()) != null) {
             System.out.println("Extrayendo: " + entry.getName());
             while ((len = in.read(buf)) > 0) {
                 out.write(buf, 0, len);
             }
         }
         out.close();
         in.close();
*/
      }
      catch(final IOException ex) {
         ex.printStackTrace();
      }
   }
}
