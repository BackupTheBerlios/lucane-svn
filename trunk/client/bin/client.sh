cd ..

# classpath generation
for file in lib/*.jar
  do export CLASSPATH=$CLASSPATH:`pwd`/$file
done

# library path
export VM_OPTIONS="-Djava.library.path=lib/"

# run program
java $VM_OPTIONS org.lucane.client.Client $*
