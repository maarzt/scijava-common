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

package org.scijava.io.remote;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.scijava.io.AbstractLocation;
import org.scijava.io.Location;

/**
 * A {@link Location} that can be accessed via HTTP. backed by an {@link URL}.
 *
 * @author Curtis Rueden
 * @author Gabriel Einsdorf
 */
public class HTTPLocation extends AbstractLocation {

	/** The URL backing this location. */
	private final URL url;

	public HTTPLocation(final URL url) {
		this.url = url;
	}

	/**
	 * Creates an HTTPLocation from an URI.
	 * 
	 * @param uri the uri of the location
	 * @throws MalformedURLException if the uri can not be converted to an URL, or
	 *           the uri does not point to an HTTP(S) location.
	 */
	public HTTPLocation(final URI uri) throws MalformedURLException {
		String scheme = uri.getScheme();
		if ("http".equals(scheme) || "https".equals(scheme)) {
			this.url = uri.toURL();
		}
		throw new MalformedURLException(
			"URI does not point to an HTTP(S) location.");
	}

	// -- HTTPLocation methods --

	/** Gets the associated {@link URL}. */
	public URL getURL() {
		return url;
	}

	// -- Location methods --

	/**
	 * Gets the associated {@link URI}, or null if this URL is not formatted
	 * strictly according to to RFC2396 and cannot be converted to a URI.
	 */
	@Override
	public URI getURI() {
		try {
			return getURL().toURI();
		}
		catch (final URISyntaxException exc) {
			return null;
		}
	}

}
