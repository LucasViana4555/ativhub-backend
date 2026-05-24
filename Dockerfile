# Estágio 1: Build da aplicação
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copia o pom.xml e baixa as dependências (Aproveita o cache do Docker)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copia o código-fonte e gera o .jar da aplicação
COPY src ./src
RUN mvn clean package -DskipTests

# Estágio 2: Imagem final mais leve para rodar a aplicação
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Pega o .jar gerado no Estágio 1 e copia para a imagem final
COPY --from=build /app/target/*.jar app.jar

# Expõe a porta que o Spring Boot roda
EXPOSE 8080

# Comando para iniciar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]