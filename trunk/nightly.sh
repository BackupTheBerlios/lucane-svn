#!/bin/sh

SVN_URL=svn://svn.berlios.de/lucane/trunk
SCP_URL=vfiack@shell.berlios.de:/home/groups/lucane/htdocs/groupware/download/nightly

DATE=`date +%Y%m%d`
SRC_DIR=lucane-$DATE-src
BIN_DIR=lucane-$DATE-bin

# fetch sources
mkdir /tmp/$SRC_DIR
cd /tmp/$SRC_DIR
svn --force export $SVN_URL .
cd ..

# create source archive
tar zcvf $SRC_DIR.tgz $SRC_DIR
zip -r $SRC_DIR.zip $SRC_DIR

# compile
cd /tmp/$SRC_DIR
maven
cd ..

# create bin archive
mv dist $BIN_DIR
tar zcvf $BIN_DIR.tgz $BIN_DIR
zip -r $BIN_DIR.zip $BIN_DIR

scp $SRC_DIR.tgz $SRC_SIR.zip $BIN_DIR.tgz $BIN_DIR.zip $SCP_URL

