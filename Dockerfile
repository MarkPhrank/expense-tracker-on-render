# ========== СТАДИЯ 1: Сборка (Maven + JDK) ==========
FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /build

# Копируем только pom.xml сначала (кэшируем зависимости)
COPY pom.xml .
RUN mvn dependency:go-offline -q

# Копируем исходный код и собираем
COPY src ./src
RUN mvn clean package -DskipTests -q

# ========== СТАДИЯ 2: Запуск (минимальный JRE) ==========
FROM eclipse-temurin:17-jre-alpine

# Создаём пользователя для безопасности
RUN addgroup -S app && adduser -S app -G app
USER app:app

# Копируем готовый JAR из стадии builder
COPY --from=builder /build/target/expense-tracker-1.0.jar /app/app.jar

# Порт и запуск
ENV PORT=8080
EXPOSE 8080
ENTRYPOINT ["java", "-Xmx256m", "-jar", "/app/app.jar"]
