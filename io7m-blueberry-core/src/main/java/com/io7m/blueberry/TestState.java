/*
 * Copyright © 2013 <code@io7m.com> http://io7m.com
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

import javax.annotation.Nonnull;

import nu.xom.Attribute;
import nu.xom.Element;

abstract class TestState implements ToXMLReport<TestName>
{
  static enum TestStateType
  {
    STATE_INITIALIZED,
    STATE_SUCCEEDED,
    STATE_SKIPPED,
    STATE_FAILED
  }

  private final @Nonnull TestStateType type;

  public @Nonnull TestStateType getType()
  {
    return this.type;
  }

  private TestState(
    final @Nonnull TestStateType type)
  {
    this.type = type;
  }

  static final class Initialized extends TestState
  {
    @SuppressWarnings("synthetic-access") public Initialized()
    {
      super(TestStateType.STATE_INITIALIZED);
    }

    @Override public @Nonnull Element toXML(
      final @Nonnull TestName name)
    {
      final Element e = new Element("test-missed", XMLVersion.XML_URI);
      e.addAttribute(new Attribute("name", name.actual));
      return e;
    }
  }

  static final class Succeeded extends TestState
  {
    private final @Nonnull StringBuilder output_stdout;
    private final @Nonnull StringBuilder output_stderr;

    @SuppressWarnings("synthetic-access") public Succeeded(
      final @Nonnull StringBuilder output_stdout,
      final @Nonnull StringBuilder output_stderr)
    {
      super(TestStateType.STATE_SUCCEEDED);
      this.output_stderr = output_stderr;
      this.output_stdout = output_stdout;
    }

    @Override public @Nonnull Element toXML(
      final @Nonnull TestName name)
    {
      final Element e = new Element("test-succeeded", XMLVersion.XML_URI);
      e.addAttribute(new Attribute("name", name.actual));
      return e;
    }
  }

  static final class Skipped extends TestState
  {
    private final @Nonnull String reason;

    @SuppressWarnings("synthetic-access") public Skipped(
      final @Nonnull String reason)
    {
      super(TestStateType.STATE_SKIPPED);
      this.reason = reason;
    }

    @Override public @Nonnull Element toXML(
      final @Nonnull TestName name)
    {
      final Element e = new Element("test-skipped", XMLVersion.XML_URI);
      e.addAttribute(new Attribute("name", name.actual));
      return e;
    }
  }

  static final class Failed extends TestState
  {
    private final @Nonnull StringBuilder output_stdout;
    private final @Nonnull StringBuilder output_stderr;
    private final @Nonnull Throwable     throwable;

    @SuppressWarnings("synthetic-access") public Failed(
      final @Nonnull StringBuilder output_stdout,
      final @Nonnull StringBuilder output_stderr,
      final @Nonnull Throwable throwable)
    {
      super(TestStateType.STATE_FAILED);
      this.output_stderr = output_stderr;
      this.output_stdout = output_stdout;
      this.throwable = throwable;
    }

    @Override public @Nonnull Element toXML(
      final @Nonnull TestName name)
    {
      final Element e = new Element("test-failed", XMLVersion.XML_URI);
      e.addAttribute(new Attribute("name", name.actual));
      return e;
    }
  }
}
