/*
 * #%L
 * SciJava Common shared library for SciJava software.
 * %%
 * Copyright (C) 2009 - 2017 Board of Regents of the University of
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

import org.scijava.service.AbstractService;

/**
 * Base class for {@link LogService} implementations.
 *
 * @author Johannes Schindelin
 * @author Curtis Rueden
 * @author Matthias Arzt
 */
@IgnoreAsCallingClass
public abstract class AbstractLogService extends AbstractService implements
	LogService
{

	private final Logger rootLogger;

	private final LogLevelStrategy logLevelStrategy = new LogLevelStrategy();

	// -- constructor --

	public AbstractLogService() {
		rootLogger = new RootLogger();
	}

	// -- AbstractLogService methods --

	@Override
	public void setLevel(final int level) {
		logLevelStrategy.setLevel(level);
	}

	@Override
	public void setLevel(final String classOrPackageName, final int level) {
		logLevelStrategy.setLevel(classOrPackageName, level);
	}

	abstract void messageLogged(LogMessage message);

	// -- Logger methods --

	@Override
	public void alwaysLog(final int level, final Object msg, final Throwable t) {
		rootLogger.alwaysLog(level, msg, t);
	}

	@Override
	public LogSource getSource() {
		return rootLogger.getSource();
	}

	@Override
	public int getLevel() {
		return logLevelStrategy.getLevel();
	}

	@Override
	public Logger subLogger(String name, int level) {
		return rootLogger.subLogger(name, level);
	}

	@Override
	public void addListener(final LogListener listener) {
		rootLogger.addListener(listener);
	}

	@Override
	public void removeListener(final LogListener listener) {
		rootLogger.removeListener(listener);
	}

	// -- Deprecated --

	/** @deprecated Use {@link LogLevel#prefix(int)} instead. */
	@Deprecated
	protected String getPrefix(final int level) {
		return "[" + LogLevel.prefix(level) + "]";
	}

	// -- Helper classes --

	@IgnoreAsCallingClass
	private class RootLogger extends DefaultLogger
	{
		public RootLogger() {
			super(AbstractLogService.this::messageLogged, LogSource.root(), LogLevel.NONE);
		}

		@Override
		public int getLevel() {
			return logLevelStrategy.getLevel();
		}
	}
}
