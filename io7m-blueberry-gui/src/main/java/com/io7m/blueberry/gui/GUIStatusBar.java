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

import java.awt.FlowLayout;

import javax.annotation.Nonnull;
import javax.swing.JLabel;
import javax.swing.JPanel;

final class GUIStatusBar extends JPanel
{
  private static final long     serialVersionUID = 624901598432848882L;
  private final @Nonnull JLabel text;

  GUIStatusBar()
  {
    this.text = new JLabel("Initialized");
    this.text.setVisible(true);
    this.setLayout(new FlowLayout(FlowLayout.LEADING));
    this.add(this.text);
  }

  void setStatus(
    final @Nonnull String new_text)
  {
    this.text.setText(new_text);
  }
}
