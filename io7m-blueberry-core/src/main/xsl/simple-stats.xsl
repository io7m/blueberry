<xt:stylesheet version="2.0"
  xmlns="http://www.w3.org/1999/xhtml"
  xmlns:xt="http://www.w3.org/1999/XSL/Transform"
  xmlns:b="http://www.io7m.com/software/blueberry/0.1.0">

  <xt:output encoding="UTF-8" method="text" indent="yes"/>

  <xt:template match="/">
    <xt:text>Succeeded : </xt:text>
    <xt:value-of select="count(//b:test-succeeded)"/>
    <xt:text>&#xa;</xt:text>

    <xt:text>Failed    : </xt:text>
    <xt:value-of select="count(//b:test-failed)"/>
    <xt:text>&#xa;</xt:text>

    <xt:text>Skipped   : </xt:text>
    <xt:value-of select="count(//b:test-skipped)"/>
    <xt:text>&#xa;</xt:text>

    <xt:text>Total     : </xt:text>
    <xt:value-of select="count(//b:test-succeeded) + count(//b:test-failed) + count(//b:test-skipped)"/>
    <xt:text>&#xa;</xt:text>
  </xt:template>

</xt:stylesheet>
