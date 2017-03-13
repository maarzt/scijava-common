/*
 * #%L
 * SciJava Common shared library for SciJava software.
 * %%
 * Copyright (C) 2009 - 2016 Board of Regents of the University of
 * Wisconsin-Madison, Broad Institute of MIT and Harvard, and Max Planck
 * Institute of Molecular Cell Biology and Genetics.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package org.scijava.log;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.scijava.log.LogLevel.WARN;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Tests {@link LogService}.
 * 
 * @author Johannes Schindelin
 */
public class LogServiceTest {

	@Test
	public void testDefaultLevel() {
		final LogService log = new StderrLogService();
		LogLevel level = log.getLogLevel();
		assertTrue("default level (" + level + //
			") is at least INFO(" + WARN + ")", level.isHigherOrEqual(WARN));
	}

	@Test
	public void testAllChannelsListener() {
		// setup
		LogService logService = new AbstractLogService() { };
		Logger loggerA = logService.channel("A");
		Logger loggerB = logService.channel("B");
		String messageA = "Hello World A!";
		String messageB = "Hello World B!";
		List<Object[]> results = new ArrayList<>();

		// process
		logService.addAllChannelsLogListener(msg -> results.add(new Object[] {msg.source(), msg.text()}));
		loggerA.warn(messageA);
		loggerB.warn(messageB);

		// test
		assertEquals(2, results.size());
		assertLogged(loggerA, messageA, results.get(0));
		assertLogged(loggerB, messageB, results.get(1));
	}

	private void assertLogged(Logger source, String message, Object[] actual) {
		assertEquals(source, actual[0]);
		assertEquals(message, actual[1]);
	}

}
