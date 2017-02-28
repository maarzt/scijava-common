package org.scijava.log;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Matthias Arzt
 */
public class CallingClassUtilsTest {
	@Test
	public void testGetCallingClass() {
		Class<?> callingClass = CallingClassUtils.getCallingClass();
		assertEquals(this.getClass(), callingClass);
	}

	@Test
	public void testIgnoreAsCallingClass() {
		assertEquals(ClassA.class, ClassA.returnGetCallingClass());
		assertEquals(this.getClass(), ClassB.returnGetCallingClass());
	}

	public static class ClassA {
		static Class<?> returnGetCallingClass() {
			return CallingClassUtils.getCallingClass();
		}
	}

	@IgnoreAsCallingClass
	private static class ClassB {
		static Class<?> returnGetCallingClass() {
			return CallingClassUtils.getCallingClass();
		}
	}
}
