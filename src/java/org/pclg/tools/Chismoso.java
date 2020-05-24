package org.pclg.tools;
/**
 *  Envia un string a donde le digan.
 */

import java.io.PrintStream;

class Chismoso {
   private final PrintStream dest; // A quien va el mensaje en general
   private String header;    // Mensaje standard cada vez que chismee

   // Constructor CON header y destino no standard
   public Chismoso(final PrintStream dest, final String msg) {
      this.dest = dest;
      header = msg;
   }

   // Constructor SIN header y destino no standard
   public Chismoso(final PrintStream dest) {
      this.dest = dest;
      header = "";
   }

   // Constructor CON header y destino standard
   public Chismoso(final String msg) {
      dest = System.out;
      dest.println(msg);
   }
   // Constructor SIN header y destino standard
   public Chismoso() {
      dest = System.out;
      header = "";
   }

   // Este es un correveidile
   // Mensajes comunes
   public void dile(final String msg) {
      dest.println(header + msg);
   }
   public void dile() {
      dest.println(header + "");
   }
   // estos mensajes van para otro
   public void dile(final PrintStream ps, final String msg) {
      ps.println(header + msg);
   }
   public void dile(final PrintStream ps) {
      ps.println(header + "");
   }
}
