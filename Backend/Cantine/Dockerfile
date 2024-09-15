

FROM maven:3.9.0-eclipse-temurin-19 AS maven_build
WORKDIR /cantine
COPY pom.xml .
COPY src ./src
RUN --mount=type=cache,target=/root/.m2  mvn package -Dmaven.test.skip=true
RUN mkdir -p target/docker-packaging && cd target/docker-packaging && jar -xf ../cantine*.war

FROM eclipse-temurin:latest
WORKDIR /cantine
ARG DOCKER_PACKAGING_DIR=/cantine/target/docker-packaging
COPY --from=maven_build ${DOCKER_PACKAGING_DIR}/WEB-INF/lib /cantine/lib
COPY --from=maven_build ${DOCKER_PACKAGING_DIR}/WEB-INF/classes /cantine/classes
COPY --from=maven_build ${DOCKER_PACKAGING_DIR}/META-INF /cantine/META-INF

CMD java -cp .:classes:lib/* \
         -Djava.security.egd=file:/dev/./urandom \
         fr.sqli.cantine.CantineApplication

