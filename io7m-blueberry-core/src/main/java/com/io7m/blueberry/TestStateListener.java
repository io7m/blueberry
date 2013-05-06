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

/**
 * An interface for listening for test state changes.
 */

public interface TestStateListener
{
  /**
   * The tests have started running, and <code>count</code> tests will be
   * executed.
   */

  void testStateRunStarted(
    final long count);

  /**
   * A test named <code>test</code> in class <code>class_name</code> has been
   * added with state <code>state</code>.
   */

  void testStateCreated(
    final @Nonnull ClassName class_name,
    final @Nonnull TestName test,
    final @Nonnull TestState state);

  /**
   * A test named <code>test</code> in class <code>class_name</code> has
   * started running.
   */

  void testStateStarted(
    final @Nonnull ClassName class_name,
    final @Nonnull TestName test,
    final long n);

  /**
   * The state of the test named <code>test</code> in class
   * <code>class_name</code> has been updated to state <code>state</code>.
   */

  void testStateUpdated(
    final @Nonnull ClassName class_name,
    final @Nonnull TestName test,
    final @Nonnull TestState state);

  /**
   * The tests have finished running.
   */

  void testStateRunFinished();
}