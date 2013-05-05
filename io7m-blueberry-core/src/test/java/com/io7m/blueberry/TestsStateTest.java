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

import org.junit.Assert;
import org.junit.Test;

public class TestsStateTest
{
  @SuppressWarnings("static-method") @Test public void testEmpty()
  {
    @SuppressWarnings("unused") final TestsState tstate =
      new TestsState(new HashSet<Class<?>>(), new TestStateListener() {
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
      });
  }

  class Counter implements TestStateListener
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
  }

  @Test public void testCreateCount()
  {
    final Counter counter = new Counter();
    final HashSet<Class<?>> classes = new HashSet<Class<?>>();
    classes.add(AllPass.class);
    @SuppressWarnings("unused") final TestsState tstate =
      new TestsState(classes, counter);

    Assert.assertEquals(3, counter.creates);
    Assert.assertEquals(3, counter.updates);
  }

  @Test public void testUpdateCount()
  {
    final Counter counter = new Counter();
    final HashSet<Class<?>> classes = new HashSet<Class<?>>();
    classes.add(AllPass.class);
    final TestsState tstate = new TestsState(classes, counter);

    final ClassName class_name = new ClassName("com.io7m.Xyz");

    tstate.testsStatePut(
      class_name,
      new TestName("a"),
      new TestState.Initialized());

    tstate.testsStatePut(
      class_name,
      new TestName("b"),
      new TestState.Initialized());

    tstate.testsStatePut(
      class_name,
      new TestName("c"),
      new TestState.Initialized());

    Assert.assertEquals(6, counter.creates);
    Assert.assertEquals(6, counter.updates);
  }
}
