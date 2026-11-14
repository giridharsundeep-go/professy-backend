FROM eclipse-temurin:26-jdk-noble AS build
WORKDIR /workspace
COPY . .
RUN chmod +x gradlew && ./gradlew --no-daemon bootJar -x test \
    && find build/libs -maxdepth 1 -type f -name '*.jar' ! -name '*-plain.jar' -exec cp {} /tmp/service.jar \; \
    && test -s /tmp/service.jar

FROM eclipse-temurin:26-jre-noble
WORKDIR /app
RUN groupadd --system app && useradd --system --gid app --uid 10001 --no-create-home app
COPY --from=build --chown=app:app /tmp/service.jar /app/service.jar
USER 10001:10001
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/service.jar"]
