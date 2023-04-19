#
# Build stage
#
FROM maven:3.6.0-jdk-17-slim AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package

FROM openjdk:11-jre-slim
COPY --from=build /home/app/target/ammensa-0.0.1-SNAPSHOT.jar /usr/local/lib/ammensa.jar
CMD ["java","-Xss512k","-Xmx128M","-XX:+UseContainerSupport","-jar","usr/local/lib/ammensa.jar"]