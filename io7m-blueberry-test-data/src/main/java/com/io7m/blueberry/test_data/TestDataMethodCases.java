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

import org.junit.Test;

/**
 * A class containing methods of varying visibility.
 * 
 * This is test suite data and not intended for use by developers.
 */

@SuppressWarnings({ "unused", "static-method" }) public final class TestDataMethodCases
{
  /**
   * Default constructor.
   */

  public TestDataMethodCases()
  {

  }

  /**
   * A test.
   */

  private void privateNotStatic()
  {
    throw new AssertionError();
  }

  /**
   * A test.
   */

  private static void privateAndStatic()
  {
    throw new AssertionError();
  }

  /**
   * A test.
   */

  public void publicNotStatic()
  {
    throw new AssertionError();
  }

  /**
   * A test.
   */

  public static void publicAndStatic()
  {
    throw new AssertionError();
  }

  /**
   * A test.
   */

  @Test public void publicActual()
  {
    System.out.println("publicActual");
  }
}
