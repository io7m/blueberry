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

package com.io7m.blueberry.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import com.io7m.blueberry.TestReportConfig;

/**
 * The main test window.
 */

final class GUIMainWindow extends JFrame
{
  private final JTabbedPane   tabs;
  private final GUITestsPanel tests_tab;
  private final GUIStatusBar  status_bar;
  private final GUIInfoPanel  info_tab;
  private final GUILogPanel   log_tab;
  private final GUILogger     logger;
  private static final long   serialVersionUID;

  static {
    serialVersionUID = 2766673586988551555L;
  }

  GUIMainWindow(
    final GUIProjectInfo info,
    final TestReportConfig xml_config)
  {
    final StringBuilder title_buffer = new StringBuilder();
    title_buffer.append(info.toString());

    this.setTitle(title_buffer.toString());
    this.setLayout(new BorderLayout());
    this.setMinimumSize(new Dimension(640, 480));

    this.status_bar = new GUIStatusBar();
    this.log_tab = new GUILogPanel();
    this.logger = new GUILogger(this.status_bar, this.log_tab);
    this.tests_tab = new GUITestsPanel(info, xml_config, this.logger);
    this.info_tab = new GUIInfoPanel(info);

    this.tabs = new JTabbedPane();
    this.tabs.add("Tests", this.tests_tab);
    this.tabs.add("Log", this.log_tab);
    this.tabs.add("Info", this.info_tab);

    this.add(this.tabs, BorderLayout.CENTER);
    this.add(this.status_bar, BorderLayout.PAGE_END);
    this.pack();
  }
}
