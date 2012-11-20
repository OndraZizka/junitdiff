function downloadAndUnzip {
    iDB=$1
    iNum=$2

    #URL='http://hudson.qa.jboss.com/hudson/job/hibernate-eap5-qa2/DATABASE='$iDB',JDBC=jdbc4,jdk=java15_default,label=hibernate/lastSuccessfulBuild/artifact/src/*zip*/src.zip';
    #URL='http://hudson.qa.jboss.com/hudson/job/hibernate-eap5-qa2/'$iNum'/DATABASE='$iDB',JDBC=jdbc4,jdk=java15_default,label=hibernate/artifact/src/core/testsuite/target/surefire-reports/*zip*/surefire-reports.zip'
    URL='http://hudson.qa.jboss.com/hudson/job/hibernate-eap5-qa2/DATABASE='$iDB',JDBC=jdbc4,jdk=java15_default,label=hibernate/'$iNum'/artifact/src/*zip*/src.zip'
    URL='http://hudson.qa.jboss.com/hudson/job/hibernate-eap5-qa2/DATABASE='$iDB',JDBC=jdbc4,jdk=java15_default,label=hibernate/lastBuild/artifact/src/*zip*/src.zip'

    echo "Processing $iDB, build $iNum..."
    group="$iDB-$iNum";
    if [ ! -f workdir/$group.zip ] ; then 
      wget -nc $URL -O workdir/$group.zip;
    fi
    rm -rf workdir/$group
    mkdir workdir/$group
    unzip -q workdir/$group.zip -d workdir/$group
    REPORTS_ZIP_PATHS="$REPORTS_ZIP_PATHS workdir/$group"
}




## --- main() --- ##

mkdir -p workdir

# db2-97 oracle10g oracle11gR1 oracle11gR1RAC oracle11gR2 oracle11gR2RAC mysql50 mysql51 mssql2005 mssql2008 postgresql82 postgresql83 postgresql84 sybase15
# db2-97 oracle11gR2 mysql50 mysql51 mssql2005 mssql2008
for iDB in `echo '
 db2-97 oracle10g oracle11gR1 oracle11gR1RAC oracle11gR2 oracle11gR2RAC mysql50 mysql51 mssql2005 mssql2008 postgresql82 postgresql83 postgresql84 sybase15
'`; #  sybase15
do 
  for iNum in lastBuild; do
    downloadAndUnzip $iDB $iNum
  done
done




mkdir -p bin
wget -nc https://svn.devel.redhat.com/repos/jboss-qa/tools/JUnitDiff/dist/1.5/JUnitDiff.jar --no-check-certificate -O bin/JUnitDiff.jar
mkdir -p output
#java -jar bin/JUnitDiff.jar -o output/index.html $REPORTS_ZIP_PATHS
#java -jar ../trunk/target/dist/JUnitDiff.jar -xml -o output/AGG-TESTS.xml $REPORTS_ZIP_PATHS
java -jar ../trunk/target/dist/JUnitDiff.jar -o output/index.html $REPORTS_ZIP_PATHS
