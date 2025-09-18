# Usar OpenJDK 21 como imagen base
FROM openjdk:21-jdk

# Directorio de trabajo
WORKDIR /app

# Copiar el JAR ya construido
COPY build/libs/*.jar app.jar

# Exponer puerto
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
