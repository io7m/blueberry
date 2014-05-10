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

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import com.io7m.jnull.Nullable;

/**
 * Information about the project.
 */

public final class GUIProjectInfo
{
  private final String            project_name;
  private @Nullable URI           project_uri;
  private final GUIProjectVersion project_version;
  private final Set<String>       project_package_prefixes;
  private @Nullable URI           project_icon;

  /**
   * Construct project info.
   * 
   * @param in_project_name
   *          The project name.
   * @param in_project_version
   *          The project version.
   */

  public GUIProjectInfo(
    final String in_project_name,
    final GUIProjectVersion in_project_version)
  {
    this.project_name = in_project_name;
    this.project_uri = null;
    this.project_version = in_project_version;
    this.project_package_prefixes = new HashSet<String>();
  }

  /**
   * Add a package prefix.
   * 
   * @param prefix
   *          The prefix.
   */

  public void addPackagePrefix(
    final String prefix)
  {
    this.project_package_prefixes.add(prefix);
  }

  /**
   * @return The list of package prefixes.
   */

  public Set<String> getPackagePrefixes()
  {
    return this.project_package_prefixes;
  }

  /**
   * @return The icon for the project, if any.
   */

  public @Nullable URI getProjectIcon()
  {
    return this.project_icon;
  }

  /**
   * @return The project name.
   */

  public String getProjectName()
  {
    return this.project_name;
  }

  /**
   * @return The URI for the project, if any.
   */

  public @Nullable URI getProjectURI()
  {
    return this.project_uri;
  }

  /**
   * @return The version for the project.
   */

  public GUIProjectVersion getProjectVersion()
  {
    return this.project_version;
  }

  /**
   * Set the project icon URI.
   * 
   * @param icon
   *          The icon.
   */

  public void setProjectIcon(
    final URI icon)
  {
    this.project_icon = icon;
  }

  /**
   * Set the project URI.
   * 
   * @param uri
   *          The URI.
   */

  public void setProjectURI(
    final URI uri)
  {
    this.project_uri = uri;
  }

  @Override public String toString()
  {
    final StringBuilder builder = new StringBuilder();
    builder.append(this.project_name);
    builder.append(" ");
    builder.append(this.project_version);

    final URI u = this.project_uri;
    if (u != null) {
      builder.append(" - ");
      builder.append(u.toString());
    }
    final String r = builder.toString();
    assert r != null;
    return r;
  }
}
