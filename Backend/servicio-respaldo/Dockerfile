FROM eclipse-temurin:17-jdk-jammy

RUN apt-get update \
 && apt-get install -y --no-install-recommends mysql-client ca-certificates bash \
 && rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 9091
ENTRYPOINT ["java","-jar","app.jar"]
