FROM eclipse-temurin:17-jre-alpine
RUN apk add --no-cache ca-certificates curl && update-ca-certificates
WORKDIR /app
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
EXPOSE 8083
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75.0"
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
