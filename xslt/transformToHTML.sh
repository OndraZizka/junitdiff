PROGNAME=`basename $0`
DIRNAME=`dirname $0`


if [ "" == "$1" ] ; then
    echo "  Usage:  transform.sh <XML file>"
    exit
fi


$DIRNAME/xslt.sh $1 $DIRNAME/JUnitDiff-to-HTML.xsl $1.html
