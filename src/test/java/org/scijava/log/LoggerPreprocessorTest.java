package org.scijava.log;

import org.junit.Test;
import org.scijava.Context;
import org.scijava.command.Command;
import org.scijava.command.CommandModule;
import org.scijava.command.CommandService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertTrue;

/**
 * Tests {@link LoggerPreprocessor}.
 *
 * @author Matthias Arzt
 */
public class LoggerPreprocessorTest {

	@Test
	public void testInjection() throws InterruptedException, ExecutionException {
		final Context context = new Context(CommandService.class);
		final CommandService commandService = context.service(CommandService.class);
		final LogService logService = context.service(LogService.class);
		final TestLogListener listener = new TestLogListener();
		logService.addListener(listener);

		final CommandModule module = //
				commandService.run(CommandWithLogger.class, true).get();
		assertTrue(listener.hasLogged(m -> m.source().path().contains(CommandWithLogger.class.getSimpleName())));
	}

	@Plugin(type = Command.class)
	public static class CommandWithLogger implements Command {

		@Parameter
		public Logger log;

		@Override
		public void run() {
			log.info("log from the command.");
		}
	}

}
