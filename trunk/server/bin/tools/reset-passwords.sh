# classpath generation
cd ..

for file in lib/*.jar
  do export CLASSPATH=$CLASSPATH:`pwd`/$file
done

# run program
java org.lucane.server.tools.ResetPasswords $*
