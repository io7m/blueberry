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

package com.io7m.blueberry.gui_example;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;

import javax.swing.SwingUtilities;

import com.io7m.blueberry.gui.GUI;
import com.io7m.blueberry.gui.GUIProjectInfo;

public final class GUIExample
{
  public static void main(
    final String[] args)
    throws URISyntaxException
  {
    final HashSet<String> packages = new HashSet<String>();
    packages.add("com.io7m.blueberry");

    final GUIProjectInfo info =
      new GUIProjectInfo("blueberry-example", new URI(
        "http://io7m.com/software/blueberry"), "0.1.0", packages);

    SwingUtilities.invokeLater(new Runnable() {
      @SuppressWarnings("unused") @Override public void run()
      {
        new GUI(info);
      }
    });
  }
}
