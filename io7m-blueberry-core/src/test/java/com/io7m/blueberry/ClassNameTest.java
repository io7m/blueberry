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

import org.junit.Assert;
import org.junit.Test;

public class ClassNameTest
{
  @SuppressWarnings("static-method") @Test public void testEqualsNot()
  {
    final ClassName a = new ClassName("a");
    final ClassName b = new ClassName("b");
    Assert.assertFalse(a.equals(b));
  }

  @SuppressWarnings("static-method") @Test public void testEqualsNotClass()
  {
    final ClassName a = new ClassName("a");
    Assert.assertFalse(a.equals(Integer.valueOf(23)));
  }

  @SuppressWarnings("static-method") @Test public void testEqualsNotNull()
  {
    final ClassName a = new ClassName("a");
    Assert.assertFalse(a.equals(null));
  }

  @SuppressWarnings("static-method") @Test public void testEqualsReflexive()
  {
    final ClassName a = new ClassName("a");
    Assert.assertEquals(a, a);
  }

  @SuppressWarnings("static-method") @Test public void testEqualsSymmetric()
  {
    final ClassName a = new ClassName("a");
    final ClassName b = new ClassName("a");
    Assert.assertEquals(a, b);
    Assert.assertEquals(b, a);
  }

  @SuppressWarnings("static-method") @Test public void testHashCode()
  {
    final ClassName a = new ClassName("a");
    final ClassName b = new ClassName("b");

    Assert.assertTrue(a.hashCode() == a.hashCode());
    Assert.assertFalse(a.hashCode() == b.hashCode());
  }

  @SuppressWarnings("static-method") @Test public void testToString()
  {
    final ClassName a = new ClassName("a");
    final ClassName b = new ClassName("b");
    final ClassName c = new ClassName("a");

    Assert.assertEquals(a.toString(), c.toString());
    Assert.assertFalse(a.toString().equals(b.toString()));
  }
}
