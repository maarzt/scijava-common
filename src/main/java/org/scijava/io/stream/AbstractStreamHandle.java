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

package org.scijava.io.stream;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.scijava.Context;
import org.scijava.io.AbstractDataHandle;
import org.scijava.io.DataHandle;
import org.scijava.io.Location;

/**
 * A {@link DataHandle} backed by an {@link InputStream} and/or
 * {@link OutputStream}.
 * 
 * @author Curtis Rueden
 * @author Melissa Linkert
 */
public abstract class AbstractStreamHandle<L extends Location> extends
	AbstractDataHandle<L>
{

	// -- Fields --

	/** InputStream to be used for reading. */
	private DataInputStream in;

	/** OutputStream to be used for writing. */
	private DataOutputStream out;

	/** Length of the stream. */
	private long length;

	/** Current position within the stream. */
	private long offset;

	/** Marked position within the stream. */
	private long mark;

	// -- StreamHandle methods --

	public DataInputStream in() {
		return in;
	}

	public void setIn(final DataInputStream in) {
		this.in = in;
	}

	public DataOutputStream out() {
		return out;
	}

	public void setOut(final DataOutputStream out) {
		this.out = out;
	}

	public long getMark() {
		return mark;
	}

	public void setMark(final long mark) {
		this.mark = mark;
	}

	// -- Closeable methods --

	@Override
	public void close() throws IOException {
		length = offset = mark = 0;
		if (in != null) in.close();
		if (out != null) out.close();
		in = null;
		out = null;
	}

	// -- DataHandle methods --

	@Override
	public long offset() {
		return offset;
	}

	@Override
	public long length() {
		return length;
	}

	@Override
	public void setLength(final long length) {
		this.length = length;
	}

	@Override
	public int read(final byte[] b) throws IOException {
		return read(b, 0, b.length);
	}

	@Override
	public int read(final byte[] b, final int off, final int len)
		throws IOException
	{
		int n = in.read(b, off, len);
		if (n >= 0) offset += n;
		else n = 0;
		markManager();
		while (n < len && offset < length()) {
			final int s = in.read(b, off + n, len - n);
			offset += s;
			n += s;
		}
		return n == -1 ? 0 : n;
	}

	@Override
	public int read(final ByteBuffer buffer) throws IOException {
		return read(buffer, 0, buffer.capacity());
	}

	@Override
	public int read(final ByteBuffer buffer, final int off, final int len)
		throws IOException
	{
		if (buffer.hasArray()) {
			return read(buffer.array(), off, len);
		}

		final byte[] b = new byte[len];
		final int n = read(b);
		buffer.put(b, off, len);
		return n;
	}

	@Override
	public void seek(final long pos) throws IOException {
		long diff = pos - offset;
		offset = pos;

		if (diff < 0) {
			resetStream();
			diff = offset;
		}
		int skipped = in.skipBytes((int) diff);
		while (skipped < diff) {
			final int n = in.skipBytes((int) (diff - skipped));
			if (n == 0) break;
			skipped += n;
		}
	}

	@Override
	public void write(final ByteBuffer buf) throws IOException {
		write(buf, 0, buf.capacity());
	}

	@Override
	public void write(final ByteBuffer buf, final int off, final int len)
		throws IOException
	{
		buf.position(off);
		if (buf.hasArray()) {
			write(buf.array(), off, len);
		}
		else {
			final byte[] b = new byte[len];
			buf.get(b);
			write(b);
		}
	}

	// -- DataInput methods --

	@Override
	public boolean readBoolean() throws IOException {
		offset++;
		return in().readBoolean();
	}

	@Override
	public byte readByte() throws IOException {
		offset++;
		return in().readByte();
	}

	@Override
	public char readChar() throws IOException {
		offset++;
		return in().readChar();
	}

	@Override
	public double readDouble() throws IOException {
		offset += 8;
		final double v = in.readDouble();
		return order.equals(ByteOrder.LITTLE_ENDIAN) ? DataTools.swap(v) : v;
	}

	@Override
	public float readFloat() throws IOException {
		offset += 4;
		final float v = in.readFloat();
		return order.equals(ByteOrder.LITTLE_ENDIAN) ? DataTools.swap(v) : v;
	}

	@Override
	public void readFully(final byte[] b) throws IOException {
		in.readFully(b);
		offset += b.length;
	}

	@Override
	public void readFully(final byte[] b, final int off, final int len)
		throws IOException
	{
		in.readFully(b, off, len);
		offset += len;
	}

	@Override
	public int readInt() throws IOException {
		offset += 4;
		final int v = in.readInt();
		return order.equals(ByteOrder.LITTLE_ENDIAN) ? DataTools.swap(v) : v;
	}

	@Override
	public String readLine() throws IOException {
		throw new IOException("Unimplemented");
	}

	@Override
	public long readLong() throws IOException {
		offset += 8;
		final long v = in.readLong();
		return order.equals(ByteOrder.LITTLE_ENDIAN) ? DataTools.swap(v) : v;
	}

	@Override
	public short readShort() throws IOException {
		offset += 2;
		final short v = in.readShort();
		return order.equals(ByteOrder.LITTLE_ENDIAN) ? DataTools.swap(v) : v;
	}

	@Override
	public int readUnsignedByte() throws IOException {
		offset++;
		return in.readUnsignedByte();
	}

	@Override
	public int readUnsignedShort() throws IOException {
		return readShort() & 0xffff;
	}

	@Override
	public String readUTF() throws IOException {
		final String s = in.readUTF();
		offset += s.length();
		return s;
	}

	@Override
	public int skipBytes(final int n) throws IOException {
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
	public void write(final byte[] b) throws IOException {
		if (out == null) {
			throw new HandleException("This stream is read-only.");
		}
		out.write(b);
	}

	@Override
	public void write(final byte[] b, final int off, final int len)
		throws IOException
	{
		if (out == null) {
			throw new HandleException("This stream is read-only.");
		}
		out.write(b, off, len);
	}

	@Override
	public void write(int b) throws IOException {
		if (out == null) {
			throw new HandleException("This stream is read-only.");
		}
		if (order.equals(ByteOrder.LITTLE_ENDIAN)) b = DataTools.swap(b);
		out.write(b);
	}

	@Override
	public void writeBoolean(final boolean v) throws IOException {
		if (out == null) {
			throw new HandleException("This stream is read-only.");
		}
		out.writeBoolean(v);
	}

	@Override
	public void writeByte(int v) throws IOException {
		if (out == null) {
			throw new HandleException("This stream is read-only.");
		}
		if (order.equals(ByteOrder.LITTLE_ENDIAN)) v = DataTools.swap(v);
		out.writeByte(v);
	}

	@Override
	public void writeBytes(final String s) throws IOException {
		if (out == null) {
			throw new HandleException("This stream is read-only.");
		}
		out.writeBytes(s);
	}

	@Override
	public void writeChar(int v) throws IOException {
		if (out == null) {
			throw new HandleException("This stream is read-only.");
		}
		if (order.equals(ByteOrder.LITTLE_ENDIAN)) v = DataTools.swap(v);
		out.writeChar(v);
	}

	@Override
	public void writeChars(final String s) throws IOException {
		if (out == null) {
			throw new HandleException("This stream is read-only.");
		}
		out.writeChars(s);
	}

	@Override
	public void writeDouble(double v) throws IOException {
		if (out == null) {
			throw new HandleException("This stream is read-only.");
		}
		if (order.equals(ByteOrder.LITTLE_ENDIAN)) v = DataTools.swap(v);
		out.writeDouble(v);
	}

	@Override
	public void writeFloat(float v) throws IOException {
		if (out == null) {
			throw new HandleException("This stream is read-only.");
		}
		if (order.equals(ByteOrder.LITTLE_ENDIAN)) v = DataTools.swap(v);
		out.writeFloat(v);
	}

	@Override
	public void writeInt(int v) throws IOException {
		if (out == null) {
			throw new HandleException("This stream is read-only.");
		}
		if (order.equals(ByteOrder.LITTLE_ENDIAN)) v = DataTools.swap(v);
		out.writeInt(v);
	}

	@Override
	public void writeLong(long v) throws IOException {
		if (out == null) {
			throw new HandleException("This stream is read-only.");
		}
		if (order.equals(ByteOrder.LITTLE_ENDIAN)) v = DataTools.swap(v);
		out.writeLong(v);
	}

	@Override
	public void writeShort(int v) throws IOException {
		if (out == null) {
			throw new HandleException("This stream is read-only.");
		}
		if (order.equals(ByteOrder.LITTLE_ENDIAN)) v = DataTools.swap(v);
		out.writeShort(v);
	}

	@Override
	public void writeUTF(final String str) throws IOException {
		if (out == null) {
			throw new HandleException("This stream is read-only.");
		}
		out.writeUTF(str);
	}

	// -- Helper methods --

	/** Reset the marked position, if necessary. */
	private void markManager() {
		if (offset >= mark + RandomAccessInputStream.MAX_OVERHEAD - 1) {
			mark = offset;
			in.mark(RandomAccessInputStream.MAX_OVERHEAD);
		}
	}

}
