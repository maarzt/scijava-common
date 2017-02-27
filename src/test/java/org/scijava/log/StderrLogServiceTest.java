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

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Matthias Arzt
 */
public class StderrLogServiceTest {

	@Test
	public void testForwardingToConsole() {

		try(RedirectSystemOut r = new RedirectSystemOut()) {

			LogService logService = new StderrLogService();
			Logger defaultChannel = logService;
			Logger defaultChannel2 = logService.channel(LogService.DEFAULT_CHANNEL);

			defaultChannel.warn("Hello World 1");
			defaultChannel2.warn("Hello World 2");

			String stderr = r.systemErr();
			assertTrue(stderr.contains("Hello World 1"));
			assertTrue(stderr.contains("Hello World 2"));
		}
	}

	class RedirectSystemOut implements AutoCloseable {

		private final OutputStream out = new ByteArrayOutputStream();
		private final OutputStream err = new ByteArrayOutputStream();
		private final PrintStream systemOutBefore;
		private final PrintStream systemErrBefore;

		public RedirectSystemOut() {
			systemOutBefore = System.out;
			systemErrBefore = System.err;
			System.setOut(new PrintStream(out));
			System.setErr(new PrintStream(err));
		}

		public void close() {
			System.setOut(systemOutBefore);
			System.setErr(systemErrBefore);
		}

		public String systemErr() {
			return err.toString();
		}

		public String systemOut() {
			return out.toString();
		}
	}
}
