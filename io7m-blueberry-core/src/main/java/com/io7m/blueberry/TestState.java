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

/**
 * The state of a given test.
 */

public abstract class TestState implements ToXMLReport<TestName>
{
  public static enum TestStateType
  {
    STATE_INITIALIZED,
    STATE_SUCCEEDED,
    STATE_SKIPPED,
    STATE_FAILED;

    public String toHumanString()
    {
      switch (this) {
        case STATE_FAILED:
          return "Failed";
        case STATE_INITIALIZED:
          return "Initialized";
        case STATE_SKIPPED:
          return "Skipped";
        case STATE_SUCCEEDED:
          return "Succeeded";
      }

      throw new AssertionError("Unreachable code");
    }
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

  public static final class Initialized extends TestState
  {
    @SuppressWarnings("synthetic-access") public Initialized()
    {
      super(TestStateType.STATE_INITIALIZED);
    }

    @Override public @Nonnull Element toXML(
      final @Nonnull TestName name)
    {
      final Element e = new Element("test-missed", TestReportXMLVersion.XML_URI);
      e.addAttribute(new Attribute("name", name.actual));
      return e;
    }
  }

  public static final class Succeeded extends TestState
  {
    private final @Nonnull StringBuilder output_stdout;
    private final @Nonnull StringBuilder output_stderr;
    private final long                   time_elapsed_nano;

    @SuppressWarnings("synthetic-access") public Succeeded(
      final @Nonnull StringBuilder output_stdout,
      final @Nonnull StringBuilder output_stderr,
      final long time_elapsed_nano)
    {
      super(TestStateType.STATE_SUCCEEDED);
      this.output_stderr = output_stderr;
      this.output_stdout = output_stdout;
      this.time_elapsed_nano = time_elapsed_nano;
    }

    public long getTimeElapsedNanoseconds()
    {
      return this.time_elapsed_nano;
    }

    @Override public @Nonnull Element toXML(
      final @Nonnull TestName name)
    {
      final Element e = new Element("test-succeeded", TestReportXMLVersion.XML_URI);
      e.addAttribute(new Attribute("name", name.actual));
      final Element oso = new Element("output-stdout", TestReportXMLVersion.XML_URI);
      oso.appendChild(this.output_stdout.toString());
      final Element ose = new Element("output-stderr", TestReportXMLVersion.XML_URI);
      ose.appendChild(this.output_stderr.toString());
      final Element em = new Element("elapsed-nanos", TestReportXMLVersion.XML_URI);
      em.appendChild(Long.toString(this.time_elapsed_nano));

      e.appendChild(oso);
      e.appendChild(ose);
      e.appendChild(em);
      return e;
    }
  }

  public static final class Skipped extends TestState
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
      final Element e = new Element("test-skipped", TestReportXMLVersion.XML_URI);
      e.addAttribute(new Attribute("name", name.actual));
      final Element er = new Element("reason", TestReportXMLVersion.XML_URI);
      er.appendChild(this.reason);
      e.appendChild(er);
      return e;
    }
  }

  public static final class Failed extends TestState
  {
    private static final int             STACK_TRACE_LIMIT = 256;
    private final @Nonnull StringBuilder output_stdout;
    private final @Nonnull StringBuilder output_stderr;
    private final @Nonnull Throwable     throwable;
    private final long                   time_elapsed_nano;

    @SuppressWarnings("synthetic-access") public Failed(
      final @Nonnull StringBuilder output_stdout,
      final @Nonnull StringBuilder output_stderr,
      final @Nonnull Throwable throwable,
      final long time_elapsed_nano)
    {
      super(TestStateType.STATE_FAILED);
      this.output_stderr = output_stderr;
      this.output_stdout = output_stdout;
      this.throwable = throwable;
      this.time_elapsed_nano = time_elapsed_nano;
    }

    public long getTimeElapsedNanoseconds()
    {
      return this.time_elapsed_nano;
    }

    private static @Nonnull Element traceElementToXML(
      final @Nonnull StackTraceElement trace)
    {
      final Element t = new Element("trace", TestReportXMLVersion.XML_URI);
      final Element tc = new Element("trace-class", TestReportXMLVersion.XML_URI);
      tc.appendChild(trace.getClassName());
      final Element tm = new Element("trace-method", TestReportXMLVersion.XML_URI);
      tm.appendChild(trace.getMethodName());
      final Element tf = new Element("trace-file", TestReportXMLVersion.XML_URI);
      tf.appendChild(trace.getFileName());
      final Element tl = new Element("trace-line", TestReportXMLVersion.XML_URI);
      tl.appendChild(Integer.toString(trace.getLineNumber()));
      t.appendChild(tc);
      t.appendChild(tm);
      t.appendChild(tf);
      t.appendChild(tl);
      return t;
    }

    @Override public @Nonnull Element toXML(
      final @Nonnull TestName name)
    {
      final Element e = new Element("test-failed", TestReportXMLVersion.XML_URI);
      e.addAttribute(new Attribute("name", name.actual));
      final Element oso = new Element("output-stdout", TestReportXMLVersion.XML_URI);
      oso.appendChild(this.output_stdout.toString());
      final Element ose = new Element("output-stderr", TestReportXMLVersion.XML_URI);
      ose.appendChild(this.output_stderr.toString());
      final Element em = new Element("elapsed-nanos", TestReportXMLVersion.XML_URI);
      em.appendChild(Long.toString(this.time_elapsed_nano));

      final Element xs = new Element("exceptions", TestReportXMLVersion.XML_URI);
      Throwable error = this.throwable;
      for (int index = 0; index < Failed.STACK_TRACE_LIMIT; ++index) {
        final Element x = new Element("exception", TestReportXMLVersion.XML_URI);
        x.addAttribute(new Attribute("type", error
          .getClass()
          .getCanonicalName()));
        x.addAttribute(new Attribute("level", Integer.toString(index)));
        final StackTraceElement[] st = error.getStackTrace();
        for (final StackTraceElement trace : st) {
          x.appendChild(Failed.traceElementToXML(trace));
        }

        xs.appendChild(x);

        if (error.getCause() != null) {
          error = error.getCause();
        } else {
          break;
        }
      }

      e.appendChild(oso);
      e.appendChild(ose);
      e.appendChild(em);
      e.appendChild(xs);
      return e;
    }
  }
}
