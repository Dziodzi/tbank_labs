FROM amazoncorretto:17-alpine AS layers
WORKDIR /application
COPY build/libs/*.jar app.jar
RUN java -Djarmode=layertools -jar app.jar extract

FROM amazoncorretto:17-alpine
WORKDIR /app
RUN adduser -S -D -H spring-user
USER spring-user
COPY --from=layers /application/dependencies/ ./
COPY --from=layers /application/spring-boot-loader/ ./
COPY --from=layers /application/snapshot-dependencies/ ./
COPY --from=layers /application/application/ ./
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]
