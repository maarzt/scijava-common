package org.scijava.log;

import org.junit.Test;

import java.util.function.BiConsumer;
import java.util.function.Function;

import static org.junit.Assert.*;

/**
 * Tests {@link AbstractLogService}
 *
 * @author Matthias Arzt
 */

public class AbstractLogServiceTest {

	@Test
	public void testGetPrefix() {
		TestableLogService logService = new TestableLogService();
		assertEquals("[ERROR]", logService.getPrefix(LogService.ERROR));
		assertEquals("[TRACE]", logService.getPrefix(LogService.TRACE));
	}

	@Test
	public void testCompleteLogMethod() {
		testCompleteLogMethod("ERROR", (logService, msg, t) -> logService.error(msg, t));
		testCompleteLogMethod("WARN", (logService, msg, t) -> logService.warn(msg, t));
		testCompleteLogMethod("INFO", (logService, msg, t) -> logService.info(msg, t));
		testCompleteLogMethod("DEBUG", (logService, msg, t) -> logService.debug(msg, t));
		testCompleteLogMethod("TRACE", (logService, msg, t) -> logService.trace(msg, t));
	}

	@Test
	public void testMessageLogMethod() {
		testMessageLogMethod("ERROR", (logService, msg) -> logService.error(msg));
		testMessageLogMethod("WARN", (logService, msg) -> logService.warn(msg));
		testMessageLogMethod("INFO", (logService, msg) -> logService.info(msg));
		testMessageLogMethod("DEBUG", (logService, msg) -> logService.debug(msg));
		testMessageLogMethod("TRACE", (logService, msg) -> logService.trace(msg));
	}

	@Test
	public void testExceptionLogMethod() {
		testExceptionLogMethod("ERROR", (logService, t) -> logService.error(t));
		testExceptionLogMethod("WARN", (logService, t) -> logService.warn(t));
		testExceptionLogMethod("INFO", (logService, t) -> logService.info(t));
		testExceptionLogMethod("DEBUG", (logService, t) -> logService.debug(t));
		testExceptionLogMethod("TRACE", (logService, t) -> logService.trace(t));
	}

	private void testCompleteLogMethod(String prefix, LogMethodCall logMethodCall) {
		testLogMethod(prefix, logMethodCall, true, true);
	}

	private void testMessageLogMethod(String prefix, BiConsumer<LogService, Object> call) {
		testLogMethod(prefix, (log, text, exception) -> call.accept(log, text), true, false);
	}

	private void testExceptionLogMethod(String prefix, BiConsumer<LogService, Throwable> call) {
		testLogMethod(prefix, (log, text, exception) -> call.accept(log, exception), false, true);

	}

	private void testLogMethod(String prefix, LogMethodCall logMethodCall, boolean testMessage, boolean testException) {
		// setup
		TestableLogService logService = new TestableLogService();
		logService.setLevel(LogService.TRACE);
		String text = "Message";
		NullPointerException exception = new NullPointerException();
		// process
		logMethodCall.run(logService, text, exception);
		// test
		if(testMessage) {
			assertTrue(logService.message().contains(prefix));
			assertTrue(logService.message().contains(text));
		}
		if(testException)
			assertEquals(exception, logService.exception());
	}

	@Test
	public void testSetLevel() {
		TestableLogService logService = new TestableLogService();
		logService.setLevel(LogService.TRACE);
		assertEquals(LogService.TRACE, logService.getLevel());
		logService.setLevel(LogService.ERROR);
		assertEquals(LogService.ERROR, logService.getLevel());
	}

	@Test
	public void testSetClassSpecificLevel() {
		TestableLogService logService = new TestableLogService();
		MyTestClass testClass = new MyTestClass(logService);
		logService.setLevel(LogService.ERROR);
		logService.setLevel(MyTestClass.class.getName(), LogService.TRACE);
		assertEquals(LogService.ERROR, logService.getLevel());
		assertEquals(LogService.TRACE, testClass.getLevel());
	}

	@Test
	public void testIsWarn() {
		testIsLevel(LogService.ERROR, LogService::isError);
		testIsLevel(LogService.WARN, LogService::isWarn);
		testIsLevel(LogService.INFO, LogService::isInfo);
		testIsLevel(LogService.DEBUG, LogService::isDebug);
		testIsLevel(LogService.TRACE, LogService::isTrace);
	}

	private void testIsLevel(int level, Function<LogService, Boolean> isLevel) {
		TestableLogService logService = new TestableLogService();
		logService.setLevel(LogService.NONE);
		assertFalse(isLevel.apply(logService));
		logService.setLevel(level);
		assertTrue(isLevel.apply(logService));
		logService.setLevel(LogService.TRACE);
		assertTrue(isLevel.apply(logService));
	}

	// -- Helper classes --

	private static class MyTestClass {

		private final LogService log;

		MyTestClass(LogService log) {
			this.log = log;
		}

		int getLevel() {
			return log.getLevel();
		}
	}

	private interface LogMethodCall {
		void run(LogService logService, Object text, Throwable exception);
	}

	private static class TestableLogService extends AbstractLogService {

		String message = null;
		Throwable exception = null;

		public String message() {
			return message;
		}

		public Throwable exception() {
			return exception;
		}

		@Override
		protected void log(String msg) {
			this.message = msg;
		}

		@Override
		protected void log(Throwable t) {
			this.exception = t;
		}
	}
}
