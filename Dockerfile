FROM openjdk:11-jre-slim
VOLUME /tmp
COPY target/ammensa-*.jar ammensa.jar
CMD ["java","-Djava.security.egd=file:/dev/./urandom","-Xss512k","-Xmx256M","-XX:+UseContainerSupport","-jar","/ammensa.jar"]