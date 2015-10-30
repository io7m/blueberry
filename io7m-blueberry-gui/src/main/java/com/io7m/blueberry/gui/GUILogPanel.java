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

import java.awt.Font;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

import net.java.dev.designgridlayout.DesignGridLayout;

/**
 * The project log panel.
 */

final class GUILogPanel extends JPanel
{
  private static final long serialVersionUID;

  static {
    serialVersionUID = -893271289371892391L;
  }

  private final JTextArea   text_area;
  private final JScrollPane scrollpane;

  GUILogPanel()
  {
    this.text_area = new JTextArea();
    this.text_area.setEditable(false);
    this.text_area.setFont(Font.decode(Font.MONOSPACED + " 9"));
    this.scrollpane = new JScrollPane(this.text_area);
    this.scrollpane
      .setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

    final DesignGridLayout tp_layout = new DesignGridLayout(this);
    tp_layout.row().grid().add(this.scrollpane);

    this.scrollpane.setPreferredSize(this.getSize());
  }

  void write(
    final String message)
  {
    this.text_area.append(message + "\n");
  }
}
