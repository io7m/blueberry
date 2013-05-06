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

import java.util.concurrent.TimeUnit;

import org.junit.Test;

/**
 * A class containing tests that are slow to complete.
 * 
 * This is test suite data and not intended for use by developers.
 */

public final class TestDataSlowAndBlocking
{
  @SuppressWarnings("static-method") @Test public void testSlowOne()
    throws InterruptedException
  {
    Thread.sleep(TimeUnit.MILLISECONDS.convert(3, TimeUnit.SECONDS));
  }

  @SuppressWarnings("static-method") @Test public void testSlowTwo()
    throws InterruptedException
  {
    Thread.sleep(TimeUnit.MILLISECONDS.convert(3, TimeUnit.SECONDS));
  }

  @SuppressWarnings("static-method") @Test public void testSlowThree()
    throws InterruptedException
  {
    Thread.sleep(TimeUnit.MILLISECONDS.convert(3, TimeUnit.SECONDS));
  }

  @SuppressWarnings("static-method") @Test public void testSlowFour()
    throws InterruptedException
  {
    Thread.sleep(TimeUnit.MILLISECONDS.convert(3, TimeUnit.SECONDS));
  }

  @SuppressWarnings("static-method") @Test public void testSlowFive()
    throws InterruptedException
  {
    Thread.sleep(TimeUnit.MILLISECONDS.convert(3, TimeUnit.SECONDS));
  }
}
