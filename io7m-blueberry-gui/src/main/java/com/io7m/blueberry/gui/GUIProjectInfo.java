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

import java.net.URI;
import java.util.Set;

import javax.annotation.Nonnull;

public final class GUIProjectInfo
{
  private final @Nonnull String      project_name;
  private final @Nonnull URI         project_uri;
  private final @Nonnull String      project_version;
  private final @Nonnull Set<String> project_package_prefixes;

  public GUIProjectInfo(
    final @Nonnull String project_name,
    final @Nonnull URI project_uri,
    final @Nonnull String project_version,
    final @Nonnull Set<String> project_package_prefixes)
  {
    this.project_name = project_name;
    this.project_uri = project_uri;
    this.project_version = project_version;
    this.project_package_prefixes = project_package_prefixes;
  }

  @Override public String toString()
  {
    final StringBuilder builder = new StringBuilder();
    builder.append(this.project_name);
    builder.append(" ");
    builder.append(this.project_version);
    builder.append(" - ");
    builder.append(this.project_uri.toString());
    return builder.toString();
  }

  public Set<String> getPackagePrefixes()
  {
    return this.project_package_prefixes;
  }
}
