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
import java.net.MalformedURLException;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.java.dev.designgridlayout.DesignGridLayout;

/**
 * The project info panel.
 */

final class GUIInfoPanel extends JPanel
{
  private static final long             serialVersionUID;

  static {
    serialVersionUID = -2865822489490944908L;
  }

  private final @CheckForNull ImageIcon icon;
  private final @Nonnull JLabel         icon_label;
  private final @Nonnull JLabel         project_label;
  private final @Nonnull JLabel         project_uri_label;

  private static @CheckForNull ImageIcon makeIcon(
    final @Nonnull GUIProjectInfo info)
  {
    if (info.getProjectIcon() != null) {
      try {
        return new ImageIcon(info.getProjectIcon().toURL());
      } catch (final MalformedURLException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }

    return null;
  }

  GUIInfoPanel(
    final @Nonnull GUIProjectInfo info)
  {
    this.project_label =
      new JLabel(info.getProjectName() + " " + info.getProjectVersion());
    this.project_uri_label = new JLabel(info.getProjectURI().toString());

    this.icon_label = new JLabel();
    this.icon = GUIInfoPanel.makeIcon(info);
    if (this.icon != null) {
      this.icon_label.setIcon(this.icon);
    }

    final JPanel text_panel = new JPanel();
    final DesignGridLayout tp_layout = new DesignGridLayout(text_panel);
    tp_layout.row().grid().add(this.project_label);
    tp_layout.row().grid().add(this.project_uri_label);

    this.setLayout(new FlowLayout(FlowLayout.LEADING, 16, 16));
    this.add(this.icon_label);
    this.add(text_panel);
  }
}
