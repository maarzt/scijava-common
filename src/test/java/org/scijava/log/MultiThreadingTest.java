package org.scijava.log;

import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static org.junit.Assert.assertTrue;

/**
 * Check if calling {@link AbstractLogService#channel(String)} always only returns one Logger object per name,
 * even if multiple Threads try to create the same channels in parallel.
 *
 * @author Matthias Arzt
 */
public class MultiThreadingTest {

	private LogService logService;

	@Before
	public void setUp() {
		logService = new StderrLogService();
	}

	@Test
	public void run() throws InterruptedException {
		List<List<Logger>> results = runParallelCollectResults(10, this::getLoggers);
		boolean passed = allEqual(results, MultiThreadingTest::referencesInListEqual);
		assertTrue(passed);
	}

	private List<Logger> getLoggers() {
		List<Logger> result = new ArrayList<>();
		for(int i = 0; i < 100; i++)
			result.add(logService.channel("test" + i));
		return result;
	}

	// -- Helper methods --

	private static <T> List<T> runParallelCollectResults(int nTasks, Callable<T> callable) throws InterruptedException {
		ExecutorService executorService = Executors.newFixedThreadPool(nTasks);
		List<Future<T>> futures = executorService.invokeAll(Collections.nCopies(nTasks, callable));
		return futures.stream().map(MultiThreadingTest::removeFuture).collect(Collectors.toList());
	}

	private static <T> T removeFuture(Future<T> future) {
		try {
			return future.get();
		} catch (InterruptedException | ExecutionException e) {
			return null;
		}
	}

	private static boolean referencesInListEqual(List<? extends Object> a, List<? extends Object> b) {
		Iterator<?> ia = a.iterator();
		Iterator<?> ib = b.iterator();
		boolean result = a.size() == b.size();
		while(result && ia.hasNext() && ib.hasNext())
			result = ia.next() == ib.next();
		return result;
	}

	private static <T> boolean allEqual(Iterable<T> l, BiFunction<T, T, Boolean> equals) {
		Iterator<T> it = l.iterator();
		if(!it.hasNext())
			return true;
		T first = it.next();
		while(it.hasNext())
			if(! equals.apply(first, it.next()) )
				return false;
		return true;
	}

}
