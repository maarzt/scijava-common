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

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.scijava.service.AbstractService;

/**
 * Base class for {@link LogService} implementations.
 *
 * @author Curtis Rueden
 */
@IgnoreAsCallingClass
public abstract class AbstractLogService extends AbstractService implements
	LogService
{

	private final Map<String, Logger> channels = new ConcurrentHashMap<>();
	private final Logger defaultChannel;
	private final LogBroadcaster broadcaster = new LogBroadcaster();

	// -- constructor --

	public AbstractLogService() {
		defaultChannel = channel(DEFAULT_CHANNEL);
	}

	// -- LogService methods --

	@Override
	public Logger channel(final String name) {
		final Logger channel = channels.get(name);
		if (channel != null) return channel;
		final Logger newChannel = new DefaultLogger();
		newChannel.setName(name);
		newChannel.addLogListener(broadcaster);
		channels.putIfAbsent(name, newChannel);
		return channels.get(name);
	}

	@Override
	public Collection<Logger> allChannels() {
		return channels.values();
	}

	@Override
	public void addAllChannelsLogListener(LogListener listener) {
		broadcaster.addLogListener(listener);
	}

	@Override
	public void removeAllChannelsLogListener(LogListener listener) {
		broadcaster.removeLogListener(listener);
	}

	@Deprecated
	@Override
	public int getLevel() {
		return getLogLevel().intLevel();
	}

	@Deprecated
	@Override
	public void setLevel(int level) {
		setLogLevel(LogLevel.valueOf(level));
	}

	@Deprecated
	@Override
	public void setLevel(String classOrPackageName, int level) {
		setLogLevel(classOrPackageName, LogLevel.valueOf(level));
	}

	// -- Logger methods --

	@Override
	public LogLevel getLogLevel() {
		return defaultChannel.getLogLevel();
	}

	@Override
	public void setLogLevel(final LogLevel level) {
		defaultChannel.setLogLevel(level);
	}

	@Override
	public void setLogLevel(final String classOrPackageName, final LogLevel level) {
		defaultChannel.setLogLevel(classOrPackageName, level);
	}

	@Override
	public void alwaysLog(final LogLevel level, final Object msg,
		final Throwable t)
	{
		defaultChannel.alwaysLog(level, msg, t);
	}

	@Override
	public void addLogListener(final LogListener l) {
		defaultChannel.addLogListener(l);
	}

	@Override
	public void removeLogListener(final LogListener l) {
		defaultChannel.removeLogListener(l);
	}

	@Override
	public void notifyListeners(final LogMessage message)
	{
		defaultChannel.notifyListeners(message);
	}

	// -- Named methods --

	@Override
	public String getName() {
		return defaultChannel.getName();
	}

	@Override
	public void setName(final String name) {
		defaultChannel.setName(name);
	}
}
