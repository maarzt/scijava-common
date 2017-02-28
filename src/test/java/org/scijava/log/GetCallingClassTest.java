package org.scijava.log;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Matthias Arzt
 */
public class GetCallingClassTest {
	@Test
	public void testGetCallingClass() {
		Class<?> callingClass = Utils.getCallingClass();
		assertEquals(this.getClass(), callingClass);
	}

	@Test
	public void testIgnoreAsCallingClass() {
		assertEquals(ClassA.class, ClassA.returnGetCallingClass());
		assertEquals(this.getClass(), ClassB.returnGetCallingClass());
	}

	public static class ClassA {
		static Class<?> returnGetCallingClass() {
			return Utils.getCallingClass();
		}
	}

	@IgnoreAsCallingClass
	private static class ClassB {
		static Class<?> returnGetCallingClass() {
			return Utils.getCallingClass();
		}
	}
}
