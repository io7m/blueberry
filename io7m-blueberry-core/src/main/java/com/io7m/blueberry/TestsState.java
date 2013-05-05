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
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nonnull;

import nu.xom.Attribute;
import nu.xom.Element;

import org.junit.runners.model.InitializationError;

import com.io7m.blueberry.TestState.Failed;
import com.io7m.blueberry.TestState.Initialized;

public final class TestsState implements ToXMLReport<Void>
{
  private final @Nonnull HashMap<ClassName, HashMap<TestName, TestState>> tests;
  private final @Nonnull TestStateListener                                listener;
  private final @Nonnull Initialized                                      initial_state;

  TestsState(
    final @Nonnull Set<Class<?>> classes,
    final @Nonnull TestStateListener listener)
  {
    this.tests = new HashMap<ClassName, HashMap<TestName, TestState>>();
    this.listener = listener;
    this.initial_state = new TestState.Initialized();

    for (final Class<?> c : classes) {
      final ClassName class_name = new ClassName(c.getCanonicalName());
      final Set<Method> methods = TestClasses.getRunnableTestMethods(c);
      for (final Method m : methods) {
        final TestName test_name = new TestName(m.getName());
        this.testsStatePut(class_name, test_name, this.initial_state);
      }
    }
  }

  public void testsStateClassInitializationFailed(
    final @Nonnull Class<?> current_class,
    final @Nonnull InitializationError e)
  {
    final Set<Method> methods =
      TestClasses.getRunnableTestMethods(current_class);

    final ClassName class_name =
      new ClassName(current_class.getCanonicalName());
    final Failed state =
      new TestState.Failed(new StringBuilder(), new StringBuilder(), e);

    for (final Method m : methods) {
      this.testsStatePut(class_name, new TestName(m.getName()), state);
    }
  }

  public boolean testsStateExists(
    final @Nonnull ClassName class_name,
    final @Nonnull TestName test)
  {
    if (this.tests.containsKey(class_name)) {
      final HashMap<TestName, TestState> class_tests =
        this.tests.get(class_name);
      return class_tests.containsKey(test);
    }
    return false;
  }

  private void testsStateWriteMap(
    final @Nonnull ClassName class_name,
    final @Nonnull TestName test,
    final @Nonnull TestState state)
  {
    if (this.tests.containsKey(class_name)) {
      final HashMap<TestName, TestState> class_tests =
        this.tests.get(class_name);
      class_tests.put(test, state);
    } else {
      final HashMap<TestName, TestState> class_tests =
        new HashMap<TestName, TestState>();
      class_tests.put(test, state);
      this.tests.put(class_name, class_tests);
    }
  }

  public void testsStatePut(
    final @Nonnull ClassName class_name,
    final @Nonnull TestName test,
    final @Nonnull TestState state)
  {
    if (this.testsStateExists(class_name, test)) {
      this.testsStateWriteMap(class_name, test, state);
      this.listener.testStateUpdated(class_name, test, state);
    } else {
      this.testsStateWriteMap(class_name, test, state);
      this.listener.testStateCreated(class_name, test, state);
      this.listener.testStateUpdated(class_name, test, state);
    }
  }

  @Override public @Nonnull Element toXML(
    final Void x)
  {
    final Element root = new Element("report", XMLVersion.XML_URI);
    final Element classes = new Element("classes", XMLVersion.XML_URI);

    for (final Entry<ClassName, HashMap<TestName, TestState>> ce : this.tests
      .entrySet()) {
      final ClassName class_name = ce.getKey();
      final HashMap<TestName, TestState> class_tests = ce.getValue();
      final Element class_element = new Element("class", XMLVersion.XML_URI);
      class_element.addAttribute(new Attribute("name", class_name.actual));

      for (final Entry<TestName, TestState> te : class_tests.entrySet()) {
        final TestName test_name = te.getKey();
        final TestState state = te.getValue();
        final Element test_element = state.toXML(test_name);
        class_element.appendChild(test_element);
      }
    }

    root.appendChild(classes);
    return root;
  }
}
