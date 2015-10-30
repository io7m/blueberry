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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import com.io7m.junreachable.UnreachableCodeException;

/**
 * Functions for scanning for tests, and inspecting classes and methods.
 */

public final class TestScanning
{
  private TestScanning()
  {
    throw new UnreachableCodeException();
  }

  /**
   * @return The set of all classes on the classpath that are in packages
   *         prefixed by <code>prefix</code> and are runnable according to
   *         {@link #isRunnableTestClass(Class)}.
   * 
   *         That is, a prefix of <code>x.y</code> will find classes
   *         <code>x.y.z.A</code>, <code>x.y.q.B</code>, and so on.
   * 
   * @param prefix
   *          The package prefix.
   */

  public static Set<Class<?>> getPackageClasses(
    final String prefix)
  {
    final ConfigurationBuilder cb = new ConfigurationBuilder();
    cb.setScanners(new SubTypesScanner(false));
    cb.setUrls(ClasspathHelper.forClassLoader());
    cb.filterInputsBy(new FilterBuilder().include(FilterBuilder
      .prefix(prefix)));

    final Reflections r = cb.build();
    final Set<Class<? extends Object>> ts = r.getSubTypesOf(Object.class);
    final Set<Class<? extends Object>> selected =
      new HashSet<Class<? extends Object>>();

    for (final Class<? extends Object> c : ts) {
      assert c != null;

      if (TestScanning.isRunnableTestClass(c)) {
        selected.add(c);
      }
    }

    return selected;
  }

  /**
   * @return The set of all runnable test methods, according to
   *         {@link #isTestMethod(Method)}.
   * @param c
   *          The class.
   */

  public static Set<Method> getRunnableTestMethods(
    final Class<?> c)
  {
    final Set<Method> methods = new HashSet<Method>();
    final Method[] all = c.getMethods();
    for (final Method m : all) {
      assert m != null;

      if (TestScanning.isTestMethod(m)) {
        methods.add(m);
      }
    }
    return methods;
  }

  /**
   * @return <code>true</code> iff the given class is <code>public</code>, not
   *         <code>abstract</code>, and has at least one test method.
   * 
   * @param c
   *          The class.
   * 
   * @see #isTestMethod(Method)
   */

  public static boolean isRunnableTestClass(
    final Class<?> c)
  {
    final int mods = c.getModifiers();
    if ((mods & Modifier.PUBLIC) != Modifier.PUBLIC) {
      return false;
    }
    if ((mods & Modifier.ABSTRACT) == Modifier.ABSTRACT) {
      return false;
    }

    final Method[] methods = c.getMethods();
    for (final Method m : methods) {
      assert m != null;

      if (TestScanning.isTestMethod(m)) {
        return true;
      }
    }

    return false;
  }

  /**
   * @return <code>true</code> iff the given method is not <code>static</code>
   *         and is annotated with {@link org.junit.Test}.
   * 
   * @param m
   *          The method.
   */

  public static boolean isTestMethod(
    final Method m)
  {
    if ((m.getModifiers() & Modifier.STATIC) == Modifier.STATIC) {
      return false;
    }

    final Annotation[] annotations = m.getAnnotations();
    for (final Annotation a : annotations) {
      if (a.annotationType() == org.junit.Test.class) {
        return true;
      }
    }

    return false;
  }
}
