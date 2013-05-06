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

import javax.annotation.Nonnull;
import javax.swing.JFrame;

import com.io7m.blueberry.TestReportConfig;

/**
 * The main Swing GUI structure.
 */

public final class GUI
{
  private final @Nonnull GUIMainWindow    main_window;
  private final @Nonnull GUIProjectInfo   info;
  private final @Nonnull TestReportConfig xml_config;

  public GUI(
    final @Nonnull GUIProjectInfo info,
    final @Nonnull TestReportConfig xml_config)
  {
    this.info = info;
    this.xml_config = xml_config;
    this.main_window = new GUIMainWindow(info, xml_config);
    this.main_window.setVisible(true);
  }

  public @Nonnull JFrame getMainWindow()
  {
    return this.main_window;
  }
}
