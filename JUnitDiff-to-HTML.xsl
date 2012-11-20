<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0" 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
>
    <xsl:output method="xml" encoding="UTF-8" omit-xml-declaration="yes" indent="yes"/>

    <!-- HTML sauce. -->
    <xsl:template match="/">
       <html>
          <head>
              <title><xsl:value-of select="count(/testsuite/groups/group)"/> runs - JUnitDiff</title>
          </head>
          <body>
            <xsl:apply-templates select="@*|node()" />
          </body>
       </html>
    </xsl:template>




    <!-- Table. -->
    <xsl:template match="/testsuite">
        <table>
            <thead>
                <tr>
                    <xsl:apply-templates select="groups/group" />
                </tr>
            </thead>
            <tbody>
                <xsl:apply-templates select="@*|node()" />
            </tbody>
        </table>
    </xsl:template>

    <!-- Table. -->
    <xsl:template match="/testsuite/groups/group">
            <th>
                <xsl:value-of select="@name" />
            </th>
    </xsl:template>

    <!-- Test case. -->
    <xsl:template match="/testsuite/testcase">
        <tr>
            <td>
                <xsl:value-of select="concat(@classname,'.',@name" />
            </td>
            <xsl:apply-templates select="testrun" />
        </tr>
    </xsl:template>


    <!-- Test run. -->
    <xsl:template match="/testsuite/testcase/testrun">
            <td>
                <xsl:value-of select="concat(@result,' ',@time" />
                <xsl:apply-templates select="failure" />
            </td>
    </xsl:template>

</xsl:stylesheet>
