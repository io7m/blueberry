<xt:stylesheet version="2.0"
  xmlns="http://www.w3.org/1999/xhtml"
  xmlns:xt="http://www.w3.org/1999/XSL/Transform"
  xmlns:b="http://schemas.io7m.com/blueberry/1.0.0">

  <xt:output encoding="UTF-8" method="text" indent="yes"/>

  <xt:template match="b:trace">
    <xt:value-of select="concat(b:trace-file, ':', b:trace-line, ' ', b:trace-class, '.', b:trace-method)"/>
    <xt:text>&#xa;</xt:text>
  </xt:template>

  <xt:template match="b:message">
    <xt:value-of select="."/>
  </xt:template>

  <xt:template match="b:exception">
    <xt:value-of select="@type"/>
    <xt:text>("</xt:text>
    <xt:apply-templates select="b:message"/>
    <xt:text>")&#xa;</xt:text>
    <xt:apply-templates select="b:trace"/>
  </xt:template>

  <xt:template match="b:exceptions">
    <xt:apply-templates select="b:exception"/>
  </xt:template>

  <xt:template match="b:test-failed">
    <xt:value-of select="concat(../@name, '.', @name)"/>
    <xt:text>&#xa;</xt:text>
    <xt:text>Exception trace: &#xa;</xt:text>
    <xt:text>&#xa;</xt:text>
    <xt:apply-templates select="b:exceptions"/>
    <xt:text>------------------------------------------------------------------------&#xa;&#xa;</xt:text>
  </xt:template>

  <xt:template match="b:class">
    <xt:apply-templates select="b:test-failed"/>
  </xt:template>

  <xt:template match="b:classes">
    <xt:apply-templates select="b:class"/>
  </xt:template>

  <xt:template match="b:report">
    <xt:apply-templates select="b:classes"/>
  </xt:template>

  <xt:template match="/">
    <xt:text>FAILURE REPORT&#xa;</xt:text>
    <xt:text>------------------------------------------------------------------------&#xa;&#xa;</xt:text>
    <xt:apply-templates select="b:report"/>
  </xt:template>

  <!--
  <xt:template match="*"/>
  -->

</xt:stylesheet>
