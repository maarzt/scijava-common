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

import java.util.EnumSet;

/**
 * Constants for specifying a logger's level of verbosity.
 * 
 * @author Curtis Rueden
 */
public enum LogLevel {

	NONE(0),
	ERROR(1), WARN(2), INFO(3), DEBUG(4), TRACE(5);

	public static final EnumSet<LogLevel> VALID_FOR_MESSAGES = EnumSet.range(ERROR, TRACE);

	public static final LogLevel MAX_VALUE = TRACE;

	public static final LogLevel MIN_VALUE = NONE;

	private final int intLevel;

	LogLevel(int intLevel) {
		this.intLevel = intLevel;
	}

	public int intLevel() {
		return intLevel;
	}

	public String prefix() {
		return "[" + toString() + "] ";
	}

	public static LogLevel valueOf(int intLevel) {
		switch (intLevel) {
			case 0: return NONE;
			case 1: return ERROR;
			case 2: return WARN;
			case 3: return INFO;
			case 4: return DEBUG;
			case 5: return TRACE;
		}
		throw new IllegalArgumentException();
	}

	public boolean isHigherThan(LogLevel level) {
		return intLevel() > level.intLevel();
	}

	public boolean isLowerThan(LogLevel level) {
		return intLevel() < level.intLevel();
	}

	public boolean isHigherOrEqual(LogLevel level) {
		return intLevel() >= level.intLevel();
	}

	public boolean isLowerOrEqual(LogLevel level) {
		return intLevel() <= level.intLevel();
	}

	public static LogLevel max(LogLevel a, LogLevel b) {
		return a.isHigherThan(b) ? a : b;
	}

	public static LogLevel min(LogLevel a, LogLevel b) {
		return a.isLowerThan(b) ? a : b;
	}

}
