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

import java.io.PrintStream;
import java.util.Set;

import nu.xom.Element;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runner.notification.StoppedByUserException;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

import com.io7m.blueberry.TestState.Failed;
import com.io7m.blueberry.TestState.Skipped;
import com.io7m.blueberry.TestState.Succeeded;
import com.io7m.blueberry.TestState.TestStateType;
import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;

/**
 * A test runner that runs all tests in the given classes, and publishes test
 * state changes on the given listener.
 *
 * @see TestStateListenerType
 */

@SuppressWarnings("synthetic-access") public final class TestCollectionRunner implements
  Runnable,
  ToXMLReportType<TestReportConfig>
{
  private final Set<Class<?>> classes;
  private final TestsState    states;

  /**
   * Construct a test collection runner.
   *
   * @param in_listener
   *          The listener.
   * @param in_classes
   *          The classes to check.
   */

  public TestCollectionRunner(
    final TestStateListenerType in_listener,
    final Set<Class<?>> in_classes)
  {
    this.classes = in_classes;
    this.states = new TestsState(in_classes, in_listener);
  }

  private final PrintStream original_stdout = NullCheck.notNull(System.out);
  private final PrintStream original_stderr = NullCheck.notNull(System.err);
  private long              test_number;

  /**
   * <p>
   * Execute all tests in the set of classes given to the constructor.
   * </p>
   * <p>
   * Note that this class implements {@link java.lang.Runnable},
   * and the intention is that this method will be called from an
   * {@link java.util.concurrent.Executor} in order to run all unit tests on a
   * separate thread.
   * </p>
   *
   * @see #TestCollectionRunner(TestStateListenerType, Set)
   */

  @Override public void run()
  {
    this.test_number = 0;
    this.states.testsStateRunStarted();

    try {

      for (final Class<?> current_class : this.classes) {
        final String c_name = current_class.getCanonicalName();
        assert c_name != null;
        final ClassName class_name = new ClassName(c_name);

        try {
          final BlockJUnit4ClassRunner runner =
            new BlockJUnit4ClassRunner(current_class);

          runner.run(new RunNotifier() {
            private @Nullable StringBuilderOutputStream output_stdout;
            private @Nullable StringBuilderOutputStream output_stderr;
            private @Nullable TestState.TestStateType   result_type;
            private @Nullable TestName                  test_name;
            private long                                time_start;

            private void streamsInit()
            {
              this.output_stderr = new StringBuilderOutputStream();
              this.output_stdout = new StringBuilderOutputStream();
              System.setOut(new PrintStream(this.output_stdout));
              System.setErr(new PrintStream(this.output_stderr));
            }

            private void streamsReset()
            {
              System.setOut(TestCollectionRunner.this.original_stdout);
              System.setErr(TestCollectionRunner.this.original_stderr);
            }

            @Override public void fireTestStarted(
              final @Nullable Description description)
              throws StoppedByUserException
            {
              assert description != null;

              this.result_type = TestStateType.STATE_SUCCEEDED;
              final String d_name = description.getMethodName();
              assert d_name != null;
              this.test_name = new TestName(d_name);
              this.time_start = System.nanoTime();
              this.streamsInit();

              assert this.test_name != null;
              TestCollectionRunner.this.states.testsStateStarted(
                class_name,
                this.test_name,
                TestCollectionRunner.this.test_number);
              ++TestCollectionRunner.this.test_number;
            }

            @Override public void fireTestFailure(
              final @Nullable Failure failure)
            {
              final long elapsed = System.nanoTime() - this.time_start;
              this.result_type = TestStateType.STATE_FAILED;
              assert failure != null;

              final StringBuilderOutputStream oo = this.output_stdout;
              final StringBuilderOutputStream oe = this.output_stderr;
              final Throwable e = failure.getException();

              assert e != null;
              assert oo != null;
              assert oe != null;

              final Failed state =
                new TestState.Failed(
                  oo.getBuffer(),
                  oe.getBuffer(),
                  e,
                  elapsed);

              final TestName t_name = this.test_name;
              assert t_name != null;

              TestCollectionRunner.this.states.testsStatePut(
                class_name,
                t_name,
                state);
            }

            @Override public void fireTestAssumptionFailed(
              final @Nullable Failure failure)
            {
              this.result_type = TestStateType.STATE_SKIPPED;

              final TestName t_name = this.test_name;
              final StringBuilderOutputStream oe = this.output_stderr;
              final StringBuilderOutputStream oo = this.output_stdout;

              assert oo != null;
              assert oe != null;
              assert t_name != null;

              final Skipped state =
                new TestState.Skipped(
                  oe.getBuffer(),
                  oo.getBuffer(),
                  "Assumption failed");

              TestCollectionRunner.this.states.testsStatePut(
                class_name,
                t_name,
                state);
            }

            @Override public void fireTestIgnored(
              final @Nullable Description description)
            {
              assert description != null;
              this.result_type = TestStateType.STATE_SKIPPED;
              final String d_name = description.getMethodName();
              assert d_name != null;

              /**
               * Note that fireTestStarted() will not have been called here,
               * so the output buffers will not have been initialized.
               */

              final Skipped state =
                new TestState.Skipped(
                  new StringBuilder(),
                  new StringBuilder(),
                  "@Ignore annotation");

              final TestName t_name = new TestName(d_name);
              this.test_name = t_name;
              TestCollectionRunner.this.states.testsStatePut(
                class_name,
                t_name,
                state);
            }

            @Override public void fireTestFinished(
              final @Nullable Description description)
            {
              final TestName t_name = this.test_name;
              final StringBuilderOutputStream stdout = this.output_stdout;
              final StringBuilderOutputStream stderr = this.output_stderr;
              this.streamsReset();

              assert stdout != null;
              assert stderr != null;
              assert t_name != null;

              switch (this.result_type) {
                case STATE_FAILED:
                case STATE_INITIALIZED:
                case STATE_SKIPPED:
                {
                  break;
                }
                case STATE_SUCCEEDED:
                {
                  final long elapsed = System.nanoTime() - this.time_start;
                  final Succeeded state =
                    new TestState.Succeeded(stdout.getBuffer(), stderr
                      .getBuffer(), elapsed);
                  TestCollectionRunner.this.states.testsStatePut(
                    class_name,
                    t_name,
                    state);
                  break;
                }
              }
            }
          });
        } catch (final InitializationError e) {
          this.states.testsStateClassInitializationFailed(current_class, e);
        }
      }

    } finally {
      this.states.testsStateRunFinished();
    }
  }

  @Override public Element toXML(
    final TestReportConfig config)
  {
    return this.states.toXML(config);
  }
}
