FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /build
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src/ src/
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine AS runtime
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
WORKDIR /app
COPY --from=builder /build/target/Billing-Service-*.jar billing-service.jar
ENV SPRING_PROFILES_ACTIVE=prod
EXPOSE 4000
EXPOSE 8000
USER appuser
ENTRYPOINT ["java", "-jar", "billing-service.jar"]
