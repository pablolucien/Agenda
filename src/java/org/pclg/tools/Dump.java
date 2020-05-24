package org.pclg.tools;

import java.io.FileInputStream;
import java.io.IOException;
public class Dump {
   public static void main(final String [] args) {
       for (final String arg : args) {
           dump(arg);
       }
   }

   private static void dump(final String fileName) {
      try {
         final FileInputStream dis = new FileInputStream(fileName);
         final byte[] input = new byte [16];
         int count = 0;
         while((count = dis.read(input)) > 0) {
            for(int i = 0; i < count; i++) {
               System.out.print(String.valueOf(Character.forDigit(input[i] >>> 4, 16))
                   + Character.forDigit(input[i] & 0x0F, 16));
               System.out.print(" ");
            }

            for(int i = count; i < 16; i++) {
               System.out.print("   ");
            }

            final String s = new String(input, 0, count);
            char c;
            for(int i = 0; i < count; i++) {
               c = s.charAt(i);
               if (Character.isLetterOrDigit(c) || c == ' ' || c > 90)   //???
               {
                   System.out.print(c);
               }
               else {
                   System.out.print(".");
               }
            }

            System.out.println();
         }
         dis.close();
      }
      catch(final IOException ex) {
         ex.printStackTrace();
      }
   }
}
