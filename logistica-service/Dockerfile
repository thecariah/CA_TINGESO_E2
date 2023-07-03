FROM openjdk:17
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} logistica-service-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/logistica-service-0.0.1-SNAPSHOT.jar"]