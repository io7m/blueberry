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

import com.io7m.blueberry.test_data.TestDataAllFail;
import com.io7m.blueberry.test_data.TestDataAllPass;
import com.io7m.blueberry.test_data.TestDataMethodCases;
import com.io7m.blueberry.test_data.TestDataRunnableClass;
import com.io7m.blueberry.test_data.TestDataUnrunnableClassAbstract;
import com.io7m.blueberry.test_data.TestDataUnrunnableClassNoMethods;

@SuppressWarnings("static-method") public class TestClassesTest
{
  @Test public void testIsTestMethodPrivateNotStatic()
    throws NoSuchMethodException
  {
    final TestDataMethodCases mc = new TestDataMethodCases();
    final Class<? extends TestDataMethodCases> c = mc.getClass();
    final Method m =
      c.getDeclaredMethod("privateNotStatic", (Class<?>[]) null);
    Assert.assertFalse(TestScanning.isTestMethod(m));
  }

  @Test public void testIsTestMethodPrivateAndStatic()
    throws NoSuchMethodException
  {
    final TestDataMethodCases mc = new TestDataMethodCases();
    final Class<? extends TestDataMethodCases> c = mc.getClass();
    final Method m =
      c.getDeclaredMethod("privateAndStatic", (Class<?>[]) null);
    Assert.assertFalse(TestScanning.isTestMethod(m));
  }

  @Test public void testIsTestMethodPublicNotStatic()
    throws NoSuchMethodException
  {
    final TestDataMethodCases mc = new TestDataMethodCases();
    final Class<? extends TestDataMethodCases> c = mc.getClass();
    final Method m = c.getMethod("publicNotStatic", (Class<?>[]) null);
    Assert.assertFalse(TestScanning.isTestMethod(m));
  }

  @Test public void testIsTestMethodPublicAndStatic()
    throws NoSuchMethodException
  {
    final TestDataMethodCases mc = new TestDataMethodCases();
    final Class<? extends TestDataMethodCases> c = mc.getClass();
    final Method m = c.getMethod("publicAndStatic", (Class<?>[]) null);
    Assert.assertFalse(TestScanning.isTestMethod(m));
  }

  @Test public void testIsTestMethodActual()
    throws NoSuchMethodException
  {
    final TestDataMethodCases mc = new TestDataMethodCases();
    final Class<? extends TestDataMethodCases> c = mc.getClass();
    final Method m = c.getMethod("publicActual", (Class<?>[]) null);
    Assert.assertTrue(TestScanning.isTestMethod(m));
  }

  @Test public void testIsRunnableAbstract()
  {
    @SuppressWarnings("unused") final TestDataUnrunnableClassAbstract uca =
      new TestDataUnrunnableClassAbstract() {
        // Nothing
      };

    Assert.assertFalse(TestScanning
      .isRunnableTestClass(TestDataUnrunnableClassAbstract.class));
  }

  @Test public void testIsRunnableNoMethods()
  {
    final TestDataUnrunnableClassNoMethods unm =
      new TestDataUnrunnableClassNoMethods();
    Assert.assertFalse(TestScanning.isRunnableTestClass(unm.getClass()));
  }

  @Test public void testIsRunnableNotPublic()
  {
    final TestDataUnrunnableClassNotPublic unm =
      new TestDataUnrunnableClassNotPublic();
    Assert.assertFalse(TestScanning.isRunnableTestClass(unm.getClass()));
  }

  @Test public void testIsRunnable()
  {
    final TestDataRunnableClass rc = new TestDataRunnableClass();
    Assert.assertTrue(TestScanning.isRunnableTestClass(rc.getClass()));
  }

  @Ignore("Fails when executed from Maven, for unknown reasons") @Test public
    void
    testGetClasses()
  {
    final Set<Class<?>> cs =
      TestScanning.getPackageClasses("com.io7m.blueberry");
    Assert.assertNotNull(cs);
    Assert.assertFalse(cs.size() == 0);

    Assert.assertTrue(cs.contains(TestClassesTest.class));
    Assert.assertTrue(cs.contains(TestDataRunnableClass.class));
    Assert.assertTrue(cs.contains(TestDataMethodCases.class));
    Assert.assertTrue(cs.contains(TestDataAllPass.class));
    Assert.assertTrue(cs.contains(TestDataAllFail.class));
  }

  @Test public void testGetMethods()
  {
    final Set<Method> ms =
      TestScanning.getRunnableTestMethods(TestDataAllPass.class);
    Assert.assertTrue(ms.size() == 3);
  }
}
