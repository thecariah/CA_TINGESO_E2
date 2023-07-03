FROM openjdk:17
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} grasol-service-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/grasol-service-0.0.1-SNAPSHOT.jar"]