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

/**
 * An interface for listening for test state changes.
 */

public interface TestStateListenerType
{
  /**
   * The tests have started running, and <code>count</code> tests will be
   * executed.
   * 
   * @param count
   *          The number of tests.
   */

  void testStateRunStarted(
    final long count);

  /**
   * A test named <code>test</code> in class <code>class_name</code> has been
   * added with state <code>state</code>.
   * 
   * @param class_name
   *          The class name.
   * @param test
   *          The test name.
   * @param state
   *          The test state.
   */

  void testStateCreated(
    final ClassName class_name,
    final TestName test,
    final TestState state);

  /**
   * A test named <code>test</code> in class <code>class_name</code> has
   * started running.
   * 
   * @param class_name
   *          The class name.
   * @param test
   *          The test name.
   * @param n
   *          The test number.
   */

  void testStateStarted(
    final ClassName class_name,
    final TestName test,
    final long n);

  /**
   * The state of the test named <code>test</code> in class
   * <code>class_name</code> has been updated to state <code>state</code>.
   * 
   * @param class_name
   *          The class name.
   * @param test
   *          The test name.
   * @param state
   *          The test state.
   */

  void testStateUpdated(
    final ClassName class_name,
    final TestName test,
    final TestState state);

  /**
   * The tests have finished running.
   */

  void testStateRunFinished();
}
