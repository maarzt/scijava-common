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

package org.scijava.console;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.function.Consumer;

/**
 * ListenableSystemStream allow listening to System.out and System.err
 *
 * @author Matthais Arzt
 */
class ListenableSystemStreams {

	private ListenableSystemStreams() {
		// prevent from being initialized
	}

	private static final ListenableStream out = new ListenableStream(System.out, System::setOut);

	private static final ListenableStream err = new ListenableStream(System.err, System::setErr);

	public static ListenableStream out() {
		return out;
	}

	public static ListenableStream err() {
		return err;
	}

	public static class ListenableStream {

		private final OutputStream out;
		private PrintStream in;
		private final MultiOutputStream multi;
		private final Consumer<PrintStream> streamSetter;
		private boolean initialized = false;

		private ListenableStream(OutputStream os, Consumer<PrintStream> streamSetter) {
			this.out = os;
			this.multi = new MultiOutputStream(os);
			this.in = new PrintStream(multi);
			this.streamSetter = streamSetter;
		}

		public void addListener(OutputStream os) {
			init();
			multi.addOutputStream(os);
		}

		public void removeListener(OutputStream os) {
			multi.removeOutputStream(os);
		}

		public OutputStream bypass() {
			return out;
		}

		private void init() {
			if(initialized) return;
			initialized = true;
			// No synchronization is needed. Even if init is called in parallel,
			// and streamSetter happens to be called twice. It will only set the same value twice.
			streamSetter.accept(in);
		}
	}

}
