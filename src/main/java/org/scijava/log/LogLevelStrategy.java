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

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author Matthias Arzt
 */
class LogLevelStrategy {

	private final Map<String, LogLevel> classAndPackageLevels = new HashMap<>();

	private LogLevel currentLevel = levelFromEnvironment();

	LogLevelStrategy() {
		// check SciJava log level system properties for initial logging levels

		// global log level property
		try {
			final String logProp = System.getProperty(LogService.LOG_LEVEL_PROPERTY);
			setLogLevel(logLevelOf(logProp));
		} catch(NullPointerException | IllegalArgumentException e) {
			setLogLevel(levelFromEnvironment());
		}

		// populate custom class- and package-specific log level properties
		final String logLevelPrefix = LogService.LOG_LEVEL_PROPERTY + ":";
		final Properties props = System.getProperties();
		for (final Object propKey : props.keySet()) {
			if (!(propKey instanceof String)) continue;
			final String propName = (String) propKey;
			if (!propName.startsWith(logLevelPrefix)) continue;
			final String classOrPackageName = propName.substring(logLevelPrefix
					.length());
			try {
				setLogLevel(classOrPackageName, logLevelOf(props.getProperty(propName)));
			} catch(NullPointerException | IllegalArgumentException ignore) { }
		}

	}

	public LogLevel getLogLevel() {
		if (!classAndPackageLevels.isEmpty()) {
			// check for a custom log level for calling class or its parent packages
			String classOrPackageName = callingClass();
			while (classOrPackageName != null) {
				final LogLevel level = classAndPackageLevels.get(classOrPackageName);
				if (level != null) return level;
				classOrPackageName = parentPackage(classOrPackageName);
			}
		}
		// no custom log level; return the global log level
		return currentLevel;
	}

	public void setLogLevel(final LogLevel level) {
		currentLevel = level;
	}

	public void setLogLevel(final String classOrPackageName, final LogLevel level) {
		classAndPackageLevels.put(classOrPackageName, level);
	}

	// -- Helper methods --

	private String callingClass() {
		final String thisClass = DefaultLogger.class.getName();
		for (final StackTraceElement element : new Exception().getStackTrace()) {
			final String className = element.getClassName();
			// NB: Skip stack trace elements from other methods of this class.
			if (!thisClass.equals(className)) return className;
		}
		return null;
	}

	private String parentPackage(final String classOrPackageName) {
		final int dot = classOrPackageName.lastIndexOf(".");
		if (dot < 0) return null;
		return classOrPackageName.substring(0, dot);
	}

	private LogLevel levelFromEnvironment() {
		return System.getenv("DEBUG") == null ? LogLevel.INFO : LogLevel.DEBUG;
	}

	/**
	 * Extracts the log level value from a string.
	 *
	 * @return The log level, or -1 if the level cannot be parsed.
	 */
	public static LogLevel logLevelOf(final String s) {
		if (s == null)
			throw new NullPointerException();

		// check whether it's a string label (e.g., "debug")
		final String log = s.trim().toLowerCase();
		if (log.startsWith("n")) return LogLevel.NONE;
		if (log.startsWith("e")) return LogLevel.ERROR;
		if (log.startsWith("w")) return LogLevel.WARN;
		if (log.startsWith("i")) return LogLevel.INFO;
		if (log.startsWith("d")) return LogLevel.DEBUG;
		if (log.startsWith("t")) return LogLevel.TRACE;

		// check whether it's a numerical value (e.g., 5)
		try {
			return LogLevel.valueOf(Integer.parseInt(log));
		}
		catch (final NumberFormatException exc) { }
		throw new IllegalArgumentException();
	}

}
