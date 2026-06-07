FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /build
COPY pom.xml .de
RUN mvn dependency:go-offline -B
COPY src/ src/
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine AS runtime
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
WORKDIR /app
COPY --from=builder /build/target/patient-service-*.jar patient-service.jar
ENV SPRING_PROFILES_ACTIVE=prod
EXPOSE 4000
USER appuser
ENTRYPOINT ["java", "-jar", "patient-service.jar"]
