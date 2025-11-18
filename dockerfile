# ------------ STAGE 1: сборка WAR ------------
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# сначала pom.xml, чтобы кешировались зависимости
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

# затем весь исходный код
COPY src ./src

# сборка WAR
RUN mvn -B -DskipTests clean package


# ------------ STAGE 2: Tomcat с нашим WAR ------------
FROM tomcat:10.1-jdk17-temurin

# по желанию чистим дефолтные приложения
RUN rm -rf /usr/local/tomcat/webapps/*

# копируем наш WAR как ROOT.war
COPY --from=build /app/target/SkillBench.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080

CMD ["catalina.sh", "run"]
