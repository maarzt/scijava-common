package org.scijava.log;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Matthias Arzt
 */
public class LogPerformanceTest {

	@Test
	public void messureLogSpeed() {
		List<Class> classes = new ArrayList<>(20000);
		LogListener listener = msg -> classes.add(Object.class);
		LogListener callingClassListener = msg -> classes.add(Utils.getCallingClass());
		measureTimeForNLogMessages("without calling class", 10000, listener);
		measureTimeForNLogMessages("with calling class", 10000,  callingClassListener);
	}

	private void measureTimeForNLogMessages(String text, int count, LogListener listener) {
		LogService logService = new StderrLogService();
		Logger logger = logService.channel("TimeMeasure");
		logService.addAllChannelsLogListener(listener);
		StopWatch sw = new StopWatch();
		for (int i = 0; i < count; i++)
			logger.warn("messureLogSpeed test message");
		Time time = sw.get().devide(count);
		System.out.println("Time elapsed per log (" + text + "): " + time);
	}

	static class StopWatch {
		private final long start;
		StopWatch() { start = System.nanoTime(); }
		public Time get() { return Time.nanoseconds(System.nanoTime() - start); }
	}

	static class Time {
		public static final Time ZERO = new Time(0);
		private final long nanoseconds;
		private Time(long nanoseconds) { this.nanoseconds = nanoseconds; }
		public Time devide(long value) { return new Time(nanoseconds / value); }
		public static Time nanoseconds(long nanoseconds) { return new Time(nanoseconds); }
		public long nanoseconds() { return nanoseconds; }
		public double microseconds() { return ((double) nanoseconds) / 1000; }
		public double milliseconds() { return ((double) nanoseconds) / 1000000; }
		public String toString() {
			double ms = milliseconds();
			if(ms > 1.0) return ms + " ms";
			double micros = microseconds();
			if(micros > 1.0) return micros + "µs";
			return nanoseconds() + " ns";
		}
		Time add(Time b) { return new Time(nanoseconds + b.nanoseconds); }
	}
}
