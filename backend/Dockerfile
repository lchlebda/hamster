# syntax=docker/dockerfile:1
FROM openjdk:17-jdk-alpine
#WORKDIR /app
#COPY .mvn/ .mvn
#COPY mvnw pom.xml ./
COPY ./target/*.jar /hamster.jar
ENTRYPOINT ["java","-jar","/hamster.jar"]
#CMD java -jar hamster.jar
#RUN ./mvnw dependency:go-offline

#COPY src ./src

#CMD ["./mvnw", "spring-boot:run"]