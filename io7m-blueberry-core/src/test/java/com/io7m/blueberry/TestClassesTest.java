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

import java.lang.reflect.Method;
import java.util.Set;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class TestClassesTest
{
  @SuppressWarnings("static-method") @Test public
    void
    testIsTestMethodPrivateNotStatic()
      throws NoSuchMethodException
  {
    final MethodCases mc = new MethodCases();
    final Class<? extends MethodCases> c = mc.getClass();
    final Method m =
      c.getDeclaredMethod("privateNotStatic", (Class<?>[]) null);
    Assert.assertFalse(TestClasses.isTestMethod(m));
  }

  @SuppressWarnings("static-method") @Test public
    void
    testIsTestMethodPrivateAndStatic()
      throws NoSuchMethodException
  {
    final MethodCases mc = new MethodCases();
    final Class<? extends MethodCases> c = mc.getClass();
    final Method m =
      c.getDeclaredMethod("privateAndStatic", (Class<?>[]) null);
    Assert.assertFalse(TestClasses.isTestMethod(m));
  }

  @SuppressWarnings("static-method") @Test public
    void
    testIsTestMethodPublicNotStatic()
      throws NoSuchMethodException
  {
    final MethodCases mc = new MethodCases();
    final Class<? extends MethodCases> c = mc.getClass();
    final Method m = c.getMethod("publicNotStatic", (Class<?>[]) null);
    Assert.assertFalse(TestClasses.isTestMethod(m));
  }

  @SuppressWarnings("static-method") @Test public
    void
    testIsTestMethodPublicAndStatic()
      throws NoSuchMethodException
  {
    final MethodCases mc = new MethodCases();
    final Class<? extends MethodCases> c = mc.getClass();
    final Method m = c.getMethod("publicAndStatic", (Class<?>[]) null);
    Assert.assertFalse(TestClasses.isTestMethod(m));
  }

  @SuppressWarnings("static-method") @Test public
    void
    testIsTestMethodActual()
      throws NoSuchMethodException
  {
    final MethodCases mc = new MethodCases();
    final Class<? extends MethodCases> c = mc.getClass();
    final Method m = c.getMethod("publicActual", (Class<?>[]) null);
    Assert.assertTrue(TestClasses.isTestMethod(m));
  }

  @SuppressWarnings("static-method") @Test public
    void
    testIsRunnableAbstract()
  {
    @SuppressWarnings("unused") final UnrunnableClassAbstract uca =
      new UnrunnableClassAbstract() {
        // Nothing
      };

    Assert.assertFalse(TestClasses
      .isRunnableTestClass(UnrunnableClassAbstract.class));
  }

  @SuppressWarnings("static-method") @Test public
    void
    testIsRunnableNoMethods()
  {
    final UnrunnableClassNoMethods unm = new UnrunnableClassNoMethods();
    Assert.assertFalse(TestClasses.isRunnableTestClass(unm.getClass()));
  }

  @SuppressWarnings("static-method") @Test public
    void
    testIsRunnableNotPublic()
  {
    final UnrunnableClassNotPublic unp = new UnrunnableClassNotPublic();
    Assert.assertFalse(TestClasses.isRunnableTestClass(unp.getClass()));
  }

  @SuppressWarnings("static-method") @Test public void testIsRunnable()
  {
    final RunnableClass rc = new RunnableClass();
    Assert.assertTrue(TestClasses.isRunnableTestClass(rc.getClass()));
  }

  @Ignore("Fails when executed from Maven, for unknown reasons") @SuppressWarnings("static-method") @Test public
    void
    testGetClasses()
  {
    final Set<Class<?>> cs =
      TestClasses.getPackageClasses("com.io7m.blueberry");
    Assert.assertNotNull(cs);
    Assert.assertFalse(cs.size() == 0);

    Assert.assertTrue(cs.contains(TestClassesTest.class));
    Assert.assertTrue(cs.contains(RunnableClass.class));
    Assert.assertTrue(cs.contains(MethodCases.class));
    Assert.assertTrue(cs.contains(AllPass.class));
    Assert.assertTrue(cs.contains(AllFail.class));
  }

  @SuppressWarnings("static-method") @Test public void testGetMethods()
  {
    final Set<Method> ms = TestClasses.getRunnableTestMethods(AllPass.class);
    Assert.assertTrue(ms.size() == 3);
  }
}
