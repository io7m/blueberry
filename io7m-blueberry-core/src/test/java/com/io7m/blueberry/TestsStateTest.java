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

import com.io7m.blueberry.test_data.TestDataAllFail;
import com.io7m.blueberry.test_data.TestDataAllPass;
import com.io7m.blueberry.test_data.TestDataIgnored;
import nu.xom.Element;
import nu.xom.Nodes;
import nu.xom.XPathContext;
import org.junit.Assert;
import org.junit.Test;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;

public class TestsStateTest
{
  @SuppressWarnings("static-method") @Test public void testEmpty()
  {
    @SuppressWarnings("unused") final TestsState tstate = new TestsState(
      new HashSet<Class<?>>(), new TestStateListenerType()
    {
      @Override public void testStateUpdated(
        final @Nonnull ClassName class_name,
        final @Nonnull TestName test,
        final @Nonnull TestState state)
      {
        Assert.fail();
      }

      @Override public void testStateCreated(
        final @Nonnull ClassName class_name,
        final @Nonnull TestName test,
        final @Nonnull TestState state)
      {
        Assert.fail();
      }

      @Override public void testStateRunStarted(
        final long count)
      {
        // Nothing
      }

      @Override public void testStateRunFinished()
      {
        // Nothing
      }

      @Override public void testStateStarted(
        final @Nonnull ClassName class_name,
        final @Nonnull TestName test,
        final long n)
      {
        // Nothing
      }
    });
  }

  @Test public void testCreateCount()
  {
    final Counter counter = new Counter();
    final HashSet<Class<?>> classes = new HashSet<Class<?>>();
    classes.add(TestDataAllPass.class);
    @SuppressWarnings("unused") final TestsState tstate =
      new TestsState(classes, counter);

    Assert.assertEquals(3, counter.creates);
    Assert.assertEquals(3, counter.updates);
  }

  @Test public void testUpdateCount()
  {
    final Counter counter = new Counter();
    final HashSet<Class<?>> classes = new HashSet<Class<?>>();
    classes.add(TestDataAllPass.class);
    final TestsState tstate = new TestsState(classes, counter);

    final ClassName class_name = new ClassName("com.io7m.Xyz");

    tstate.testsStatePut(
      class_name, new TestName("a"), new TestState.Initialized());

    tstate.testsStatePut(
      class_name, new TestName("b"), new TestState.Initialized());

    tstate.testsStatePut(
      class_name, new TestName("c"), new TestState.Initialized());

    Assert.assertEquals(6, counter.creates);
    Assert.assertEquals(6, counter.updates);
  }

  @Test public void testsStateXML()
  {
    final TestsState tstate = this.makeState();
    final TestReportConfig config = this.getTestReportConfig();

    final XPathContext namespaces = new XPathContext();
    namespaces.addNamespace("s", TestReportXMLVersion.XML_URI);

    {
      config.setOutputSystemProperties(true);
      config.setOutputEnvironment(true);
      final Element e = tstate.toXML(config);
      final Nodes nse = e.query("/s:report/s:system-environment", namespaces);
      Assert.assertEquals(1, nse.size());
      final Nodes nsp = e.query("/s:report/s:system-properties", namespaces);
      Assert.assertEquals(1, nsp.size());
    }

    {
      config.setOutputSystemProperties(false);
      config.setOutputEnvironment(false);
      final Element e = tstate.toXML(config);
      final Nodes nse = e.query("/s:report/s:system-environment", namespaces);
      Assert.assertEquals(0, nse.size());
      final Nodes nsp = e.query("/s:report/s:system-properties", namespaces);
      Assert.assertEquals(0, nsp.size());
    }
  }

  private TestReportConfig getTestReportConfig()
  {
    return new TestReportConfig("test", "0.1.0");
  }

  private TestsState makeState()
  {
    final Set<Class<?>> classes = new HashSet<Class<?>>();
    classes.add(TestDataAllPass.class);
    classes.add(TestDataAllFail.class);
    classes.add(TestDataIgnored.class);
    final TestsState tstate = new TestsState(classes, new IgnoreListener());

    final AssertionError e1 = new AssertionError("root");
    final AssertionError e2 = new AssertionError(e1);
    final AssertionError e3 = new AssertionError(e2);

    final StringBuilder stdout = new StringBuilder("output");
    final StringBuilder stderr = new StringBuilder("error");
    final TestState.Failed failure =
      new TestState.Failed(stdout, stderr, e3, 23);
    final TestState.Succeeded success =
      new TestState.Succeeded(stdout, stderr, 71);

    final ClassName all_fail_name =
      new ClassName(TestDataAllFail.class.getCanonicalName());
    final ClassName all_pass_name =
      new ClassName(TestDataAllPass.class.getCanonicalName());

    tstate.testsStatePut(all_fail_name, new TestName("testFailOne"), failure);
    tstate.testsStatePut(all_fail_name, new TestName("testFailTwo"), failure);
    tstate.testsStatePut(
      all_fail_name, new TestName("testFailThree"), failure);

    tstate.testsStatePut(all_pass_name, new TestName("testPassOne"), success);
    tstate.testsStatePut(all_pass_name, new TestName("testPassTwo"), success);
    tstate.testsStatePut(
      all_pass_name, new TestName("testPassThree"), success);
    return tstate;
  }

  class Counter implements TestStateListenerType
  {
    int creates = 0;
    int updates = 0;

    @Override public void testStateCreated(
      final @Nonnull ClassName class_name,
      final @Nonnull TestName test,
      final @Nonnull TestState state)
    {
      ++this.creates;
    }

    @Override public void testStateUpdated(
      final ClassName class_name,
      final TestName test,
      final TestState state)
    {
      ++this.updates;
    }

    @Override public void testStateRunStarted(
      final long count)
    {
      // Nothing
    }

    @Override public void testStateRunFinished()
    {
      // Nothing
    }

    @Override public void testStateStarted(
      final @Nonnull ClassName class_name,
      final @Nonnull TestName test,
      final long n)
    {
      // Nothing
    }
  }

  class IgnoreListener implements TestStateListenerType
  {
    @Override public void testStateCreated(
      final ClassName class_name,
      final TestName test,
      final TestState state)
    {
      // Nothing
    }

    @Override public void testStateUpdated(
      final ClassName class_name,
      final TestName test,
      final TestState state)
    {
      // Nothing
    }

    @Override public void testStateRunStarted(
      final long count)
    {
      // Nothing
    }

    @Override public void testStateRunFinished()
    {
      // Nothing
    }

    @Override public void testStateStarted(
      final @Nonnull ClassName class_name,
      final @Nonnull TestName test,
      final long n)
    {
      // Nothing
    }
  }
}
