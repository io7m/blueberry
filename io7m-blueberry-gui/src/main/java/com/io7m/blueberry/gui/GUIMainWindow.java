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

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.annotation.Nonnull;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;

public final class GUIMainWindow extends JFrame
{
  private final @Nonnull JTabbedPane   tabs;
  private final @Nonnull GUITestsPanel tests_tab;
  private final @Nonnull GUIStatusBar  status_bar;
  private static final long            serialVersionUID;

  static {
    serialVersionUID = 2766673586988551555L;
  }

  public GUIMainWindow(
    final @Nonnull GUIProjectInfo info)
  {
    final StringBuilder title_buffer = new StringBuilder();
    title_buffer.append(info.toString());

    this.setTitle(title_buffer.toString());
    this.setLayout(new BorderLayout());
    this.setMinimumSize(new Dimension(640, 480));

    this.status_bar = new GUIStatusBar();
    this.tests_tab = new GUITestsPanel(info, this.status_bar);

    this.tabs = new JTabbedPane();
    this.tabs.add("Tests", this.tests_tab);

    this.add(this.tabs, BorderLayout.CENTER);
    this.add(this.status_bar, BorderLayout.PAGE_END);
    this.pack();
  }
}
