package org.scijava.log;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * TestLogListener is a LogListener usable for testing.
 *
 * It stores all the LogMessages it get and allows, testing if any LogMessage fulfills a predicate.
 *
 * @author Matthias Arzt
 */

class TestLogListener implements LogListener {

	List<LogMessage> messages = new ArrayList<>();

	@Override
	public void messageLogged(LogMessage message) {
		messages.add(message);
	}

	public boolean hasLogged(Predicate<LogMessage> predicate) {
		return messages.stream().anyMatch(predicate);
	}

	public void clear() {
		messages.clear();
	}

}
