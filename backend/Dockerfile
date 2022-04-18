FROM openjdk:17-jdk-alpine
COPY ./target/*.jar /hamster.jar
WORKDIR /opt/workdir/
ARG CERT="ZscalerRootCA.pem"
COPY $CERT /opt/workdir/
RUN keytool -importcert -file $CERT -alias $CERT -cacerts -storepass changeit -noprompt
ENTRYPOINT ["java","-jar","/hamster.jar"]