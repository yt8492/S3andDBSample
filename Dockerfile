FROM openjdk:8-jdk-alpine as build-stage
ADD . /S3andDBSample
WORKDIR /S3andDBSample
RUN chmod +x ./gradlew && ./gradlew shadowJar

FROM openjdk:8-jdk-alpine as exec-stage
COPY --from=build-stage /S3andDBSample/build/libs/S3andDBSample-0.0.1-all.jar .

ENTRYPOINT ["java", "-jar", "S3andDBSample-0.0.1-all.jar"]