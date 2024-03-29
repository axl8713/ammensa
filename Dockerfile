#
# Build stage
#
FROM maven:3.9.1-amazoncorretto-17-debian AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package

FROM maven:3.9.1-amazoncorretto-17-debian
COPY --from=build /home/app/target/ammensa-0.0.1-SNAPSHOT.jar /usr/local/lib/ammensa.jar
CMD ["java","-Xss512k","-Xmx128M","-XX:+UseContainerSupport","-jar","usr/local/lib/ammensa.jar"]