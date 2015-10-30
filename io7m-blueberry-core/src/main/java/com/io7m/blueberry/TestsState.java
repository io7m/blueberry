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

import com.io7m.blueberry.TestState.Failed;
import com.io7m.blueberry.TestState.Initialized;
import nu.xom.Attribute;
import nu.xom.Element;
import org.junit.runners.model.InitializationError;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

/**
 * Opaque structure representing the current testing state.
 */

public final class TestsState implements ToXMLReportType<TestReportConfig>
{
  private final Map<ClassName, Map<TestName, TestState>> tests;
  private final TestStateListenerType                    listener;
  private final Initialized                              initial_state;

  TestsState(
    final Set<Class<?>> classes,
    final TestStateListenerType in_listener)
  {
    this.tests = new HashMap<ClassName, Map<TestName, TestState>>();
    this.listener = in_listener;
    this.initial_state = new TestState.Initialized();

    for (final Class<?> c : classes) {
      final String c_name = c.getCanonicalName();
      assert c_name != null;
      final ClassName class_name = new ClassName(c_name);
      final Set<Method> methods = TestScanning.getRunnableTestMethods(c);
      for (final Method m : methods) {
        final String m_name = m.getName();
        assert m_name != null;
        final TestName test_name = new TestName(m_name);
        this.testsStatePut(class_name, test_name, this.initial_state);
      }
    }
  }

  private static Element toXMLEnvironment()
  {
    final Element ee =
      new Element("system-environment", TestReportXMLVersion.XML_URI);
    final Map<String, String> env = System.getenv();

    for (final Entry<String, String> e : env.entrySet()) {
      final Element p = new Element(
        "system-environment-variable", TestReportXMLVersion.XML_URI);
      final Element pk = new Element("key", TestReportXMLVersion.XML_URI);
      pk.appendChild(e.getKey());
      final Element pv = new Element("value", TestReportXMLVersion.XML_URI);
      pv.appendChild(e.getValue());
      p.appendChild(pk);
      p.appendChild(pv);
      ee.appendChild(p);
    }

    return ee;
  }

  private static Element toXMLProperties()
  {
    final Element pe =
      new Element("system-properties", TestReportXMLVersion.XML_URI);
    final Properties props = System.getProperties();

    for (final Entry<Object, Object> e : props.entrySet()) {
      final String key = (String) e.getKey();
      final String val = (String) e.getValue();
      final Element p =
        new Element("system-property", TestReportXMLVersion.XML_URI);
      final Element pk = new Element("key", TestReportXMLVersion.XML_URI);
      pk.appendChild(key);
      final Element pv = new Element("value", TestReportXMLVersion.XML_URI);
      pv.appendChild(val);
      p.appendChild(pk);
      p.appendChild(pv);
      pe.appendChild(p);
    }

    return pe;
  }

  void testsStateClassInitializationFailed(
    final Class<?> current_class,
    final InitializationError e)
  {
    final Set<Method> methods =
      TestScanning.getRunnableTestMethods(current_class);

    final String c_name = current_class.getCanonicalName();
    assert c_name != null;
    final ClassName class_name = new ClassName(c_name);
    final Failed state =
      new TestState.Failed(new StringBuilder(), new StringBuilder(), e, 0);

    for (final Method m : methods) {
      final String m_name = m.getName();
      assert m_name != null;
      this.testsStatePut(class_name, new TestName(m_name), state);
    }
  }

  boolean testsStateExists(
    final ClassName class_name,
    final TestName test)
  {
    if (this.tests.containsKey(class_name)) {
      final Map<TestName, TestState> class_tests = this.tests.get(class_name);
      return class_tests.containsKey(test);
    }
    return false;
  }

  void testsStatePut(
    final ClassName class_name,
    final TestName test,
    final TestState state)
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

  private void testsStateWriteMap(
    final ClassName class_name,
    final TestName test,
    final TestState state)
  {
    if (this.tests.containsKey(class_name)) {
      final Map<TestName, TestState> class_tests = this.tests.get(class_name);
      class_tests.put(test, state);
    } else {
      final Map<TestName, TestState> class_tests =
        new HashMap<TestName, TestState>();
      class_tests.put(test, state);
      this.tests.put(class_name, class_tests);
    }
  }

  @Override public Element toXML(
    final TestReportConfig config)
  {
    final Element root = new Element("report", TestReportXMLVersion.XML_URI);

    final Element info = this.toXMLPackageInfo(config);
    root.appendChild(info);

    final Element classes = this.toXMLClasses();

    if (config.wantOutputEnvironment()) {
      final Element environment = TestsState.toXMLEnvironment();
      root.appendChild(environment);
    }
    if (config.wantOutputSystemProperties()) {
      final Element properties = TestsState.toXMLProperties();
      root.appendChild(properties);
    }

    root.appendChild(classes);
    return root;
  }

  private Element toXMLPackageInfo(final TestReportConfig config)
  {
    final Element info =
      new Element("package-info", TestReportXMLVersion.XML_URI);
    final Element e_name = new Element("name", TestReportXMLVersion.XML_URI);
    e_name.appendChild(config.getPackageName());
    final Element e_ver = new Element("version", TestReportXMLVersion.XML_URI);
    e_ver.appendChild(config.getPackageVersion());
    info.appendChild(e_name);
    info.appendChild(e_ver);
    return info;
  }

  private Element toXMLClasses()
  {
    final Element classes =
      new Element("classes", TestReportXMLVersion.XML_URI);

    for (final Entry<ClassName, Map<TestName, TestState>> ce : this.tests
      .entrySet()) {
      final ClassName class_name = ce.getKey();
      final Map<TestName, TestState> class_tests = ce.getValue();
      final Element class_element =
        new Element("class", TestReportXMLVersion.XML_URI);
      class_element.addAttribute(new Attribute("name", class_name.getActual()));

      for (final Entry<TestName, TestState> te : class_tests.entrySet()) {
        final TestName test_name = te.getKey();
        assert test_name != null;
        final TestState state = te.getValue();
        final Element test_element = state.toXML(test_name);
        class_element.appendChild(test_element);
      }

      classes.appendChild(class_element);
    }
    return classes;
  }

  long testsCount()
  {
    long count = 0;
    for (final Entry<ClassName, Map<TestName, TestState>> ce : this.tests
      .entrySet()) {
      count += ce.getValue().size();
    }
    return count;
  }

  void testsStateStarted(
    final ClassName class_name,
    final TestName test,
    final long n)
  {
    this.listener.testStateStarted(class_name, test, n);
  }

  void testsStateRunStarted()
  {
    this.listener.testStateRunStarted(this.testsCount());
  }

  void testsStateRunFinished()
  {
    this.listener.testStateRunFinished();
  }
}
