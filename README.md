# Gif Search Service
This server acts as a proxy to https://giphy.com
You can search gifs by querying the service. 

Server is created using embeded Jetty server. It serves the request in async way using asyncontext. It also uses Kotlin coroutines to serve requests. 

Steps to run the server:
1. mvn package # create jar 
2. java -jar target/GifService-1.0-SNAPSHOT-jar-with-dependencies.jar  # run the jar 
3. curl http://localhost:8000/signals?q=cheese # search gifs using query parameter
