# classpath generation
for file in lib/*.jar
  do export CLASSPATH=$CLASSPATH:`pwd`/$file
done

# run program
java org.lucane.client.Client
