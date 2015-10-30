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

package com.io7m.blueberry;

/**
 * XML report configuration.
 */

public final class TestReportConfig
{
  private boolean output_environment       = true;
  private boolean output_system_properties = true;
  private String package_name;
  private String package_version;

  /**
   * Construct a new empty configuration.
   *
   * @param in_package_name    The package name
   * @param in_package_version The package version
   */

  public TestReportConfig(
    final String in_package_name,
    final String in_package_version)
  {
    this.package_name = in_package_name;
    this.package_version = in_package_version;
  }

  /**
   * Set whether or not the contents of the JVM's environment should appear in
   * the resulting report.
   *
   * @param output <code>true</code> if the JVM's environment should appear in
   *               reports.
   */

  public void setOutputEnvironment(
    final boolean output)
  {
    this.output_environment = output;
  }

  /**
   * Set whether or not the contents of the JVM's system properties should
   * appear in the resulting report.
   *
   * @param output <code>true</code> if the JVM's system properties should
   *               appear in reports.
   */

  public void setOutputSystemProperties(
    final boolean output)
  {
    this.output_system_properties = output;
  }

  /**
   * @return <code>true</code> if the report will contain the contents of the
   * JVM's environment.
   *
   * @see #setOutputEnvironment(boolean)
   */

  public boolean wantOutputEnvironment()
  {
    return this.output_environment;
  }

  /**
   * @return <code>true</code> if the report will contain the contents of the
   * JVM's system properties.
   *
   * @see #setOutputSystemProperties(boolean)
   */

  public boolean wantOutputSystemProperties()
  {
    return this.output_system_properties;
  }

  /**
   * @return The package name
   */

  public String getPackageName()
  {
    return this.package_name;
  }

  /**
   * Set the package name.
   *
   * @param in_name The package name
   */

  public void setPackageName(final String in_name)
  {
    this.package_name = in_name;
  }

  /**
   * @return The package version
   */

  public String getPackageVersion()
  {
    return this.package_version;
  }

  /**
   * Set the package version.
   *
   * @param in_package_version The package version
   */

  public void setPackageVersion(
    final String in_package_version)
  {
    this.package_version = in_package_version;
  }
}
