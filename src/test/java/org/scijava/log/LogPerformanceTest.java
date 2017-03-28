package org.scijava.log;

import java.util.ArrayList;
import java.util.List;

/**
 * This class measures the time a call of Utils.getCallingClass takes,
 * when used to inspect the calling class of {@link Logger#log}.
 *
 * @author Matthias Arzt
 */
public class LogPerformanceTest {

	public static void main(String... args) {

		measureLogSpeed();

		//deepStack(10, LogPerformanceTest::measureLogSpeed); // retrieving the stack takes longer if there are more entries.

	}

	public static void measureLogSpeed() {
		List<Class> classes = new ArrayList<>(20000);
		LogListener listener = (source, level, msg, t) -> classes.add(Object.class);
		LogListener callingClassListener = (source, level, msg, t) -> classes.add(Utils.getCallingClass());
		measureTimeForNLogMessages("without calling class", 10000, listener);
		measureTimeForNLogMessages("with calling class", 10000,  callingClassListener);
	}

	private static void measureTimeForNLogMessages(String text, int count, LogListener listener) {
		LogService logService = new StderrLogService();
		Logger logger = logService.channel("TimeMeasure");
		logService.addAllChannelsLogListener(listener);
		StopWatch sw = new StopWatch();
		for (int i = 0; i < count; i++)
			logger.warn("messureLogSpeed test message");
		Time time = sw.get().devide(count);
		System.out.println("Time elapsed per log (" + text + "): " + time);
	}

	public static void deepStack(int depth, Runnable runnable) {
		if(depth > 1)
			deepStack(depth - 1, runnable);
		else
			runnable.run();
	}

	private static class StopWatch {
		private final long start;
		StopWatch() { start = System.nanoTime(); }
		public Time get() { return Time.nanoseconds(System.nanoTime() - start); }
	}

	private static class Time {
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
