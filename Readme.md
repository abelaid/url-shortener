# URL Shortener

### Build
You need Java 21 and to set JAVA_HOME env var.

You also need Docker engine if you want to run the container.

### Run

You can run the Spring Boot jar

```
mvnw clean package
java -jar target/url-shortener-0.0.1-SNAPSHOT.jar
```

Or build (without pushing to registry) and run the container
```
mvnw compile jib:buildTar
docker load --input target/jib-image.tar
```

### Test
in your browser enter http://localhost:8080/?url=https://www.lemonde.fr

A result containing the shortened url will be returned.

Reenter the result in your browser then you will be redirected to the complete url.


