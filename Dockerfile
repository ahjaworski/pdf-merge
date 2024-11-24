FROM eclipse-temurin:23-jre-alpine AS builder
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} application.jar
RUN java -Djarmode=tools -jar application.jar extract --layers --launcher

FROM eclipse-temurin:23-jre-alpine
RUN adduser -u 1000 -D java
WORKDIR application
COPY --chown=java:java --from=builder application/dependencies/ ./
COPY --chown=java:java --from=builder application/spring-boot-loader/ ./
COPY --chown=java:java --from=builder application/snapshot-dependencies/ ./
COPY --chown=java:java --from=builder application/application/ ./
USER 1000
EXPOSE 8080
# Use JAVA_TOOL_OPTIONS to specify runtime JVM arguments (like memory settings), not JAVA_OPTS
# since that doesn't work with Docker's entrypoint
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
