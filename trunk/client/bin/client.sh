# go in the client directory 
if [ -n "$LUCANE_CLIENT" ]; then
  cd $LUCANE_CLIENT;
elif [ -n "$LUCANE_HOME" ]; then
  cd $LUCANE_HOME/client;
elif [ -e "client.sh" ]; then
  cd ..;
elif [ -e "bin/client.sh" ]; then
  cd .;
elif [ -e "client/bin/client.sh" ]; then
  cd client;
else
  echo "unable to find server, set $LUCANE_HOME or $LUCANE_CLIENT !"
  exit 1;
fi
# classpath generation
for file in lib/*.jar
  do export CLASSPATH=$CLASSPATH:`pwd`/$file
done

# library path
export VM_OPTIONS="-Djava.library.path=lib/"

# run program
java $VM_OPTIONS org.lucane.client.Client $*
