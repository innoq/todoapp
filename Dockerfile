FROM eclipse-temurin:17-jdk-alpine
COPY target/todoapp-jar-with-dependencies.jar /
CMD java -Djava.security.egd=file:/dev/./urandom -jar /todoapp-jar-with-dependencies.jar
EXPOSE 8080
