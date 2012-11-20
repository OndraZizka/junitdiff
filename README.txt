

JUnitDiff
*********

Compares several JUnit reports and creates a comparison table.
Curently quite simple, more to be done.


Usage:

   1) Create the XML file aggregated from several JUnit test runs.

        java -jar JUnitDiff-1.0.jar ( dir | TEST-foo.xml | report-paths-list.txt | http://reports.zip | -o output.file | -xml )+


   2) Transform the XML file to HTML

        ./transformToHTML.sh AGG-TEST.xml


See https://svn.devel.redhat.com/repos/jboss-qa/tools/JUnitDiff/xslt/AGG-TEST.xml.html 
for the expected result.
