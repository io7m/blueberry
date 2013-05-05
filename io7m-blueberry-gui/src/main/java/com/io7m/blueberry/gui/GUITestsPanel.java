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

package com.io7m.blueberry.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Nonnull;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

import net.java.dev.designgridlayout.DesignGridLayout;

import com.io7m.blueberry.ClassName;
import com.io7m.blueberry.TestClasses;
import com.io7m.blueberry.TestName;
import com.io7m.blueberry.TestState;
import com.io7m.blueberry.TestState.Failed;
import com.io7m.blueberry.TestState.Succeeded;
import com.io7m.blueberry.TestStateListener;
import com.io7m.blueberry.TestSuiteRunner;

final class GUITestsPanel extends JPanel
{
  private static final long serialVersionUID = 576981915184121927L;

  static class ClassTestPair implements Comparable<ClassTestPair>
  {
    private final ClassName class_name;
    private final TestName  test_name;

    ClassTestPair(
      final ClassName class_name,
      final TestName test_name)
    {
      assert class_name != null;
      assert test_name != null;
      this.class_name = class_name;
      this.test_name = test_name;
    }

    @Override public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = (prime * result) + this.class_name.hashCode();
      result = (prime * result) + this.test_name.hashCode();
      return result;
    }

    @Override public boolean equals(
      final Object obj)
    {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      if (this.getClass() != obj.getClass()) {
        return false;
      }
      final ClassTestPair other = (ClassTestPair) obj;
      if (!this.class_name.equals(other.class_name)) {
        return false;
      }
      if (!this.test_name.equals(other.test_name)) {
        return false;
      }
      return true;
    }

    @Override public int compareTo(
      final @Nonnull ClassTestPair o)
    {
      final int cnc = this.class_name.actual.compareTo(o.class_name.actual);
      if (cnc == 0) {
        return this.test_name.actual.compareTo(o.test_name.actual);
      }
      return cnc;
    }
  }

  static class TestsTableModel extends AbstractTableModel
  {
    private final @Nonnull SortedMap<ClassTestPair, TestState> data_map;
    private static final long                                  serialVersionUID;
    private static final @Nonnull String[]                     column_names;
    private final @Nonnull NumberFormat                        formatter;

    static {
      serialVersionUID = -912792027128516356L;
      column_names =
        new String[] { "Class", "Test", "State", "Elapsed (ms)" };
    }

    public TestsTableModel()
    {
      this.data_map = new TreeMap<ClassTestPair, TestState>();
      this.formatter = NumberFormat.getInstance();
      this.formatter.setMaximumFractionDigits(2);
      this.formatter.setMinimumFractionDigits(2);
    }

    @Override public String getColumnName(
      final int column)
    {
      return TestsTableModel.column_names[column];
    }

    @Override public int getRowCount()
    {
      return this.data_map.size();
    }

    @Override public int getColumnCount()
    {
      return TestsTableModel.column_names.length;
    }

    @Override public Object getValueAt(
      final int rowIndex,
      final int columnIndex)
    {
      if (rowIndex < this.data_map.size()) {
        final Iterator<ClassTestPair> iter =
          this.data_map.keySet().iterator();
        int index = 0;
        while (iter.hasNext()) {
          final ClassTestPair x = iter.next();
          if (index == rowIndex) {
            return this.getValue(columnIndex, x);
          }
          ++index;
        }
      }

      throw new AssertionError("Unreachable");
    }

    private Object getValue(
      final int columnIndex,
      final ClassTestPair x)
    {
      switch (columnIndex) {
        case 0:
          return x.class_name.actual;
        case 1:
          return x.test_name.actual;
        case 2:
          return this.data_map.get(x).getType().toHumanString();
        case 3:
        {
          final TestState z = this.data_map.get(x);
          switch (z.getType()) {
            case STATE_FAILED:
            {
              final Failed f = (TestState.Failed) z;
              final double k = (f.getTimeElapsedNanoseconds()) * 0.000001;
              return this.formatter.format(k);
            }
            case STATE_INITIALIZED:
            {
              return this.formatter.format(0.0);
            }
            case STATE_SKIPPED:
            {
              return this.formatter.format(0.0);
            }
            case STATE_SUCCEEDED:
            {
              final Succeeded s = (TestState.Succeeded) z;
              final double k = (s.getTimeElapsedNanoseconds()) * 0.000001;
              return this.formatter.format(k);
            }
          }
        }
      }

      throw new AssertionError("Unreachable code!");
    }

    public void testStateSet(
      final ClassName class_name,
      final TestName test,
      final TestState state)
    {
      final ClassTestPair cp = new ClassTestPair(class_name, test);
      this.data_map.put(cp, state);
      this.fireTableDataChanged();
    }
  }

  static class TestsTable extends JTable
  {
    private static final long serialVersionUID = 432410053774535219L;

    TestsTable(
      final @Nonnull TestsTableModel data)
    {
      super(data);
      this.getColumnModel().getColumn(0).setPreferredWidth(300);
      this.getColumnModel().getColumn(1).setPreferredWidth(100);
      this.getColumnModel().getColumn(2).setPreferredWidth(32);
      this.getColumnModel().getColumn(3).setPreferredWidth(32);
    }
  }

  private final @Nonnull TestsTableModel                  table_data;
  private final @Nonnull TestsTable                       table;
  private final @Nonnull JScrollPane                      table_pane;
  private final @Nonnull JButton                          button_run;
  private final @Nonnull JButton                          button_save;
  private final @Nonnull GUIStatusBar                     status;
  private final @Nonnull GUIProjectInfo                   info;
  private final @Nonnull Executor                         executor;
  private final @Nonnull AtomicReference<TestSuiteRunner> runner;
  private final @Nonnull RunnerListener                   listener;

  private static class RunnerListener implements TestStateListener
  {
    private final @Nonnull TestsTableModel table_data;
    private final @Nonnull GUIStatusBar    status;
    private long                           test_total = 0;

    public RunnerListener(
      final @Nonnull TestsTableModel table_data,
      final @Nonnull GUIStatusBar status)
    {
      this.table_data = table_data;
      this.status = status;
    }

    @Override public void testStateRunStarted(
      final long count)
    {
      SwingUtilities.invokeLater(new Runnable() {
        @SuppressWarnings("synthetic-access") @Override public void run()
        {
          RunnerListener.this.test_total = count;
          RunnerListener.this.status.setStatus("Running tests...");
        }
      });
    }

    @Override public void testStateCreated(
      final ClassName class_name,
      final TestName test,
      final TestState state)
    {
      SwingUtilities.invokeLater(new Runnable() {
        @SuppressWarnings("synthetic-access") @Override public void run()
        {
          RunnerListener.this.table_data
            .testStateSet(class_name, test, state);
        }
      });
    }

    @Override public void testStateUpdated(
      final ClassName class_name,
      final TestName test,
      final TestState state)
    {
      SwingUtilities.invokeLater(new Runnable() {
        @SuppressWarnings("synthetic-access") @Override public void run()
        {
          RunnerListener.this.table_data
            .testStateSet(class_name, test, state);
        }
      });
    }

    @Override public void testStateRunFinished()
    {
      SwingUtilities.invokeLater(new Runnable() {
        @SuppressWarnings("synthetic-access") @Override public void run()
        {
          RunnerListener.this.status.setStatus("Test run completed");
        }
      });
    }

    @Override public void testStateStarted(
      final ClassName class_name,
      final TestName test,
      final long n)
    {
      SwingUtilities.invokeLater(new Runnable() {
        @SuppressWarnings("synthetic-access") @Override public void run()
        {
          final StringBuilder buffer = new StringBuilder();
          buffer.append("Running test ");
          buffer.append(class_name.actual);
          buffer.append(".");
          buffer.append(test.actual);
          buffer.append(" (");
          buffer.append(n);
          buffer.append(" of ");
          buffer.append(RunnerListener.this.test_total);
          buffer.append(")");

          RunnerListener.this.status.setStatus(buffer.toString());
        }
      });
    }
  }

  /**
   * An asynchronous class that loads all test classes and then saves the new
   * runner in the given atomic reference.
   */

  private static class RunnerInitializationTask implements Runnable
  {
    private long                                            load_time = 0;
    private final @Nonnull GUIProjectInfo                   info;
    private final @Nonnull GUIStatusBar                     status;
    private final @Nonnull AtomicReference<TestSuiteRunner> runner;
    private final @Nonnull RunnerListener                   listener;
    private final @Nonnull JButton                          button;

    public RunnerInitializationTask(
      final @Nonnull GUIProjectInfo info,
      final @Nonnull GUIStatusBar status,
      final @Nonnull AtomicReference<TestSuiteRunner> runner,
      final @Nonnull RunnerListener listener,
      final @Nonnull JButton button)
    {
      this.info = info;
      this.status = status;
      this.runner = runner;
      this.listener = listener;
      this.button = button;
    }

    private Set<Class<?>> getClasses()
    {
      final long time_before_load = System.nanoTime();
      final Set<Class<?>> classes = new HashSet<Class<?>>();
      for (final String prefix : this.info.getPackagePrefixes()) {
        final Set<Class<?>> cs = TestClasses.getPackageClasses(prefix);
        classes.addAll(cs);
      }
      this.load_time = System.nanoTime() - time_before_load;
      return classes;
    }

    @SuppressWarnings("synthetic-access") @Override public void run()
    {
      /**
       * Load classes.
       */

      SwingUtilities.invokeLater(new Runnable() {
        @Override public void run()
        {
          RunnerInitializationTask.this.status
            .setStatus("Loading test classes...");
        }
      });

      final Set<Class<?>> classes = this.getClasses();

      SwingUtilities.invokeLater(new Runnable() {
        @Override public void run()
        {
          final long ms =
            TimeUnit.MILLISECONDS.convert(
              RunnerInitializationTask.this.load_time,
              TimeUnit.NANOSECONDS);

          final StringBuilder buffer = new StringBuilder();
          buffer.append("Loaded ");
          buffer.append(classes.size());
          buffer.append(" classes in ");
          buffer.append(ms);
          buffer.append(" milliseconds");

          RunnerInitializationTask.this.status.setStatus(buffer.toString());
          RunnerInitializationTask.this.button.setEnabled(true);
        }
      });

      /**
       * Initialize runner.
       */

      final TestSuiteRunner new_runner =
        new TestSuiteRunner(this.listener, classes);

      this.runner.set(new_runner);
    }
  }

  GUITestsPanel(
    final @Nonnull GUIProjectInfo info,
    final @Nonnull GUIStatusBar status)
  {
    this.info = info;
    this.status = status;
    this.executor = Executors.newCachedThreadPool();
    this.runner = new AtomicReference<TestSuiteRunner>();

    /**
     * Initialize table.
     */

    this.table_data = new TestsTableModel();
    this.table = new TestsTable(this.table_data);
    this.table_pane = new JScrollPane(this.table);
    this.table_pane
      .setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
    this.table_pane
      .setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
    this.table_pane.setPreferredSize(new Dimension(640, 100));

    /**
     * Initialize controls.
     */

    this.button_run = new JButton("Run");
    this.button_run.setEnabled(false);
    this.button_run.addActionListener(new ActionListener() {
      @SuppressWarnings("synthetic-access") @Override public
        void
        actionPerformed(
          final ActionEvent e)
      {
        final TestSuiteRunner r = GUITestsPanel.this.runner.get();
        if (r != null) {
          GUITestsPanel.this.executor.execute(r);
        }
      }
    });

    this.button_save = new JButton("Save report...");
    this.button_save.setEnabled(false);

    final DesignGridLayout dg_layout = new DesignGridLayout(this);
    dg_layout.row().grid().add(this.table_pane);
    dg_layout.row().grid().add(this.button_run).add(this.button_save);

    this.listener = new RunnerListener(this.table_data, status);
    this.executor.execute(new RunnerInitializationTask(
      info,
      status,
      this.runner,
      this.listener,
      this.button_run));
  }
}
