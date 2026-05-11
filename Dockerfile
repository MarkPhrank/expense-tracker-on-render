# Используем минимальный образ с Java 17
FROM eclipse-temurin:17-jre-alpine

# Создаём пользователя для безопасности
RUN addgroup -S app && adduser -S app -G app
USER app:app

# Копируем только JAR (маленький размер образа)
COPY target/expense-tracker-1.0.jar /app/app.jar

# Переменная порта для облака
ENV PORT=8080
EXPOSE 8080

# Запуск
ENTRYPOINT ["java", "-Xmx256m", "-jar", "/app/app.jar"]
