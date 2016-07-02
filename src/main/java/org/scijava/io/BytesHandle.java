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

import java.io.EOFException;
import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.scijava.plugin.Plugin;

/**
 * {@link DataHandle} for a {@link BytesLocation}.
 * 
 * @author Curtis Rueden
 * @author Melissa Linkert
 */
@Plugin(type = DataHandle.class)
public class BytesHandle extends AbstractDataHandle<BytesLocation> {

	// -- DataHandle methods --

	@Override
	public long offset() {
		return bytes().position();
	}

	@Override
	public long length() {
		return bytes().limit();
	}

	@Override
	public void setLength(final long length) throws IOException {
		if (length > bytes().capacity()) {
			// NB: We cannot extend the length of a ByteBuffer beyond its capacity.
			// We have no choice but to allocate a new, higher capacity ByteBuffer,
			// and copy the contents and state of the old ByteBuffer into it.
			// This will change the backing ByteBuffer of the BytesLocation!

			// save state of the existing buffer
			final long off = offset();
			final ByteOrder order = getOrder();

			// allocate a new, higher capacity buffer
			final ByteBuffer newBytes = ByteBuffer.allocate((int) (length * 2));

			// copy bytes from the old buffer to the new one
			seek(0);
			newBytes.put(bytes());

			// update the backing BytesLocation's backing ByteBuffer
			get().setByteBuffer(newBytes);

			// restore the previous state (order and offset) to the new buffer
			setOrder(order);
			seek(off);
		}

		// set the length (a.k.a. "limit") of the buffer to match
		bytes().limit((int) length);
	}

	@Override
	public ByteOrder getOrder() {
		return bytes().order();
	}

	@Override
	public void setOrder(final ByteOrder order) {
		bytes().order(order);
	}

	@Override
	public int read(final byte[] b, final int off, int len) throws IOException {
		if (offset() + len > length()) {
			len = (int) (length() - offset());
		}
		bytes().get(b, off, len);
		return len;
	}

	@Override
	public void seek(final long pos) throws IOException {
		if (pos > length()) setLength(pos);
		bytes().position((int) pos);
	}

	// -- DataInput methods --

	@Override
	public byte readByte() throws IOException {
		if (offset() + 1 > length()) {
			throw new EOFException();
		}
		try {
			return bytes().get();
		}
		catch (final BufferUnderflowException e) {
			throw eofException(e);
		}
	}

	@Override
	public char readChar() throws IOException {
		if (offset() + 2 > length()) {
			throw new EOFException();
		}
		try {
			return bytes().getChar();
		}
		catch (final BufferUnderflowException e) {
			throw eofException(e);
		}
	}

	@Override
	public double readDouble() throws IOException {
		if (offset() + 8 > length()) {
			throw new EOFException();
		}
		try {
			return bytes().getDouble();
		}
		catch (final BufferUnderflowException e) {
			throw eofException(e);
		}
	}

	@Override
	public float readFloat() throws IOException {
		if (offset() + 4 > length()) {
			throw new EOFException();
		}
		try {
			return bytes().getFloat();
		}
		catch (final BufferUnderflowException e) {
			throw eofException(e);
		}
	}

	@Override
	public void readFully(final byte[] b, final int off, final int len)
		throws IOException
	{
		if (offset() + len > length()) {
			throw new EOFException();
		}
		try {
			bytes().get(b, off, len);
		}
		catch (final BufferUnderflowException e) {
			throw eofException(e);
		}
	}

	@Override
	public int readInt() throws IOException {
		if (offset() + 4 > length()) {
			throw new EOFException();
		}
		try {
			return bytes().getInt();
		}
		catch (final BufferUnderflowException e) {
			throw eofException(e);
		}
	}

	@Override
	public long readLong() throws IOException {
		if (offset() + 8 > length()) {
			throw new EOFException();
		}
		try {
			return bytes().getLong();
		}
		catch (final BufferUnderflowException e) {
			throw eofException(e);
		}
	}

	@Override
	public short readShort() throws IOException {
		if (offset() + 2 > length()) {
			throw new EOFException();
		}
		try {
			return bytes().getShort();
		}
		catch (final BufferUnderflowException e) {
			throw eofException(e);
		}
	}

	// -- DataOutput methods --

	@Override
	public void write(final byte[] b, final int off, final int len)
		throws IOException
	{
		validateLength(len);
		bytes().put(b, off, len);
	}

	@Override
	public void write(final int b) throws IOException {
		validateLength(1);
		bytes().put((byte) b);
	}

	@Override
	public void writeChar(final int v) throws IOException {
		validateLength(2);
		bytes().putChar((char) v);
	}

	@Override
	public void writeChars(final String s) throws IOException {
		final int len = 2 * s.length();
		validateLength(len);
		final char[] c = s.toCharArray();
		for (int i = 0; i < c.length; i++) {
			writeChar(c[i]);
		}
	}

	@Override
	public void writeDouble(final double v) throws IOException {
		validateLength(8);
		bytes().putDouble(v);
	}

	@Override
	public void writeFloat(final float v) throws IOException {
		validateLength(4);
		bytes().putFloat(v);
	}

	@Override
	public void writeInt(final int v) throws IOException {
		validateLength(4);
		bytes().putInt(v);
	}

	@Override
	public void writeLong(final long v) throws IOException {
		validateLength(8);
		bytes().putLong(v);
	}

	@Override
	public void writeShort(final int v) throws IOException {
		validateLength(2);
		bytes().putShort((short) v);
	}

	// -- Closeable methods --

	@Override
	public void close() {
		// NB: No action needed.
	}

	// -- Typed methods --

	@Override
	public Class<BytesLocation> getType() {
		return BytesLocation.class;
	}

	// -- Helper methods --

	/** Backing {@link ByteBuffer} accessor, purely for succinctness. */
	private ByteBuffer bytes() {
		return get().getByteBuffer();
	}

	private EOFException eofException(final Throwable cause) {
		final EOFException eof = new EOFException();
		eof.initCause(cause);
		return eof;
	}

}
