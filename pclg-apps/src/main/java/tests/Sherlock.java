package tests;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
   Averigua lo que hay dentro de una clase
*/
public class Sherlock {
   public static void main(final String [] args) throws Exception {
      String className = "Consola";
      if(args.length > 0) {
		  className = args[0];
	  }
      final Class c = Class.forName(className);
//      Object o = c.newInstance();
      printModifiers(c);
      System.out.println(c +  " extends " + c.getSuperclass().getName() );
      printSuperclasses(c);
      printInterfaceNames(c);
      printFieldNames(c);
      showConstructors(c);
      showMethods(c);
      showClasses(c);
   }

   private static void printModifiers(final Class c) {
      final int m = c.getModifiers();
      if (Modifier.isPublic(m)) {
		  System.out.print("public ");
	  }
      if (Modifier.isAbstract(m)) {
		  System.out.print("abstract ");
	  }
      if (Modifier.isFinal(m)) {
		  System.out.print("final ");
	  }
//      System.out.println();
   }

   private static void printSuperclasses(Class subclass) {
      System.out.println("Superclases ==========================================");
      Class superclass = subclass.getSuperclass();
      while (superclass != null) {
         final String className = superclass.getName();
         System.out.println(className);
         subclass = superclass;
         superclass = subclass.getSuperclass();
      }
   }

   private static void printInterfaceNames(final Class c) {
      System.out.println("Interfases ==========================================");
      final Class[] theInterfaces = c.getInterfaces();
      if(theInterfaces.length > 0) {
		  System.out.println("   implements ");
	  }
      for (int i = 0; i < theInterfaces.length; i++) {
         final String interfaceName = theInterfaces[i].getName();
         System.out.println("      " + interfaceName);
      }
   }

   private static void printFieldNames(final Class c) {
      System.out.println("Campos ==========================================");
      final Field[] publicFields = c.getDeclaredFields();
      for (int i = 0; i < publicFields.length; i++) {
         final String fieldName = publicFields[i].getName();
         final Class typeClass = publicFields[i].getType();
         final String fieldType = typeClass.getName();
         final int modifiers = publicFields[i].getModifiers();
         System.out.print(Modifier.toString(modifiers) + " ");
         System.out.println(fieldType + " " + fieldName + ";");
      }
   }

   private static void showConstructors(final Class c) {
      System.out.println("Constructores ==========================================");
      final Constructor[] theConstructors = c.getDeclaredConstructors();
      for (int i = 0; i < theConstructors.length; i++) {
         final int modifiers = theConstructors[i].getModifiers();
         System.out.print(Modifier.toString(modifiers) + " " + c.getName());
         System.out.print("(");
         final Class[] parameterTypes = theConstructors[i].getParameterTypes();
         for (int k = 0; k < parameterTypes.length; k ++) {
            final String parameterString = parameterTypes[k].getName();
            System.out.print(parameterString + (k < parameterTypes.length - 1 ? ", " : ""));
         }
         System.out.println(")");
//         System.out.println(theConstructors[i].toString());
         }
   }

   private static void showMethods(final Class c) {
      System.out.println("Metodos ==========================================");
      final Method[] theMethods = c.getDeclaredMethods();
      for (int i = 0; i < theMethods.length; i++) {
         final String methodString = theMethods[i].getName();
         final String returnString = theMethods[i].getReturnType().getName();
         final Class[] parameterTypes = theMethods[i].getParameterTypes();
         final int modifiers = theMethods[i].getModifiers();
         System.out.print(Modifier.toString(modifiers) + " ");
         System.out.print(returnString + " " + methodString + "(");
         for (int k = 0; k < parameterTypes.length; k ++) {
            final String parameterString = parameterTypes[k].getName();
            System.out.print(parameterString + (k < parameterTypes.length - 1 ? ", " : ""));
         }
         System.out.println(")");
//         System.out.println(theMethods[i].toString());
      }
   }

   private static void showClasses(final Class c) {
      System.out.println("Clases que incluye ==================================");
      final Class[] theClasses = c.getDeclaredClasses();
      for (int i = 0; i < theClasses.length; i++) {
         System.out.println(theClasses[i].toString());
      }
   }
}
