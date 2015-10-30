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

package com.io7m.blueberry.test_data;

import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

/**
 * A set of tests that will be skipped due to a failed assumption.
 * 
 * This is test suite data and not intended for use by developers.
 */

@SuppressWarnings("static-method") public final class TestDataClassAssumptionFailed
{
  /**
   * Default constructor.
   */

  public TestDataClassAssumptionFailed()
  {

  }

  /**
   * A test.
   */

  @Before public void checkAssumption()
  {
    Assume.assumeTrue(false);
  }

  /**
   * A test.
   */

  @Test public void testPassOne()
  {
    System.out.println(TestDataClassAssumptionFailed.class.getCanonicalName()
      + ": testPassOne");
  }
}
