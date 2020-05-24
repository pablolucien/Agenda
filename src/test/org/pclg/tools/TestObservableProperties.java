package org.pclg.tools;

import org.apache.log4j.Category;
import org.pclg.log.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * FIXME: Convert to test
 */
@SuppressWarnings("deprecation")
public class TestObservableProperties implements ChangeObserver<ObservableProperties> {
	private static final Category LOGGER = LoggerFactory.makeLog4J();

	private TestObservableProperties() throws Exception {
		final ObservableProperties properties = new ObservableProperties();
		properties.addChangeObserver(this);

		testSetProperty(properties);

		testPut(properties);

		testLoad(properties);

//		properties.storeToXML(new
//			FileOutputStream("C:/plucien/PERSONNEL/kk.properties.xml"),
//				"commentario");

		testLoadFromXML(properties);

		testPutAll(properties);

		LOGGER.debug("*** that's all folks ");
	}

	private void testLoadFromXML(final ObservableProperties properties)
			throws IOException, InterruptedException {
		properties.loadFromXML(new FileInputStream(
			"C:/plucien/PERSONNEL/kk.properties.xml"));
		Thread.sleep(100);
	}

	private void testLoad(final ObservableProperties properties) throws IOException,
			InterruptedException {
		properties.load(new FileInputStream("E:/bin/Agenda_icons.properties"));
		Thread.sleep(100);
	}

	private void testPut(final ObservableProperties properties)
			throws InterruptedException {
		properties.put("b", "value b");
		for (int ii = 0; ii < 5; ii++) {
			properties.put("b" + ii, "value b" + ii);
		}
		Thread.sleep(100);
	}

	private void testPutAll(final ObservableProperties properties)
			throws InterruptedException {
		final Map<String, String> map = new HashMap<String, String>();
		map.put("key map I", "value map I");
		map.put("key map II", "value map II");
		properties.putAll(map);
		Thread.sleep(100);
	}

	private void testSetProperty(final ObservableProperties properties)
			throws InterruptedException {
		properties.setProperty("a", "value a");
		for (int ii = 0; ii < 5; ii++) {
			properties.setProperty("a" + ii, "value a" + ii);
		}
		Thread.sleep(100);
	}

	/**
	 * @param args
	 *
	 * @throws Exception
	 */
	public static void main(final String[] args) throws Exception {
		new TestObservableProperties();
	}

	@Override
	public void objectChanged(final ObservableProperties observableProperties) {
		LOGGER.debug("*** propertiesChanged() called by "
			+ observableProperties);
	}
}
