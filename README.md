
🥪 JUnitDiff - JUnit test reports aggregator
==========================================================
2022 revival

JUnitDiff is a tool, which aggregates several
[JUnit reports](https://howtodoinjava.com/junit5/xml-reports/), 
and creates a comprehensible comparison table.
Here is a sample output screenshot:

---

![JUnitDiff JUnit aggregated sample report](docs/screenshot-htmlReport.png)

---

Clicking on the `OK` reveals the output (and a stacktrace) of that test.

### Download:

1) Download the latest release's distribution:

```bash
wget --no-check-certificate https://github.com/OndraZizka/junitdiff/releases/download/JUnitDiff-2.0.0/JUnitDiff-2.0.0-dist.zip
unzip JUnitDiff-*-dist.zip
```

2) Install and try:

```bash
java -jar junitdiff-*/JUnitDiff-2.0.0-executable.jar
sudo echo 'java -jar $(pwd)/junitdiff-*/JUnitDiff-2.0.0-executable.jar "$@"' > /usr/bin/junitdiff
sudo chmod +x /usr/bin/junitdiff

junitdiff
```

### Usage:

```
junitdiff <input1> <input2> ... -o <output-path> [-xml]

# Or, without the installation steps above:
java -jar JUnitDiff.jar <input1> <input2> ... -o <output-path> [-xml]
```

Where `<input>` may be one of:

* Path to a JUnit XML report file.

      java -jar JUnitDiff.jar -o aggregatedReport.xml ./target/surefire-reports/
 
* Path to a directory. JUnitDiff scans the directory for `.xml` files.

      ls -1 ./target/surefire-reports > target/listOfReports.txt
      java -jar JUnitDiff.jar -o aggregatedReport.xml target/listOfReports.txt

* Path to a text file with a list of paths to JUnit XML report files.

      cd target/surefire-reports/
      java -jar JUnitDiff.jar -o aggregatedReport.xml ./TEST-MyClass.xml ./TEST-OtherClass.xml

* URL to a ZIP file containing JUnit XML report files.

      java -jar JUnitDiff.jar -o aggregatedReport.xml https://mycompany.github.com/myproject/build/123/artifacts/junitReports.zip

The `-xml` option skips creating the HTML output, and instead, produces an aggregated XML.

You can use the XSLT template on it to get the HTML on the fly, using `xsltproc` or `Xalan`:
```
./xslt/transformToHTML.sh aggregatedReport.xml
```

## Changelog

* **Plan for 2.2:** Make it also a Maven reporting plugin. 
* **Plan for 2.1:** 
  * Improve the generated page
  * Include stdout and stderr in the popups
* **Version 2.0:** Made it great again.
  * Made it work
  * Fix the in-page popups.
  * Clean up 
* **Version 1.6:** Switched to Kotlin.
* **Version 1.5:** The XSLT in this version seems not to work.
* **Version 1.4:** The version from around 2009.
