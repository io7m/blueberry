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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedMap;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.zip.GZIPOutputStream;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;

import net.java.dev.designgridlayout.DesignGridLayout;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Serializer;

import com.io7m.blueberry.ClassName;
import com.io7m.blueberry.StringBuilderOutputStream;
import com.io7m.blueberry.TestCollectionRunner;
import com.io7m.blueberry.TestName;
import com.io7m.blueberry.TestReportConfig;
import com.io7m.blueberry.TestScanning;
import com.io7m.blueberry.TestState;
import com.io7m.blueberry.TestState.Failed;
import com.io7m.blueberry.TestState.Succeeded;
import com.io7m.blueberry.TestStateListener;

/**
 * The main testing panel.
 */

final class GUITestsPanel extends JPanel
{
  private static final long serialVersionUID = 576981915184121927L;

  static class ClassTestPair implements Comparable<ClassTestPair>
  {
    public final @Nonnull ClassName class_name;
    public final @Nonnull TestName  test_name;

    ClassTestPair(
      final @Nonnull ClassName class_name,
      final @Nonnull TestName test_name)
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

    TestsTableModel()
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

    @Nonnull ClassTestPair getClassTestPairAt(
      final int row)
    {
      if (row < this.data_map.size()) {
        final Iterator<ClassTestPair> iter =
          this.data_map.keySet().iterator();
        int index = 0;
        while (iter.hasNext()) {
          final ClassTestPair x = iter.next();
          if (index == row) {
            return x;
          }
          ++index;
        }
      }

      throw new AssertionError("Unreachable code");
    }

    @Override public Object getValueAt(
      final int row,
      final int column)
    {
      return this.getValue(column, this.getClassTestPairAt(row));
    }

    private String getValue(
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
          return this.getElapsed(this.data_map.get(x));
      }

      throw new AssertionError("Unreachable code");
    }

    private @Nonnull String getElapsed(
      final @Nonnull TestState state)
    {
      switch (state.getType()) {
        case STATE_FAILED:
        {
          final Failed f = (TestState.Failed) state;
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
          final Succeeded s = (TestState.Succeeded) state;
          final double k = (s.getTimeElapsedNanoseconds()) * 0.000001;
          return this.formatter.format(k);
        }
      }

      throw new AssertionError("Unreachable code");
    }

    @CheckForNull TestState lookup(
      final @Nonnull ClassTestPair cp)
    {
      return this.data_map.get(cp);
    }

    void testStateSet(
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
    private static final long              serialVersionUID =
                                                              432410053774535219L;
    private final @Nonnull TestsTableModel data;

    TestsTable(
      final @Nonnull TestsTableModel data)
    {
      super(data);
      this.data = data;
      this.getColumnModel().getColumn(0).setPreferredWidth(300);
      this.getColumnModel().getColumn(1).setPreferredWidth(100);
      this.getColumnModel().getColumn(2).setPreferredWidth(32);
      this.getColumnModel().getColumn(3).setPreferredWidth(32);
    }

    private static final @Nonnull Color color_failed;
    private static final @Nonnull Color color_skipped;
    private static final @Nonnull Color color_initialized;
    private static final @Nonnull Color color_succeeded;

    static {
      color_failed = new Color(1.0f, 0.8f, 0.8f);
      color_skipped = new Color(1.0f, 1.0f, 0.8f);
      color_initialized = new Color(1.0f, 1.0f, 1.0f);
      color_succeeded = new Color(0.8f, 1.0f, 0.8f);
    }

    @Override public @Nonnull Component prepareRenderer(
      final @Nonnull TableCellRenderer renderer,
      final int row,
      final int column)
    {
      final Component c = super.prepareRenderer(renderer, row, column);
      final ClassTestPair cp = this.data.getClassTestPairAt(row);
      final TestState state = this.data.lookup(cp);

      switch (state.getType()) {
        case STATE_FAILED:
        {
          c.setBackground(TestsTable.color_failed);
          break;
        }
        case STATE_INITIALIZED:
        {
          c.setBackground(TestsTable.color_initialized);
          break;
        }
        case STATE_SKIPPED:
        {
          c.setBackground(TestsTable.color_skipped);
          break;
        }
        case STATE_SUCCEEDED:
        {
          c.setBackground(TestsTable.color_succeeded);
          break;
        }
      }

      return c;
    }
  }

  private final @Nonnull TestsTableModel                       table_data;
  private final @Nonnull TestsTable                            table;
  private final @Nonnull JScrollPane                           table_pane;
  private final @Nonnull ButtonRun                             button_run;
  private final @Nonnull ButtonSaveReport                      button_save;
  private final @Nonnull GUIProjectInfo                        info;
  private final @Nonnull Executor                              executor;
  private final @Nonnull AtomicReference<TestCollectionRunner> runner;
  private final @Nonnull RunnerListener                        listener;
  private final @Nonnull TestReportConfig                      xml_config;
  private final @Nonnull GUILogger                             logger;

  private static class RunnerListener implements TestStateListener
  {
    private final @Nonnull TestsTableModel  table_data;
    private final @Nonnull GUILogger        logger;
    private long                            test_total      = 0;
    private final @Nonnull ButtonRun        button_run;
    private final @Nonnull ButtonSaveReport button_save;

    private long                            tests_run       = 0;
    private long                            tests_succeeded = 0;
    private long                            tests_skipped   = 0;
    private long                            tests_failed    = 0;

    public RunnerListener(
      final @Nonnull TestsTableModel table_data,
      final @Nonnull GUILogger logger,
      final @Nonnull ButtonRun button_run,
      final @Nonnull ButtonSaveReport button_save)
    {
      this.table_data = table_data;
      this.logger = logger;
      this.button_run = button_run;
      this.button_save = button_save;
    }

    @Override public void testStateRunStarted(
      final long count)
    {
      SwingUtilities.invokeLater(new Runnable() {
        @SuppressWarnings("synthetic-access") @Override public void run()
        {
          RunnerListener.this.test_total = count;
          RunnerListener.this.logger.write("Running tests...");
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
          switch (state.getType()) {
            case STATE_INITIALIZED:
              break;
            case STATE_FAILED:
              ++RunnerListener.this.tests_failed;
              ++RunnerListener.this.tests_run;
              break;
            case STATE_SKIPPED:
              ++RunnerListener.this.tests_skipped;
              ++RunnerListener.this.tests_run;
              break;
            case STATE_SUCCEEDED:
              ++RunnerListener.this.tests_succeeded;
              ++RunnerListener.this.tests_run;
              break;
          }

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
          final StringBuilder b = new StringBuilder();
          b.append("Test run completed ");
          b.append("(");
          b.append(RunnerListener.this.tests_run);
          b.append(" executed, ");
          b.append(RunnerListener.this.tests_succeeded);
          b.append(" succeeded, ");
          b.append(RunnerListener.this.tests_failed);
          b.append(" failed, ");
          b.append(RunnerListener.this.tests_skipped);
          b.append(" skipped)");

          RunnerListener.this.logger.write(b.toString());
          RunnerListener.this.button_save.setEnabled(true);
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

          RunnerListener.this.logger.write(buffer.toString());
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
    private long                                                 load_time =
                                                                             0;
    private final @Nonnull GUIProjectInfo                        info;
    private final @Nonnull GUILogger                             logger;
    private final @Nonnull AtomicReference<TestCollectionRunner> runner;
    private final @Nonnull RunnerListener                        listener;
    private final @Nonnull JButton                               button;

    public RunnerInitializationTask(
      final @Nonnull GUIProjectInfo info,
      final @Nonnull GUILogger logger,
      final @Nonnull AtomicReference<TestCollectionRunner> runner,
      final @Nonnull RunnerListener listener,
      final @Nonnull JButton button)
    {
      this.info = info;
      this.logger = logger;
      this.runner = runner;
      this.listener = listener;
      this.button = button;
    }

    private Set<Class<?>> getClasses()
    {
      final long time_before_load = System.nanoTime();
      final Set<Class<?>> classes = new HashSet<Class<?>>();

      for (final String prefix : this.info.getPackagePrefixes()) {
        try {
          final Set<Class<?>> cs = TestScanning.getPackageClasses(prefix);
          classes.addAll(cs);
        } catch (final Throwable e) {
          SwingUtilities.invokeLater(new Runnable() {
            @SuppressWarnings("synthetic-access") @Override public void run()
            {
              final StringBuilderOutputStream stream =
                new StringBuilderOutputStream();
              final PrintWriter pw = new PrintWriter(stream);

              pw.write("Error loading classes: ");
              pw.write(e.getMessage());
              pw.write("\n");
              e.printStackTrace(pw);
              pw.flush();

              RunnerInitializationTask.this.logger.write(stream
                .getBuffer()
                .toString());

              try {
                stream.close();
              } catch (final IOException _) {
                // Ignore
              }
            }
          });
        }
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
          RunnerInitializationTask.this.logger
            .write("Loading test classes...");
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

          RunnerInitializationTask.this.logger.write(buffer.toString());
          RunnerInitializationTask.this.button.setEnabled(true);
        }
      });

      /**
       * Initialize runner.
       */

      final TestCollectionRunner new_runner =
        new TestCollectionRunner(this.listener, classes);

      this.runner.set(new_runner);
    }
  }

  static class ButtonRun extends JButton
  {
    private static final long serialVersionUID = -4090482958181194934L;

    ButtonRun(
      final @Nonnull Executor executor,
      final @Nonnull AtomicReference<TestCollectionRunner> runner)
    {
      this.setText("Run");
      this.setToolTipText("Run all tests");
      this.addActionListener(new ActionListener() {
        @Override public void actionPerformed(
          final ActionEvent e)
        {
          final TestCollectionRunner r = runner.get();
          if (r != null) {
            executor.execute(r);
            ButtonRun.this.setEnabled(false);
          }
        }
      });
    }
  }

  static class XMLSaveTask implements Runnable
  {
    private final @Nonnull GUILogger logger;
    private final @Nonnull File      file;
    private final @Nonnull Element   xml_data;

    XMLSaveTask(
      final @Nonnull GUILogger logger,
      final @Nonnull File file,
      final @Nonnull Element xml_data)
    {
      this.logger = logger;
      this.file = file;
      this.xml_data = xml_data;
    }

    @Override public void run()
    {
      try {
        final Document document = new Document(this.xml_data);
        final FileOutputStream f_out = new FileOutputStream(this.file);
        final GZIPOutputStream z_out = new GZIPOutputStream(f_out);
        final Serializer serializer = new Serializer(z_out, "UTF-8");
        serializer.setIndent(2);
        serializer.setMaxLength(80);
        serializer.setLineSeparator("\n");
        serializer.write(document);
        z_out.flush();
        z_out.close();
        this.logger.write("Report saved to " + this.file);
      } catch (final FileNotFoundException e) {
        SwingUtilities.invokeLater(new Runnable() {
          @SuppressWarnings("synthetic-access") @Override public void run()
          {
            XMLSaveTask.this.logger.write("Error saving report: "
              + e.getMessage());
          }
        });
      } catch (final UnsupportedEncodingException e) {
        SwingUtilities.invokeLater(new Runnable() {
          @SuppressWarnings("synthetic-access") @Override public void run()
          {
            XMLSaveTask.this.logger.write("Error saving report: "
              + e.getMessage());
          }
        });
      } catch (final IOException e) {
        SwingUtilities.invokeLater(new Runnable() {
          @SuppressWarnings("synthetic-access") @Override public void run()
          {
            XMLSaveTask.this.logger.write("Error saving report: "
              + e.getMessage());
          }
        });
      }
    }
  }

  static class ButtonSaveReport extends JButton
  {
    private static final long serialVersionUID = 2332514499389125540L;

    ButtonSaveReport(
      final @Nonnull GUILogger logger,
      final @Nonnull TestReportConfig xml_config,
      final @Nonnull Executor executor,
      final @Nonnull AtomicReference<TestCollectionRunner> runner)
    {
      this.setText("Save report...");
      this
        .setToolTipText("Save a gzip-compressed XML report of the test results");
      this.addActionListener(new ActionListener() {
        @Override public void actionPerformed(
          final ActionEvent e)
        {
          final TestCollectionRunner r = runner.get();
          if (r != null) {
            executor.execute(new Runnable() {
              @Override public void run()
              {
                final Element report_xml = r.toXML(xml_config);
                SwingUtilities.invokeLater(new Runnable() {
                  @Override public void run()
                  {
                    final Calendar cal =
                      Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                    final SimpleDateFormat df =
                      new SimpleDateFormat("yyyy-MM-dd_hhmmss");
                    final StringBuilder name = new StringBuilder();
                    name.append("report-");
                    name.append(df.format(cal.getTime()));
                    name.append(".xml.gz");

                    final JFileChooser fc = new JFileChooser();
                    fc.setSelectedFile(new File(name.toString()));
                    final int result =
                      fc.showSaveDialog(ButtonSaveReport.this);

                    switch (result) {
                      case JFileChooser.APPROVE_OPTION:
                      {
                        executor.execute(new XMLSaveTask(logger, fc
                          .getSelectedFile(), report_xml));
                        break;
                      }
                      case JFileChooser.CANCEL_OPTION:
                      case JFileChooser.ERROR_OPTION:
                        break;
                    }
                  }
                });
              }
            });
          }
        }
      });
    }
  }

  GUITestsPanel(
    final @Nonnull GUIProjectInfo info,
    final @Nonnull TestReportConfig xml_config,
    final @Nonnull GUILogger logger)
  {
    this.info = info;
    this.logger = logger;
    this.executor = Executors.newCachedThreadPool();
    this.runner = new AtomicReference<TestCollectionRunner>();
    this.xml_config = xml_config;

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

    this.button_run = new ButtonRun(this.executor, this.runner);
    this.button_run.setEnabled(false);
    this.button_save =
      new ButtonSaveReport(
        this.logger,
        this.xml_config,
        this.executor,
        this.runner);
    this.button_save.setEnabled(false);

    final DesignGridLayout dg_layout = new DesignGridLayout(this);
    dg_layout.row().grid().add(this.table_pane);
    dg_layout.row().grid().add(this.button_run).add(this.button_save);

    this.listener =
      new RunnerListener(
        this.table_data,
        logger,
        this.button_run,
        this.button_save);

    this.executor.execute(new RunnerInitializationTask(
      info,
      this.logger,
      this.runner,
      this.listener,
      this.button_run));
  }
}
