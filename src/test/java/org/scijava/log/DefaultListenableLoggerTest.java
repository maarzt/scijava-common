package org.scijava.log;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Matthias Arzt
 */
public class DefaultListenableLoggerTest {

	private ListenableLogger logger;
	private TestLogListener listener;

	@Before
	public void setup() {
		logger = new DefaultListenableLogger(LogLevel.INFO);
		listener = new TestLogListener();
		logger.addListener(listener);
	}

	@Test
	public void test() {
		listener.clear();

		logger.error("Hello World!");

		assertTrue(listener.hasLogged(m -> m.text().equals("Hello World!")));
		assertTrue(listener.hasLogged(m -> m.level() == LogLevel.ERROR));
	}
}
