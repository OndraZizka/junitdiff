

if [ "" == "$1" ] ; then
	echo "  Usage:  transform.sh <XML file>"
	exit
fi


~/scripts/xslt $1 JUnitDiff-to-HTML.xsl $1.html
#./xslt $1 JUnitDiff-to-HTML.xsl $1.html

