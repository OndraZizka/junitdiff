

###  Try xsltproc.  `sudo apt-get install xsltproc`
if [ `which xsltproc` ] ; then  
  xsltproc --output "$3" "$2" "$1"
  return
fi


###  Try Xalan.
XALAN_XSLT_SCRIPT=${XALAN_XSLT_SCRIPT:-~/sw/tools/xalan/2.7.1/transform.sh}

if [ `which $XALAN_XSLT_SCRIPT` ] ; then
  
  if [ $# != 3 ] ; then
    echo "    Usage: $0 <in.xml> <xslt> <out.xml>";
    exit 1;
  fi

  if [ ! -f $1 ] ; then echo "$1 does not exist."; exit 2; fi
  if [ ! -f $2 ] ; then echo "$2 does not exist."; exit 3; fi
  if [ -f $3 ] ; then echo "Warning: $3 does already exists."; fi

  "$XALAN_XSLT_SCRIPT" $*

  return
fi

###  Nothing found.
echo "  No XSLT transformer found." > &2
echo "  Install xsltproc:       sudo apt-get install xsltproc" > &2
echo "  Or set path to XALAN:   export XALAN_XSLT_SCRIPT=~/sw/tools/xalan/2.7.1/transform.sh" > &2
return 1;