package tests;

import org.apache.logging.log4j.Logger;
import org.pclg.annotations.QuickAndDirty;
import org.pclg.log.LoggerFactory;

import java.lang.annotation.Annotation;

/**
 * @author El Coyote Cojo
 * @since 23/08/16 18:41
 */
@QuickAndDirty(reason = "Porque yo lo valgo")
final class AnnotationTest {
	/** Logger for this class. */
	private static final Logger LOGGER = LoggerFactory.makeLog4J();

	AnnotationTest() {
		final Annotation[] annotations = getClass().getAnnotations();
		for (Annotation annotation : annotations) {
			LOGGER.error(annotation.toString());
		}
		final QuickAndDirty annotation = getClass().getAnnotation(QuickAndDirty.class);
		// Ojo: Esto vale si la anotaci�n tiene @Retention(RetentionPolicy.RUNTIME)
		if (annotation != null) {
			LOGGER.error(annotation.reason());
		}
	}

	/**
	 * Applications entry point.
	 */
	public static void main(final String[] args) {
		new AnnotationTest();
	}
}
