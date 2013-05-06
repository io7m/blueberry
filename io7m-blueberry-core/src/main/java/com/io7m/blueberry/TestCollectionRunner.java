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

import java.io.PrintStream;
import java.util.Set;
import java.util.concurrent.Executor;

import javax.annotation.Nonnull;

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

/**
 * A test runner that runs all tests in the given classes, and publishes test
 * state changes on the given listener.
 * 
 * @see TestStateListener
 */

public final class TestCollectionRunner implements
  Runnable,
  ToXMLReport<TestReportConfig>
{
  private final @Nonnull Set<Class<?>> classes;
  private final @Nonnull TestsState    states;

  public TestCollectionRunner(
    final @Nonnull TestStateListener listener,
    final @Nonnull Set<Class<?>> classes)
  {
    this.classes = classes;
    this.states = new TestsState(classes, listener);
  }

  private final @Nonnull PrintStream original_stdout = System.out;
  private final @Nonnull PrintStream original_stderr = System.err;
  private long                       test_number     = 0;

  /**
   * <p>
   * Execute all tests in the set of classes given to the constructor.
   * </p>
   * <p>
   * Note that this class implements {@link Runnable}, and the intention is
   * that this method will be called from an {@link Executor} in order to run
   * all unit tests on a separate thread.
   * </p>
   * 
   * @see #TestCollectionRunner(TestStateListener, Set)
   */

  @Override public void run()
  {
    this.test_number = 0;
    this.states.testsStateRunStarted();

    try {

      for (final Class<?> current_class : this.classes) {
        final ClassName class_name =
          new ClassName(current_class.getCanonicalName());
        try {
          final BlockJUnit4ClassRunner runner =
            new BlockJUnit4ClassRunner(current_class);

          runner.run(new RunNotifier() {
            private StringBuilderOutputStream output_stdout;
            private StringBuilderOutputStream output_stderr;
            private TestState.TestStateType   result_type;
            private TestName                  test_name;
            private long                      time_start;

            private void streamsInit()
            {
              this.output_stderr = new StringBuilderOutputStream();
              this.output_stdout = new StringBuilderOutputStream();
              System.setOut(new PrintStream(this.output_stdout));
              System.setErr(new PrintStream(this.output_stderr));
            }

            @SuppressWarnings("synthetic-access") private void streamsReset()
            {
              System.setOut(TestCollectionRunner.this.original_stdout);
              System.setErr(TestCollectionRunner.this.original_stderr);
            }

            @SuppressWarnings("synthetic-access") @Override public
              void
              fireTestStarted(
                final Description description)
                throws StoppedByUserException
            {
              this.result_type = TestStateType.STATE_SUCCEEDED;
              this.test_name = new TestName(description.getMethodName());
              this.time_start = System.nanoTime();
              this.streamsInit();
              TestCollectionRunner.this.states.testsStateStarted(
                class_name,
                this.test_name,
                TestCollectionRunner.this.test_number);
              ++TestCollectionRunner.this.test_number;
            }

            @SuppressWarnings("synthetic-access") @Override public
              void
              fireTestFailure(
                final Failure failure)
            {
              final long elapsed = System.nanoTime() - this.time_start;
              this.result_type = TestStateType.STATE_FAILED;
              final Failed state =
                new TestState.Failed(
                  this.output_stdout.getBuffer(),
                  this.output_stderr.getBuffer(),
                  failure.getException(),
                  elapsed);

              TestCollectionRunner.this.states.testsStatePut(
                class_name,
                this.test_name,
                state);
            }

            @SuppressWarnings("synthetic-access") @Override public
              void
              fireTestAssumptionFailed(
                final Failure failure)
            {
              this.result_type = TestStateType.STATE_SKIPPED;
              final Skipped state =
                new TestState.Skipped("Assumption failed");
              TestCollectionRunner.this.states.testsStatePut(
                class_name,
                this.test_name,
                state);
            }

            @SuppressWarnings("synthetic-access") @Override public
              void
              fireTestIgnored(
                final Description description)
            {
              this.result_type = TestStateType.STATE_SKIPPED;
              this.test_name = new TestName(description.getMethodName());
              final Skipped state =
                new TestState.Skipped("@Ignore annotation");
              TestCollectionRunner.this.states.testsStatePut(
                class_name,
                this.test_name,
                state);
            }

            @SuppressWarnings("synthetic-access") @Override public
              void
              fireTestFinished(
                final Description description)
            {
              final StringBuilderOutputStream stdout = this.output_stdout;
              final StringBuilderOutputStream stderr = this.output_stderr;
              this.streamsReset();

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
                    this.test_name,
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

  @Override public @Nonnull Element toXML(
    final @Nonnull TestReportConfig config)
  {
    return this.states.toXML(config);
  }
}
