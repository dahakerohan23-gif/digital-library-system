# Multi-stage Dockerfile for Digital Library Management System
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app
COPY . .
RUN javac StandaloneServer.java

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/StandaloneServer*.class /app/
COPY --from=build /app/public /app/public

ENV PORT=8080
EXPOSE 8080

CMD ["java", "StandaloneServer"]
