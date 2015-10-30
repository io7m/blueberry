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

import nu.xom.Attribute;
import nu.xom.Element;

/**
 * The state of a given test.
 */

public abstract class TestState implements ToXMLReportType<TestName>
{
  /**
   * The test state enum.
   */

  public static enum TestStateType
  {
    /**
     * The initialized state.
     */

    STATE_INITIALIZED,

    /**
     * The succeeded state.
     */

    STATE_SUCCEEDED,

    /**
     * The skipped state.
     */

    STATE_SKIPPED,

    /**
     * The failed state.
     */

    STATE_FAILED;

    /**
     * @return The state as a humanly-readable string.
     */

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

  private final TestStateType type;

  /**
   * @return The test state.
   */

  public TestStateType getType()
  {
    return this.type;
  }

  private TestState(
    final TestStateType in_type)
  {
    this.type = in_type;
  }

  /**
   * An initialized test.
   */

  public static final class Initialized extends TestState
  {
    /**
     * Construct a test state.
     */

    @SuppressWarnings("synthetic-access") public Initialized()
    {
      super(TestStateType.STATE_INITIALIZED);
    }

    @Override public Element toXML(
      final TestName name)
    {
      final Element e =
        new Element("test-missed", TestReportXMLVersion.XML_URI);
      e.addAttribute(new Attribute("name", name.getActual()));
      return e;
    }
  }

  /**
   * A succeeded test.
   */

  public static final class Succeeded extends TestState
  {
    private final StringBuilder output_stdout;
    private final StringBuilder output_stderr;
    private final long          time_elapsed_nano;

    /**
     * Construct a test state.
     * 
     * @param in_output_stdout
     *          The text written to the standard output stream.
     * @param in_output_stderr
     *          The text written to the standard error stream.
     * @param in_time_elapsed_nano
     *          The elapsed time.
     */

    @SuppressWarnings("synthetic-access") public Succeeded(
      final StringBuilder in_output_stdout,
      final StringBuilder in_output_stderr,
      final long in_time_elapsed_nano)
    {
      super(TestStateType.STATE_SUCCEEDED);
      this.output_stderr = in_output_stderr;
      this.output_stdout = in_output_stdout;
      this.time_elapsed_nano = in_time_elapsed_nano;
    }

    /**
     * @return The elapsed time in nanoseconds.
     */

    public long getTimeElapsedNanoseconds()
    {
      return this.time_elapsed_nano;
    }

    @Override public Element toXML(
      final TestName name)
    {
      final Element e =
        new Element("test-succeeded", TestReportXMLVersion.XML_URI);
      e.addAttribute(new Attribute("name", name.getActual()));

      final Element oso =
        new Element("output-stdout", TestReportXMLVersion.XML_URI);
      oso.addAttribute(new Attribute(
        "xml:space",
        "http://www.w3.org/XML/1998/namespace",
        "preserve"));
      oso.appendChild(this.output_stdout.toString());

      final Element ose =
        new Element("output-stderr", TestReportXMLVersion.XML_URI);
      ose.addAttribute(new Attribute(
        "xml:space",
        "http://www.w3.org/XML/1998/namespace",
        "preserve"));
      ose.appendChild(this.output_stderr.toString());

      final Element em =
        new Element("elapsed-nanos", TestReportXMLVersion.XML_URI);
      em.appendChild(Long.toString(this.time_elapsed_nano));

      e.appendChild(oso);
      e.appendChild(ose);
      e.appendChild(em);
      return e;
    }
  }

  /**
   * A skipped test.
   */

  public static final class Skipped extends TestState
  {
    private final StringBuilder output_stdout;
    private final StringBuilder output_stderr;
    private final String        reason;

    /**
     * Construct a test state.
     * 
     * @param in_output_stdout
     *          The text written to the standard output stream.
     * @param in_output_stderr
     *          The text written to the standard error stream.
     * @param in_reason
     *          The reason the test was skipped.
     */

    @SuppressWarnings("synthetic-access") public Skipped(
      final StringBuilder in_output_stdout,
      final StringBuilder in_output_stderr,
      final String in_reason)
    {
      super(TestStateType.STATE_SKIPPED);
      this.output_stderr = in_output_stderr;
      this.output_stdout = in_output_stdout;
      this.reason = in_reason;
    }

    @Override public Element toXML(
      final TestName name)
    {
      final Element e =
        new Element("test-skipped", TestReportXMLVersion.XML_URI);
      e.addAttribute(new Attribute("name", name.getActual()));

      final Element er = new Element("reason", TestReportXMLVersion.XML_URI);
      er.appendChild(this.reason);

      final Element oso =
        new Element("output-stdout", TestReportXMLVersion.XML_URI);
      oso.addAttribute(new Attribute(
        "xml:space",
        "http://www.w3.org/XML/1998/namespace",
        "preserve"));
      oso.appendChild(this.output_stdout.toString());

      final Element ose =
        new Element("output-stderr", TestReportXMLVersion.XML_URI);
      ose.addAttribute(new Attribute(
        "xml:space",
        "http://www.w3.org/XML/1998/namespace",
        "preserve"));
      ose.appendChild(this.output_stderr.toString());

      e.appendChild(er);
      e.appendChild(oso);
      e.appendChild(ose);
      return e;
    }
  }

  /**
   * A failed test.
   */

  public static final class Failed extends TestState
  {
    private static final int    STACK_TRACE_LIMIT = 256;
    private final StringBuilder output_stdout;
    private final StringBuilder output_stderr;
    private final Throwable     throwable;
    private final long          time_elapsed_nano;

    /**
     * Construct a test state.
     * 
     * @param in_output_stdout
     *          The text written to the standard output stream.
     * @param in_output_stderr
     *          The text written to the standard error stream.
     * @param in_throwable
     *          The exception raised.
     * @param in_time_elapsed_nano
     *          The elapsed time.
     */

    @SuppressWarnings("synthetic-access") public Failed(
      final StringBuilder in_output_stdout,
      final StringBuilder in_output_stderr,
      final Throwable in_throwable,
      final long in_time_elapsed_nano)
    {
      super(TestStateType.STATE_FAILED);
      this.output_stderr = in_output_stderr;
      this.output_stdout = in_output_stdout;
      this.throwable = in_throwable;
      this.time_elapsed_nano = in_time_elapsed_nano;
    }

    /**
     * @return The elapsed time in nanoseconds.
     */

    public long getTimeElapsedNanoseconds()
    {
      return this.time_elapsed_nano;
    }

    private static Element traceElementToXML(
      final StackTraceElement trace)
    {
      final Element t = new Element("trace", TestReportXMLVersion.XML_URI);
      final Element tc =
        new Element("trace-class", TestReportXMLVersion.XML_URI);
      tc.appendChild(trace.getClassName());
      final Element tm =
        new Element("trace-method", TestReportXMLVersion.XML_URI);
      tm.appendChild(trace.getMethodName());
      final Element tf =
        new Element("trace-file", TestReportXMLVersion.XML_URI);
      tf.appendChild(trace.getFileName());
      final Element tl =
        new Element("trace-line", TestReportXMLVersion.XML_URI);
      tl.appendChild(Integer.toString(trace.getLineNumber()));
      t.appendChild(tc);
      t.appendChild(tm);
      t.appendChild(tf);
      t.appendChild(tl);
      return t;
    }

    @Override public Element toXML(
      final TestName name)
    {
      final Element e =
        new Element("test-failed", TestReportXMLVersion.XML_URI);
      e.addAttribute(new Attribute("name", name.getActual()));

      final Element oso =
        new Element("output-stdout", TestReportXMLVersion.XML_URI);
      oso.addAttribute(new Attribute(
        "xml:space",
        "http://www.w3.org/XML/1998/namespace",
        "preserve"));
      oso.appendChild(this.output_stdout.toString());

      final Element ose =
        new Element("output-stderr", TestReportXMLVersion.XML_URI);
      ose.addAttribute(new Attribute(
        "xml:space",
        "http://www.w3.org/XML/1998/namespace",
        "preserve"));
      ose.appendChild(this.output_stderr.toString());

      final Element em =
        new Element("elapsed-nanos", TestReportXMLVersion.XML_URI);
      em.appendChild(Long.toString(this.time_elapsed_nano));

      final Element xs =
        new Element("exceptions", TestReportXMLVersion.XML_URI);

      Throwable error = this.throwable;
      for (int index = 0; index < Failed.STACK_TRACE_LIMIT; ++index) {
        final Element x =
          new Element("exception", TestReportXMLVersion.XML_URI);
        x.addAttribute(new Attribute("type", error
          .getClass()
          .getCanonicalName()));
        x.addAttribute(new Attribute("level", Integer.toString(index)));

        final Element xm =
          new Element("message", TestReportXMLVersion.XML_URI);
        xm.appendChild(error.getMessage());
        x.appendChild(xm);

        final StackTraceElement[] st = error.getStackTrace();
        for (final StackTraceElement trace : st) {
          assert trace != null;
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
