# go in the server directory 
if [ -n "$LUCANE_SERVER" ]; then
  cd $LUCANE_SERVER;
elif [ -n "$LUCANE_HOME" ]; then
  cd $LUCANE_HOME/server;
elif [ -e "server.sh" ]; then
  cd ..;
elif [ -e "bin/server.sh" ]; then
  cd .;
elif [ -e "server/bin/server.sh" ]; then
  cd server;
else
  echo "unable to find server, set LUCANE_HOME or LUCANE_SERVER !"
  exit 1;
fi

# classpath generation
for file in lib/*.jar
  do export CLASSPATH=$CLASSPATH:`pwd`/$file
done

# run program
java org.lucane.server.Server $*
