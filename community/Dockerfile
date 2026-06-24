FROM eclipse-temurin:17-jdk-focal

ARG JAR_FILE=community/build/libs/*.jar

COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java","-Duser.timezone=Asia/Seoul","-jar","/app.jar"]
