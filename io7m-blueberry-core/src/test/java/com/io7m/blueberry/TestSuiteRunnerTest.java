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

import java.util.HashSet;

import javax.annotation.Nonnull;

import junit.framework.Assert;

import org.junit.Test;

public class TestSuiteRunnerTest
{
  static class FailureCounter implements TestStateListener
  {
    int updates  = 0;
    int failures = 0;

    @Override public void testStateUpdated(
      final @Nonnull ClassName class_name,
      final @Nonnull TestName test,
      final @Nonnull TestState state)
    {
      this.updates++;

      switch (state.getType()) {
        case STATE_FAILED:
          this.failures++;
          break;
        case STATE_INITIALIZED:
          break;
        case STATE_SKIPPED:
          break;
        case STATE_SUCCEEDED:
          break;
      }
    }

    @Override public void testStateCreated(
      final @Nonnull ClassName class_name,
      final @Nonnull TestName test,
      final @Nonnull TestState state)
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

  static class PassCounter implements TestStateListener
  {
    int updates  = 0;
    int succeeds = 0;

    @Override public void testStateUpdated(
      final @Nonnull ClassName class_name,
      final @Nonnull TestName test,
      final @Nonnull TestState state)
    {
      this.updates++;

      switch (state.getType()) {
        case STATE_FAILED:
          break;
        case STATE_INITIALIZED:
          break;
        case STATE_SKIPPED:
          break;
        case STATE_SUCCEEDED:
          this.succeeds++;
          break;
      }
    }

    @Override public void testStateCreated(
      final @Nonnull ClassName class_name,
      final @Nonnull TestName test,
      final @Nonnull TestState state)
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

  static class SkipCounter implements TestStateListener
  {
    int updates = 0;
    int skips   = 0;

    @Override public void testStateUpdated(
      final @Nonnull ClassName class_name,
      final @Nonnull TestName test,
      final @Nonnull TestState state)
    {
      this.updates++;

      switch (state.getType()) {
        case STATE_FAILED:
          break;
        case STATE_INITIALIZED:
          break;
        case STATE_SKIPPED:
          this.skips++;
          break;
        case STATE_SUCCEEDED:
          break;
      }
    }

    @Override public void testStateCreated(
      final @Nonnull ClassName class_name,
      final @Nonnull TestName test,
      final @Nonnull TestState state)
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

  @SuppressWarnings("static-method") @Test public void testRunFailure()
  {
    final HashSet<Class<?>> classes = new HashSet<Class<?>>();
    classes.add(new AllFail().getClass());

    final FailureCounter counter = new FailureCounter();
    final TestSuiteRunner runner = new TestSuiteRunner(counter, classes);
    runner.run();
    Assert.assertEquals(3, counter.failures);
  }

  @SuppressWarnings("static-method") @Test public void testRunSuccess()
  {
    final HashSet<Class<?>> classes = new HashSet<Class<?>>();
    classes.add(new AllPass().getClass());

    final PassCounter counter = new PassCounter();
    final TestSuiteRunner runner = new TestSuiteRunner(counter, classes);
    runner.run();
    Assert.assertEquals(3, counter.succeeds);
  }

  @SuppressWarnings("static-method") @Test public
    void
    testRunClassAssumptionFailed()
  {
    final HashSet<Class<?>> classes = new HashSet<Class<?>>();
    classes.add(new ClassAssumptionFailed().getClass());

    final SkipCounter counter = new SkipCounter();
    final TestSuiteRunner runner = new TestSuiteRunner(counter, classes);
    runner.run();
    Assert.assertEquals(1, counter.skips);
  }

  @SuppressWarnings("static-method") @Test public
    void
    testRunTestAssumptionFailed()
  {
    final HashSet<Class<?>> classes = new HashSet<Class<?>>();
    classes.add(new TestAssumptionFailed().getClass());

    final SkipCounter counter = new SkipCounter();
    final TestSuiteRunner runner = new TestSuiteRunner(counter, classes);
    runner.run();
    Assert.assertEquals(1, counter.skips);
  }

  @SuppressWarnings("static-method") @Test public void testRunTestIgnored()
  {
    final HashSet<Class<?>> classes = new HashSet<Class<?>>();
    classes.add(new Ignored().getClass());

    final SkipCounter counter = new SkipCounter();
    final TestSuiteRunner runner = new TestSuiteRunner(counter, classes);
    runner.run();
    Assert.assertEquals(1, counter.skips);
  }

  @SuppressWarnings("static-method") @Test public void testRunTestCrash()
  {
    final HashSet<Class<?>> classes = new HashSet<Class<?>>();
    classes.add(Explosive.class);

    final FailureCounter counter = new FailureCounter();
    final TestSuiteRunner runner = new TestSuiteRunner(counter, classes);
    runner.run();
    Assert.assertEquals(3, counter.failures);
  }
}
