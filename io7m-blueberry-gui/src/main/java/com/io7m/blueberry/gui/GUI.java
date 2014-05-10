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

import javax.swing.JFrame;

import com.io7m.blueberry.TestReportConfig;

/**
 * The main Swing GUI structure.
 */

public final class GUI
{
  private final GUIMainWindow    main_window;
  private final GUIProjectInfo   info;
  private final TestReportConfig xml_config;

  /**
   * Construct a new GUI.
   * 
   * @param in_info
   *          The project info.
   * @param in_config
   *          The configuration for the window.
   */

  public GUI(
    final GUIProjectInfo in_info,
    final TestReportConfig in_config)
  {
    this.info = in_info;
    this.xml_config = in_config;
    this.main_window = new GUIMainWindow(in_info, in_config);
    this.main_window.setVisible(true);
  }

  /**
   * @return The main window.
   */

  public JFrame getMainWindow()
  {
    return this.main_window;
  }
}
