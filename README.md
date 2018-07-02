JUnitDiff
==========

Compares several JUnit reports and creates a comparison table.
Curently quite simple, more to be done.


Usage:

1) Create the XML file aggregated from several JUnit test runs.

```
java -jar JUnitDiff.jar ( dir | TEST-foo.xml | report-paths-list.txt | http://reports.zip | -o output.file | -xml )+
```

2) Transform the XML file to HTML

```
./transformToHTML.sh AGG-TEST.xml
```

See http://www.qa.jboss.com/~ozizka/projects/junitdiff/AGG_TEST.xml.html for the expected result.
