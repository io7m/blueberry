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

import com.io7m.jnull.Nullable;

/**
 * A project version number.
 */

public final class GUIProjectVersion
{
  private final int              major;
  private final int              minor;
  private final int              patch;
  private final @Nullable String qualifier;

  /**
   * Construct a project version.
   * 
   * @param in_major
   *          The major version.
   * @param in_minor
   *          The minor version.
   * @param in_patch
   *          The patch version.
   * @param in_qualifier
   *          The qualifier.
   */

  public GUIProjectVersion(
    final int in_major,
    final int in_minor,
    final int in_patch,
    final @Nullable String in_qualifier)
  {
    this.major = in_major;
    this.minor = in_minor;
    this.patch = in_patch;
    this.qualifier = in_qualifier;
  }

  /**
   * @return The major version.
   */

  public int getMajor()
  {
    return this.major;
  }

  /**
   * @return The minor version.
   */

  public int getMinor()
  {
    return this.minor;
  }

  /**
   * @return The patch version.
   */

  public int getPatch()
  {
    return this.patch;
  }

  /**
   * @return The qualifier, if any.
   */

  public @Nullable String getQualifier()
  {
    return this.qualifier;
  }

  @Override public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + this.major;
    result = (prime * result) + this.minor;
    result = (prime * result) + this.patch;

    final String q = this.qualifier;
    if (q != null) {
      result = (prime * result) + q.hashCode();
    }
    return result;
  }

  @Override public boolean equals(
    final @Nullable Object obj)
  {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (this.getClass() != obj.getClass()) {
      return false;
    }
    final GUIProjectVersion other = (GUIProjectVersion) obj;
    if (this.major != other.major) {
      return false;
    }
    if (this.minor != other.minor) {
      return false;
    }
    if (this.patch != other.patch) {
      return false;
    }

    final String q = this.qualifier;
    if (q != null) {
      return q.equals(other.qualifier);
    }

    return other.qualifier == null;
  }

  @Override public String toString()
  {
    final StringBuilder builder = new StringBuilder();
    builder.append(this.major);
    builder.append(".");
    builder.append(this.minor);
    builder.append(".");
    builder.append(this.patch);
    if (this.qualifier != null) {
      builder.append("-");
      builder.append(this.qualifier);
    }
    final String r = builder.toString();
    assert r != null;
    return r;
  }
}
