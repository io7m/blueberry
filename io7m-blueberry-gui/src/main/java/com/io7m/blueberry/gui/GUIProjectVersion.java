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

import javax.annotation.CheckForNull;

/**
 * A project version number.
 */

public final class GUIProjectVersion
{
  private final int                  major;
  private final int                  minor;
  private final int                  patch;
  private final @CheckForNull String qualifier;

  public GUIProjectVersion(
    final int major,
    final int minor,
    final int patch,
    final @CheckForNull String qualifier)
  {
    this.major = major;
    this.minor = minor;
    this.patch = patch;
    this.qualifier = qualifier;
  }

  public int getMajor()
  {
    return this.major;
  }

  public int getMinor()
  {
    return this.minor;
  }

  public int getPatch()
  {
    return this.patch;
  }

  public @CheckForNull String getQualifier()
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
    result =
      (prime * result)
        + ((this.qualifier == null) ? 0 : this.qualifier.hashCode());
    return result;
  }

  @Override public boolean equals(
    final Object obj)
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
    if (this.qualifier == null) {
      if (other.qualifier != null) {
        return false;
      }
    } else if (!this.qualifier.equals(other.qualifier)) {
      return false;
    }
    return true;
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
    return builder.toString();
  }
}
