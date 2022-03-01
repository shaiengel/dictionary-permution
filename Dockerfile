FROM openjdk:8-jre-alpine3.9

EXPOSE 8000
WORKDIR /app

RUN mkdir -p /app/config
COPY ./config/* /app/config/
COPY dictionary-1.0-RELEASE.jar /app/

CMD ["java", "-jar", "dictionary-1.0-RELEASE.jar"]
