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

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Default implementation for {@link Logger}.
 *
 * @author Curtis Rueden
 */
public class DefaultLogger implements Logger {

	private String name;

	private List<LogListener> listeners = new CopyOnWriteArrayList<>();

	private LogLevelStrategy logLevelStrategy = new LogLevelStrategy();

	// -- Logger methods --

	@Override
	public int getLevel() {
		return logLevelStrategy.getLevel();
	}

	@Override
	public void setLevel(final int level) {
		logLevelStrategy.setLevel(level);
	}

	@Override
	public void setLevel(final String classOrPackageName, final int level) {
		logLevelStrategy.setLevel(classOrPackageName, level);
	}

	@Override
	public void alwaysLog(final int level, final Object msg, final Throwable t) {
		notifyListeners(level, msg, t);
	}

	@Override
	public void addLogListener(final LogListener l) {
		listeners.add(Objects.requireNonNull(l));
	}

	@Override
	public void removeLogListener(final LogListener l) {
		listeners.remove(Objects.requireNonNull(l));
	}

	@Override
	public void notifyListeners(final int level, final Object msg,
		final Throwable t)
	{
		for (final LogListener l : listeners)
			l.messageLogged(level, msg, t);
	}

	// -- Named methods --

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(final String name) {
		this.name = name;
	}

}
