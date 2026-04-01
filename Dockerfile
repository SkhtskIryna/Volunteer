# Базовий образ з Java 17
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Копіювання fat JAR серверу
COPY server/build/libs/server.jar server.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "server.jar"]