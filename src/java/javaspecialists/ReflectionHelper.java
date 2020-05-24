package javaspecialists;

import sun.reflect.FieldAccessor;
import sun.reflect.ReflectionFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class ReflectionHelper {
  private static final String MODIFIERS_FIELD = "modifiers";

  private static final ReflectionFactory reflection =
      ReflectionFactory.getReflectionFactory();

	private ReflectionHelper() {
	}

	public static void setStaticFinalField(final Field field, final Object value)
			throws NoSuchFieldException, IllegalAccessException {
	  // we mark the field to be public
	  field.setAccessible(true);
	  // next we change the modifier in the Field instance to
	  // not be final anymore, thus tricking reflection into
	  // letting us modify the static final field
	  final Field modifiersField =
		  Field.class.getDeclaredField(MODIFIERS_FIELD);
	  modifiersField.setAccessible(true);
	  int modifiers = modifiersField.getInt(field);
	  // blank out the final bit in the modifiers int
	  modifiers &= ~Modifier.FINAL;
	  modifiersField.setInt(field, modifiers);
	  final FieldAccessor fa = reflection.newFieldAccessor(
		  field, false
	  );
	  fa.set(null, value);
	}
}
