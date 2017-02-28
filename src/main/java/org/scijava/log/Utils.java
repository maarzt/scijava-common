package org.scijava.log;

import java.util.Iterator;

/**
 * @author Matthias Arzt
 */

@IgnoreAsCallingClass
public class Utils {

	private Utils() {}

	/**
	 * Inspects the stack trace, return the class that calls this method,
	 * but ignores every class annotated with @IgnoreAsCallingClass.
	 *
	 * @throws IllegalStateException if every method on the stack,
	 * is in a class annotated with @IgnoreAsCallingClass.
	 */
	public static Class<?> getCallingClass() {
		try {
			StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
			for(int i = 1; i < stackTrace.length - 1; i++) {
				Class<?> clazz = Class.forName(stackTrace[i].getClassName());
				if (!clazz.isAnnotationPresent(IgnoreAsCallingClass.class))
					return clazz;
			}
		} catch (ClassNotFoundException ignore) { }
		throw new IllegalStateException();
	}

}
