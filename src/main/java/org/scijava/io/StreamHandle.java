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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.scijava.util.Bytes;

/**
 * A {@link DataHandle} backed by an {@link InputStream} and/or
 * {@link OutputStream}.
 * 
 * @author Curtis Rueden
 * @author Melissa Linkert
 */
public interface StreamHandle<L extends Location> extends DataHandle<L> {

	// -- Constants --

	/** Maximum size of the input stream buffer. */
	public static final int MAX_OVERHEAD = 1048576;

	// -- StreamHandle methods --

	/**
	 * Gets an input stream for reading data, positioned at the current offset.
	 * 
	 * @return the appropriate input stream, or null if the handle is write-only.
	 */
	InputStream in();

	/**
	 * Gets an output stream for writing data, positioned at the current offset.
	 * 
	 * @return the appropriate output stream, or null if the handle is read-only.
	 */
	OutputStream out();

	/**
	 * Increments the handle's offset by the given amount.
	 * <p>
	 * This method is intended to be called only in conjunction with reading from
	 * the input stream, or writing to the output stream. Otherwise, the contents
	 * may get out of sync.
	 * </p>
	 */
	void advance(final long bytes);

	// -- DataHandle methods --

	@Override
	default void ensureReadable(final long count) throws IOException {
		if (in() == null) throw new IOException("This handle is write-only.");
		DataHandle.super.ensureReadable(count);
	}

	@Override
	default boolean ensureWritable(final long count) throws IOException {
		if (out() == null) throw new IOException("This handle is read-only.");
		return DataHandle.super.ensureWritable(count);
	}

	@Override
	default int read(final byte[] b, final int off, final int len)
		throws IOException
	{
		final int n = in().read(b, off, len);
		if (n >= 0) advance(n);
		return n;
	}

	@Override
	default void seek(final long pos) throws IOException {
		long diff = pos - offset;
		offset = pos;

		if (diff < 0) {
			resetStream();
			diff = offset;
		}
		int skipped = in().skipBytes((int) diff);
		while (skipped < diff) {
			final int n = in().skipBytes((int) (diff - skipped));
			if (n == 0) break;
			skipped += n;
		}
	}

	// -- DataInput methods --

	@Override
	default boolean readBoolean() throws IOException {
		offset++;
		return in().readBoolean();
	}

	@Override
	default byte readByte() throws IOException {
		offset++;
		return in().readByte();
	}

	@Override
	default char readChar() throws IOException {
		offset += 2;
		return in().readChar();
	}

	@Override
	default double readDouble() throws IOException {
		offset += 8;
		final double v = in().readDouble();
		return isLittleEndian() ? Bytes.swap(v) : v;
	}

	@Override
	default float readFloat() throws IOException {
		offset += 4;
		final float v = in().readFloat();
		return isLittleEndian() ? Bytes.swap(v) : v;
	}

	@Override
	default void readFully(final byte[] b) throws IOException {
		in().readFully(b);
		offset += b.length;
	}

	@Override
	default void readFully(final byte[] b, final int off, final int len)
		throws IOException
	{
		offset += len;
		in().readFully(b, off, len);
	}

	@Override
	default int readInt() throws IOException {
		offset += 4;
		final int v = in().readInt();
		return isLittleEndian() ? Bytes.swap(v) : v;
	}

	@Override
	default String readLine() throws IOException {
		final String s = DataHandle.super.readLine();
		offset += s.length() + 1;
	}

	@Override
	default long readLong() throws IOException {
		offset += 8;
		final long v = in().readLong();
		return isLittleEndian() ? Bytes.swap(v) : v;
	}

	@Override
	default short readShort() throws IOException {
		offset += 2;
		final short v = in().readShort();
		return isLittleEndian() ? Bytes.swap(v) : v;
	}

	@Override
	default int readUnsignedByte() throws IOException {
		offset++;
		return in().readUnsignedByte();
	}

	@Override
	default String readUTF() throws IOException {
		final String s = in().readUTF();
		offset += s.length();
		return s;
	}

	@Override
	default int skipBytes(final int n) throws IOException {
		int skipped = 0;
		try {
			for (int i = 0; i < n; i++) {
				if (readUnsignedByte() != -1) skipped++;
				markManager();
			}
		}
		catch (final EOFException e) {}
		return skipped;
	}

	// -- DataOutput methods --

	@Override
	default void write(final byte[] b) throws IOException {
		if (out() == null) {
			throw new IOException("This stream is read-only.");
		}
		out().write(b);
	}

	@Override
	default void write(final byte[] b, final int off, final int len)
		throws IOException
	{
		if (out() == null) {
			throw new IOException("This stream is read-only.");
		}
		out().write(b, off, len);
	}

	@Override
	default void write(int b) throws IOException {
		if (out() == null) {
			throw new IOException("This stream is read-only.");
		}
		if (isLittleEndian()) b = Bytes.swap(b);
		out().write(b);
	}

	@Override
	default void writeBoolean(final boolean v) throws IOException {
		if (out() == null) {
			throw new IOException("This stream is read-only.");
		}
		out().writeBoolean(v);
	}

	@Override
	default void writeByte(int v) throws IOException {
		if (out() == null) {
			throw new IOException("This stream is read-only.");
		}
		if (isLittleEndian()) v = Bytes.swap(v);
		out().writeByte(v);
	}

	@Override
	default void writeBytes(final String s) throws IOException {
		if (out() == null) {
			throw new IOException("This stream is read-only.");
		}
		out().writeBytes(s);
	}

	@Override
	default void writeChar(int v) throws IOException {
		if (out() == null) {
			throw new IOException("This stream is read-only.");
		}
		if (isLittleEndian()) v = Bytes.swap(v);
		out().writeChar(v);
	}

	@Override
	default void writeChars(final String s) throws IOException {
		if (out() == null) {
			throw new IOException("This stream is read-only.");
		}
		out().writeChars(s);
	}

	@Override
	default void writeDouble(double v) throws IOException {
		if (out() == null) {
			throw new IOException("This stream is read-only.");
		}
		if (isLittleEndian()) v = Bytes.swap(v);
		out().writeDouble(v);
	}

	@Override
	default void writeFloat(float v) throws IOException {
		if (out() == null) {
			throw new IOException("This stream is read-only.");
		}
		if (isLittleEndian()) v = Bytes.swap(v);
		out().writeFloat(v);
	}

	@Override
	default void writeInt(int v) throws IOException {
		if (out() == null) {
			throw new IOException("This stream is read-only.");
		}
		if (isLittleEndian()) v = Bytes.swap(v);
		out().writeInt(v);
	}

	@Override
	default void writeLong(long v) throws IOException {
		if (out() == null) {
			throw new IOException("This stream is read-only.");
		}
		if (isLittleEndian()) v = Bytes.swap(v);
		out().writeLong(v);
	}

	@Override
	default void writeShort(int v) throws IOException {
		if (out() == null) {
			throw new IOException("This stream is read-only.");
		}
		if (isLittleEndian()) v = Bytes.swap(v);
		out().writeShort(v);
	}

	@Override
	default void writeUTF(final String str) throws IOException {
		if (out() == null) {
			throw new IOException("This stream is read-only.");
		}
		out().writeUTF(str);
	}

	// -- Helper methods --

	/** Reset the marked position, if necessary. */
	private void markManager() {
		if (offset >= mark + MAX_OVERHEAD - 1) {
			mark = offset;
			in().mark(MAX_OVERHEAD);
		}
	}

	//////////////// NEEDED TO IMPLEMENT

	// -- DataInput methods --

	@Override
	public int read(final byte[] b, final int off, final int len)
		throws IOException
	{
		final int r = in().read(b, off, len);
		if (r >= 0) advance(r);
		return r;
	}

	@Override
	public byte readByte() throws IOException {
		final int r = in().read();
		if (r < 0) throw new EOFException();
		advance(1);
		return r;
	}

	// -- DataOutput methods --

	@Override
	public void write(final byte[] b, final int off, final int len)
		throws IOException
	{
		ensureWritable(len);
		offset += len;
	}

	@Override
	public void writeByte(final int v) throws IOException {
		// NB: Do nothing.
		ensureWritable(1);
		offset++;
	}

	// -- Closeable methods --

	@Override
	default void close() throws IOException {
		// TODO: Double check this logic.
		try (final InputStream in = in()) {
			if (in != null) in.close();
		}
		try (final OutputStream out = out()) {
			if (out != null) out.close();
		}
	}

}
