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
import java.util.HashSet;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

/**
 * Information about the project.
 */

public final class GUIProjectInfo
{
  private final @Nonnull String            project_name;
  private @CheckForNull URI                project_uri;
  private final @Nonnull GUIProjectVersion project_version;
  private final @Nonnull Set<String>       project_package_prefixes;
  private @CheckForNull URI                project_icon;

  public GUIProjectInfo(
    final @Nonnull String project_name,
    final @Nonnull GUIProjectVersion project_version)
  {
    this.project_name = project_name;
    this.project_uri = null;
    this.project_version = project_version;
    this.project_package_prefixes = new HashSet<String>();
  }

  public void addPackagePrefix(
    final @Nonnull String prefix)
  {
    this.project_package_prefixes.add(prefix);
  }

  public @Nonnull Set<String> getPackagePrefixes()
  {
    return this.project_package_prefixes;
  }

  public URI getProjectIcon()
  {
    return this.project_icon;
  }

  public @Nonnull String getProjectName()
  {
    return this.project_name;
  }

  public @CheckForNull URI getProjectURI()
  {
    return this.project_uri;
  }

  public @Nonnull GUIProjectVersion getProjectVersion()
  {
    return this.project_version;
  }

  public void setProjectIcon(
    final @Nonnull URI icon)
  {
    this.project_icon = icon;
  }

  public void setProjectURI(
    final @Nonnull URI uri)
  {
    this.project_uri = uri;
  }

  @Override public String toString()
  {
    final StringBuilder builder = new StringBuilder();
    builder.append(this.project_name);
    builder.append(" ");
    builder.append(this.project_version);
    if (this.project_uri != null) {
      builder.append(" - ");
      builder.append(this.project_uri.toString());
    }
    return builder.toString();
  }
}
