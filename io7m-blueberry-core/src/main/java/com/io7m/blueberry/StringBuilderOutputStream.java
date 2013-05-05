package com.io7m.blueberry;

import java.io.IOException;
import java.io.OutputStream;

import javax.annotation.Nonnull;

final class StringBuilderOutputStream extends OutputStream
{
  private final @Nonnull StringBuilder buffer;

  public StringBuilderOutputStream()
  {
    this.buffer = new StringBuilder();
  }

  @Override public void write(
    final int b)
    throws IOException
  {
    this.buffer.appendCodePoint(b);
  }

  public @Nonnull StringBuilder getBuffer()
  {
    return this.buffer;
  }
}
