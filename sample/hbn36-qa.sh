#set -x

workdir=$1
workdir=${workdir:="workdir-"$(basename $0)}

echo "  Working in $workdir.";


function downloadAndUnzip {
    iDB=$1
    iNum=$2

    URL="http://hudson.qa.jboss.com/hudson/job/hibernate-core-testsuite-branch36/database=$iDB,jdk=java15_default,label=hibernate/lastSuccessfulBuild/artifact/*zip*/archive.zip"

    echo " --- Processing $iDB, build $iNum..."
    group="$iDB-$iNum";
    if [ ! -f $workdir/$group.zip ] ; then
      #echo "  Downloading $URL";
      wget -nv -nc $URL -O $workdir/$group.zip
      if [ 0 != $? ] ; then rm $workdir/$group.zip; return 1; fi;
    fi
    #echo 1 ###
    rm -rf $workdir/$group
    mkdir $workdir/$group
    #echo 2 ###
    unzip -q $workdir/$group.zip -d $workdir/$group
    fileCount=$(ls -1 "$workdir/$group/archive" | wc -l);
    zipCount=$(ls -1 -d $workdir/$group/archive/*.zip | wc -l);
    if [ 1 == $fileCount -a  1 == $zipCount ] ; then
      echo "  It was zipped, unzipping the inner zip.";
      rm -rf $workdir/tmp-2nd-unzip
      mv $workdir/$group $workdir/tmp-2nd-unzip
      unzip -q $workdir/tmp-2nd-unzip/archive/*.zip -d $workdir/$group
      rm -rf $workdir/tmp-2nd-unzip
    fi
    REPORTS_ZIP_PATHS="$REPORTS_ZIP_PATHS $workdir/$group"
}




## --- main() --- ##

mkdir -p $workdir

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
