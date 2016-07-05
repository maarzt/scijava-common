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

package org.scijava.io;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import org.scijava.Context;
import org.scijava.plugin.Plugin;

/**
 * {@link DataHandle} for a {@link URLLocation}.
 * 
 * @author Curtis Rueden
 * @author Melissa Linkert
 * @see java.net.URLConnection
 */
@Plugin(type = DataHandle.class)
public class URLHandle extends AbstractStreamHandle<URLLocation> {

	// -- Fields --

	/** Socket underlying this stream. */
	private URLConnection conn;

	// -- StreamHandle methods --

	@Override
	public void resetStream() throws IOException {
		conn = (new URL(url)).openConnection();
		setStream(new DataInputStream(new BufferedInputStream(
			conn.getInputStream(), RandomAccessInputStream.MAX_OVERHEAD)));
		setFp(0);
		setMark(0);
		setLength(conn.getContentLength());
		if (getStream() != null) getStream().mark(
			RandomAccessInputStream.MAX_OVERHEAD);
	}

	// -- Helper methods --

	private URLConnection conn() throws IOException {
		if (conn == null) initConn();
		return conn;
	}

	private synchronized void initConn() throws IOException {
		if (conn != null) return;
		conn = get().getURL().openConnection();
		conn.setDoInput(isReadable());
		conn.setDoOutput(isWritable());
	}

}
