# ============================================
# Stage 1: Build
# ============================================
FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Copy pom trước để tận dụng cache layer
COPY pom.xml .

# Tải dependencies (cache nếu pom không đổi)
RUN mvn dependency:go-offline -B

# Copy source và build
COPY src src
RUN mvn package -DskipTests -B

# ============================================
# Stage 2: Runtime
# ============================================
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# User không root (bảo mật)
RUN addgroup -g 1000 appgroup && adduser -u 1000 -G appgroup -D appuser
USER appuser

# Copy jar từ stage build
COPY --from=build /app/target/*.jar app.jar

# Port ứng dụng
EXPOSE 8081

# Chạy Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]
