FROM openjdk:11.0.11-jre
EXPOSE 8083
ARG JAR_FILE=target/ManagementService-0.0.1-SNAPSHOT.jar
ADD ${JAR_FILE} manangement.jar
ENTRYPOINT ["java","-jar","/manangement.jar"]