FROM eclipse-temurin:17-jdk AS build
WORKDIR /app
COPY . .
RUN ./gradlew :server:shadowJar --no-daemon

FROM eclipse-temurin:17-jdk
WORKDIR /app

# Копіляція файлу, який створив shadowJar
COPY --from=build /app/server/build/libs/server.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-cp", "app.jar", "eu.tutorials.server.ServerKt"]
