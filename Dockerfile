FROM maven:alpine AS build

WORKDIR /usr/src/app

COPY src src
COPY pom.xml pom.xml

RUN mvn clean package

FROM openjdk:8-jre-alpine

EXPOSE 8080

COPY --from=build /usr/src/app/target/service-mdh.jar /usr/src/app/target/service-mdh.jar

CMD ["java", "-jar", "/usr/src/app/target/service-mdh.jar"]