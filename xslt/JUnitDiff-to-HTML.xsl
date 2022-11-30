<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="html" encoding="UTF-8" omit-xml-declaration="yes" indent="yes" doctype-public="html"/>

    <xsl:key name="isOkRow" match="/aggregate/testcase/testrun[@result != 'OK']" use="@group"/>
	<xsl:variable name="groupCount" select="count(/aggregate/groups/group)"/>

    <xsl:param name="title" select="''"/>
    <xsl:variable name="titleToUse">
        <xsl:if test=" $title  = '' ">JUnitDiff - JUnit test reports aggregator</xsl:if>
        <xsl:if test=" $title != '' ">
            <xsl:value-of select="$title"/> (JUnitDiff report)
        </xsl:if>
    </xsl:variable>

    <!-- HTML sauce. -->
    <xsl:template match="/">
       <!-- <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"> -->
       <html lang="en-US" xml:lang="en-US" xmlns="http://www.w3.org/1999/xhtml">
          <head>
              <meta http-equiv="content-type" content="text/html; charset=utf-8" />
              <meta charset="utf-8" />
              <meta name="viewport" content="width=device-width, initial-scale=1"/>

              <title><xsl:value-of select="count(/aggregate/groups/group)"/> runs - JUnitDiff</title>
              <script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"><xsl:comment> </xsl:comment></script>
              <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-EVSTQN3/azprG1Anm3QDgpJLIm9Nao0Yz1ztcQTwFspd3yD65VohhpuuCOmLASjC" crossorigin="anonymous"><xsl:comment> </xsl:comment></link>
              <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js" integrity="sha384-MrcW6ZMFYlzcLA8Nl+NtUVF0sA7MsXsP1UyJoMp4YLEuNSfAP+JcXn/tWtIaxVXM" crossorigin="anonymous"><xsl:comment> </xsl:comment></script>

              <link rel="shortcut icon" href="http://static.jquery.com/favicon.ico" type="image/x-icon"/>
              <style type="text/css" id="style">
                  * { font-family: Verdana; }
                  h1 { font-size: 20pt; }
                  h2 { font-size: 14pt; }

                  div.groups div.group span.stats { font-size: 75%; }
                  div.groups div.group span.name { margin: 0 1ex; }
                  div.groups div.group span.path { font-size: 75%; }
                  div.groups div.group span.index { display: none; }

                  table.results { border-collapse:collapse; }
                  table.results th, td { padding: 0.1ex 0.2ex; }
                  table.results th { font-size: 65%; }
                  table.results th.okRun { background-color: #A4F294; }
                  table.results td { white-space: nowrap; font-size: 95%; }
                  table.results td a { cursor: pointer; }
                  table.results td.result_OK a,      table.results tr.header span.OK      { color: green; }
                  table.results td.result_FAIL a,    table.results tr.header span.FAIL    { color: red; }
                  table.results td.result_ERROR a,   table.results tr.header span.ERROR   { color: orange; }
                  table.results td.result_SKIPPED a, table.results tr.header span.SKIPPED { color: blue }

                  table.results tr       td { border-top: 1px dotted silver; }
                  table.results tr.first td { border-top: 1px solid gray;   padding-top: 1ex; }

                  table.results .testcase .known     { font-size: 65%; margin-right: 1ex; }
                  table.results .testcase .classname { font-size: 65%; margin-right: 1ex; }
                  table.results .run .time       { font-size: 65%; margin-right: 1ex; font-weight: normal; font-style: normal; }

                  table.results tbody.hideOkTests tr.okTest { display: none; }
                  table.results tbody.hideNodiffTests tr.nodiffTest { display: none; }
                  table.hideOkRuns  td.or     { display: none; }
                  table.hideOkRuns  th.okRun     { display: none; }

                  /* Popup for the test run details. */
                  .popup               {
                    position: absolute; top: 20px; left: 20px;
                    min-width: 600px; min-height: 200px;
                    border: 1px solid green;
                    background-color: white;
                    padding: 1ex 1ex;
                  }
                  .popup.hidden        { display: none; }
                  .data.hidden         { display: none; }     /* Bottom of the page. */
                  .failure.hidden         { display: none; }  /* Inside test run cells. */

                  .run.popup .label { font-weight: bold; }
                  .run.popup .text {
                    white-space: pre; font-size: 75%; font-family: Courier New, monospaced;
                  }
                  .run.popup .testsuite .text {
                    padding: 1ex 1ex;
                    margin: 0.3ex 0;
                  }
                  .run.popup .failure       .trace { border-left: 2px solid orange; }
                  .run.popup .testsuite.out .text { border-left: 2px solid green; }
                  .run.popup .testsuite.err .text { border-left: 2px solid red; }
              </style>
              <script type="text/javascript" src="functions.js"/>
          </head>
          <body>
            <xsl:apply-templates select="/aggregate" />

            <!-- Hidden popup content -->
                <div class="run popup hidden" id="popup-div">
                  <div class="result"><span class="label">Result: </span> <span id="popup-result">Still loading...</span></div>
                  <div class="origin"><span class="label">Origin: </span> <span class="text">Still loading...</span></div>
                  <!-- <xsl:apply-templates select="failure" mode="content"/> -->
                  <div class="failure"      ><span class="label">Failure:</span> <div class="text">Still loading...</div></div>
                  <div class="testsuite out"><span class="label">Stdout:</span> <div class="text">Still loading...</div></div>
                  <div class="testsuite err"><span class="label">Stderr:</span> <div class="text">Still loading...</div></div>
                </div>

            <!-- TODO: Hidden popup content - testsuites' stdout, strerr - TODO -->
            <xsl:apply-templates mode="content" select="/aggregate/testsuites/testsuite" />

          </body>
       </html>
    </xsl:template>



    <!-- Table header. -->
    <xsl:template match="/aggregate" mode="table-header">
                <tr class="header">
                    <th></th>
                    <xsl:for-each select="groups/group">
                        <th>
                            <xsl:attribute name="class">
                                <xsl:if test="not( /aggregate/testcase/testrun[ @group = current()/@id and @result != 'OK' ] )">okRun </xsl:if>
                            </xsl:attribute>
                            <xsl:value-of select="substring(@name, string-length(@name) - 15)" /><br/>
                            <span class="OK">
                                <xsl:value-of select="count(/aggregate/testcase/testrun[ @group = current()/@id and @result = 'OK' ])" />
                            </span> /
                            <span class="FAIL">
                                <xsl:value-of select="count(/aggregate/testcase/testrun[ @group = current()/@id and @result = 'FAIL' ])" />
                            </span> /
                            <span class="ERROR">
                                <xsl:value-of select="count(/aggregate/testcase/testrun[ @group = current()/@id and @result = 'ERROR' ])" />
                            </span> /
                            <span class="SKIPPED">
                                <xsl:value-of select="count(/aggregate/testcase/testrun[ @group = current()/@id and @result = 'SKIPPED' ])" />
                            </span>
                        </th>
                    </xsl:for-each>
                </tr>
    </xsl:template>

    <xsl:key name="groupsByPath" match="/aggregate/groups/group" use="@path"/>

    <!-- Content and table. -->
    <xsl:template match="/aggregate">

        <h1>🍡 <xsl:value-of select="$titleToUse"/></h1>

        <h2>Runs:</h2>
        <div class="groups" id="groups">
            <xsl:for-each select="groups/group">
                <div class="group g01 this-will-be-replaced">
                    <xsl:attribute name="class">group g<xsl:number format="01" value="position()"/></xsl:attribute>
                    <xsl:variable name="position" select="position()"/>
                    <span class="stats">
                        (<span class="tests"><xsl:value-of select="count(/aggregate/testcase/testrun[@group = $position])" /> tests</span>)
                    </span>
                    <span class="name">🎢 Test run "<xsl:value-of select="@name" />"</span>
                    <span class="path">from 📂 <a>
                        <xsl:attribute name="href">file://<xsl:value-of select="@path" /></xsl:attribute>
                        <xsl:value-of select="@path" /></a></span>
                    <span class="index"><xsl:value-of select="position()" /></span>
                </div>
            </xsl:for-each>
        </div>

        <h2>Tests \ test runs:</h2>

        <div><input type="checkbox" id="cbShowOnlyNonOkTests" onchange="toggleShowOnlyNonOkTests(this.checked);"/> <label for="cbShowOnlyNonOkTests"> ⛔ Show only non-OK tests (rows)</label></div>
        <div><input type="checkbox" id="cbShowOnlyDiffTests" onchange="toggleShowOnlyDiffTests(this.checked);"/> <label for="cbShowOnlyDiffTests"> 🧮 Show only tests with differing results (rows) - also counts missing vs. present.</label></div>
        <div><input type="checkbox" id="cbShowOnlyNonOkRuns"  onchange="toggleShowOnlyNonOkRuns (this.checked);"/> <label for="cbShowOnlyNonOkRuns"> 🚧 Show only non-OK runs (columns)</label></div>

        <table class="results" id="results-table">
            <thead>
                <xsl:apply-templates select="." mode="table-header" />
            </thead>
            <tfoot>
                <xsl:apply-templates select="." mode="table-header" />
            </tfoot>
            <tbody> <!-- class="hideOkTests" -->
                <xsl:apply-templates select="testcase" />
            </tbody>
        </table>

        <p class="footer">Created by <a href="${project.scm.url}">JUnitDiff</a> ${project.version}.</p>
        <div class="footer">TODO:
          <ul> <li> Known issues - the service is not ready yet.
          </li><li> Links to Jira's in popup.
          </li><li> Links to test source if -srcUrl is provided.
          </li><li> Fix popup to appear to the scrolled view, or change it to a browser popup window (optionally).
          </li></ul>
        </div>
    </xsl:template>



    <!-- Test case (table row). -->
    <xsl:template match="/aggregate/testcase">
        <tr class="testcase">
			<xsl:variable name="first_run_result" select="./testrun[1]/@result"/>
            <xsl:attribute name="class">
                <xsl:text>testcase</xsl:text>
                <xsl:if test="preceding-sibling::testcase[1]/@classname != @classname"> first</xsl:if>
                <xsl:if test="not( testrun[@result != 'OK'] )"> okTest</xsl:if>
                <xsl:if test="not( testrun[@result != $first_run_result] ) and count(./testrun) = $groupCount"> nodiffTest</xsl:if>
            </xsl:attribute>
            <!--<xsl:if test="preceding-sibling::testcase[1]/@classname != @classname"><xsl:attribute name="class">testcase first</xsl:attribute></xsl:if>-->
          
            <!-- replace() does not work?? -->
            <!--<xsl:value-of select="concat(@classname,'.',@name)" />-->
            <!-- replace(@name,&quot;'&quot;,&quot;\'&quot;) 
                 replace(@name,'A','B')
                 replace(string(@name),"&apos;","\&apos;")           -->
            <xsl:variable name="nameEscaped">
              <xsl:call-template name="escapeJavaScriptString">
                <xsl:with-param name="stringIn" select="string(concat(@classname,'.',@name))"/>
              </xsl:call-template>
            </xsl:variable>
          
            <!-- TestCase name -->
            <td class="name">
                <!-- kiA() does document.write(...) which creates <a href="javascript:...">Known issues</a>. Takes values from <td>'s content. -->
                <script>kiA()</script>
                <span class="classname"><xsl:value-of select="@classname" /></span>.<xsl:value-of select="@name" />
            </td>


            <!-- Test runs -->
            <xsl:variable name="testcase" select="." />
            <xsl:variable name="testClassName" select="@classname" />
            <xsl:variable name="testMethodName" select="@name" />
            <!-- Remember! One byte here may mean ~40 kB in the result file!! -->
            <xsl:for-each select="/aggregate/groups/group">
                <xsl:variable name="groupid" select="@id" />
                <xsl:variable name="curTestRun" select="$testcase/testrun[@group=$groupid]" />

                <td class="run">
                  <xsl:attribute name="class">
                      <xsl:text>run</xsl:text>
                      <xsl:if test="not( key('isOkRow',@id) )"> or</xsl:if>
                      <xsl:text> result_</xsl:text><xsl:value-of select="$curTestRun/@result"/>
				  </xsl:attribute>

                    <xsl:apply-templates  mode="link" select="$testcase/testrun[@group=$groupid]">
                      <xsl:with-param name="testCaseFullNameEscaped" select="string($nameEscaped)"/>
                      <xsl:with-param name="testClassName" select="$testClassName"/>  <!-- string($testcase/@classname) doesn't work -->
                      <xsl:with-param name="testMethodName" select="$testMethodName"/>  <!-- string($testcase/@classname) doesn't work -->
                    </xsl:apply-templates>

                    <!-- Failure message and stacktrace. -->
                    <xsl:apply-templates select="failure" mode="content"/>
                </td>
            </xsl:for-each>

        </tr>
    </xsl:template>


    <!-- Test run - link. -->
    <!-- Remember! One byte here may mean ~40 kB in the result file!! -->
    <xsl:template name="testrun" mode="link" match="/aggregate/testcase/testrun">
        <xsl:param name="testCaseFullNameEscaped"/>
        <xsl:param name="testClassName"/>
        <xsl:param name="testMethodName"/>

        <xsl:variable name="groupNameEscaped">
          <xsl:call-template name="escapeXmlIdString">
            <xsl:with-param name="stringIn" select="string(@group)"/>
          </xsl:call-template>
        </xsl:variable>

        <xsl:variable name="resultId_cssIdEscaped">
          <xsl:call-template name="escapeCssId">
            <xsl:with-param name="stringIn" select="concat($testCaseFullNameEscaped,'_',$groupNameEscaped)"/>
          </xsl:call-template>
        </xsl:variable>

        <!-- Modal test -->

        <a data-bs-toggle="modal" data-bs-target="#modal-{$resultId_cssIdEscaped}">x</a>
        <div class="modal fade" id="modal-{$resultId_cssIdEscaped}" tabindex="-1" aria-labelledby="modal-{$resultId_cssIdEscaped}-Label" style="display: none;" aria-hidden="true">
            <div class="modal-dialog modal-fullscreen">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title h4" id="modal-{$resultId_cssIdEscaped}-Label">Full screen modal</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">Here would go the logs.</div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close (or press <code>Esc</code>)</button>
                    </div>
                </div>
            </div>
        </div>

        <a>
              <!--
              <xsl:attribute name="href">javascript:out('<xsl:value-of
                select="concat($testCaseFullNameEscaped,'|',$groupNameEscaped)"/>', '<xsl:value-of
                select="concat($testClassName,'|',$groupNameEscaped)"/>', '<xsl:value-of select="@result"/>');</xsl:attribute>
              <xsl:attribute name="href">javascript:out('<xsl:value-of
                select="concat($testClassName)"/>','<xsl:value-of
                select="concat($testMethodName)"/>','<xsl:value-of
                select="concat($groupNameEscaped)"/>','<xsl:value-of
                select="@result"/>');</xsl:attribute>
              -->
              <!-- TODO: Replace sGroup with group index (will significantly reduce output size). -->
              <xsl:attribute name="onclick">out2(this,<xsl:value-of
                select="count(/aggregate/groups/group[@path=current()/@group]/preceding-sibling::*)+1"/>);</xsl:attribute>
                <!-- concat($groupNameEscaped) -->
              <xsl:value-of select="@result"/>
        </a>
        <xsl:if test="@result = 'FAIL'">
            <xsl:text> </xsl:text>
            <a class="known" href="#" onclick="kb_ki(event.target)">kn.is.</a>
        </xsl:if>
        <i class="time"><xsl:value-of select="@time"/></i>
        <xsl:apply-templates select="failure" mode="content"/>
    </xsl:template>

    
    <!-- Test run popup - failure. -->
    <xsl:template name="failure-content" mode="content" match="/aggregate/testcase/testrun/failure">
          <!-- TODO: To reduce size, only put here DIVs with -->
          <div class="failure hidden">
            <div class="type">    <span class="label">Failure: </span> <span class="text"><xsl:value-of select="@type"/></span> </div>
            <div class="message"> <span class="label">Message: </span>
                <a class="text" href="#" onclick="jira(this)"><xsl:value-of select="@message"/></a>
                <!--Jira:
                    <a class="text" href="#" onclick="hbn(this.parentElement)">hbn</a>
                    <a class="text" href="#" onclick="jira(this.parentElement)">jboss</a>
                 -->
            </div>
            <div class="trace text"><xsl:value-of select="."/></div>
          </div>
    </xsl:template>
    

    
    <!-- Test suite - data. All that is common to whole testsuite. -->
    <!-- This is a storage, JavaScript will take data from here. -->
    <xsl:template name="testsuite-content" mode="content" match="/aggregate/testsuites/testsuite">

        <!-- TODO: Change group references to indexes everywhere. -->
        <xsl:variable name="testSuiteNameEscaped">
          <xsl:call-template name="escapeXmlIdString">
            <xsl:with-param name="stringIn" select="concat(@name,'|',@group)"/>
          </xsl:call-template>
        </xsl:variable>

        <div class="testsuite data hidden">
          <xsl:attribute name="id"><xsl:value-of select="$testSuiteNameEscaped"/></xsl:attribute>
          <div class="origin"><xsl:value-of select="@origin"/></div>
          <div class="out"><xsl:value-of select="system-out"/></div>
          <div class="err"><xsl:value-of select="system-err"/></div>
        </div>
    </xsl:template>
    
    
    
    
    
    
    
    

    <!-- XML ID escape -->
    <xsl:template name="escapeXmlIdString">
        <xsl:param name="stringIn"/>
        
        <xsl:variable name="step1">
          <xsl:call-template name="replaceCharsInString">
            <xsl:with-param name="stringIn" select="string($stringIn)"/>
            <xsl:with-param name="charsIn" select="'&quot;'"/>
            <xsl:with-param name="charsOut" select="'-'"/> <!-- select="'\&quot;'" -->
          </xsl:call-template>
        </xsl:variable>
        
        <xsl:call-template name="replaceCharsInString">
          <xsl:with-param name="stringIn" select="string($step1)"/>
          <xsl:with-param name="charsIn" select="'\'"/>
          <xsl:with-param name="charsOut" select="'-'"/>
        </xsl:call-template>
    </xsl:template>
    

    <!-- JavaScript escape -->
    <xsl:template name="escapeJavaScriptString">
        <xsl:param name="stringIn"/>
        
        <xsl:variable name="step1">
          <xsl:call-template name="replaceCharsInString">
            <xsl:with-param name="stringIn" select="string($stringIn)"/>
            <xsl:with-param name="charsIn" select="'&quot;'"/>
            <xsl:with-param name="charsOut" select="'\&quot;'"/> <!-- select="'\&quot;'" -->
          </xsl:call-template>
        </xsl:variable>
        
        <xsl:call-template name="replaceCharsInString">
          <xsl:with-param name="stringIn" select="string($step1)"/>
          <xsl:with-param name="charsIn" select="'\'"/>
          <xsl:with-param name="charsOut" select="'\\'"/>
        </xsl:call-template>
    </xsl:template>
    
    <!-- CSS ID escape -->
    <xsl:template name="escapeCssId">
        <xsl:param name="stringIn"/>

        <xsl:call-template name="replaceCharsInString">
          <xsl:with-param name="stringIn" select="string($stringIn)"/>
          <xsl:with-param name="charsIn" select="'.'"/>
          <xsl:with-param name="charsOut" select="'_'"/>
        </xsl:call-template>
    </xsl:template>


    <!-- here is the template that does the replacement -->
    <xsl:template name="replaceCharsInString">
        <xsl:param name="stringIn"/>
        <xsl:param name="charsIn"/>
        <xsl:param name="charsOut"/>
        
        <xsl:choose>
          <xsl:when test="contains($stringIn,$charsIn)">
            <xsl:value-of select="concat(substring-before($stringIn,$charsIn),$charsOut)"/>
            <xsl:call-template name="replaceCharsInString">
              <xsl:with-param name="stringIn" select="substring-after($stringIn,$charsIn)"/>
              <xsl:with-param name="charsIn" select="$charsIn"/>
              <xsl:with-param name="charsOut" select="$charsOut"/>
            </xsl:call-template>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="$stringIn"/>
          </xsl:otherwise>
        </xsl:choose>
    </xsl:template>



</xsl:stylesheet>
