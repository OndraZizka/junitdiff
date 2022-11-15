

JUnitDiff, 2022 revival
==========

A tool which aggregates several JUnit reports and creates a comprehensible comparison table.  
Currently, quite simple, more to be done.


### Usage:

**Step 1)** Create the XML file aggregated from several JUnit test runs.

```
java -jar JUnitDiff.jar <input>+ -o <output-path> [-xml]
```

Where `<input>` may be 1 or more of:

* Path to a JUnit XML report file.
* Path to a directory. JUnitDiff scans the directory for `.xml` files.
* Path to a text file with a list of paths to JUnit XML report files.
* URL to a ZIP file containing JUnit XML report files.

Examples:

```bash

java -jar JUnitDiff.jar -o aggregatedReport.xml ./target/surefire-reports/

ls -1 ./target/surefire-reports > target/listOfReports.txt
java -jar JUnitDiff.jar -o aggregatedReport.xml target/listOfReports.txt

cd target/surefire-reports/
java -jar JUnitDiff.jar -o aggregatedReport.xml ./TEST-MyClass.xml ./TEST-OtherClass.xml

java -jar JUnitDiff.jar -o aggregatedReport.xml https://mycompany.github.com/myproject/build/123/artifacts/junitReports.zip
```

**Step 2)** Transform the XML file to HTML.

```
./transformToHTML.sh aggregatedReport.xml
```

## Changelog

* **Plan for 1.8:** Make it also a Maven reporting plugin. 
* **Plan for 1.7:** Planning to make it great again.
  * Make it work 
  * Clean up the xslt/ dir
  * Make the transformation without a bash script
* **Version 1.6:** Switched to kotlin
* **Version 1.5:** Not sure what happened there but XSLT seems not to work
* **Version 1.4:** Last version known to work properly