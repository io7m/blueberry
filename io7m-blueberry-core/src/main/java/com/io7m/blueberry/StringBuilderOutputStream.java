/*
 * Copyright © 2014 <code@io7m.com> http://io7m.com
 * 
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
 * SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
 * IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.io7m.blueberry;

import java.io.IOException;
import java.io.OutputStream;

import javax.annotation.Nonnull;

/**
 * A trivial output stream that appends to a {@link StringBuilder} internally.
 */

public final class StringBuilderOutputStream extends OutputStream
{
  private final @Nonnull StringBuilder buffer;

  /**
   * Construct a new output stream.
   */

  public StringBuilderOutputStream()
  {
    this.buffer = new StringBuilder();
  }

  @Override public void write(
    final int b)
    throws IOException
  {
    this.buffer.append((char) (b & 0xff));
  }

  /**
   * @return The internal {@link StringBuilder}.
   */

  public @Nonnull StringBuilder getBuffer()
  {
    return this.buffer;
  }
}
