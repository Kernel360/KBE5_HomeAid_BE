# 베이스 이미지 선택
FROM openjdk:17-jdk-alpine

# JAR 파일을 컨테이너로 복사
COPY build/libs/*.jar app.jar

# 기본 실행 명령
ENTRYPOINT ["java", "-jar", "/app.jar"]
