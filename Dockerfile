FROM openjdk:17
ARG JAR_FILE=target/graduation-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} /graduation.jar
ENTRYPOINT ["java","-jar","/graduation.jar"]
EXPOSE 8080